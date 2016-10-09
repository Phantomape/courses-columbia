I = imread('hough_3.png');
BW = edge(I, 'canny');
H = generateHoughAccumulator(BW, 180, 1537);
L = lineFinder(I, H, 0);
S = lineSegmentFinder(I, H, 0);