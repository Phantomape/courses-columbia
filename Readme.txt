Calculate Normal Vector
In matlab, we would use a coordinate system like this: the row represents the y coordinate 
while the col represents the x coordinates, the z coordinates will perpendicular to the image plane
Thus, we have an equation (x - x_0)^2 + (y - y_0)^2 + z^2 = R^2, 
so z = sqrt(R^2 - (x - x_0)^2 - (y - y_0)^2)
the derivative to x:derivative_x = -(x - x_0)/sqrt(R^2 - (x - x_0)^2 - (y - y_0)^2)
the derivative to y:derivative_y = -(y - y_0)/sqrt(R^2 - (x - x_0)^2 - (y - y_0)^2)
normal vector = (-derivative_x, -derivative_y, 1)