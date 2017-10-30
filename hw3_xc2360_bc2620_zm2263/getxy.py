import cv2
import numpy as np

list_of_clicks = []


def getXY(img):

   def getxy_callback(event, x, y, flags, param):
    global list_of_clicks
    if event == cv2.EVENT_LBUTTONDOWN :
       list_of_clicks.append([x,y])
       print "click point is...", (x,y)
       cv2.destroyAllWindows()

   print "Reading the image..."
   cv2.namedWindow('image')
   cv2.setMouseCallback('image', getxy_callback)
   print "Please select the color by clicking on the screen..."
   cv2.imshow('image', img)
   cv2.waitKey(0)
   cv2.destroyAllWindows()
   print "The clicked points..."
   print list_of_clicks

   return list_of_clicks

