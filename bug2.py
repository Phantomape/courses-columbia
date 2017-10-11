robot_size
cell_size
grid_size
map
current_pos
goal
speed
rot_time_per_90_degrees
travel_time_per_cell

def main():

	initialize_enviroment()
	next_cell = get_next_cell()
	while(next_cell != 1):

		next_cell = follow_m_line()
		if(next_cell == -2)

			return
		next_cell = trace_object()
		if(next_cell == -2)

			return

def initialize_enviroment()

	print(“Initializing enviroment”)

	robot_size =  cm
	cell_size = cm
	if(cell_size < robot_size):

		print(“Error: cell size too small”)
		return -2
	grid_size = width, height
	if(width < 2 or height < 2 or width <= (cell_size / 2) or height <= (cell_size / 2)):

		print(“Error: invalid map dimensions”)
		return -1
	map = math.ceil(grid_size[0] / cell_length), math.ceil(grid_size[1] / cell_length), null
	
	current_pos = 0, initial_y_pos, 0
	goal = 0, current_pos[1] + dist_2_goal
	if(goal[1] < 0):

		print(“Error: invalid location of goal ”)
		return -1
	map(0, 0) = 0
	map(x, y) = 1

	speed = m/sec
	set_speed(speed)
	rot_time_per_90_degrees = sec
	travel_time_per_cell = sec

def follow_m_line():

	print(“Following m line”)

	if(current_pos[0] != 0):

		print(“Error: can’t follow m line, not on m line”)
		return -2
	face_goal()

	next_cell = get_next_cell()
	while(next_cell == 0):

		go_to_next_cell(next_cell)
		next_cell = get_next_cell()

	return next_cell

def face_goal():

	print(“Facing goal”)

	theta =  current_pos[2]
	while(theta != 0):

		left_rot()
		time.sleep(rot_time_per_90_degrees)
		stop()
		current_pos[2] = (current_pos[2] + 90) % 360
		theta = current_pos[2] 

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
	
	
def trace_object()

	print(“Tracing Object”)

	starting_pos = current_pos
	starting_trace = 1
	if(starting_pos[0] != 0):

		print(“Error: not starting trace from m line”)
		return -2

	next_cell = get_next_cell()
	if(next_cell == next_cell):

		return next_cell

	if(next_cell != -1):

		print(“Error: not starting trace from obstacle”)
		return -2
		
	left_rot()
	time.sleep(rot_time_per_90_degrees)
	stop()
	current_pos[2] = (current_pos[2] + 90) % 360

	while((current_pos != starting_pos or starting_trace == 1) and next_cell != 1 and (current_pos[0] == 0 and math.abs(current_pos[1] - goal[1]) < math.abs(starting_pos[1] - goal[1])):

		right_rot()
		time.sleep(rot_time_per_90_degrees)
		stop()
		current_pos[2] = (current_pos[2] + 270) % 360
		current_theta = 0
		next_cell = get_next_cell()
			
		while(next_cell == -1):

			left_rot()
			time.sleep(rot_time_per_90_degrees)
			stop()
			current_pos[2] = (current_pos[2] + 90) % 360
			current_theta += 90
			next_cell = get_next_cell()
	
			if(current_theta == 360):

				print(“Error: can’t trace, obstacles on all sides”)
				return -2
		
		go_to_next_cell(next_cell)
		
		starting_trace = 0
			
	if(current_pos == starting_pos):
		
		print(“Error: robot enclosed in obstacle”)
		return -2

	else

		return next_cell

-1 == obstacle
null == unknown
0 == empty
1 == goal

x_pos = current_pos[0]
y_pos = current_pos[1]
theta =  current_pos[2]