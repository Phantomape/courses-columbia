function [theta, rho] = generateThetaRho(BW, theta_num_bins, rho_num_bins)
    %   Define the hough space, normally we would set the resolution to 1. 
    %   Here, theta number be even numbers and rho_num_bins must be odd.
    [M, N] = size(BW);
    dtheta = 90/(theta_num_bins/2);

    theta = linspace(-90, 0, ceil(90/dtheta) + 1);
    theta = [theta -fliplr(theta(2 : end - 1))];
    
    D = sqrt((M - 1)^2 + (N - 1)^2);
    Q = (rho_num_bins - 1)/2;
    drho = round(D/Q);
    rho = linspace(-Q * drho, Q * drho, rho_num_bins);
end
