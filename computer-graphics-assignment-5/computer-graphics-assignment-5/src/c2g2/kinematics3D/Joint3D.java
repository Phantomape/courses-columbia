package c2g2.kinematics3D;

import java.util.ArrayList;

import org.joml.Vector3d;

public class Joint3D {
	
	public boolean isFixed = false;
	public boolean isLeaf = false;
	public Vector3d pos = null;
	public Joint3D parent = null;
	public ArrayList<Joint3D> child = new ArrayList<Joint3D>();
	public int idx = -1;
	
	public Joint3D(Vector3d v){
		pos = v;
	}

}
