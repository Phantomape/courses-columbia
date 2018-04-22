function imResult = blendMode_Overlay(A, B, maskA, maskB)
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
blendMode_checkInput(nargin, a, b, func2str(@blendMode_Overlay));

%% Implementation
maskOverlap = double(logical(maskA)) + double(logical(maskB));
maskOverlap(find(maskOverlap == 1)) = 0;
maskOverlap = maskOverlap/2;
imResult(:,:,1) = A(:,:,1).*(1 - maskOverlap) + B(:,:,1);
imResult(:,:,2) = A(:,:,2).*(1 - maskOverlap) + B(:,:,2);
imResult(:,:,3) = A(:,:,3).*(1 - maskOverlap) + B(:,:,3);

end