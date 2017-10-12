This is a Lab 2 submission for group 22
Team Members: Benjamin Carlin bc2620, Xucheng Chen xc2360, Ziyi Mu zm2263




Youtube link for bug2 demo:



Description:

Our bug 2 algorithm consists of two main stages: follow m line and trace object. 
Starting from follow m line, those two stages will be switched based on the algorithm specification.

Initially we set up a map grid with robot starting at (0, 0) and goal point at (0, n), 
and m line is the straight line x = 0, based on the assumption the robot is always facing the goal position.
Then we can adjust the distance calculation to (y coordinate of goal) - (y coordinate of current position).

The grid size is adjusted to be large enough for the robot size to make sure the robot will not crash on 
the obstacle when turning and moving. For each movement, we updated the current position, facing angle, and 
store the path to be mapped by mapplot. 

The robot cannot always make a 90 degree turn perfectly, we have calculated some parameter to adjust the degree, 
so we can minimize the effect of different terrains.
