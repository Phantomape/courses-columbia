package c2g2.kinematics3D;

import java.util.ArrayList;

import org.joml.Vector3d;

public class ForwardKinematics3D {

	private Skeleton3D skeleton;
	
	private Joint3D currJoint;
	
	private Joint3D prevJoint;
	
	public ForwardKinematics3D(Skeleton3D skeleton) {
		if ( skeleton == null ) 
			throw new NullPointerException("The provided skeleton is NULL");
		this.skeleton = skeleton;
	}

	public void selectChildJoint(int idx) {
		ArrayList<Joint3D> joints = skeleton.getJoints();
		for(int i = 0; i < joints.size(); i++)
			if(joints.get(i).idx == idx)
				currJoint = joints.get(i);
	}
	
	public void selectParentJoint(int idx) {
		ArrayList<Joint3D> joints = skeleton.getJoints();
		for(int i = 0; i < joints.size(); i++)
			if(joints.get(i).idx == idx)
				prevJoint = joints.get(i);
	}

	public void updateStates(double deltaTheta, double deltaPhi) {
		updateStatesRecursive(prevJoint, currJoint, deltaTheta, deltaPhi);
	}

	private void updateStatesRecursive(Joint3D prevJoint, Joint3D currJoint, double deltaTheta, double deltaPhi) {
		System.out.println("Previous angles: " + currJoint.theta + " , " + currJoint.phi);
		currJoint.theta += Math.toRadians(deltaTheta);
		currJoint.phi += Math.toRadians(deltaPhi);
		System.out.println("Current angles: " + currJoint.theta + " , " + currJoint.phi);
		System.out.println("Previous ");
		currJoint.showPos();
		double r = skeleton.getLengths().get(new String(prevJoint.idx + "-" + currJoint.idx));
		currJoint.pos = new Vector3d(prevJoint.pos.x + r * Math.sin(currJoint.theta) * Math.cos(currJoint.phi), 
				prevJoint.pos.y + r * Math.sin(currJoint.theta) * Math.sin(currJoint.phi),
				prevJoint.pos.z + r * Math.cos(currJoint.theta));
		System.out.println("Current ");
		currJoint.showPos();
		for(int i = 0; i < currJoint.child.size(); i++)
			updateStatesRecursive(currJoint, currJoint.child.get(i), deltaTheta, deltaPhi);
		
	}

	public void init() {
		Joint3D root = skeleton.getRoot();
		Vector3d dir = root.pos;
		Vector3d z = new Vector3d(0, 0, 1);
		Vector3d x = new Vector3d(1, 0, 0);
		root.theta = getTheta(z, dir);
		root.phi = getPhi(x, dir);
		
		for(int i = 0; i < root.child.size(); i++)
			setRotation(root, root.child.get(i));
	}

	private double getPhi(Vector3d x, Vector3d dir) {
		Vector3d dirxy = new Vector3d(dir.x, dir.y, 0);
		double res = Math.acos(dir.x/(dirxy.length() * x.length()));
		if(dir.y < 0)
			res = 2 * Math.PI - res;
		return res;
	}

	private double getTheta(Vector3d z, Vector3d dir) {
		double res = Math.acos(dir.z/(z.length() * dir.length()));
		return res;
	}

	private void setRotation(Joint3D root, Joint3D child) {
		Vector3d dir = new Vector3d(child.pos.x - root.pos.x, child.pos.y - root.pos.y, child.pos.z - root.pos.z);
		Vector3d z = new Vector3d(0, 0, 1);
		Vector3d x = new Vector3d(1, 0, 0);
		//
		child.theta = getTheta(z, dir);
		child.phi = getPhi(x, dir);
		
		for(int i = 0; i < child.child.size(); i++)
			setRotation(child, child.child.get(i));
	}

}
