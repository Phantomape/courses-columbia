package c2g2.kinematics;

/*!
 * Class that implements the Forward kinematics algorithm
 */
public class ForwardKinematics {
	
	private Skeleton2D skeleton = null;
	
	public ForwardKinematics(Skeleton2D ske) {
		if ( ske == null ) throw new NullPointerException("The provided skeleton is NULL");

		skeleton = ske;
		// Here you need to create a map that maps parameter array (see updateState(double[]) below)
		// to individual rotational angles of all the joints on the skeleton
	}
	
	public void updateState(double[] params) {
		// TODO: Implement the forward kinematics algorithm here
	}
	
}
