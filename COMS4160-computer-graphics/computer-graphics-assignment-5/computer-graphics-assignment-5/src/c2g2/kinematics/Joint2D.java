package c2g2.kinematics;

import org.joml.Vector2d;

public abstract class Joint2D {
	/*
	 * Position of the 2D joint
	 */
	public boolean isLeaf = false;
	protected Vector2d position = new Vector2d(0.,0.);
	
	protected boolean fixed = false;
	
	public Vector2d getPos(){
		return position;
	}

	public boolean isFixed() {
		return fixed;
	}
	
	public void setPos(Vector2d v){
		position = v;
	}
	
	public boolean equals(Joint2D j){
		if(position.x == j.getPos().x && position.y == j.getPos().y)
			return true;
		return false;
	}
	
	public void showPos(){
		System.out.println("Position:(" + position.x + "," + position.y + ")");
	}
	
	public abstract void setParam(double p);
}
