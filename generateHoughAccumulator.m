function H = generateHoughAccumulator(BW, theta_num_bins, rho_num_bins)
    %   Define the hough space, normally we would set the resolution to 1. 
    %   Here, theta number be even numbers and rho_num_bins must be odd.
    I = BW;
    BW = double(BW);
    [M, N] = size(BW);
    dtheta = 90/(theta_num_bins/2);

    theta = linspace(-90, 0, ceil(90/dtheta) + 1);
    theta = [theta -fliplr(theta(2 : end - 1))];
    
    D = sqrt((M - 1)^2 + (N - 1)^2);
    Q = (rho_num_bins - 1)/2;
    drho = round(D/Q);
    rho = linspace(-Q * drho, Q * drho, rho_num_bins);
   
    mu = [0,0];sigma = [0.3 0;0 0.35];
    [x y] = meshgrid(linspace(-1,1,3)', linspace(-1,1,3)');
    X = [x(:) y(:)];
    z = mvnpdf(X,mu,sigma);
    z = z/(max(z(:)));
    z = reshape(z, 3, 3);

    H = zeros(rho_num_bins, theta_num_bins);
    for row = 1 : size(BW, 1)
        for col = 1 : size(BW, 2)
            if I(row, col) == 1
                for p = 1 : length(theta)
                    theta_p = theta(p)*pi/180;
                    rho_q = row * cos(theta_p) + col * sin(theta_p);
                    [~,q] = min(abs(rho_q - rho));
                    H(q, p) = H(q, p) + 1;
                    %   Modified Voting Scheme: use distribution, not
                    %   necessary
%                     for i = 1 : 3
%                         for j = 1 : 3
%                             if(q + (i - 2) > 0 && p +(j - 2) > 0 && q + (i - 2) < rho_num_bins && p +(j - 2) < theta_num_bins )
%                                 H(q +(i - 2),p + (j - 2)) = H(q +(i - 2),p + (j - 2)) + z(i,j);
%                             end
%                         end
%                     end
                    
                end
            end
        end
    end
    %   Scale
    H = H/max(H(:)) * 255;
    
    %Visualization
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
    colormap(hot)
        
%   ----------------------------------------------------------------------
%     %   Version 1
%     [x, y, val] = find(BW);
%     x = x - 1; y = y - 1;
%     
%     for k = 1 : ceil(length(val)/1000)
%         first = (k - 1) * 1000 + 1;
%         last = min(first + 999, length(x));
%         %   For every theta
%         x_mat = repmat(x(first:last), 1, theta_num_bins);
%         y_mat = repmat(y(first:last), 1, theta_num_bins);
%         val_mat = repmat(val(first:last), 1, theta_num_bins);
%         theta_mat = repmat(theta, size(x_mat,1),1)*pi/180;
%         rho_mat = x_mat.*cos(theta_mat) + y_mat.*sin(theta_mat);
%         slope = (rho_num_bins - 1)/(rho(end) - rho(1));
%         rho_bin_idx = round(slope*(rho_mat - rho(1)) + 1);
%         theta_bin_idx = repmat(1 : theta_num_bins, size(x_mat, 1), 1);
%         H = H + full(sparse( rho_bin_idx(:), theta_bin_idx(:),double(val_mat(:)), rho_num_bins, theta_num_bins ) );
%     end

%     [xId, yId] = find(BW);
%     
%     for k = 1 : length(xId)
%         p = 1;
%         while p < length(theta)
%             theta_p = theta(p)*pi/180;
%             rho_q = xId(k) * cos(theta_p) + yId(k) * sin(theta_p);
%             [~, q] = min(abs(rho_q - rho));
%             H(q, p) = H(q, p) + 1;
%             p = p + 1;
%         end
%     end