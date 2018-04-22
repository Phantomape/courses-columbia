package c2g2.game;

import java.util.Calendar;

import org.joml.Vector2d;
import org.joml.Vector2f;
import org.joml.Vector3f;

import c2g2.engine.Camera;
import c2g2.engine.GameItem;
import c2g2.engine.IGameLogic;
import c2g2.engine.MouseInput;
import c2g2.engine.Window;
import c2g2.engine.XMLLoader;
import c2g2.kinematics3D.Renderer3D;
import c2g2.kinematics3D.Skeleton3D;


public class Game implements IGameLogic{
    private static final float MOUSE_SENSITIVITY = 0.2f;
    private static final float CAMERA_POS_STEP = 0.05f;
    private final Vector3f cameraInc;
    private final Camera camera;
    private final Renderer3D renderer;
	private int seconds = 0;
    private GameItem[] gameItems = null;
    
	public Game(){
        cameraInc = new Vector3f(0.0f, 0.0f, 0.0f);
		camera = new Camera();
		renderer = new Renderer3D();
	}

	@Override
	public void init(Window window) throws Exception {
		renderer.init(window);
	    float reflectance = 1f;     
	    seconds = Calendar.getInstance().get(Calendar.SECOND);
	    //Skeleton3D skeleton = XMLLoader.loadXML("src/resources/models/object.xml");
	    //skeleton.init();
        //GameItem obj = new GameItem(skeleton);
        //gameItems = new GameItem[]{obj};
	}

	@Override
	public void input(Window window, MouseInput mouseInput) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void update(Window window, float interval, MouseInput mouseInput) {
        // Update camera position
        camera.movePosition(cameraInc.x * CAMERA_POS_STEP, cameraInc.y * CAMERA_POS_STEP, cameraInc.z * CAMERA_POS_STEP);
        
        // Update camera based on mouse            
        if (mouseInput.isLeftButtonPressed()) {
            Vector2f rotVec = mouseInput.getDisplVec();
            System.out.println(rotVec);

            if(gameItems != null && gameItems.length != 0){
	            for(GameItem gameItem : gameItems){
	            	Vector3f curr = gameItem.getRotation();
	            	gameItem.setRotation(curr.x+ rotVec.x * MOUSE_SENSITIVITY, curr.y+rotVec.y * MOUSE_SENSITIVITY, 0);
	            }
            }
            
        }
		
        if (mouseInput.isRightButtonPressed()) {
        	System.out.println("Right mouse clicked");
        	Vector2d pos = mouseInput.getCurrentPos();
        	System.out.println("Pos:(" + pos.x + "," + pos.y + ")");
        }
	}

	@Override
	public void render(Window window) {
		renderer.render(window, camera, gameItems);
	}

	@Override
	public void cleanup() {
		// TODO Auto-generated method stub
	}

}
