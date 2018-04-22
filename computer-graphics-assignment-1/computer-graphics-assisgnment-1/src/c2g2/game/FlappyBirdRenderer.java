package c2g2.game;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glViewport;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.joml.Matrix4f;

import c2g2.engine.GameItem;
import c2g2.engine.Window;
import c2g2.engine.graph.Camera;
import c2g2.engine.graph.Mesh;
import c2g2.engine.graph.ShaderProgram;
import c2g2.engine.graph.Transformation;

public class FlappyBirdRenderer {

	private int id;
	
	private static final float FOV = (float) Math.toRadians(60.0f);
	
    private static final float Z_NEAR = 0.01f;

    private static final float Z_FAR = 1000.f;
	
    private final Transformation transformation;

    private ShaderProgram sp;
    
    private final float specularPower;
    
	public FlappyBirdRenderer(){
        transformation = new Transformation();
        specularPower = 10f;
        id = 0; 
	}
	
	/*
	public FlappyBirdRenderer(String vertex, String fragment) throws IOException, Exception{
        transformation = new Transformation();
        specularPower = 10f;
        sp = new ShaderProgram();
        sp.createVertexShader(new String(Files.readAllBytes(Paths.get(vertex))));
        sp.createFragmentShader(new String(Files.readAllBytes(Paths.get(fragment))));
        sp.link();
        id = sp.programId;
	}*/
	
	public void init(Window window, String vertex, String fragment) throws Exception{
        sp = new ShaderProgram();
        sp.createVertexShader(new String(Files.readAllBytes(Paths.get(vertex))));
        sp.createFragmentShader(new String(Files.readAllBytes(Paths.get(fragment))));
        sp.link();
        sp.createUniform("pr_matrix");
        sp.createUniform("vw_matrix");
        id = sp.programId;
	}
	
    public void clear() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }
    
	public void render(Window window, Camera camera, GameItem[] gameItems){
        clear();
        if ( window.isResized() ) {
        	System.out.println("isresized");
            window.setResized(false);
        }
        
        glViewport(0, 0, window.getWidth()*2, window.getHeight()*2);
        sp.bind();
        
        //Matrix4f projectionMatrix = transformation.getProjectionMatrix(FOV, window.getWidth(), window.getHeight(), Z_NEAR, Z_FAR);
        Matrix4f projectionMatrix = transformation.getOrthographicMatrix(-10.0f, 10.0f, -10.0f * 9.0f / 16.0f, 10.0f * 9.0f / 16.0f, -1.0f, 1.0f);
        sp.setUniform("pr_matrix", projectionMatrix);
        Matrix4f viewMatrix = transformation.getViewMatrix(camera);
        sp.setUniform("vw_matrix", viewMatrix);
        for(GameItem gameItem : gameItems) {
            Mesh mesh = gameItem.getMesh();
            // Set model view matrix for this item
            //Matrix4f modelViewMatrix = transformation.getModelViewMatrix(gameItem, viewMatrix);
            mesh.render();
        }
        
	}
	
	public void cleanup(){ 
		if (sp != null) {
			sp.cleanup();
		}
	}
}
