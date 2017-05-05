package c2g2.kinematics3D;

import java.util.ArrayList;
import java.util.Collections;

import org.joml.Vector2d;
import org.joml.Vector3d;

import c2g2.kinematics.Joint2D;
import c2g2.kinematics.RigidLink2D;
import c2g2.kinematics.Skeleton2D;

public class InverseKinematics2D {

	private Skeleton2D skeleton;
	
	private RigidLink2D end;
	
	private Vector2d pos;
	
	public InverseKinematics2D(Skeleton2D skeleton, RigidLink2D end, Vector2d pos) {
		this.skeleton = skeleton;
		this.end = end;
		this.pos = pos;
	}

	public void updateStates(Vector2d pos) {
		// TODO Auto-generated method stub
		ArrayList<Vector2d> fc = getForwardChain();
		ArrayList<Vector2d> bc = getBackwardChain();		
		ArrayList<Double> lens = getLengths();
		ArrayList<Vector2d> rc = getFinalPos(fc, bc, lens);
		updateStates(rc);
	}

	private void updateStates(ArrayList<Vector2d> rc) {
		RigidLink2D iter = end;
		for(int i = 0; i < rc.size(); i++){
			Joint2D j = iter.getChildJoint();
			j.setPos(rc.get(i));
			iter = iter.getParent().getParent();
		}
	}

	private ArrayList<Vector2d> getFinalPos(ArrayList<Vector2d> fc, ArrayList<Vector2d> bc, ArrayList<Double> lens) {
		ArrayList<Vector2d> rc = new ArrayList<Vector2d>();
		Vector2d start = fc.get(0); 
		System.out.println("Starting pos: (" + start.x + "," + start.y + ")");
		for(int i = 0; i < lens.size(); i++){
			System.out.println("Towards pos: (" + bc.get(i + 1).x + "," + bc.get(i + 1).x + ")");
			Vector2d dir = new Vector2d(bc.get(i + 1).x - start.x, bc.get(i + 1).y - start.y);
			dir.normalize();
			double len = lens.get(i);
			Vector2d res = new Vector2d(start.x + len * dir.x, start.y + len * dir.y);
			System.out.println("Destination pos: (" + res.x + "," + res.y + ")");
			start = res;
			rc.add(res);
		}
		Collections.reverse(rc);
		return rc;
	}

	private ArrayList<Double> getLengths() {
		ArrayList<Double> lens = new ArrayList<Double>();
		RigidLink2D iter = end;
		while(iter != null){
			lens.add(iter.getLength());
			iter = iter.getParent().getParent();
		}
		Collections.reverse(lens);
		return lens;
	}

	private ArrayList<Vector2d> getBackwardChain() {
		ArrayList<Vector2d> bc = new ArrayList<Vector2d>();
		Vector2d v = pos;
		RigidLink2D iter = end;
		bc.add(v);
		while(iter != null){
			Vector2d dir = new Vector2d(iter.getParentJoint().getPos().x - v.x, iter.getParentJoint().getPos().y - v.y);
			dir.normalize();
			double len = iter.getLength();
			Vector2d res = new Vector2d(v.x + len * dir.x, v.y + len * dir.y);
			bc.add(res);
			iter = iter.getParent().getParent();
			v = res;
		}
		Collections.reverse(bc);
		return bc;
	}

	private ArrayList<Vector2d> getForwardChain() {
		ArrayList<Vector2d> fc = new ArrayList<Vector2d>();
		RigidLink2D iter = end;
		fc.add(iter.getChildJoint().getPos());
		while(iter.getParent().getParent() != null){
			iter = iter.getParent().getParent();
			fc.add(iter.getChildJoint().getPos());
		}
		fc.add(iter.getParentJoint().getPos());
		Collections.reverse(fc);
		return fc;
	}
}
