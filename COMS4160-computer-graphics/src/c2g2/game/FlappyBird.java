package c2g2.game;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.glViewport;

import java.io.IOException;

import c2g2.engine.GameItem;
import c2g2.engine.IGameLogic;
import c2g2.engine.MouseInput;
import c2g2.engine.Window;
import c2g2.engine.graph.Camera;
import c2g2.engine.graph.DirectionalLight;
import c2g2.engine.graph.Material;
import c2g2.engine.graph.Mesh;
import c2g2.engine.graph.OBJLoader;
import c2g2.engine.graph.PointLight;

public class FlappyBird implements IGameLogic {
	
    private int direction = 0;
    private float color = 0.0f;

    private static final float MOUSE_SENSITIVITY = 0.2f;
    
    private static final float SCALE_STEP = 0.01f;
    
    private static final float TRANSLATE_STEP = 0.01f;
    
    private static final float ROTATION_STEP = 0.3f;

    private final Vector3f cameraInc;

    private final FlappyBirdRenderer bg;//, bird, pipe, fade;
    
    private final Camera camera;

    private GameItem[] gameItems;

    private Vector3f ambientLight;

    private PointLight pointLight;

    private DirectionalLight directionalLight;

    private float lightAngle;

    private static final float CAMERA_POS_STEP = 0.05f;
    
    private int currentObj;

	
	public FlappyBird() throws IOException, Exception{
		bg = new FlappyBirdRenderer();
		//bg = new FlappyBirdRenderer("src/resources/shaders/bg.vert", "src/resources/shaders/bg.frag");
		//bird = new FlappyBirdRenderer("src/resources/shaders/bg.vert", "src/resources/shaders/bg.frag");
		//pipe = new FlappyBirdRenderer("src/resources/shaders/bg.vert", "src/resources/shaders/bg.frag");
		//fade = new FlappyBirdRenderer("src/resources/shaders/bg.vert", "src/resources/shaders/bg.frag");
        camera = new Camera();
        cameraInc = new Vector3f(0.0f, 0.0f, 0.0f);
        lightAngle = -90;
        currentObj=0;
	}
	
	@Override
	public void init(Window window) throws Exception {
		// TODO Auto-generated method stub
		bg.init(window, "src/resources/shaders/bg.vert", "src/resources/shaders/bg.frag");
        float reflectance = 1f;        
		float[] vertices = new float[] {
				-10.0f, -10.0f * 9.0f / 16.0f, 0.0f,
				-10.0f,  10.0f * 9.0f / 16.0f, 0.0f,
				  0.0f,  10.0f * 9.0f / 16.0f, 0.0f,
				  0.0f, -10.0f * 9.0f / 16.0f, 0.0f
			};
		
		int[] indices = new int[] {
				0, 1, 2,
				2, 3, 0
			};
		
		float[] tcs = new float[] {
				0, 1,
				0, 0,
				1, 0,
				1, 1
			};
		
		Mesh mesh = new Mesh(vertices, tcs, indices);
        Material material = new Material(new Vector3f(0.2f, 0.5f, 0.5f), reflectance);
        

        mesh.setMaterial(material);
        GameItem gameItem = new GameItem(mesh);

        gameItems = new GameItem[]{gameItem};

        ambientLight = new Vector3f(0.3f, 0.3f, 0.3f);
        Vector3f lightColour = new Vector3f(1, 1, 1);
        Vector3f lightPosition = new Vector3f(0, 0, 1);
        float lightIntensity = 1.0f;
        pointLight = new PointLight(lightColour, lightPosition, lightIntensity);
        PointLight.Attenuation att = new PointLight.Attenuation(0.0f, 0.0f, 1.0f);
        pointLight.setAttenuation(att);

        lightPosition = new Vector3f(-1, 0, 0);
        lightColour = new Vector3f(1, 1, 1);
        directionalLight = new DirectionalLight(lightColour, lightPosition, lightIntensity);
	}

	@Override
	public void input(Window window, MouseInput mouseInput) {
		// TODO Auto-generated method stub
        if(window.isKeyPressed(GLFW_KEY_SPACE)){
        	System.out.println("FLAP!");
        }
        
        if ( window.isKeyPressed(GLFW_KEY_UP) ) {
            direction = 1;
        } else if ( window.isKeyPressed(GLFW_KEY_DOWN) ) {
            direction = -1;
        } else {
            direction = 0;
        }
        

	}

	@Override
	public void update(float interval, MouseInput mouseInput) {
		// TODO Auto-generated method stub
        color += direction * 0.01f;
        if (color > 1) {
            color = 1.0f;
        } else if ( color < 0 ) {
            color = 0.0f;
        }
	}

	@Override
	public void render(Window window) {
		// TODO Auto-generated method stub
        /*if ( window.isResized() ) {
            glViewport(0, 0, window.getWidth(), window.getHeight());
        	//glViewport(0, 0, window.getWidth()*2, window.getHeight()*2);
            window.setResized(false);
        }
        window.setClearColor(color, color, color, 0.0f);
        */
        bg.render(window, camera, gameItems);

	}

	@Override
	public void cleanup() {
		// TODO Auto-generated method stub
        bg.cleanup();
        for (GameItem gameItem : gameItems) {
            gameItem.getMesh().cleanUp();
        }
	}

}
