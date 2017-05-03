package c2g2.kinematics.engine;

import java.util.ArrayList;

import org.joml.Vector2d;

import c2g2.kinematics.*;
import c2g2.kinematics3D.Skeleton3D;

public class Scene {
	
	public Skeleton2D skeleton;
	
	public Skeleton3D s;
	
	public Scene(){
		
	}
	void loadfromXML(String filename){
		skeleton = new Skeleton2D(filename);
	}
	
	void test(){
		skeleton = new Skeleton2D("src/resources/models/test.xml");
		
//		skeleton.buildTestSkeleton2();
		
	}
	
	public ArrayList<Vector2d> getJointPos() {
		ArrayList<Vector2d> output = new ArrayList<Vector2d>();
		RigidLink2D current = skeleton.getRoot();
		visitNode(current, output);
		return output;
		
	}
	
	private void visitNode(RigidLink2D current, ArrayList<Vector2d> output){
		output.add(current.getParentJoint().getPos());
		output.add(current.getChildJoint().getPos());

		for(int i=0; i<current.childsize(); i++){
			if(current.getChild(i).isEnd()){
				return;
			}
			visitNode(current.getChild(i).getChild(), output);
		}
		return;
	}

	
}
