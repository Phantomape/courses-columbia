function out_img = blendImagePair(wrapped_imgs, masks, wrapped_imgd, maskd, mode)
%% Check input
if nargin < 4
    error('Need at least four inputs');
end

%% Implementation

% convert to double, easier implementation
if (strcmp(class(wrapped_imgs), 'uint8'))
    wrapped_imgs = im2double(wrapped_imgs);
end
if (strcmp(class(wrapped_imgd), 'uint8'))
    wrapped_imgd = im2double(wrapped_imgd);
end

switch (lower(mode))
    case {'overlay'} 
        out_img = blendMode_Overlay(wrapped_imgs, wrapped_imgd, masks, maskd);
    case {'blend'} 
        out_img = blendMode_Blend(wrapped_imgs, wrapped_imgd, masks, maskd);
end

end