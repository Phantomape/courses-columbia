import random, pygame
from collections import deque
from math import sqrt, atan2, cos, sin

XDIM = 640
YDIM = 480
white = 255, 240, 200
black = 20, 20, 40
red = 255, 0, 0
blue = 0, 255, 0
green = 0, 0, 255
cyan = 0, 255, 255


class RRT:
    def __init__(self, start, dest, obstacles, increment, screen):
        self.start = start
        self.dest = dest
        self.obstacles = obstacles
        self.K = 1000
        self.root = RRTNode(start[0], start[1])
        self.incremental_dist = increment
        self.state = "Init"
        self.screen = screen

    def build(self):
        for k in range(self.K):
            x = self.random_state()
            self.extend(RRTNode(int(x[0]), int(x[1])))

            pygame.display.update()
            fpsClock.tick(10000)

    def extend(self, x_rand):
        found = False
        while found is False:
            x_near = self.nearest_neighbor(x_rand)
            x_new = self.step_to(x_near, x_rand)
            no_collision = self.no_collision(x_rand, x_new)
            if no_collision is True:
                x_near.children.append(x_new)
                x_new.parent = x_near
                found = True

                reached = self.is_reached(x_new)
                if reached is True:
                    self.state = "Reached"
                else:
                    self.state = "Advanced"

                pygame.draw.line(self.screen, white, [x_near.x, x_near.y], [x_new.x, x_new.y])

    def is_reached(self, x_new):
        return False

    def step_to(self, p1, p2):
        theta = atan2(p2.y - p1.y, p2.x - p1.x)
        return RRTNode(int(p1.x + self.incremental_dist * cos(theta)), int(p1.y + self.incremental_dist * sin(theta)))

    def nearest_neighbor(self, x_rand):
        min_dist = 1000000
        d = deque()
        d.append(self.root)
        res = None
        while len(d) > 0:
            front = d.popleft()
            curr_dist = self.dist(front, x_rand)
            if curr_dist < min_dist:
                min_dist = curr_dist
                res = front
            for child in front.children:
                d.append(child)
        return res

    def dist(self, p1, p2):
        return sqrt((p1.x - p2.x) * (p1.x - p2.x) + (p1.y - p2.y) * (p1.y - p2.y))

    def no_collision(self, old_node, new_node):
        # collision detection by inspecting all the lines formed by obstacle
        origin_line = [[old_node.x, old_node.y], [new_node.x, new_node.y]]

        for vertices_set in self.obstacles:
            for i in range(len(vertices_set)):
                line = [[vertices_set[i][0], vertices_set[i][1]], [vertices_set[i-1][0], vertices_set[i-1][1]]]
                if self.line_intersect(origin_line, line):
                    return False
        return True

    def line_intersect(self, line1, line2):
        return False

    def random_state(self):
        #while True:
        x = [random.random() * XDIM, random.random() * YDIM]
            #no_collision = self.is_collided(x)
            #if no_collision is True:
        return x


class RRTNode:
    def __init__(self, x, y):
        self.x = x
        self.y = y
        self.children = []
        self.parent = None


def parse(obs_file, start_dest_file, screen):
    f = open(obs_file, "r")
    obstacles_num = f.readline()
    obstacles = []
    for _ in range(int(obstacles_num)):
        vertices = []
        num_vertices = int(f.readline())
        for _ in range(num_vertices):
            vertices_pair = f.readline()
            vertices_pair = vertices_pair.split()
            vertices_pair[0] = int(vertices_pair[0])
            vertices_pair[1] = int(vertices_pair[1])
            if vertices:
                pygame.draw.line(screen, white, [vertices_pair[0], vertices_pair[1]], [vertices[-1][0], vertices[-1][1]])
            vertices.append(vertices_pair)
        pygame.draw.line(screen, white, [vertices[0][0], vertices[0][1]], [vertices[-1][0], vertices[-1][1]])
        obstacles.append(vertices)

        pygame.display.update()
        fpsClock.tick(10000)
    f.close()

    f = open(start_dest_file, "r")
    lines = f.readlines()
    start = lines[0].split()
    dest = lines[1].split()
    start[0] = int(start[0])
    start[1] = int(start[1])
    dest[0] = int(dest[0])
    dest[1] = int(dest[1])
    f.close()
    return start, dest, obstacles


if __name__ == "__main__":
    pygame.init()
    fpsClock = pygame.time.Clock()

    screen = pygame.display.set_mode([XDIM, YDIM])
    pygame.display.set_caption('Rapidly Exploring Random Tree')

    start, dest, obstacles = parse("obstacles.txt", "start_and_dest.txt", screen)
    rrt = RRT(start, dest, obstacles, 10, screen)
    rrt.build()
