function [center, radius] = findSphere(img)
    bw = im2bw(img);
    imshow(bw)
    stats = regionprops('table',bw,'Centroid','MajorAxisLength','MinorAxisLength');
    center = stats.Centroid;
    diameters = mean([stats.MajorAxisLength stats.MinorAxisLength],2);
    radius = diameters/2;
    
    hold on
    viscircles(center,radius);
    hold off
