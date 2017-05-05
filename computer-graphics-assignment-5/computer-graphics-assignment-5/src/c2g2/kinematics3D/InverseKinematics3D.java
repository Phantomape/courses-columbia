package c2g2.kinematics3D;

import java.util.ArrayList;
import java.util.Collections;

import org.joml.Vector2d;
import org.joml.Vector3d;

public class InverseKinematics3D {

	private Skeleton3D skeleton;
	
	private Joint3D end;
	
	private Vector3d targetPos;
	
	public InverseKinematics3D(Skeleton3D s, Joint3D end, Vector3d v) {
		this.skeleton = s;
		this.end = end;
		this.targetPos = v;
		targetPos.z = end.pos.z;
	}

	public void updateStates() {		
		//	Forward chain
		ArrayList<Vector3d> fc = getForwardChain();		
		//	Backward chain
		ArrayList<Vector3d> bc = getBackwardChain();
		//	Lengths
		ArrayList<Double> lens = getLengths();
		//	Get final destination
		ArrayList<Vector3d> rc = getFinalPos(fc, bc, lens);
		//	Update 
		updateStates(rc);
		Joint3D iter = end;
		for(int i = 0; i < rc.size(); i++){
			iter.pos = rc.get(i);
			iter = iter.parent;
		}
		
	}

	private void updateStates(ArrayList<Vector3d> rc) {
		Joint3D iter = end;
		for(int i = 0; i < rc.size(); i++){
			iter.pos = rc.get(i);
			iter = iter.parent;
		}		
	}

	private ArrayList<Vector3d> getFinalPos(ArrayList<Vector3d> fc, ArrayList<Vector3d> bc, ArrayList<Double> lens) {
		ArrayList<Vector3d> rc = new ArrayList<Vector3d>();
		Vector3d start = fc.get(0); 
		for(int i = 0; i < lens.size(); i++){
			Vector3d dir = new Vector3d(bc.get(i + 1).x - start.x, bc.get(i + 1).y - start.y, bc.get(i + 1).z - start.z);
			dir.normalize();
			double len = lens.get(i);
			Vector3d res = new Vector3d(start.x + len * dir.x, start.y + len * dir.y, start.z + len * dir.z);
			start = res;
			rc.add(res);
		}
		Collections.reverse(rc);
		return rc;
	}

	private ArrayList<Vector3d> getBackwardChain() {
		ArrayList<Vector3d> bc = new ArrayList<Vector3d>();
		Vector3d v = targetPos;
		Joint3D iter = end;
		bc.add(v);
		while(iter.parent != null){
			Vector3d dir = new Vector3d(iter.parent.pos.x - v.x, iter.parent.pos.y - v.y, iter.parent.pos.z - v.z);
			dir.normalize();
			double len = skeleton.getLengths().get(new String(iter.parent.idx + "-" + iter.idx));
			Vector3d res = new Vector3d(v.x + len * dir.x, v.y + len * dir.y, v.z + len * dir.z);
			bc.add(res);
			iter = iter.parent;
			v = res;
		}
		Collections.reverse(bc);
		return bc;
	}

	private ArrayList<Vector3d> getForwardChain() {
		ArrayList<Vector3d> fc = new ArrayList<Vector3d>();
		Joint3D iter = end;
		fc.add(iter.pos);
		while(iter.parent != null){
			iter = iter.parent;
			fc.add(iter.pos);
		}
		Collections.reverse(fc);
		return fc;
	}
	
	private ArrayList<Double> getLengths(){
		ArrayList<Double> lens = new ArrayList<Double>();
		Joint3D iter = end;
		while(iter.parent != null){
			lens.add(skeleton.getLengths().get(new String(iter.parent.idx + "-" + iter.idx)));
			iter = iter.parent;
		}
		Collections.reverse(lens);
		return lens;
	}

 
}
