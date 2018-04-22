function result_img = ...
    showCorrespondence(orig_img, warped_img, src_pts_nx2, dest_pts_nx2)
    I1 = orig_img;
    I2 = warped_img;
    matchedPoints1 = src_pts_nx2;
    matchedPoints2 = dest_pts_nx2;
    fh = figure; ax = axes;
    showMatchedPoints(I1,I2,matchedPoints1,matchedPoints2,'montage','Parent',ax);
    title(ax, 'Putative point matches');
    legend(ax,'Matched points 1','Matched points 2');
    frame = getframe(fh);
    result_img = frame.cdata;