function light_dirs_5x3 = computeLightDirections(center, radius, img_cell)
    %   The equation of this circle is (x - center(1))^2 + (y - center(2))^2 +
    %   z^2 = radius^2
    for i = 1 : 5
        I = img_cell{i};
        [~,ind] = max(I(:));
        [x,y] = ind2sub(size(I), ind);
        
        %   Calculate normal vector
%         d_x = -(x - center(1))/sqrt(radius^2 - (x - center(1))^2 - (y - center(2))^2);
%         d_y = -(y - center(2))/sqrt(radius^2 - (x - center(1))^2 - (y - center(2))^2);
        d_x = -(y - center(2))/sqrt(radius^2 - (x - center(1))^2 - (y - center(2))^2);
        d_y = -(x - center(1))/sqrt(radius^2 - (x - center(1))^2 - (y - center(2))^2);
        light_dirs_5x3(i,:) = [-d_x, -d_y, 1];
    end