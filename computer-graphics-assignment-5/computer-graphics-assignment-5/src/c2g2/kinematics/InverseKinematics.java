package c2g2.kinematics;

import org.joml.Matrix3d;
import org.joml.Vector2d;

import java.util.ArrayList;
import java.util.Collections;

import org.ejml.factory.SingularMatrixException;
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
	ArrayList<LinkConnection2D> linksChain = new ArrayList<LinkConnection2D>();
	
	public InverseKinematics(Skeleton2D ske) {
		if ( ske == null ) throw new NullPointerException("The provided skeleton is NULL");
		skeleton = ske;
	}
	
	public void setEndEffector(Vector2d v){
		currLeaf = v;
		//	Find which link this joint belongs to
		ArrayList<RigidLink2D> endLinks = skeleton.getEndLinks();
		for(int i = 0; i < endLinks.size(); i++){
			if(endLinks.get(i).getChildJoint().getPos().x == currLeaf.x &&
					endLinks.get(i).getChildJoint().getPos().y == currLeaf.y ){
				endLink = endLinks.get(i);
				break;
			}
		}
	}
	
	public void updateState(Vector2d v){
		Vector2d diff = new Vector2d(v.x - currLeaf.x, v.y - currLeaf.y);
		System.out.println("Diff length:" + diff.length());
		
		SimpleMatrix jacobian = calcJacobian();
    	SimpleMatrix JacTranspose = jacobian.transpose();
    	SimpleMatrix pseudoinverse = jacobian.mult(JacTranspose);
    	
    	SimpleMatrix scaledIdentity = SimpleMatrix.identity(2);
    	float lambda = 0.5f;
    	scaledIdentity.scale(Math.pow(lambda, 2));
    	
    	SimpleMatrix deltaTheta;
    	SimpleMatrix cray = ((pseudoinverse).plus(scaledIdentity)).invert();
    	SimpleMatrix e = new SimpleMatrix(2, 1);
    	e.set(0, diff.x);
    	e.set(1, diff.y);
    	deltaTheta = JacTranspose.mult(cray).mult(e);
		ForwardKinematics fk = new ForwardKinematics(skeleton);
		endLink = fk.updateState(deltaTheta, linksChain);
		updateLeaf(endLink);
		/*
		SimpleMatrix offset = new SimpleMatrix(2,1);
		offset.set(0, 0, v.x - currLeaf.x);
		offset.set(1, 0, v.y - currLeaf.y);

		System.out.println("Offset:(" + offset.get(0, 0) + "," + offset.get(1, 0) + ")");
		System.out.println();
		try {
			  SimpleMatrix x = jacobian.solve(offset);
			  //	update angle
			  x.print();
			  ForwardKinematics fk = new ForwardKinematics(skeleton);
			  endLink = fk.updateState(x, linksChain);
			  updateLeaf(endLink);
		} catch ( SingularMatrixException e ) {
			  throw new IllegalArgumentException("Singular matrix");
		}
    	System.out.println("Inverse kinematics done.");
		*/
	}
	
	private void updateLeaf(RigidLink2D el){
		currLeaf = new Vector2d(el.getChildJoint().getPos().x, el.getChildJoint().getPos().y);
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
	
	
	private SimpleMatrix calcJacobian(){
		//	Get all links connections in the particular chain
		RigidLink2D iter = endLink;
		while(iter != null){
			linksChain.add(iter.getParent());
			iter = iter.getParent().getParent();
		}
		Collections.reverse(linksChain);
		
		//	Calculate jacobian matrix
		int dim = linksChain.size();
		SimpleMatrix tmp = new SimpleMatrix(2, dim);
		/*
		for(int j = 0; j < dim; j++){
			Matrix3d res = new Matrix3d();
			for(int i = 0; i < dim; i++){
				if(i == j)
					res.mul(autoDiff(linksChain.get(j)));
				else
					res.mul(linksChain.get(j).T);
			}
			tmp.set(0, j, res.m02);
			tmp.set(1, j, res.m12);
		}
		*/
    	for(int i = 0; i < dim; i++){
    		int currentAngle = 0;
    		for(int j = 0; j < dim; j++){
    			currentAngle += linksChain.get(j).getAngle();
    			if(j >= i){
        			tmp.set(0, i, tmp.get(0, i) + (linksChain.get(j).getChild().getLength())*-1*(Math.sin(Math.toRadians(currentAngle))) );
        			tmp.set(1, i, tmp.get(1, i) + (linksChain.get(j).getChild().getLength())*(Math.cos(Math.toRadians(currentAngle))) );
    			}
    		}
    	}
		return tmp;
	}
	
	private Matrix3d autoDiff(LinkConnection2D lk){
		double delta = 0.01;
		double len = lk.getChild().getLength();
		
		Matrix3d A = new Matrix3d();
		A.m00(Math.cos(lk.getAngle() + delta));
		A.m01(Math.sin(-(lk.getAngle() + delta)));
		A.m02(len * Math.cos(lk.getAngle() + delta));
		A.m10(Math.sin(lk.getAngle() + delta));
		A.m11(Math.cos(lk.getAngle() + delta));
		A.m12(len * Math.sin(lk.getAngle() + delta));
		
		Matrix3d B = new Matrix3d();
		A.m00(Math.cos(lk.getAngle() - delta));
		A.m01(Math.sin(-(lk.getAngle() - delta)));
		A.m02(len * Math.cos(lk.getAngle() - delta));
		A.m10(Math.sin(lk.getAngle() - delta));
		A.m11(Math.cos(lk.getAngle() - delta));
		A.m12(len * Math.sin(lk.getAngle() - delta));
		
		A.sub(B);
		A.scale(1.0/(2 * delta));
		return A;
	}
	
	@Deprecated
	private int getNumJoints(){
		int res = 1;

		RigidLink2D iter = endLink;
		while(iter.getParent().getParent() != null){
			res++;
			iter = iter.getParent().getParent();
		}
		
		return res;
	}
	
	
	
}
