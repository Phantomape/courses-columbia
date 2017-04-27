package c2g2.kinematics;

/*
 * This is the class to implement your inverse kinematics algorithm.
 * 
 * TODO:
 * Please include your data structure and methods for the inverse kinematics here.
 */
public class InverseKinematics {
	
	private Skeleton2D skeleton = null;
	
	public InverseKinematics(Skeleton2D ske) {
		if ( ske == null ) throw new NullPointerException("The provided skeleton is NULL");
		skeleton = ske;
	}
}
