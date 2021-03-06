function L = lineFinder(I, H, thres)
    %   Initial required params
    BW = edge(I, 'canny');
    nhood = size(H)/500;
    nhood = max(2 * ceil(nhood/2) + 1, 1);
    [theta, rho] = generateThetaRho(I, size(H, 2), size(H, 1));
    if thres == 0
        thres = 0.1 * max(H(:));
    end
    numpeaks = 15;
    
    done = false;
    Hnew = H;rhoPeaksIdx = [];thetaPeaksIdx = [];
    while ~done
        [rhoMaxIdx, thetaMaxIdx] = find(Hnew == max(Hnew(:)));
        rhoMaxIdx = rhoMaxIdx(1);thetaMaxIdx = thetaMaxIdx(1);
        if Hnew(rhoMaxIdx,thetaMaxIdx) >= thres
            rhoPeaksIdx(end + 1) = rhoMaxIdx; thetaPeaksIdx(end + 1) = thetaMaxIdx;
            %   Suppress the peak
            rowMin = rhoMaxIdx - (nhood(1) - 1)/2; rowMax = rhoMaxIdx + (nhood(1) - 1)/2;
            colMin = thetaMaxIdx - (nhood(2) - 1)/2; colMax = thetaMaxIdx + (nhood(1) - 1)/2;
            [gridRowMat,gridColMat] = ndgrid(rowMin:rowMax,colMin:colMax);
            gridRowMat = gridRowMat(:);
            gridColMat = gridColMat(:);
            %   Throw away neightbor coordinate that are out of range
            badrho = find((gridRowMat < 1) | (gridRowMat >size(H, 1)));
            gridRowMat(badrho) = []; gridColMat(badrho) = [];
            thetaLow = find(gridColMat < 1);
            gridColMat(thetaLow) = size(H, 2) + gridColMat(thetaLow);
            gridRowMat(thetaLow) = size(H, 1) - gridRowMat(thetaLow) + 1;
            thetaHigh = find(gridColMat > size(H, 2));
            gridColMat(thetaHigh) = gridColMat(thetaHigh) - size(H, 2);
            gridRowMat(thetaHigh) = size(H, 1) - gridRowMat(thetaHigh) + 1;
            %   Suppress the neighborhood
            Hnew(sub2ind(size(Hnew), gridRowMat, gridColMat)) = 0;
            done = length(rhoPeaksIdx) == numpeaks;
        else
            done = true;
        end
    end

    %   --------
    figure()
    imshow(imadjust(mat2gray(H)),[],...
        'XData',theta,...
        'YData',rho,...
        'InitialMagnification','fit');
    xlabel('\theta (degrees)')
    ylabel('\rho')
    axis on
    axis normal
    hold on
    colormap(gray)
    
    x = theta(thetaPeaksIdx);
    y = rho(rhoPeaksIdx);
    plot(x, y, 's', 'Color','w')


    %   Find lines (20,4)
    lines = hlines(BW, theta, rho, rhoPeaksIdx, thetaPeaksIdx, 15, 6);

    h = figure(), imshow(double(I),[]), hold on;
    for k = 1 : length(lines)
        xy = [lines(k).point1; lines(k).point2];
        t = (xy(2,1) - xy(1,1))/(xy(2,2) - xy(1,2));
        x = xy(2,2) - 700 : xy(1,2) + 700;
        y = xy(1, 1) + t * (x - xy(1,2));
        plot(x, y, 'LineWidth',2,'Color','b');
    end
    f = getframe(h);
    L = f.cdata;
    saveas(h, 'res.png');
    
%-----------------------------------------------------------------------------
function [r, c] = houghpixels(BW, theta, rho, rbin, cbin)
%HOUGHPIXELS Compute image pixels belonging to Hough transform bin.
%   [R, C] = HOUGHPIXELS(NONZEROPIX, THETA, RHO, PEAK) computes the
%   row-column indices (R, C) for nonzero pixels NONZEROPIX that map
%   to a particular Hough transform bin, PEAK which is a two element
%   vector [RBIN CBIN].  RBIN and CBIN are scalars indicating the 
%   row-column bin location in the Hough transform matrix returned by
%   function HOUGH.  THETA and RHO are the second and third output 
%   arguments from the HOUGH function.

[x, y, val] = find(BW);
x = x - 1; y = y - 1;

theta_c = theta(cbin) * pi / 180;
rho_xy = x*cos(theta_c) + y*sin(theta_c);
nrho = length(rho);
slope = (nrho - 1)/(rho(end) - rho(1));
rho_bin_index = round(slope*(rho_xy - rho(1)) + 1);

idx = find(rho_bin_index == rbin);

r = x(idx) + 1; c = y(idx) + 1;


function lines = hlines(BW, theta, rho, rr, cc, fillgap, minlength)
    if fillgap == 0
        fillgap = 20;
    end
    if minlength == 0
        minlength = 40;
    end
    numlines = 0; lines = struct([]);
    for k = 1 : length(rr)
        rbin = rr(k); cbin = cc(k);
        [r, c] = houghpixels(BW, theta, rho, rbin, cbin);
        if isempty(r)
            continue
        end
        omega = (90 - theta(cbin)) * pi/180;
        T = [cos(omega) sin(omega); -sin(omega) cos(omega)];
        xy = [r - 1, c - 1] * T;
        x = sort(xy(:, 1));
        
        diff_x = [diff(x); Inf];
        idx = [0; find(diff_x >fillgap)];
        for p = 1 : length(idx) - 1
            x1 = x(idx(p) + 1); x2 = x(idx(p + 1));
            linelength = x2 - x1;
            if linelength >= minlength
                point1 = [x1 rho(rbin)]; point2 = [x2 rho(rbin)];
                Tinv = inv(T);
                point1 = point1 * Tinv; point2 = point2 * Tinv;
                
                numlines = numlines + 1;
                lines(numlines).point1 = point1 + 1;
                lines(numlines).point2 = point2 + 1;
                lines(numlines).length = linelength;
                lines(numlines).theta = theta(cbin);
                lines(numlines).rho = rho(rbin);
            end
        end
    end
        