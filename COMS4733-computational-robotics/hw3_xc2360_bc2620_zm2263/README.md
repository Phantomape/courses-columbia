Team Members: Xucheng Chen(xc2360), Ziyi Mu(zm2263), Ben Carlin(bc2620)

Youtube link for color tracking demo: https://www.youtube.com/watch?v=ygUJAdhbsGI

#   Description
The color tracker will acquire the HSV range of the object selected by user and use this range information to acquire the mask of next frame as well as the centroid and area of the object; then the robot is adjusted by comparing the shift in centroid and scale in area between two frames.

#   Assumption
*   color of the object doesn't change 
*   environment light is stable
*   no drastic movement for the object
