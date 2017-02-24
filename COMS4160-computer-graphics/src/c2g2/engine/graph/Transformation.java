package c2g2.engine.graph;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import c2g2.engine.GameItem;

public class Transformation {

    private final Matrix4f projectionMatrix;
    
    private final Matrix4f viewMatrix;
    
    private final Matrix4f modelMatrix;
    
    private final Matrix4f orthographicMatrix;

    public Transformation() {
        projectionMatrix = new Matrix4f();
        viewMatrix = new Matrix4f();
        modelMatrix = new Matrix4f();
        orthographicMatrix = new Matrix4f();
    }

    public final Matrix4f getOrthographicMatrix(float left, float right, float bottom, float top, float near, float far){
    	orthographicMatrix.identity();
		orthographicMatrix.m00(2.0f / (right - left));
		orthographicMatrix.m11(2.0f / (top - bottom));
		orthographicMatrix.m22(2.0f / (near - far));
		orthographicMatrix.m03((left + right) / (left - right));
		orthographicMatrix.m13((bottom + top) / (bottom - top));
		orthographicMatrix.m23((far + near) / (far - near));
		//result.elements[0 + 3 * 4] = (left + right) / (left - right);
		//result.elements[1 + 3 * 4] = (bottom + top) / (bottom - top);
		//result.elements[2 + 3 * 4] = (far + near) / (far - near);
		
		return orthographicMatrix;
    }
    
    public final Matrix4f getProjectionMatrix(float fov, float width, float height, float zNear, float zFar) {
        projectionMatrix.identity();
    	//// --- student code ---
        
        
        float aspectRatio = width/height;
        
        float scale = (float)((1.0f / Math.tan(fov * 0.5)) );
        
        projectionMatrix.m00(scale / aspectRatio);
        projectionMatrix.m11(scale);
        projectionMatrix.m22((zFar + zNear) / (zNear - zFar));
        projectionMatrix.m23(-1.0f);
        projectionMatrix.m32(2 * zFar * zNear / (zNear - zFar));
        projectionMatrix.m33(0.0f);

        return projectionMatrix;
    }
    
    public Matrix4f getViewMatrix(Camera camera) {
    	Vector3f cameraPos = camera.getPosition();
    	Vector3f cameraTarget = camera.getTarget();
    	Vector3f up = camera.getUp();
        viewMatrix.identity();
    	//// --- student code ---
        
        Vector3f g = new Vector3f(cameraPos.x - cameraTarget.x, cameraPos.y - cameraTarget.y, cameraPos.z - cameraTarget.z);
        g.normalize();
        
        Vector3f l = new Vector3f();
        up.cross(g, l);
        l.normalize();
        
        Vector3f v = new Vector3f();
        g.cross(l, v);
        
        viewMatrix.m00(l.x);viewMatrix.m01(v.x);viewMatrix.m02(g.x);
        viewMatrix.m10(l.y);viewMatrix.m11(v.y);viewMatrix.m12(g.y);
        viewMatrix.m20(l.z);viewMatrix.m21(v.z);viewMatrix.m22(g.z);
        viewMatrix.m30(-(l.dot(cameraPos)));
        viewMatrix.m31(-(v.dot(cameraPos)));
        viewMatrix.m32(-(g.dot(cameraPos)));
        
        return viewMatrix;
    }
    
    public Matrix4f getModelMatrix(GameItem gameItem){
        Vector3f rotation = gameItem.getRotation();
        Vector3f position = gameItem.getPosition();
        modelMatrix.identity();
    	//// --- student code ---
        
        float scale = gameItem.getScale();
        modelMatrix.translate(position);
        modelMatrix.rotateX((float)Math.toRadians(rotation.x));
        modelMatrix.rotateY((float)Math.toRadians(rotation.y));
        modelMatrix.rotateZ((float)Math.toRadians(rotation.z));
        modelMatrix.scale(scale);
     
        return modelMatrix;
    }

    public Matrix4f getModelViewMatrix(GameItem gameItem, Matrix4f viewMatrix) {
        Matrix4f viewCurr = new Matrix4f(viewMatrix);
        return viewCurr.mul(getModelMatrix(gameItem));
    }
}