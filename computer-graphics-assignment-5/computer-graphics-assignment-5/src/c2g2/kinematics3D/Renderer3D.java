package c2g2.kinematics3D;

import c2g2.engine.Camera;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glViewport;

import java.nio.file.Files;
import java.nio.file.Paths;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import c2g2.engine.GameItem;
import c2g2.engine.ShaderProgram;
import c2g2.engine.Transformation;
import c2g2.engine.Window;

public class Renderer3D {
	private static final float FOV = (float) Math.toRadians(60.0f);
    private static final float Z_NEAR = 0.01f;
    private static final float Z_FAR = 1000.f;
    private final float specularPower;
    private Transformation transformation;
    private ShaderProgram shaderProgram;
    
    public Renderer3D(){
        transformation = new Transformation();
        specularPower = 10f;
    }
    
    public void init(Window window) throws Exception {
        shaderProgram = new ShaderProgram();
        shaderProgram.createVertexShader(new String(Files.readAllBytes(Paths.get("src/resources/shaders/skeleton_vertex.vs"))));
        shaderProgram.createFragmentShader(new String(Files.readAllBytes(Paths.get("src/resources/shaders/skeleton_fragment.fs"))));
        shaderProgram.link();
        
        shaderProgram.createUniform("projectionMatrix");
        shaderProgram.createUniform("modelViewMatrix");
    }
    
    public void render(Window window, Camera camera, GameItem[] gameItems) {
        clear();
        if ( window.isResized() ) {
        	System.out.println("isresized");
            glViewport(0, 0, window.getWidth(), window.getHeight());
        	int side = window.getWidth();
        	if(window.getHeight()>side){
        		side = window.getHeight();
        	}
       		glViewport((window.getWidth()-side)/2, (window.getHeight()-side)/2, side, side);
            window.setResized(false);
        }
        glViewport(0, 0, window.getWidth()*2, window.getHeight()*2);

        shaderProgram.bind();
        
        Matrix4f projectionMatrix = transformation.getProjectionMatrix(FOV, window.getWidth(), window.getHeight(), Z_NEAR, Z_FAR);
        shaderProgram.setUniform("projectionMatrix", projectionMatrix);
        Matrix4f viewMatrix = transformation.getViewMatrix(camera);

        for(GameItem gameItem : gameItems) {
            Skeleton3D skeleton = gameItem.getMesh().getSkeleton();
            Matrix4f modelViewMatrix = transformation.getModelViewMatrix(gameItem, viewMatrix);
            shaderProgram.setUniform("modelViewMatrix", modelViewMatrix);
            skeleton.render();
        }
        

        shaderProgram.unbind();
    }
    
    public void clear() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }
    
    public float getFOV(){
    	return FOV;
    }
}
