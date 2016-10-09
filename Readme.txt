%Function statement:
%   function H = generateHoughAccumulator(BW, theta_num_bins, rho_num_bins)
%   generate the hough space accumulator. If we get a bad result, normally 
%   I would suggest that we manually tune the parameters, by increasing the 
%   number of bins, we would normally acquire a better. As for voting 
%   techniques, I used the Gaussian distribution to replace the add 1 oper-
%   ations, because gaussian distribution would give a higher voting for 
%   bins near our interested point in the parameter space, and decrease 
%   gradually, which would make the algorithm more robust to noise or 
%   insufficient accuracy
%   
%   function L = lineFinder(I, H, thres) calculates hough peaks by  
%   iteratively finding its maximum and then suppress the maximum and its
%   neighborhood values to 0. After detecting peaks, it would draw lines on
%   the original image, but to obtain better results, two parameters have 
%   to be tuned, namely fillgap and minlength in the function hlines.
%   Subfunction:
%       function hlines(BW, theta, rho, rr, cc, fillgap, minlength) this 
%       function return lines properties like starting point and ending 
%       point, length, theta and rho. 
%       function [r, c] = houghpixels(BW, theta, rho, rbin, cbin) this 
%       function returns computes the row-column indices (R, C) for 
%       edge pixels that map to a particular Hough transform bin, RBIN and 
%       CBIN are scalars indicating the row-column bin location in the 
%       Hough transform matrix returned by function generateHoughAccumulator
%      
%       
%   function S = lineSegmentFinder(I, H, thres) basically redo the whole 
%   calculation process in function lineFinder(¡¤), but this time it would 
%   draw lines based on the starting point and ending point. Comparing the
%   distance between all points and the point(1,1), if the distance is 
%   larger than fillgap and the distance in x coordinate is larger than
%   minlength, it would be recorded.
%   'fillgap'   Positive real scalar.
%               When hlines(¡¤) finds two line segments associated
%               with the same Hough transform bin that are separated
%               by less than 'FillGap' distance, hline merges
%               them into a single line segment.
%
%               Default: 15
%
%   'minlength' Positive real scalar.
%               Merged line segments shorter than 'MinLength'
%               are discarded.
%
%               Default: 6
%   points 