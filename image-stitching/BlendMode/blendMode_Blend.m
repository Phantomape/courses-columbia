function imResult = blendMode_Blend(A, B, maskA, maskB)
%% Overlay blending mode: combines Multiply and Screen blend modes.
%   The parts of the top layer where base layer is light become lighter, 
%   the parts where the base layer is dark become darker. 
% 
% Input:
%       A       -       Base Image
%       B       -       Top Image
%       maskA   -       Mask of A
%       maskB   -       Mask of B
%
% Output:
%       imResult    -   Result of the blending, having the same size of the
%                       Base Image A.
% 

%% Check Input
a = size(A);
b = size(B);
blendMode_checkInput(nargin, a, b, func2str(@blendMode_Blend));
%% Implementation
maskA = logical(maskA); maskB = logical(maskB);
maskL = maskA >= 1; maskR = maskB >= 1;
maskL = ~maskL; maskR = ~maskR;
weight1 = bwdist(maskL); weight2 = bwdist(maskR);
weight1 = weight1/max(weight1(:));
weight2 = weight2/max(weight2(:));
weight1 = cat(3, weight1, weight1, weight1);
weight2 = cat(3, weight2, weight2, weight2);
subplot(1,2,1)
imagesc(weight1)
subplot(1,2,2)
imagesc(weight2)

imRes1 = (A.*weight1)./(weight1 + weight2);
imRes2 = (B.*weight2)./(weight1 + weight2);
imRes1(isnan(imRes1)) = 0;
imRes2(isnan(imRes2)) = 0;
imResult = imRes1 + imRes2;
figure
imshow(imResult)
end

