from gopigo import *
import time
import matplotlib.pyplot as plt

# Bug 2 Algorithm based on lecture notes

CELL_SIZE = 20
GRID_SIZE = 10
DIST = 30
CUR_X = 0
CUR_Y = 0
GOAL_X = 0
GOAL_Y = 10
ANGLE = 0

''' Utility '''

DPR = 360.0/64
INF = 200
LIMIT = 100
WHEEL_RAD = 3.25
ERROR = 1.3


MAP = [[0 for x in range(2 * GRID_SIZE + 1)] for y in range(GRID_SIZE)]

offset_x_r = [1, 0, -1, 0]
offset_y_r = [0, 1, 0, -1]
offset_x_l = [1, 0, -1, 0]
offset_y_l = [0, 1, 0, -1]

def left_deg(deg=None):
    if deg is not None:
        pulse = int(deg/DPR)
        if pulse == 0:
            pulse = 1
        enc_tgt(0, 1, pulse)
    left()

def right_deg(deg=None):
    if deg is not None:
        pulse = int(deg/DPR)
        if pulse == 0:
            pulse = 1
        enc_tgt(1, 0, pulse)
    right()

def fwd_cm(dist=None):
    if dist is not None:
		pulse = int(cm2pulse(dist))
		enc_tgt(1,1,pulse)
    fwd()

def cm2pulse(dist):
    wheel_circ = 2*math.pi*WHEEL_RAD
    revs = dist/wheel_circ
    PPR = 18
    pulses = PPR*revs
    return pulses

''' Bug 2 '''

def initialize_enviroment():
	print("Initializing enviroment")
	set_speed(150)
	for i in range(GRID_SIZE):
		MAP.append([0 for _ in range(GRID_SIZE)])

def follow_m_line():
	global CUR_Y
	print("Following m line")
	servo(90)
	face_goal()
	dist_to_obj = us_dist(15)
	while (dist_to_obj > CELL_SIZE * 1.5):
		fwd_cm(CELL_SIZE)
		time.sleep(2)
		stop()
		CUR_Y += 1
		if (CUR_Y == GOAL_Y and CUR_X == GOAL_X):
			return 1
		dist_to_obj = us_dist(15)
	return 0

def face_goal():
	global ANGLE
	print("Facing goal")
	while(ANGLE != 0):
		left_deg(90)
		time.sleep(1.5)
		stop()
		ANGLE = (ANGLE - 90) % 360

def trace_object():
	global ANGLE
	print("Tracing Object")
	H_X, H_Y = CUR_X, CUR_Y
	update_position(-1)
	left_deg(90 * ERROR)
	time.sleep(1.5)
	stop()
	fwd_cm(CELL_SIZE)
	time.sleep(3)
	stop()

	while True:
		print("Coor X: " + str(CUR_X) + " Coor Y: " + str(CUR_Y) + " Angle: " + str(ANGLE))

		if (CUR_X == GOAL_X and abs(GOAL_Y - CUR_Y) < abs(GOAL_Y - H_Y) and could_follow_m_line()):
			break;
		if (CUR_Y == GOAL_Y and CUR_X == GOAL_X):
			return 1
		if (CUR_Y == H_Y and CUR_X == H_X):
			return -1

		right_flag = 0
		front_flag = 0
		servo(0)
		time.sleep(1)
		dist_to_obj = us_dist(15)
		if (dist_to_obj < CELL_SIZE * 1.5):
			right_flag = 1
		servo(90)
		time.sleep(1)
		dist_to_obj = us_dist(15)
		if (dist_to_obj < CELL_SIZE * 1.5):
			front_flag = 1

		if (front_flag):
			update_position(-1)
			left_deg(90 * ERROR)
			time.sleep(1.5)
			stop()
			fwd_cm(CELL_SIZE)
			time.sleep(2)
			stop()
		elif (right_flag):
			update_position(0)
			fwd_cm(CELL_SIZE)
			time.sleep(2)
			stop()
		else:
			update_position(1)
			right_deg(90 * ERROR)
			time.sleep(1.5)
			stop()
			fwd_cm(CELL_SIZE)
			time.sleep(2)
			stop()
	return 0

def update_position(move):
	# left and forward
	global CUR_Y
	global CUR_X
	global ANGLE
	if move == -1:
		if ANGLE == 0:
			CUR_X -= 1
		elif ANGLE == 90:
			CUR_Y += 1
		elif ANGLE == 180:
			CUR_X += 1
		else:
			CUR_Y -= 1
		ANGLE = (ANGLE - 90) % 360
	# forward
	elif move == 0:
		if ANGLE == 0:
			CUR_Y += 1
		elif ANGLE == 90:
			CUR_X += 1
		elif ANGLE == 180:
			CUR_Y -= 1
		else:
			CUR_X -= 1

	# right and forward
	else:
		if ANGLE == 0:
			CUR_X += 1
		elif ANGLE == 90:
			CUR_Y -= 1
		elif ANGLE == 180:
			CUR_X -= 1
		else:
			CUR_Y += 1
		ANGLE = (ANGLE + 90) % 360
	
	if ANGLE == 0:
		oi = 0;
	elif ANGLE == 90:
		oi = 3
	elif ANGLE == 180:
		oi = 2
	else:
		oi = 1
	plot_map(CUR_X, CUR_Y, oi, 1)

def could_follow_m_line():
	if ((ANGLE == 90 and CUR_Y > GOAL_Y) or (ANGLE == 270 and CUR_Y < GOAL_Y)):
		servo(0)
		time.sleep(1)
		dist_to_obj = us_dist(15)
		if (dist_to_obj < CELL_SIZE * 1.5):
			return False
	elif ((ANGLE == 270 and CUR_Y > GOAL_Y) or (ANGLE == 90 and CUR_Y < GOAL_Y)):
		servo(180)
		time.sleep(1)
		dist_to_obj = us_dist(15)
		if (dist_to_obj < CELL_SIZE * 1.5):
			return False
	return True

def plot_map(xi, yi, oi, wi):
	MAP[GRID_SIZE - yi][xi + GRID_SIZE] = 1
    if w[i] == 1:
        MAP[GRID_SIZE - yi - offset_y_r[oi]][xi + offset_x_r[oi] + GRID_SIZE] = 0.2
    elif w[i] == -1:
        MAP[GRID_SIZE - yi - offset_y_l[oi]][xi + offset_x_l[oi] + GRID_SIZE] = 0.2
    plt.matshow(MAP)
    plt.plot(xi + GRID_SIZE, GRID_SIZE - yi, marker=(3, 0, oi*90), markersize=20, linestyle='None')
    plt.show(False)
    plt.pause(1)
    plt.close()

def main():
	initialize_enviroment()
	while True:
		status = follow_m_line()
		if(status == 1):
			print("Goal Reached")
			stop()
			return
		status = trace_object()
		if(status == 1):
			print("Goal Reached")
			stop()
			return
		elif (status == -1):
			print("Robot Trapped")
			stop()
			return

main()