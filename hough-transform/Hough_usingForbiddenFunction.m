close all
I = imread('hough_2.png');
BW = edge(I, 'canny');
H = generateHoughAccumulator(BW, 180, 1597);
L = lineFinder(I, H, 0);
S = lineSegmentFinder(I, H, 0);