package c2g2.kinematics3D;

import org.joml.Vector2d;
import org.joml.Vector3d;

public class Segment {
	Vector3d start;
	double angle;
	double length;
	Segment parent;
	Segment child;
	
	int id;
	int parentId;
	int childId;
	
	public Segment(Vector3d v, double a, double len){
		start = v;
		angle = a;
		length = len;

	}
}
