from __future__ import division
import cv2
from math import *
import picamera
from getxy import *
from helper import *

#color selection radius
AVG_RAD = 5


'''
calculate the centroid of the largest area of targeted hsv

Args:
    bw: binary image

Returns:
    area and centroid of biggest connected component except background
'''
def area_centroid(bw):
    output = cv2.connectedComponentsWithStats(bw,4,cv2.CV_32S)
    stats = output[2]
    centroid = output[3]
    if stats.shape[0]==1:
        return 0,[0,0]
    i = np.argmax(stats[1:,cv2.CC_STAT_AREA])+1
    return stats[i,cv2.CC_STAT_AREA],centroid[i]

'''
use threshold to create binary image
restrict hue range but set loose saturation and value ranges
Args:
    image and targethsv
Returns:
    image after applying threshold
'''
def threshold_color(im,targethsv):
    #hue +-5, saturation 100--255, brightness 100--255
    low_thresh = np.array([targethsv[0]-5,100,100])
    high_thresh = np.array([targethsv[0]+5,255,255])
    mask = cv2.inRange(im,low_thresh,high_thresh)
    #To avoid border case, sometimes hue=1 and hue=179 is the same color
    if targethsv[0]<5:
        low_thresh[0] = 180+low_thresh[0]
        high_thresh[0] = 180
        mask2 = cv2.inRange(im,low_thresh,high_thresh)
        mask = cv2.bitwise_or(mask,mask2)
    elif targethsv[0]>175:
        low_thresh[0] = 0
        high_thresh[0] = high_thresh[0]-180
        mask2 = cv2.inRange(im,low_thresh,high_thresh)
        mask = cv2.bitwise_or(mask,mask2)
    return mask

'''
use dilation and dilution to remove noise
Args:
    im: image
Returns:
    image after dilation and erosion
'''
def remove_noise(im):
    kernel = np.ones((5,5),np.uint8)
    #MORPH_CLOSE is dilation followed by erosion
    #MORPH_OPEN is erosion followed by dilation
    im = cv2.morphologyEx(im,cv2.MORPH_CLOSE,kernel,iterations=3)
    return cv2.morphologyEx(im,cv2.MORPH_OPEN,kernel,iterations=3)

'''
calculate the current centroid and area of the tracked object
Args:
    targethsv:  the hsv value we chose from the target
Returns:
    new area and centroid of the target object
'''
def newArea(targethsv):
    camera.capture('image2.png')
    im = cv2.imread('image2.png')
    im = cv2.GaussianBlur(im,(5,5),0)
    hsv = cv2.cvtColor(im,cv2.COLOR_BGR2HSV)
    thresh = threshold_color(hsv,targethsv)
    thresh = remove_noise(thresh)
    area2,centroid2 = area_centroid(thresh)
    return area2,centroid2


'''
get default centroid, area and hsv values of the object
sample a central hsv value of the area around user click
then use threshold to create a binary image, use connected component to calculate the area
Args:
    camera:     GoPiGo camera class
Returns:
    initial area and centroid.x of the object that user clicked
    targethsv:  hsv value of the clicked object
'''
def get_default(camera):
    camera.capture('image.png')
    time.sleep(2)
    camera.capture('image.png')
    im = cv2.imread('image.png')
    im = cv2.GaussianBlur(im,(5,5),0)   #Apply Gaussian Blur
    l = getXY(im)
    if len(l)==0:
        print "error"
        return 0,0,0
    else:
        x = l[0][0]
        y = l[0][1]
        hsv = cv2.cvtColor(im,cv2.COLOR_BGR2HSV)
        height,width,_ = hsv.shape
        #calcaulate targethsv by calculating average hsv of the click area
        targethsv = np.mean(hsv[max(y-AVG_RAD,0):min(y+AVG_RAD+1,height),max(x-AVG_RAD,0):min(x+AVG_RAD+1,width),:],axis=(0,1)) 
        thresh = threshold_color(hsv,targethsv)
        thresh = remove_noise(thresh)
        cv2.namedWindow("thresh")
        cv2.imshow("thresh",thresh)
        cv2.waitKey(2000)
        area,centroid = area_centroid(thresh)
        targetx,_ = centroid
    return area,targetx,targethsv

'''
maintain centroid position and area of the object;
if target area does not change after 3 consecutive turns, stop turning since we are not likely 
to change the angle by much
Args:
    area:       initial area
    target:     initial target x
    targethsv:  targethsv hsv color
'''
def follow_object(area,targetx,targethsv):
    consecutive_turn = 0
    while True:
        area2,centroid2 = newArea(targethsv)
        if area2>0:#detected something
            x,_ = centroid2
            if abs(x-targetx)>50 and consecutive_turn < 3:#going to turn
                consecutive_turn += 1
                if x-targetx>50:    #turn right
                    right_deg(12)
                elif targetx-x>50:  #turn left
                    left_deg(12)
            else:
                if area/area2>1.1:  #move fwd
                    consecutive_turn = 0
                    fwd_cm(10)
                elif area/area2<0.9:    #move bwd
                    consecutive_turn = 0
                    bwd_cm(10)
        time.sleep(0.5)


'''
Use PiCamera to read in target centroid position, area and color 
Then try to maintain the position and area by adjusting the robot location gradually
'''
if __name__ == "__main__":
    camera = picamera.PiCamera()
    set_speed(100)
    area,targetx,targethsv = get_default(camera)
    follow_object(area,targetx,targethsv)
