function S = lineSegmentFinder(I, H, thres)

%%  Find Peaks
    BW = edge(I, 'canny');
    %   Radius of area that will be set to zero
    nhood = size(H)/500; 
    nhood = max(2 * ceil(nhood/2) + 1, 1);
    %   Other params
    [theta, rho] = generateThetaRho(I, size(H, 2), size(H, 1));
    if thres == 0
        thres = 0.15 * max(H(:));
    end
    numpeaks = 15;
    %   Find the peaks
    done = false;
    Hnew = H;rhoPeaksIdx = [];thetaPeaksIdx = [];
    while ~done
        [rhoMaxIdx, thetaMaxIdx] = find(Hnew == max(Hnew(:)));
        rhoMaxIdx = rhoMaxIdx(1);thetaMaxIdx = thetaMaxIdx(1);
        if Hnew(rhoMaxIdx,thetaMaxIdx) >= thres
            %   Record the rho and theta
            rhoPeaksIdx(end + 1) = rhoMaxIdx; 
            thetaPeaksIdx(end + 1) = thetaMaxIdx;
            %   Suppress the peak
            rowMin = rhoMaxIdx - (nhood(1) - 1)/2; rowMax = rhoMaxIdx + (nhood(1) - 1)/2;
            colMin = thetaMaxIdx - (nhood(2) - 1)/2; colMax = thetaMaxIdx + (nhood(1) - 1)/2;
            [gridRowMat,gridColMat] = ndgrid(rowMin:rowMax,colMin:colMax);
            gridRowMat = gridRowMat(:);
            gridColMat = gridColMat(:);
            %   Throw away coordinate that are out of range in rho
            badRow = find((gridRowMat < 1) | (gridRowMat >size(H, 1)));
            gridRowMat(badRow) = []; gridColMat(badRow) = [];
            thetaLow = find(gridColMat < 1);
            gridColMat(thetaLow) = size(H, 2) + gridColMat(thetaLow);
            gridRowMat(thetaLow) = size(H, 1) - gridRowMat(thetaLow) + 1;
            thetaHigh = find(gridColMat > size(H, 2));
            gridColMat(thetaHigh) = gridColMat(thetaHigh) - size(H, 2);
            gridRowMat(thetaHigh) = size(H, 1) - gridRowMat(thetaHigh) + 1;
            %	Suppress the neighborhood
            Hnew(sub2ind(size(Hnew), gridRowMat, gridColMat)) = 0;
            done = length(rhoPeaksIdx) == numpeaks;
        else
            done = true;
        end
    end

    %   --------
    figure()
    imshow(imadjust(mat2gray(H)),[],'XData',theta,'YData',rho,...
        'InitialMagnification','fit');
    xlabel('\theta (degrees)')
    ylabel('\rho')
    axis on
    axis normal
    hold on
    colormap(hot)
    x = theta(thetaPeaksIdx);
    y = rho(rhoPeaksIdx);
    plot(x, y, 's', 'Color','k')
%%  Find lines
    %   Find lines (20,4)
    lines = detectLines(BW, theta, rho, rhoPeaksIdx, thetaPeaksIdx, 15, 6);
    
    h = figure(); imshow(double(I),[]), hold on;
    for k = 1 : length(lines)
        xy = [lines(k).point1; lines(k).point2];
        plot(xy(1,2),xy(1,1),'x','LineWidth',2,'Color','yellow');
        plot(xy(2,2),xy(2,1),'x','LineWidth',2,'Color','red');
        plot(xy(:,2), xy(:, 1), 'LineWidth', 2, 'Color','green');
    end
    f = getframe(h);
    S = f.cdata;
    saveas(h, 'res_seg.png');
    
function [r, c] = computePixel(BW, theta, rho, rhoBinIdx, thetaBinIdx)
    [x, y, ~] = find(BW);

    theta_c = theta(thetaBinIdx) * pi / 180;
    rho_xy = x*cos(theta_c) + y*sin(theta_c);
    nrho = length(rho);
    slope = (nrho - 1)/(rho(end) - rho(1));
    rho_bin_index = round(slope*(rho_xy - rho(1)) + 1);
    idx = find(rho_bin_index == rhoBinIdx);
    r = y(idx) + 1; c = x(idx) + 1;
    r_range = max(r) - min(r);
    c_range = max(c) - min(c);
    if r_range > c_range
        sorting_order = [1 2];
    else
        sorting_order = [2 1];
    end
    [rc_new] = sortrows([r c], sorting_order);
    r = rc_new(:,1);
    c = rc_new(:,2);
    
function lines = detectLines(BW, theta, rho, rhoPeaksIdx, thetaPeaksIdx, fillGap, minLength)
    if fillGap == 0
        fillGap = 15;
    end
    if minLength == 0
        minLength = 6;
    end
    minlengthSq = minLength^2;
    fillgapSq = fillGap^2;
    numLines = 0; 
    lines = struct([]);
    for i = 1 : length(thetaPeaksIdx)
        rhoBinIdx = rhoPeaksIdx(i); thetaBinIdx = thetaPeaksIdx(i);
        % Get all pixels associated with Hough transform cell.
        [r, c] = computePixel(BW, theta, rho, rhoBinIdx, thetaBinIdx);
        if isempty(r) 
            continue 
        end
        % Compute distance^2 between the point pairs
        xy = [c r]; % x,y pairs in coordinate system with the origin at (1,1)
        diff_xy_sq = diff(xy,1,1).^2;
        dist_sq = sum(diff_xy_sq,2);
        % Find the gaps larger than the threshold.
        fillgap_idx = find(dist_sq > fillgapSq);
        idx = [0; fillgap_idx; size(xy,1)];
        for p = 1:length(idx) - 1
            p1 = xy(idx(p) + 1,:); % offset by 1 to convert to 1 based index
            p2 = xy(idx(p + 1),:); % set the end (don't offset by one this time)

            linelength_sq = sum((p2-p1).^2);
            if linelength_sq >= minlengthSq
                numLines = numLines + 1;
                lines(numLines).point1 = p1;
                lines(numLines).point2 = p2;
                lines(numLines).theta = theta(thetaBinIdx);
                lines(numLines).rho = rho(rhoBinIdx);
            end
        end
    end
