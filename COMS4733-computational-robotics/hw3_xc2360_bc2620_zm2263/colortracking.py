import cv2
import numpy as np
import time
import picamera
from helper import *

refPt = []


def get_roi(img):
    def eventcallback(event, x, y, flags, param):
        global refPt

        if event == cv2.EVENT_LBUTTONDOWN:
            refPt = [(x, y)]
        elif event == cv2.EVENT_LBUTTONUP:
            refPt.append((x, y))

    cv2.namedWindow('image')
    cv2.setMouseCallback('image', eventcallback)
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
    tgthsv, maxh, minh = get_target_hsv(imCrop, imWidth, imHeight, r)
    mask = transform(tgthsv, hsv, maxh, minh)
    area, centroid = get_area_centroid(mask)
    tgtx, tgty = centroid
    return [tgthsv, area, tgtx, maxh, minh]


def get_area_centroid(mask):
    output = cv2.connectedComponentsWithStats(mask, 4, cv2.CV_32S)
    # output: num of labels, label matrix, stat matrix, centroid matrix
    # centroid doesn't find
    if output[2].shape[0] == 1:
        return 0, [0, 0]
    # get the biggest component
    t = np.argmax(output[2][1:, cv2.CC_STAT_AREA]) + 1
    return output[2][t, cv2.CC_STAT_AREA], output[3][t]


def transform(tgthsv, hsv, maxh, minh):
    lower_bound = np.array([minh[0], 80, 80])
    upper_bound = np.array([maxh[0], 255, 255])
    mask = cv2.inRange(hsv, lower_bound, upper_bound)
    cv2.imshow('Mask', mask)
    cv2.waitKey(200)
    kernel = np.ones((3, 3), np.uint8)
    mask = cv2.morphologyEx(mask, cv2.MORPH_CLOSE, kernel, iterations=4)
    mask = cv2.morphologyEx(mask, cv2.MORPH_OPEN, kernel, iterations=4)
    return mask


def get_target_hsv(img, width, height, roi):
    avg_color_per_row = np.average(img, axis=0)
    avg_color = np.average(avg_color_per_row, axis=0)
    max_color_per_row = np.amax(img, axis=0)
    max_color = np.amax(max_color_per_row, axis=0)
    min_color_per_row = np.amin(img, axis=0)
    min_color = np.amin(min_color_per_row, axis=0)
    return avg_color, max_color, min_color


def next_target(params):
    camera.capture('candid.png')
    im = cv2.imread('candid.png')
    hsv = cv2.cvtColor(im, cv2.COLOR_BGR2HSV)
    mask = transform(params[0], hsv, params[3], params[4])
    cv2.imshow('candid', mask)
    cv2.waitKey(200)
    area, centroid = get_area_centroid(mask)
    return area, centroid


# The main function to track the object
def track(params):
    area, x, maxh, minh = params[1], params[2], params[3], params[4]
    print 'initial params: ' + str(area) + ',' + str(x)
    loop = 0
    while True:
        loop += 1
        print 'time: ' + str(loop)
        next_area, centroid = next_target(params)
        next_x, next_y = centroid
        res = next_x - x
        print 'horizontal_dist: ' + str(res)
        if abs(res) > 50:
            if res > 50:
                right_deg(20)
            elif res < -50:
                left_deg(20)
        
        if next_area == 0:
            continue
        print 'vertical_dist:' + str(float(area)/float(next_area))
        if float(area)/float(next_area) > 1.2:
            print 'should forward'
            fwd_cm(4)
        elif float(area)/float(next_area) < 0.8:
            print 'should backward'
            bwd_cm(4)
        time.sleep(.5)
        print 'curr area: ' + str(area)
        print 'next area: ' + str(next_area)
       

if __name__ == "__main__":
    camera = picamera.PiCamera()
    params = init(camera)
    track(params)
