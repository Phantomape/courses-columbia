function [mask, result_img] = backwardWarpImg(src_img, resultToSrc_H,...
    dest_canvas_width_height)
    %   Initialize
    result_img = zeros(dest_canvas_width_height(2), dest_canvas_width_height(1), 3);
    load('bg_portrait.mat');
    
    cornerPoints = round(bg_pts);

    
    min_X = min(cornerPoints(:,1));
    max_X = max(cornerPoints(:,1));
    min_Y = min(cornerPoints(:,2));
    max_Y = max(cornerPoints(:,2));
    
    [dimx,dimy,dimz] = size(src_img);

    for i = 1 : dest_canvas_width_height(1)  
        for j = 1 : dest_canvas_width_height(2)
        
            for k = 1:3
                wrapPoint = [i;j;1];
                backwrapPoint = resultToSrc_H*wrapPoint;% applying inverse transformation            
                x = backwrapPoint(1,1)/backwrapPoint(3,1);
                y = backwrapPoint(2,1)/backwrapPoint(3,1); 
                if (~(isinteger(x) && isinteger(y))) % if both coordinates are not integers
                    %bilinearly interpolate
                    x_low = floor(x);
                    y_low = floor(y);

                    x_high = x_low + 1;      
                    y_high = y_low + 1;
                    
                    if(x_low > 0 && x_high < dimy && y_low > 0 && y_high < dimx)
                        F0 = src_img(y_low, x_low, k);
                        F1 = src_img(y_high, x_low, k);
                        F2 = src_img(y_low, x_high, k);
                        F3 = src_img(y_high, x_high, k);

                        deltaX = y - y_low;
                        deltaY = x - x_low;

                        Fy0 = (1 - deltaX)*F0 + deltaX*F1;
                        Fy1 = (1 - deltaX)*F2 + deltaX*F3;

                        result_img(j, i, k) = (1 - deltaY) * Fy0 + deltaY * Fy1;
                    end
                else
                    result_img(j, i, k) = src_img(y, x, k);
                end
            end
        
        end
    end

mask = double(logical(rgb2gray(result_img)));
imshow(result_img);
end

