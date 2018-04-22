%   main function
close all,clc,clear
%%  challenge1a
load('src_dest.mat')
% I = imread('portrait.png');
% I_trans = imread('portrait_transformed.png');
% imshow(I)
% src_pts = ginput(4);
% imshow(I_trans)
% dest_pts = ginput(4);

H = computeHomography(src_pts, dest_pts);
% imshow(I)
% test_pts = ginput(4);
% save('test_pts.mat','test_pts');
load('test_pts');
dest_pts_test = applyHomography(H, test_pts);
R = showCorrespondence(I, I_trans, test_pts, dest_pts_test);

%%  challenge1b
% Test wrapping 
bg_img = im2double(imread('Osaka.png')); %imshow(bg_img);
portrait_img = im2double(imread('portrait_small.png')); %imshow(portrait_img);

% Estimate homography
% imshow(bg_img)
% bg_pts = ginput(4);
% imshow(portrait_img)
% portrait_pts = ginput(4);
% portrait_pts = [xp1 yp1; xp2 yp2; xp3 yp3; xp4 yp4];
% bg_pts = [xb1 yb1; xb2 yb2; xb3 yb3; xb4 yb4];
load('bg_portrait.mat')
H_3x3 = computeHomography(portrait_pts, bg_pts);


warpedImg = imwarp( bg_img, portrait_img, H_3x3, bg_pts);
imshow(warpedImg);
dest_canvas_width_height = [size(bg_img, 2), size(bg_img, 1)];

% Warp the portrait image
[mask, dest_img] = backwardWarpImg(portrait_img, inv(H_3x3), dest_canvas_width_height);
% mask should be of the type logical
mask = ~mask;
% Superimpose the image
result = bg_img .* cat(3, mask, mask, mask) + dest_img;
%figure, imshow(result);
imwrite(result, 'Van_Gogh_in_Osaka.png');