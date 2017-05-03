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
	
	public void offsetAngle(double a){
		angle += a;
	}
	
	public void updateT(){
	}
	
	public boolean updatePos(double baseAngle){
		Joint2D c = child.getChildJoint();
		double len = child.getLength();
		Joint2D currJoint = child.getParentJoint();
		Joint2D nextJoint = child.getChildJoint();
		Vector2d v = new Vector2d(nextJoint.getPos().x - currJoint.getPos().x, nextJoint.getPos().y - currJoint.getPos().y);
		Vector2d rot_v = new Vector2d(v.x * Math.cos(-baseAngle) - v.y * Math.sin(-baseAngle), v.x * Math.sin(-baseAngle) + v.y * Math.cos(-baseAngle));
		boolean flag = false;
		c.setPos(new Vector2d(joint.position.x + len * Math.cos(baseAngle + angle), joint.position.y + len * Math.sin(baseAngle + angle)));
		/*
		if((baseAngle > 0 && rot_v.y > 0) || (baseAngle < 0 && rot_v.y < 0)){
			c.setPos(new Vector2d(joint.position.x + len * Math.cos(baseAngle + angle), joint.position.y + len * Math.sin(baseAngle + angle)));
			flag = true;
		}
		else{
			c.setPos(new Vector2d(joint.position.x + len * Math.cos(baseAngle - angle), joint.position.y + len * Math.sin(baseAngle - angle)));
			flag = false;
		}*/
		return flag;
		
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
