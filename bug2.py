from gopigo import *
import time

# Bug 2 Algorithm based on lecture notes

CELL_SIZE = 10
GRID_SIZE = 10
CUR_X = 0
CUR_Y = 0
GOAL_X = 0
GOAL_Y = 5
ANGLE = 0
MAP = []

''' Utility '''

DPR = 360.0/64
INF = 200
LIMIT = 100
WHEEL_RAD = 3.25
ERROR = 1.3

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
        enc_tgt(0, 1, pulse)
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

def initialize_enviroment()
	print("Initializing enviroment")
	servo(90)
	for i in range(GRID_SIZE):
		MAP.append([0 for _ in range(GRID_SIZE)])

def follow_m_line():
	print("Following m line")
	face_goal()
	dist_to_obj = us_dist(15)
	while (dist_to_obj > CELL_SIZE * 1.5):
		fwd_cm(CELL_SIZE)
		time.sleep(1)
		stop()
		CUR_Y += 1
		if (CUR_Y == GOAL_Y and CUR_X == GOAL_X):
			return 1
		dist_to_obj = us_dist(15)
	return 0

def face_goal():
	print("Facing goal")
	while(ANGLE != 0):
		left_deg(90)
		time.sleep(1)
		stop()
		ANGLE = (ANGLE - 90) % 360

def trace_object()
	print("Tracing Object")
	H_X, H_Y = CUR_X, CUR_Y
	left_deg(90)
	time.sleep(1)
	stop()
	ANGLE = (ANGLE - 90) % 360
	while (CUR_X != GOAL_X or (CUR_X == GOAL_X and CUR_Y < H_Y)):
		right_flag = 0
		front_flag = 0
		servo(0)
		dist_to_obj = us_dist(15)
		if (dist_to_obj < 15):
			right_flag = 1
		time.sleep(.5)
		servo(90)
		dist_to_obj = us_dist(15)
		if (dist_to_obj < 15):
			front_flag = 1
		time.sleep(.5)

		if (right_flag and front_flag):
			update_position(-1)
			left_deg(90)
			time.sleep(1)
			stop()
			fwd_cm(CELL_SIZE)
			time.sleep(1)
			stop()
		elif (right_flag):
			update_position(0)
			fwd_cm(CELL_SIZE)
			time.sleep(1)
			stop()
		else:
			update_position(1)
			right_deg(90)
			time.sleep(1)
			stop()
			fwd_cm(CELL_SIZE)
			time.sleep(1)
			stop()

		if (CUR_Y == GOAL_Y and CUR_X == GOAL_X):
			return 1
		if (CUR_Y == H_Y and CUR_X == H_X):
			return -1
	return 0

def update_position(move):
	# left and forward
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

def main():
	initialize_enviroment()
	while True:
		status = follow_m_line()
		if(status == 1)
			print("Goal Reached")
			stop()
			return
		status = trace_object()
		if(status == 1)
			print("Goal Reached")
			stop()
			return
		elif (status == -1)
			print("Robot Trapped")
			stop()
			return

'''
def get_next_cell():

	print(“Searching next cell”)

	theta = current_pos[2] 
	if(theta == 0):

		next_cell_x = current_pos[0]
		next_cell_y = current_pos[1] + 1
	if(theta == 45):

		next_cell_x = current_pos[0] - 1
		next_cell_y = current_pos[1] + 1
	if(theta == 90):

		next_cell_x = current_pos[0] - 1
		next_cell_y = current_pos[1]
	if(theta == 135):

		next_cell_x = current_pos[0] - 1
		next_cell_y = current_pos[1] - 1
	if(theta == 180):

		next_cell_x = current_pos[0]
		next_cell_y = current_pos[1] - 1
	if(theta == 225):

		next_cell_x = current_pos[0] + 1
		next_cell_y = current_pos[1] - 1
	if(theta == 270):

		next_cell_x = current_pos[0] + 1
		next_cell_y = current_pos[1]
	if(theta == 315):

		next_cell_x = current_pos[0] + 1
		next_cell_y = current_pos[1]
	
	if(next_cell_x < 0 or next_cell_x > grid_size[0] or next_cell_y < 0 or next_cell_y > grid_size[1]):

		next_cell = -1

	else:

		next_cell = map(next_cell_x, next_cell_y)
		if(next_cell == null):

			dist_2_obj = us_dist(15)

			if(dist_2_obj <= 1.5 * cell_size and dist_2_obj >= .5 * cell_size):

				next_cell = -1
			else:

				next_cell = 0

	return next_cell

def go_to_next_cell(next_cell):

	print(“Moving forward 1 cell”)
	
	if(next_cell != 0)

		print(“Error: next cell is occupied by an obstacle or unknown”)
		return -2

	fwd()
	time.sleep(travel_time_per_cell)
	stop()
	
	theta = current_pos[2]
	if(theta == 0):

		current_pos[0] +=  0
		current_pos[1] +=  1
	if(theta == 90):

		current_pos[0] +=  -1
		current_pos[1] +=  0
	if(theta == 180):

		current_pos[0] +=  -1
		current_pos[1] +=  -1	
	if(theta == 270):

		current_pos[0] +=  1
		current_pos[1] +=  0
'''