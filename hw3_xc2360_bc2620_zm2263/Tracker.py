import cv2
import numpy as np
import time
from util import *

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
    im = cv2.imread('banana.jpg')
    hsv = cv2.cvtColor(im, cv2.COLOR_BGR2HSV)
    r = cv2.selectROI(hsv, False)
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
    lower_bound = np.array([tgthsv[0] - 5, 100, 100])
    upper_bound = np.array([tgthsv[0] + 5, 255, 255])
    hsv = cv2.GaussianBlur(hsv, (5, 5), 0)
    mask = cv2.inRange(hsv, lower_bound, upper_bound)
    kernel = np.ones((5, 5), np.uint8)
    mask = cv2.morphologyEx(mask, cv2.MORPH_CLOSE, kernel, iterations=3)
    mask = cv2.morphologyEx(mask, cv2.MORPH_OPEN, kernel, iterations=3)
    cv2.imshow('i', mask)
    cv2.waitKey(0)
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
    #camera = picamera.PiCamera()
    #init(camera)
    #track()
    params = init(2)
    track(params)