import cv2
import numpy as np
import time
import picamera
from util import *

refPt = []


def get_roi(img):
    def click_and_crop(event, x, y, flags, param):
        global refPt

        if event == cv2.EVENT_LBUTTONDOWN:
            refPt = [(x, y)]

        # check to see if the left mouse button was released
        elif event == cv2.EVENT_LBUTTONUP:
            # record the ending (x, y) coordinates and indicate that
            # the cropping operation is finished
            refPt.append((x, y))

            # draw a rectangle around the region of interest
            clone = img
            #cv2.rectangle(clone, refPt[0], refPt[1], (255, 0, 0), 5)
            #cv2.imshow("image", clone)

    cv2.namedWindow('image')
    cv2.setMouseCallback('image', click_and_crop)
    cv2.imshow('image', img)
    cv2.waitKey(0)
    cv2.destroyAllWindows()


def stream():
    cap = cv2.VideoCapture(0)

    while True:
        # Capture frame-by-frame
        ret, frame = cap.read()

        # Our operations on the frame come here
        gray = cv2.cvtColor(frame, cv2.COLOR_BGR2GRAY)

        # Display the resulting frame
        cv2.imshow('frame', gray)
        if cv2.waitKey(1) & 0xFF == ord('q'):
            break

    # When everything done, release the capture
    cap.release()
    cv2.destroyAllWindows()


def init(camera):
    #   Part one
    camera.capture('target.png')
    time.sleep(2)
    im = cv2.imread('target.png')
    hsv = cv2.cvtColor(im, cv2.COLOR_BGR2HSV)
    get_roi(hsv)

    r = tuple([refPt[0][0], refPt[0][1], refPt[1][0] - refPt[0][0] + 1, refPt[1][1] - refPt[0][1] + 1])
    imCrop = hsv[int(r[1]):int(r[1] + r[3]), int(r[0]):int(r[0] + r[2])]
    cv2.imshow('ROI', imCrop)
    cv2.waitKey(0)
    imWidth = imCrop.shape[1]
    imHeight = imCrop.shape[0]
    tgthsv = get_target_hsv(imCrop, imWidth, imHeight, r)

    #   Part two
    mask = transform(tgthsv, hsv)
    area, centroid = get_area_centroid(mask)
    tgtx, _ = centroid
    return [tgthsv, area, tgtx]


def get_area_centroid(mask):
    output = cv2.connectedComponentsWithStats(mask, 4, cv2.CV_32S)
    #   Output:
    #       num of labels, label matrix, stat matrix, centroid matrix
    stats = output[2]
    centroid = output[3]
    t = np.argmax(stats[1:, cv2.CC_STAT_AREA]) + 1
    return stats[t, cv2.CC_STAT_AREA], centroid[t]


def transform(tgthsv, hsv):
    lower_bound = np.array([tgthsv[0] - 25, 100, 100])
    upper_bound = np.array([tgthsv[0] + 25, 255, 255])
    hsv = cv2.GaussianBlur(hsv, (5, 5), 0)
    mask = cv2.inRange(hsv, lower_bound, upper_bound)
    kernel = np.ones((5, 5), np.uint8)
    mask = cv2.morphologyEx(mask, cv2.MORPH_CLOSE, kernel, iterations=4)
    mask = cv2.morphologyEx(mask, cv2.MORPH_OPEN, kernel, iterations=4)
    return mask


def get_target_hsv(img, width, height, roi):
    avg_color_per_row = np.average(img, axis=0)
    avg_color = np.average(avg_color_per_row, axis=0)
    return avg_color


def next_area(params):
    return 0


def track(params):
    while True:
        area, centroid = next_area(params)
        next_x, _ = centroid
        res = next_x - params[2]
        if abs(res) > 50:
            if res > 50:
                right_deg(12)
            elif res < -50:
                left_deg(12)
        else:
            if params[1]/area > 1.1:
                fwd_cm(10)
            elif params[1]/area < 0.9:
                bwd_cm(10)
        time.sleep(.5)

if __name__ == "__main__":
    camera = picamera.PiCamera()
    params = init(camera)
    track(params)