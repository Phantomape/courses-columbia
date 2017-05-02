package c2g2.kinematics;

import org.joml.Vector2d;

import java.util.ArrayList;
import java.util.Collections;
import org.ejml.simple.SimpleMatrix;


/*
 * This is the class to implement your inverse kinematics algorithm.
 * 
 * TODO:
 * Please include your data structure and methods for the inverse kinematics here.
 */
public class InverseKinematics {
	private Skeleton2D skeleton = null;
	private Vector2d currLeaf = null;
	RigidLink2D endLink = null;
	
	public InverseKinematics(Skeleton2D ske) {
		if ( ske == null ) throw new NullPointerException("The provided skeleton is NULL");
		skeleton = ske;
	}
	
	//	Calculate angles between 2 links
	public void initAngle(){
		dfs(skeleton.getRoot(), 0);
	}
	
	public void setEndEffector(Vector2d v){
		currLeaf = v;
	}
	
	public void updateState(Vector2d v){
		int numJoints = getNumJoints();
		calcJacobian(numJoints - 1);
    	
	}
	
	private void dfs(RigidLink2D root, int level){
		LinkConnection2D genesis = root.getParent();
		Joint2D curr = root.getParentJoint();
		Joint2D next = root.getChildJoint();
		Vector2d u = new Vector2d(1.0, 0.0);
		Vector2d v = new Vector2d(next.getPos().x - curr.getPos().x, next.getPos().y - curr.getPos().y);
		double tmp = Math.acos(u.dot(v)/(u.length() * v.length()));
		genesis.setAngle(tmp);		
		
		for(int i = 0; i < root.childsize(); i++){
			LinkConnection2D lk = root.getChild(i);
			if(lk.getChild() == null)
				continue;
			
			Joint2D prevJoint = root.getParentJoint();
			Joint2D currJoint = root.getChildJoint();
			Joint2D nextJoint = lk.getChild().getChildJoint();
			Vector2d x0x1 = new Vector2d(currJoint.getPos().x - prevJoint.getPos().x, currJoint.getPos().y - prevJoint.getPos().y);
			Vector2d x1x2 = new Vector2d(nextJoint.getPos().x - currJoint.getPos().x, nextJoint.getPos().y - currJoint.getPos().y);
			tmp = Math.acos(x0x1.dot(x1x2)/(x0x1.length() * x1x2.length()));
			lk.setAngle(tmp);
			
			dfs(lk.getChild(), level + 1);
		}
	}
	
	
	private SimpleMatrix calcJacobian(int dim){
		//	Get all links connections in the particular chain
		ArrayList<LinkConnection2D> linksChain = new ArrayList<LinkConnection2D>();
		RigidLink2D iter = endLink;
		while(iter != null){
			linksChain.add(iter.getParent());
			iter = iter.getParent().getParent();
		}
		Collections.reverse(linksChain);
		
		//	Calculate jacobian matrix
		SimpleMatrix tmp = new SimpleMatrix(2, 2);
		return null;
	}
	
	private int getNumJoints(){
		int res = 1;
		//	Find which link this joint belongs to
		ArrayList<RigidLink2D> endLinks = skeleton.getEndLinks();
		for(int i = 0; i < endLinks.size(); i++){
			if(endLinks.get(i).getChildJoint().getPos().x == currLeaf.x &&
					endLinks.get(i).getChildJoint().getPos().y == currLeaf.y ){
				endLink = endLinks.get(i);
				res++;
				break;
			}
		}
		RigidLink2D iter = endLink;
		while(iter.getParent().getParent() != null){
			res++;
			iter = iter.getParent().getParent();
		}
		
		return res;
	}
	
	
	
}
