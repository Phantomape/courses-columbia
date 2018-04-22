function dest_pts_nx2 = applyHomography(H_3x3, src_pts_nx2)
    pts = [src_pts_nx2, ones(4,1)]';
    dest_pts = zeros(3,4);
    for i = 1:4
        dest_pts(:,i) = H_3x3 * pts(:,i);
        dest_pts(:,i) = dest_pts(:,i)/dest_pts(3,i);
    end
    dest_pts_nx2 = dest_pts(1:2,:)';