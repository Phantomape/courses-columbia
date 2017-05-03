package c2g2.kinematics3D;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joml.Vector2d;
import org.joml.Vector3d;

import c2g2.kinematics.RigidLink2D;

public class Skeleton3D {
    private int vaoId;
    private List<Integer> vboIdList;
    private int vertexCount;

	private Joint3D root = null;
	private int numJoints = 0;
	private ArrayList<Joint3D> joints = new ArrayList<Joint3D>(); 
	private Map<String, Double> lengths = new HashMap<String, Double>();
	private ArrayList<Vector3d> jpos = new ArrayList<Vector3d>();
	
	public Skeleton3D(Joint3D root){
		this.root = root;
	}
	
	public void init(){
		setIdx(root);
		setLen(root);
		setJointPos(root);
	}
	
	private void setIdx(Joint3D root){
		root.idx = numJoints++;
		joints.add(root);
		if(root.child.size() == 0)
			root.isLeaf = true;
		for(int i = 0; i < root.child.size(); i++){
			setIdx(root.child.get(i));
		}
	}
	
	private void setLen(Joint3D root){
		for(int i = 0; i < root.child.size(); i++){
			Joint3D child = root.child.get(i);
			String key = root.idx + "-" + child.idx;
			Vector3d bone = new Vector3d(child.pos.sub(root.pos));
			double value = bone.length();
			lengths.put(key, value);
			setLen(child);
		}
	}

	public void setJointPos(Joint3D root) {
		jpos.add(root.pos);
		for(int i = 0; i < root.child.size(); i++){
			Joint3D child = root.child.get(i);
			setJointPos(child);
		}
	}
	
	public void render() {
    	for(int i = 0; i < jpos.size(); i++){
    		Vector3d pos = jpos.get(i);
    		addCircle((float)pos.x, (float)pos.y,(float)pos.z, 0.05f);
    		addLine((float)pos.x, (float)pos.y, (float)pos.x, (float)pos.y, 0.03f);
    	}
	}
	

	
    public int getVaoId() {
        return vaoId;
    }
}
