function panorama = stitchImg(varargin)
    %   Use default images for stitching
    close all
    T = zeros(3,3,nargin - 1);
    tforms(nargin) = projective2d(eye(3));
    for i = 2:nargin
        I_prev = varargin{i - 1};
        I_curr = varargin{i};
        [xs, xd] = genSIFTMatches(I_prev, I_curr);
        %before_img = showCorrespondence(I_prev, I_curr, xs, xd);
        % Use RANSAC to reject outliers
        ransac_n = 30; % Max number of iteractions
        ransac_eps = 4; 
        [~, H_3x3] = runRANSAC(xs, xd, ransac_n, ransac_eps);
        %after_img = showCorrespondence(I_prev, I_curr, xs(inliers_id, :), xd(inliers_id, :));
        %T(:,:,i - 1) = computeHomography(xs(inliers_id,:),xd(inliers_id,:));
        T(:,:,i - 1) = H_3x3;
        tforms(i).T = tforms(i-1).T  * T(:,:,i - 1);
    end
    
    %%
    centerIdx = floor((numel(tforms)+1)/2);
    Tinv = invert(tforms(centerIdx));
    for i = 1:numel(tforms)    
        tforms(i).T = tforms(i).T * Tinv.T;
        tforms(i).T(1,3) = tforms(i).T(1,3) + 100;
    end
    
    imageSize = size(I_prev);  % all the images are the same size
    I = varargin{1};
    [mask, res] = backwardWarpImg(I, tforms(1).T,[3*imageSize(2), 2*imageSize(1)]);
    panorama = res;
    mask_p = mask;
    for i = 2:nargin
        I = varargin{i};
        [mask, res] = backwardWarpImg(I, tforms(i).T,[3*imageSize(2), 2*imageSize(1)]);
        panorama = blendImagePair(panorama, mask_p, res, mask, 'blend');
        mask_p = logical(rgb2gray(panorama));
    end
    
    imshow(panorama);
