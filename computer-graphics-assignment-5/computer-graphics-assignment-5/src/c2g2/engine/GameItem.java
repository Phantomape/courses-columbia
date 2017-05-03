package c2g2.engine;

import org.joml.Vector3f;

import c2g2.kinematics3D.Skeleton3D;

public class GameItem {
	private Skeleton3D skeleton;
    private Vector3f position;
    private Vector3f rotation;
    private float scale;

    public GameItem() {
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

    
    public Skeleton3D getSkeleton() {
        return skeleton;
    }
    
    public void setSkeleton(Skeleton3D skeleton){
    	this.skeleton = skeleton;
    }
}
