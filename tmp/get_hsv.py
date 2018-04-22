import picamera
import cv2
camera = picamera.PiCamera()
camera.capture('orange.png')
t = cv2.imread('orange.png')
cv2.imshow('orange.png', t)
cv2.waitKey(0)
