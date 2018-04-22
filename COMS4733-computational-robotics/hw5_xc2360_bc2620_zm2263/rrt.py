import random, pygame
from collections import deque
from math import sqrt, atan2, cos, sin

XDIM = 640
YDIM = 640
white = 255, 240, 200
black = 20, 20, 40
red = 255, 0, 0
green = 0, 255, 0
blue = 0, 0, 255
cyan = 0, 255, 255


class RRT:
    def __init__(self, start, dest, obstacles, increment, screen):
        self.start = start
        self.dest = dest
        self.obstacles = obstacles
        self.K = 100000
        self.root = RRTNode(start[0], start[1])
        self.incremental_dist = increment
        self.state = "Init"
        self.screen = screen
        self.goal_radius = 10
        self.seed = None

    def init_config(self):
        self.state = "Build"
        for obs in self.obstacles:
            pygame.draw.polygon(self.screen, red, obs, 2)
        pygame.draw.circle(self.screen, cyan, self.start, 3)
        pygame.draw.circle(self.screen, blue, self.dest, 3)
        pygame.display.update()

    def build(self):
        self.init_config()
        while True:
            if self.state == "Build":
                for k in range(self.K):
                    found = False
                    while found is False:
                        x_rand = self.random_state()
                        x_rand = RRTNode(int(x_rand[0]), int(x_rand[1]))
                        x_near = self.nearest_neighbor(x_rand)
                        x_new = self.step_to(x_near, x_rand)
                        no_collision = self.is_collided([x_new.x, x_new.y])
                        if no_collision is True:
                            x_near.children.append(x_new)
                            x_new.parent = x_near
                            found = True

                            reached = self.is_reached(x_new)
                            if reached is True:
                                self.state = "Reached"
                                self.seed = x_new
                            else:
                                self.state = "Advanced"

                            pygame.draw.line(self.screen, white, [x_near.x, x_near.y], [x_new.x, x_new.y])

                    pygame.display.update()
                    fpsClock.tick(10000)
                    if self.state == "Reached":
                        break

                if self.state == "Advanced":
                	print "Cannot Reach Goal After Trying 100000 attempts"
                	pygame.quit()
                	break

            elif self.state == "Reached":
                curr_node = self.seed
                while curr_node.parent is not None:
                    pygame.draw.line(self.screen, cyan, [curr_node.x, curr_node.y], [curr_node.parent.x, curr_node.parent.y])
                    curr_node = curr_node.parent
                    pygame.display.update()

                pygame.time.wait(5000)
                pygame.quit()
                break

    def extend(self, x_rand):
        found = False
        while found is False:
            x_near = self.nearest_neighbor(x_rand)
            x_new = self.step_to(x_near, x_rand)
            no_collision = self.is_collided([x_new.x, x_new.y])
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
        dest = RRTNode(int(self.dest[0]), int(self.dest[1]))
        if self.dist(x_new, dest) < self.goal_radius:
            return True
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

    def is_collided(self, x_new):
        p1 = [int(x_new[0]), int(x_new[1])]
        p2 = [640, int(x_new[1])]

        for obs in self.obstacles:
            count = 0
            for x in range(len(obs)):
                p3 = obs[x % len(obs)]
                p4 = obs[(x + 1) % len(obs)]
                if self.intersect(p3, p4, p1, p2) is True:
                    if self.orientation(p3, p1, p2) == 0:
                        return self.on_segment(p3, p1, p2)
                    count += 1
            if count % 2 == 1:
                return False

        return True

    def intersect(self, p1, p2, p3, p4):
        o1 = self.orientation(p1, p2, p3)
        o2 = self.orientation(p1, p2, p4)
        o3 = self.orientation(p3, p4, p1)
        o4 = self.orientation(p3, p4, p2)

        if o1 != o2 and o3 != o4:
            return True

        if o1 == 0 and self.on_segment(p1, p3, p2):
            return True

        if o2 == 0 and self.on_segment(p1, p4, p2):
            return True

        if o3 == 0 and self.on_segment(p3, p1, p4):
            return True

        if o4 == 0 and self.on_segment(p3, p1, p4):
            return True

        return False

    def orientation(self, p, q, r):
        val = (q[1] - p[1]) * (r[0] - q[0]) - (q[0] - p[0]) * (r[1] - q[1])
        if val == 0:
            return 0
        elif val > 0:
            return 1
        else:
            return 2

    def on_segment(self, p, q, r):
        if q[0] <= max(p[0], r[0]) and q[0] >= min(p[0], r[0]) and q[1] >= min(p[1], r[1]) and q[1] <= max(p[1], r[1]):
            return True
        return False

    def random_state(self):
        while True:
            x = [random.random() * XDIM, random.random() * YDIM]
            no_collision = self.is_collided(x)
            if no_collision is True:
                return x


class RRTNode:
    def __init__(self, x, y):
        self.x = x
        self.y = y
        self.children = []
        self.parent = None


def parse(obs_file, start_dest_file):
    f = open(obs_file, "r")
    lines = f.readline()
    obstacles = []
    for line in range(int(lines)):
        vertices = []
        num_vertices = int(f.readline())
        for v in range(num_vertices):
            vertices_pair = f.readline()
            vertices_pair = vertices_pair.split()
            vertices_pair[0] = int(vertices_pair[0])
            vertices_pair[1] = int(vertices_pair[1])
            vertices.append(vertices_pair)
        obstacles.append(vertices)
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

    start, dest, obstacles = parse("obstacles.txt", "start_and_dest.txt")
    rrt = RRT(start, dest, obstacles, 10, screen)
    rrt.build()
