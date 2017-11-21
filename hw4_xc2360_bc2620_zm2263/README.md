#   Lab 4

### Group 22 [xc2360, bc2620, zm2263]

### Youtube link: 

You can find our main in lane_follower.py and helper functions in helper.py.
We made the following assumptions about our robot/environment: 
*   light remains steady
*   robot is placed parallel with the center line at the beginning

##  Values calculated from ta_supplied_image_set:
calculated_angle_offset = <angle_offset>
calculated_distance_offset = <distance_offset in cm>
homography_transform_matrix = <matrix>
distance_to_camera_from_homography = <distance in cm>

##  Values calculated from student_captured_image_set
calculated_angle_offset = 5.0394
calculated_distance_offset = 1.343663
homography_transform_matrix = 
[[  1.42089702e+00   5.35655622e-01  -3.07229511e+02]
 [  6.36234349e-02   2.25226959e+00  -2.63324672e+02]
 [  3.55270327e-04   3.60338583e-03   1.00000000e+00]]
distance_to_camera_from_homography = <distance in cm>

##  Questions:

####    Describe the overall architecture of your implementation.
*   Calculate homography based (car must be parallel with the center line, tilted situation can handled once the homography is correct)
*   Centralize the car on the center line
Repeat following steps:
*   Take new images
*   Warp the image
*   Filter the image
*   Get straight lines by edge detection and hough transformation
*   Calculated offset angle and distance
*   Move based the offset angle and distance
*   Check whether we hit the stop sign

####    What method did your group use to calculate the distance from the center line?
    We assume the center line is on 160 because we are warping the image to 320x240, the offset distance given by difference between the pixel value of the line and 160, and then we multiply a ratio(actual width between two thumbtacks/320 ) to get real distance

####    What method did your group use to calculate the angle offset from the center line?
    The angle is given by the angle of houghline, which is exactly the offset angle based on the OPENCV implementation of hough transform

####    Describe your control flow algorithm based on distance, angle, and whatever other metrics you used.
while
    if no lines detected:
        for i in [15, 20, 25, 30]:
            left turn i degrees
            if detected lines
                continue
            reset
            right turn i degrees
            if detected lines
                continue
            reset

    if offset_distance is large than 3cm:
        turn offset_angle
    else 
        continue

    if stop_sign encountered:
        u_turn

####    What is the purpose of the distance offset from the camera to the homography transform?