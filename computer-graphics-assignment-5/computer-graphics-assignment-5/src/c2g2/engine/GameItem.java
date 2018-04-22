package c2g2.engine;

import org.joml.Vector3f;
import c2g2.engine.Mesh;
import c2g2.kinematics3D.Skeleton3D;

public class GameItem {

    private final Mesh mesh;
    
    private Vector3f position;
    
    private Skeleton3D skeleton;
    
    private float scale;

    private Vector3f rotation;

    public GameItem(Mesh mesh) {
        this.mesh = mesh;
        position = new Vector3f(0, 0, 0);
        scale = 1;
        rotation = new Vector3f(0, 0, 0);
    }
 

    public GameItem() {
    	mesh = null;
        position = new Vector3f(0, 0, 0);
        scale = 1;
        rotation = new Vector3f(0, 0, 0);
	}

	public GameItem(Skeleton3D skeleton) {
		this();
		this.skeleton = skeleton;
        position = new Vector3f(0, 0, 0);
        scale = 1;
        rotation = new Vector3f(0, 0, 0);
	}


	public Vector3f getPosition() {
        return position;
    }

    public void setPosition(float x, float y, float z) {
        this.position.x = x;
        this.position.y = y;
        this.position.z = z;
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public Vector3f getRotation() {
        return rotation;
    }

    public void setRotation(float x, float y, float z) {
        this.rotation.x = x;
        this.rotation.y = y;
        this.rotation.z = z;
    }
    
    public Mesh getMesh() {
        return mesh;
    }
    
    public Skeleton3D getSkeleton(){
    	return skeleton;
    }
}