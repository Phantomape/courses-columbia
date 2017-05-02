package c2g2.kinematics;

import org.joml.Matrix3d;
import org.joml.Vector2d;

public class LinkConnection2D {

	/*
	 * When the two lin ks look like what follows (form into a straight line)
	 * ------(o)------
	 * Here the left link is the parent link, and the right link is the child link
	 * The joint rotateAngle is zero
	 */
	private RigidLink2D parent = null;
	
	private RigidLink2D child = null;
	
	private Joint2D joint = null;
	
	public Matrix3d T = new Matrix3d();
	
	private double angle = 0.0;
	
	public double getAngle(){
		return angle;
	}
	
	public void setAngle(double a){
		angle = a;
	}
	
	public Joint2D getJoint() {
		return joint;
	}
	
	public RigidLink2D getParent() {
		return parent;
	}
	
	public RigidLink2D getChild(){
		return child;
	}
	
	public boolean isEnd() {
		return child == null;
	}
	
	public void setParent(RigidLink2D p){
		parent = p;
	}
	
	public void setChild(RigidLink2D c){
		child = c;
	}
	
	public void setJoint(Joint2D j){
		joint = j;
	}
	
}
