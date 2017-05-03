package c2g2.kinematics;


import java.util.ArrayList;

import org.ejml.simple.SimpleMatrix;
import org.joml.Matrix3d;
import org.joml.Vector2d;
import org.joml.Vector2f;
import org.joml.Vector3f;

/*!
 * Class that implements the Forward kinematics algorithm
 */
public class ForwardKinematics {
	
	private Skeleton2D skeleton = null;
	
	public ForwardKinematics(Skeleton2D ske) {
		if ( ske == null ) throw new NullPointerException("The provided skeleton is NULL");

		skeleton = ske;
		// Here you need to create a map that maps parameter array (see updateState(double[]) below)
		// to individual rotational angles of all the joints on the skeleton
	}
	
	public void updateState(double[] params) {
		// TODO: Implement the forward kinematics algorithm here
	}
	
	public RigidLink2D updateState(SimpleMatrix x, ArrayList<LinkConnection2D> linksChain) {
		RigidLink2D root = skeleton.getRoot();
		LinkConnection2D lc = root.getParent();
		Vector2d v1 = new Vector2d(1, 0);
		Vector2d v2 = new Vector2d(lc.getJoint().getPos().x, lc.getJoint().getPos().y);
		double baseAngle = Math.acos(v1.dot(v2)/(v1.length() * v2.length()));	
		baseAngle = v2.y > 0 ? baseAngle : -baseAngle;
		lc.offsetAngle(x.get(0));
		lc.updatePos(baseAngle);
		RigidLink2D endLink = dfs(root, 1, x, linksChain, new Matrix3d(), baseAngle + lc.getAngle());
		return endLink;
	}
	
	private RigidLink2D dfs(RigidLink2D root, int level, SimpleMatrix x, ArrayList<LinkConnection2D> lc, Matrix3d T, double baseAngle){
		RigidLink2D res = null;
		for(int i = 0; i < root.childsize(); i++){
			LinkConnection2D lk = root.getChild(i);
			if(lk.getChild() == null){
				res = lk.getParent();
				continue;
			}
			if(lk.getChild().getChildJoint().equals(lc.get(level).getChild().getChildJoint())){
				lk.offsetAngle(x.get(level));
				boolean flag = lk.updatePos(baseAngle);

				res = dfs(lk.getChild(), level + 1, x, lc, T, baseAngle + lk.getAngle());

			}else continue;
		}
		return res;
	}
	
	public void updateState(double offset){
		dfs(skeleton.getRoot(), offset, 0);
	}
	
	private void dfs(RigidLink2D root, double offset, int level){
		for(int i = 0; i < root.childsize(); i++){
			LinkConnection2D lk = root.getChild(i);
			if(lk.getChild() == null)
				continue;
			Joint2D prevJoint = root.getParentJoint();
			Joint2D currJoint = root.getChildJoint();
			Joint2D nextJoint = lk.getChild().getChildJoint();
			double len2 = lk.getChild().getLength();
			
			Vector2d x0x1 = new Vector2d(currJoint.getPos().x - prevJoint.getPos().x, currJoint.getPos().y - prevJoint.getPos().y);
			Vector2d x1x2 = new Vector2d(nextJoint.getPos().x - currJoint.getPos().x, nextJoint.getPos().y - currJoint.getPos().y);
			double rotateAngle = Math.acos(x0x1.dot(x1x2)/(x0x1.length() * x1x2.length()));
			System.out.println("x0(" + prevJoint.getPos().x + "," + prevJoint.getPos().y + ")");
			System.out.println("x1(" + currJoint.getPos().x + "," + currJoint.getPos().y + ")");
			System.out.println("x2(" + nextJoint.getPos().x + "," + nextJoint.getPos().y + ")");
			
			//	Rotation
			System.out.println("x0x1 before rotation: (" + x0x1.x + "," + x0x1.y + ")");
			Vector2d u = new Vector2d(1, 0);
			double theta = Math.acos(x0x1.dot(u)/ (x0x1.length() * u.length()));
			Vector2d rot_x0x1 = new Vector2d();
			Vector2d rot_x0x1_1 = new Vector2d(x0x1.x * Math.cos(-theta) - x0x1.y * Math.sin(-theta), x0x1.x * Math.sin(-theta) + x0x1.y * Math.cos(-theta));
			Vector2d rot_x0x1_2 = new Vector2d(x0x1.x * Math.cos(theta) - x0x1.y * Math.sin(theta), x0x1.x * Math.sin(theta) + x0x1.y * Math.cos(theta));
			rot_x0x1 = (rot_x0x1_1.x > 0) ? rot_x0x1_1 : rot_x0x1_2;
			System.out.println("x0x1 after rotation: (" + rot_x0x1.x + "," + rot_x0x1.y + ")");
			
			System.out.println("x1x2 before rotation: (" + x1x2.x + "," + x1x2.y + ")");
			Vector2d rot_x1x2 = new Vector2d();
			Vector2d rot_x1x2_1 = new Vector2d(x1x2.x * Math.cos(-theta) - x1x2.y * Math.sin(-theta), x1x2.x * Math.sin(-theta) + x1x2.y * Math.cos(-theta));
			Vector2d rot_x1x2_2 = new Vector2d(x1x2.x * Math.cos(theta) - x1x2.y * Math.sin(theta), x1x2.x * Math.sin(theta) + x1x2.y * Math.cos(theta));
			rot_x1x2 = (rot_x0x1 == rot_x0x1_1) ? rot_x1x2_1 : rot_x1x2_2;
			System.out.println("x1x2 after rotation: (" + rot_x1x2.x + "," + rot_x1x2.y + ")");
			double ydir = rot_x1x2.y > 0 ? 1 : -1;

			theta = (rot_x0x1_1.x > 0) ? theta : -theta;
			
			//	New coordinates
			rotateAngle = Math.toRadians(Math.toDegrees(rotateAngle) + offset);
			rot_x1x2 = new Vector2d(len2 * Math.cos(rotateAngle), ydir * len2 * Math.sin(rotateAngle));
			System.out.println("x1x2 after changing angle: (" + rot_x1x2.x + "," + rot_x1x2.y + ")");
			Vector2d rot2_x1x2 = new Vector2d(rot_x1x2.x * Math.cos(theta) - rot_x1x2.y * Math.sin(theta), rot_x1x2.x * Math.sin(theta) + rot_x1x2.y * Math.cos(theta));
			System.out.println("x1x2 after rotation back: (" + rot2_x1x2.x + "," + rot2_x1x2.y + ")");
			System.out.println("x2(before): (" + nextJoint.getPos().x + "," + nextJoint.getPos().y + ")");
			nextJoint.setPos(new Vector2d(currJoint.getPos().x + rot2_x1x2.x, currJoint.getPos().y + rot2_x1x2.y));
			System.out.println("x2(after): (" + nextJoint.getPos().x + "," + nextJoint.getPos().y + ")");
			System.out.println("Link level: " + level);
			System.out.println();
			

			//	recursion
			dfs(lk.getChild(), offset, level + 1);
		}
	}
	
}
