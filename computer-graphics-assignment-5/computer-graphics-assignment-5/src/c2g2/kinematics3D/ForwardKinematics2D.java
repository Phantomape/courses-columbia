package c2g2.kinematics3D;

import org.joml.Vector2d;
import org.joml.Vector3d;

import c2g2.kinematics.Joint2D;
import c2g2.kinematics.RigidLink2D;
import c2g2.kinematics.Skeleton2D;

public class ForwardKinematics2D {

	private Skeleton2D skeleton;
	
	private RigidLink2D start;
	
	public ForwardKinematics2D(Skeleton2D skeleton) {
		if ( skeleton == null ) 
			throw new NullPointerException("The provided skeleton is NULL");
		this.skeleton = skeleton;
	}

	public void init() {
		RigidLink2D root = skeleton.getRoot();
		Vector2d dir = new Vector2d(root.getChildJoint().getPos().x - root.getParentJoint().getPos().x, 
				root.getChildJoint().getPos().y - root.getParentJoint().getPos().y);
		//System.out.println("Direction vector:(" + dir.x + "," + dir.y + ")");
		Vector2d x = new Vector2d(1, 0);
		root.theta = getTheta(x, dir);
		//System.out.println("angle:" + root.theta);
		for(int i = 0; i < root.childsize(); i++){
			RigidLink2D child = root.getChild(i).getChild();
			if(child.getChildJoint() != null)
				setRotation(child);
		}
	}

	public void updateStates(double delta) {
		updateStatesRecursive(start, delta);
	}
	
	private void updateStatesRecursive(RigidLink2D root, double delta) {
		for(int i = 0; i < root.childsize(); i++){
			Joint2D prevJoint = root.getParentJoint();
			Joint2D currJoint = root.getChildJoint();
			System.out.println("Previous angles: " + root.theta);
			root.theta += Math.toRadians(delta);
			System.out.println("Current angles: " + root.theta);
			System.out.println("Previous ");
			currJoint.showPos();
			double r = root.getLength();
			Vector2d res = new Vector2d(prevJoint.getPos().x + r * Math.cos(root.theta), 
					prevJoint.getPos().y + r * Math.sin(root.theta));
			currJoint.setPos(res);
			System.out.println("Current ");
			currJoint.showPos();
			RigidLink2D child = root.getChild(i).getChild();
			if(child != null)
				updateStatesRecursive(child, delta);
		}
		
	}

	private void setRotation(RigidLink2D root) {
		Vector2d dir = new Vector2d(root.getChildJoint().getPos().x - root.getParentJoint().getPos().x, 
				root.getChildJoint().getPos().y - root.getParentJoint().getPos().y);
		//System.out.println("Direction vector:(" + dir.x + "," + dir.y + ")");
		Vector2d x = new Vector2d(1, 0);
		root.theta = getTheta(x, dir);
		//System.out.println("angle:" + root.theta);
		for(int i = 0; i < root.childsize(); i++){
			RigidLink2D child = root.getChild(i).getChild();
			if(child != null)
				setRotation(child);
		}
	}

	private double getTheta(Vector2d x, Vector2d dir) {
		double res = Math.acos(dir.x/(x.length() * dir.length()));
		if(dir.y < 0)
			res = 2 * Math.PI - res;
		return res;
	}

	public void selectLink(int level) {
		// TODO Auto-generated method stub
		int l = 0;
		RigidLink2D root = skeleton.getRoot();
		dfs(root, l, level);
	}

	private void dfs(RigidLink2D root, int l, int level) {
		if(l == level){
			start = root;
			return;
		}
		for(int i = 0; i < root.childsize(); i++){
			dfs(root.getChild(i).getChild(), l + 1, level);
		}
	}

}
