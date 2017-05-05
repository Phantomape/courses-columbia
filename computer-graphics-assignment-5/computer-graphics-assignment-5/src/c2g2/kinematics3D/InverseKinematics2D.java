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
		System.out.println("Forward:");
		for(int i = 0; i < fc.size(); i++)
			System.out.println(fc.get(i));
	}

	private ArrayList<Vector2d> getForwardChain() {
		return null;

	}
}
