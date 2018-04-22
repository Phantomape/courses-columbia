package c2g2.engine;

import org.joml.Matrix4f;
import org.joml.Vector2d;
import org.joml.Vector2f;
import org.joml.Vector3d;
import org.joml.Vector4f;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL14.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.glfw.GLFW.*;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWCursorEnterCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;

import c2g2.game.DefaultRenderer;

public class MouseInput {

    private final Vector2d previousPos;

    private final Vector2d currentPos;

    private final Vector2f displVec;

    private boolean inWindow = false;

    private boolean leftButtonPressed = false;

    private boolean rightButtonPressed = false;

    private GLFWCursorPosCallback cursorPosCallback;
    
    private GLFWCursorEnterCallback cursorEnterCallback;
    
    private GLFWMouseButtonCallback mouseButtonCallback;

    public MouseInput() {
        previousPos = new Vector2d(-1, -1);
        currentPos = new Vector2d(0, 0);
        displVec = new Vector2f();
    }

    public void init(Window window) {
        glfwSetCursorPosCallback(window.getWindowHandle(), cursorPosCallback = new GLFWCursorPosCallback() {
            @Override
            public void invoke(long window, double xpos, double ypos) {
                currentPos.x = xpos;
                currentPos.y = ypos;
            }
        });
        glfwSetCursorEnterCallback(window.getWindowHandle(), cursorEnterCallback = new GLFWCursorEnterCallback() {
            @Override
            public void invoke(long window, boolean entered) {
                inWindow = entered;
            }
        });
        glfwSetMouseButtonCallback(window.getWindowHandle(), mouseButtonCallback = new GLFWMouseButtonCallback() {
            @Override
            public void invoke(long window, int button, int action, int mods) {
                leftButtonPressed = button == GLFW_MOUSE_BUTTON_1 && action == GLFW_PRESS;
                rightButtonPressed = button == GLFW_MOUSE_BUTTON_2 && action == GLFW_PRESS;
            }
        });
    }

    public Vector2f getDisplVec() {
        return displVec;
    }

    public void input(Window window) {
        displVec.x = 0;
        displVec.y = 0;
        if (previousPos.x > 0 && previousPos.y > 0 && inWindow) {
            double deltax = currentPos.x - previousPos.x;
            double deltay = currentPos.y - previousPos.y;
            boolean rotateX = deltax != 0;
            boolean rotateY = deltay != 0;
            if (rotateX) {
                displVec.y = (float) deltax;
            }
            if (rotateY) {
                displVec.x = (float) deltay;
            }
        }
        previousPos.x = currentPos.x;
        previousPos.y = currentPos.y;
    }

    public boolean isLeftButtonPressed() {
        return leftButtonPressed;
    }

    public boolean isRightButtonPressed() {
        return rightButtonPressed;
    }

    public Vector2d getCurrentPos(){
    	return currentPos;
    }
    
    public Vector3d get3DCoord(Window window, DefaultRenderer renderer, Camera camera, GameItem g){
        int wdwWitdh = window.getWidth();
        int wdwHeight = window.getHeight();
        
        float x = (float)(2 * currentPos.x) / (float)wdwWitdh - 1.0f;
        float y = 1.0f - (float)(2 * currentPos.y) / (float)wdwHeight;
        float z = -1.0f;
        
        Transformation t = renderer.getTransformation();
        Matrix4f projectionMatrix = t.getProjectionMatrix(renderer.getFOV(), window.getWidth(), window.getHeight(), renderer.getZNEAR(), renderer.getZFAR());
        Matrix4f invProjectionMatrix = new Matrix4f();
        invProjectionMatrix.set(projectionMatrix);
        invProjectionMatrix.invert();
        
        Vector4f tmpVec = new Vector4f();
        tmpVec.set(x, y, z, 1.0f);
        tmpVec.mul(invProjectionMatrix);
        tmpVec.z = -1.0f;
        tmpVec.w = 0.0f;
        
        Matrix4f viewMatrix = t.getViewMatrix(camera);
        Matrix4f invViewMatrix = new Matrix4f();
        invViewMatrix.set(viewMatrix);
        invViewMatrix.invert();
        tmpVec.mul(invViewMatrix);
        
        
        Matrix4f modelMatrix = t.getModelMatrix(g);
        Matrix4f invModelMatrix = new Matrix4f();
        invModelMatrix.set(modelMatrix);
        invModelMatrix.invert();
        //tmpVec.mul(invModelMatrix);
        
        
        Vector3d res = new Vector3d(tmpVec.x, tmpVec.y, tmpVec.z);
        
		return res;
    	
    }
}
