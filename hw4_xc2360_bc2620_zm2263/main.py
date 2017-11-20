import cv2
import unittest
import time
import numpy as np

pts_source = []
pts_roadway = [[0, 0], [320, 0], [320, 240], [0, 240]]
pts_region = []


class DemoTest(unittest.TestCase):
    def test_get_ref_pts(self):
        img = cv2.imread("banana.jpg")
        get_roi(img)
        self.assertEqual(len(pts_source), 4)


def get_roi(img):
    def eventcallback(event, x, y, flags, param):
        global pts_region

        if event == cv2.EVENT_LBUTTONDOWN:
            pts_region = [(x, y)]
        elif event == cv2.EVENT_LBUTTONUP:
            pts_region.append((x, y))

    cv2.namedWindow('image')
    cv2.setMouseCallback('image', eventcallback)
    cv2.imshow('image', img)
    cv2.waitKey(0)
    cv2.destroyAllWindows()


def get_pts(img):
    def callback(event, x, y, flags, param):
        global pts_source

        if event == cv2.EVENT_LBUTTONDOWN:
            pts_source.append([x, y])

    cv2.namedWindow('image')
    cv2.setMouseCallback('image', callback)
    cv2.imshow('image', img)
    cv2.waitKey(0)
    cv2.destroyAllWindows()


def run(camera, transform, tgthsv):
    while True:
        #   camera.capture('next.png')
        time.sleep(2)
        #    im_source = cv2.imread('banana.jpg')
        im_source = cv2.imread('lab4_images/image00l.jpg')
        im_roadway = cv2.warpPerspective(im_source, transform, (320, 240))
        im_roadway = cv2.GaussianBlur(im_roadway, (5, 5), 0)
        cv2.imshow("after warping", im_roadway)
        cv2.waitKey(200)

        hsv = cv2.cvtColor(im_roadway, cv2.COLOR_BGR2HSV)
        mask = color_filter(hsv, tgthsv)
        cv2.imshow("mask", mask)
        cv2.waitKey(200)

        mask = cv2.GaussianBlur(mask, (3, 3), 0)
        edges = cv2.Canny(mask, 50, 150)
        cv2.imshow("edge", edges)
        cv2.waitKey(200)

        lines = cv2.HoughLines(edges, 1, np.pi / 180, 50)
        #   plot_on_img(im_roadway, lines)

        #   Face forward
        if len(lines[0]) == 0:
            continue

        line = lines[0][0]
        theta = line[1]
        rho = line[0]
        print rho
        print theta
        if theta > np.pi / 2:
            offset_angle = (np.pi / 2 - (theta - np.pi / 2)) / np.pi * 180
            print "rotate right:" + str(offset_angle)
        else:
            offset_angle = theta / np.pi * 180
            print "rotate left:" + str(offset_angle)

        #   Centralize
        centralize(rho, theta)

        #   Move
        print "move fwd 1 cm"

        flag = detect_orange()
        if flag is True:
            u_turn()


def detect_orange():
    print "No orange detected"
    return False


def u_turn():
    print "U turn"


def centralize(dist, angle):
    if abs(160 - dist) <= 40:
        print "Skip centralize"
        return

    if angle > np.pi / 2:
        print "rotate right 90 degree"
        print "fwd" + str(abs(160 - dist))
        print "rotate left 90 degree"
    else:
        print "rotate left 90 degree"
        print "fwd" + str(abs(160 - dist))
        print "rotate right 90 degree"
    print "centralized"


def plot_on_img(img, lines):
    result = img.copy()
    for line in lines[0]:
        rho = line[0]
        theta = line[1]
        if (theta < (np.pi / 4.)) or (theta > (3. * np.pi / 4.0)):
            pt1 = (int(rho / np.cos(theta)), 0)
            pt2 = (int((rho - result.shape[0] * np.sin(theta)) / np.cos(theta)), result.shape[0])
            cv2.line(result, pt1, pt2, (255))
        else:
            pt1 = (0, int(rho / np.sin(theta)))
            pt2 = (result.shape[1], int((rho - result.shape[1] * np.cos(theta)) / np.sin(theta)))
            cv2.line(result, pt1, pt2, (255), 1)
    cv2.imshow("detected lines", result)
    cv2.waitKey(200)


def get_orange_hsv():
    print "get orange hsv(hardcoded)"


def init(camera):
    #   camera.capture('curr.png')
    time.sleep(2)
    #   im = cv2.imread('curr.png')
    im_source = cv2.imread('lab4_images/image00c.jpg')
    get_pts(im_source)
    transform, status = cv2.findHomography(np.array(pts_source), np.array(pts_roadway))
    im_roadway = cv2.warpPerspective(im_source, transform, (320, 240))
    cv2.imshow("after warping", im_roadway)
    cv2.waitKey(0)

    hsv = cv2.cvtColor(im_roadway, cv2.COLOR_BGR2HSV)
    get_roi(hsv)
    r = tuple([pts_region[0][0], pts_region[0][1], pts_region[1][0] - pts_region[0][0] + 1, pts_region[1][1] - pts_region[0][1] + 1])
    im_crop = hsv[int(r[1]):int(r[1] + r[3]), int(r[0]):int(r[0] + r[2])]
    avg_color_per_row = np.average(im_crop, axis=0)
    avg_color = np.average(avg_color_per_row, axis=0)
    max_color_per_row = np.amax(im_crop, axis=0)
    max_color = np.amax(max_color_per_row, axis=0)
    min_color_per_row = np.amin(im_crop, axis=0)
    min_color = np.amin(min_color_per_row, axis=0)
    target_hsv = [avg_color, max_color, min_color]

    get_orange_hsv()

    #   ref: [  array([  30.        ,  253.79674797,  212.27100271]),
    #           array([ 30, 255, 216], dtype=uint8),
    #           array([ 30, 248, 209], dtype=uint8) ]
    print target_hsv
    return transform, target_hsv


def color_filter(hsv, tgthsv):
    lower_bound = np.array([tgthsv[0][0] - 5, tgthsv[0][1] - 50, tgthsv[0][2] - 50])
    upper_bound = np.array([tgthsv[0][0] + 5, tgthsv[0][1] + 50, tgthsv[0][2] + 50])
    mask = cv2.inRange(hsv, lower_bound, upper_bound)
    kernel = np.ones((3, 3), np.uint8)
    mask = cv2.morphologyEx(mask, cv2.MORPH_CLOSE, kernel, iterations=4)
    mask = cv2.morphologyEx(mask, cv2.MORPH_OPEN, kernel, iterations=4)
    return mask


if __name__ == "__main__":
    #   unittest.main()
    camera = 0
    if camera == 0:
        print "DEBUG MODE"
    homo_transform, target_hsv = init(camera)
    run(camera, homo_transform, target_hsv)
