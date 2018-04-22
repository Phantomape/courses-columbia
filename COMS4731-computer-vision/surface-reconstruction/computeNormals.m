function [normals, albedo_img] = ...
    computeNormals(light_dirs, img_cell, mask)
    
    normals = zeros(size(img_cell{1},1),size(img_cell{1},1),3);
    for i = 1:5
        img_cell{i} = img_cell{i}.*uint8(mask)
    end
    
    for i = 1:size(img_cell{1},1)
        for j = 1:size(img_cell{1},2)
            I = [img_cell{1}(i,j);img_cell{2}(i,j);img_cell{3}(i,j);img_cell{4}(i,j);img_cell{5}(i,j)];
            S = light_dirs;
            N = inv(S'*S)*S'*double(I);
            albedo_img(i,j) = norm(N);
            normals(i,j,:) = N/norm(N);

        end
    end
    normals = normals.*cat(3, mask, mask, mask);
    normals(isnan(normals)) = 0;
