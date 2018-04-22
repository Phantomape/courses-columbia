package c2g2.animation;

import java.util.ArrayList;

import org.joml.Vector3d;

import c2g2.kinematics3D.Joint3D;

public class AnimationSample {

	private float[] pts;
	
	private int len;
	
	private double time;
	
	public AnimationSample(ArrayList<Joint3D> links, double t) {
		len = links.size() * 3;
    	pts = new float[len];
    	for(int i = 0; i < links.size(); i++){
    		pts[i * 3 + 0] = (float)links.get(i).pos.x;
    		pts[i * 3 + 1] = (float)links.get(i).pos.y;
    		pts[i * 3 + 2] = (float)links.get(i).pos.z;
    	}
    	time = t;
	}

	public Vector3d getPoints(int i) {
		Vector3d res = new Vector3d(pts[i * 3 + 0], pts[i * 3 + 1], pts[i * 3 + 2]);
		return res;
	}

	public float[] getChunks(){
		return pts;
	}
	
	public int getLen(){
		return len;
	}

}
