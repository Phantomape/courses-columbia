package c2g2.game;

import org.joml.Vector2f;
import org.joml.Vector3f;


import static org.lwjgl.glfw.GLFW.*;

import java.util.Calendar;

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

public class DummyGame implements IGameLogic {

	private int seconds = 0;
	
    private static final float MOUSE_SENSITIVITY = 0.2f;
    
    private static final float SCALE_STEP = 0.01f;
    
    private static final float TRANSLATE_STEP = 0.02f;
    
    private static final float ROTATION_STEP = 0.2f;

    private final Vector3f cameraInc;

    private final Renderer renderer;

    private final Camera camera;

    private GameItem[] gameItems;

    private Vector3f ambientLight;

    private PointLight pointLight;

    private DirectionalLight directionalLight;

    private float lightAngle;

    private static final float CAMERA_POS_STEP = 0.05f;
    
    private int currentObj;

    public DummyGame() {
        renderer = new Renderer();
        camera = new Camera();
        cameraInc = new Vector3f(0.0f, 0.0f, 0.0f);
        lightAngle = -90;
        currentObj=0;
    }

    @Override
    public void init(Window window) throws Exception {
        renderer.init(window);
        float reflectance = 1f;     
        seconds = Calendar.getInstance().get(Calendar.SECOND);
        // NOTE: 
        //   please uncomment following lines to test your OBJ Loader.
        
        Mesh mesh = OBJLoader.loadMesh("src/resources/models/Arc170.obj");
        Mesh tie1m = OBJLoader.loadMesh("src/resources/models/TIE-fighter.obj");
        Mesh tie2m = OBJLoader.loadMesh("src/resources/models/TIE-fighter.obj");
        Mesh moon1m = OBJLoader.loadMesh("src/resources/models/moon.obj");
        
        mesh.scaleMesh(0.01f, 0.01f, 0.01f);
        mesh.translateMesh(new Vector3f(0.0f, 0f, -200f));
        tie1m.scaleMesh(0.05f, 0.05f, 0.05f);
        tie1m.translateMesh(new Vector3f(-0.0f, 5f, -220f));
        tie2m.scaleMesh(0.05f, 0.05f, 0.05f);
        tie2m.translateMesh(new Vector3f(-25.0f, 0f, -230f));
        moon1m.scaleMesh(0.1f, 0.1f, 0.1f);
        moon1m.translateMesh(new Vector3f(-50.0f, 0f, -250f));
        
        Material material = new Material(new Vector3f(0.2f, 0.5f, 0.5f), reflectance);
        
        mesh.setMaterial(material);
        tie1m.setMaterial(material);
        tie2m.setMaterial(material);
        moon1m.setMaterial(material);
        GameItem arc = new GameItem(mesh);
        GameItem tie1i = new GameItem(tie1m);
        GameItem tie2i = new GameItem(tie2m); 
        GameItem moon1i = new GameItem(moon1m);
        gameItems = new GameItem[]{arc, tie1i, tie2i, moon1i};
        
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
    	
    	if(window.isKeyPressed(GLFW_KEY_Q)){
    		//select current object
    		currentObj = currentObj + 1;
    		currentObj = currentObj % gameItems.length;
    	}
    	else if(window.isKeyPressed(GLFW_KEY_W)){
    		//select current object
    		currentObj = currentObj - 1;
    		currentObj = currentObj % gameItems.length;
    	}
    	else if(window.isKeyPressed(GLFW_KEY_E)){
    		//scale object
    		float curr = gameItems[currentObj].getScale();
    		gameItems[currentObj].setScale(curr+SCALE_STEP);
    	}
    	else if(window.isKeyPressed(GLFW_KEY_R)){
    		//scale object
    		float curr = gameItems[currentObj].getScale();
    		gameItems[currentObj].setScale(curr-SCALE_STEP);
    	}
    	else if(window.isKeyPressed(GLFW_KEY_T)){
    		//move object x by step
    		Vector3f curr = gameItems[currentObj].getPosition();
    		gameItems[currentObj].setPosition(curr.x+TRANSLATE_STEP, curr.y, curr.z);
    	}
    	else if(window.isKeyPressed(GLFW_KEY_Y)){
    		//move object x by step
    		Vector3f curr = gameItems[currentObj].getPosition();
    		gameItems[currentObj].setPosition(curr.x-TRANSLATE_STEP, curr.y, curr.z);
    	}
    	else if(window.isKeyPressed(GLFW_KEY_U)){
    		//move object y by step
    		Vector3f curr = gameItems[currentObj].getPosition();
    		gameItems[currentObj].setPosition(curr.x, curr.y+TRANSLATE_STEP, curr.z);
    	}
    	else if(window.isKeyPressed(GLFW_KEY_I)){
    		//move object y by step
    		Vector3f curr = gameItems[currentObj].getPosition();
    		gameItems[currentObj].setPosition(curr.x, curr.y-TRANSLATE_STEP, curr.z);
    	}
    	else if(window.isKeyPressed(GLFW_KEY_O)){
    		//move object z by step
    		Vector3f curr = gameItems[currentObj].getPosition();
    		gameItems[currentObj].setPosition(curr.x, curr.y, curr.z+TRANSLATE_STEP);
    	}
    	else if(window.isKeyPressed(GLFW_KEY_P)){
    		//move object z by step
    		Vector3f curr = gameItems[currentObj].getPosition();
    		gameItems[currentObj].setPosition(curr.x, curr.y, curr.z-TRANSLATE_STEP);
    	}
    	else if(window.isKeyPressed(GLFW_KEY_A)){
    		//rotate object at x axis
    		Vector3f curr = gameItems[currentObj].getRotation();
    		gameItems[currentObj].setRotation(curr.x+ROTATION_STEP, curr.y, curr.z);
    	}
    	else if(window.isKeyPressed(GLFW_KEY_S)){
    		//rotate object at x axis
    		Vector3f curr = gameItems[currentObj].getRotation();
    		gameItems[currentObj].setRotation(curr.x-ROTATION_STEP, curr.y, curr.z);
    	}
    	else if(window.isKeyPressed(GLFW_KEY_D)){
    		//rotate object at x axis
    		Vector3f curr = gameItems[currentObj].getRotation();
    		gameItems[currentObj].setRotation(curr.x, curr.y+ROTATION_STEP, curr.z);
    	}
    	else if(window.isKeyPressed(GLFW_KEY_F)){
    		//rotate object at x axis
    		Vector3f curr = gameItems[currentObj].getRotation();
    		gameItems[currentObj].setRotation(curr.x, curr.y-ROTATION_STEP, curr.z);
    	}
    	else if(window.isKeyPressed(GLFW_KEY_G)){
    		//rotate object at x axis
    		Vector3f curr = gameItems[currentObj].getRotation();
    		gameItems[currentObj].setRotation(curr.x, curr.y, curr.z+ROTATION_STEP);
    	}
    	else if(window.isKeyPressed(GLFW_KEY_H)){
    		//rotate object at x axis
    		Vector3f curr = gameItems[currentObj].getRotation();
    		gameItems[currentObj].setRotation(curr.x, curr.y, curr.z-ROTATION_STEP);
    	}
    	else if(window.isKeyPressed(GLFW_KEY_0)){
    		//rotation by manipulating mesh
    		gameItems[currentObj].getMesh().translateMesh(new Vector3f(0f,0.05f,1f));
    	}
    	else if(window.isKeyPressed(GLFW_KEY_9)){
    		//rotation by manipulating mesh
    		gameItems[currentObj].getMesh().rotateMesh(new Vector3f(1,1,1), 30);
    	}
    	else if(window.isKeyPressed(GLFW_KEY_8)){
    		//rotation by manipulating mesh
    		gameItems[currentObj].getMesh().scaleMesh(1.001f,1.0f,1.0f);
    	}
    	else if(window.isKeyPressed(GLFW_KEY_7)){
    		//rotation by manipulating mesh
    		gameItems[currentObj].getMesh().reflectMesh(new Vector3f(0f,1f,0f), new Vector3f(0f, 1f, 0f));
    	}
    	else if(window.isKeyPressed(GLFW_KEY_1)){
    		//get screenshot
    		renderer.writePNG(window);
    	}
    }

    @Override
    public void update(float interval, MouseInput mouseInput) {
    	action_arc();
    	action_tie1();
    	action_tie2();
    	action_moon();
        // Update camera position
        camera.movePosition(cameraInc.x * CAMERA_POS_STEP, cameraInc.y * CAMERA_POS_STEP, cameraInc.z * CAMERA_POS_STEP);
        
        // Update camera based on mouse            
        if (mouseInput.isLeftButtonPressed()) {
            Vector2f rotVec = mouseInput.getDisplVec();
            System.out.println(rotVec);
            for(GameItem gameItem : gameItems){
            	Vector3f curr = gameItem.getRotation();
            	gameItem.setRotation(curr.x+ rotVec.x * MOUSE_SENSITIVITY, curr.y+rotVec.y * MOUSE_SENSITIVITY, 0);
            }
        }

        // Update directional light direction, intensity and colour
        lightAngle += 1.1f;
        
        if (lightAngle > 90) {
            directionalLight.setIntensity(0);
            if (lightAngle >= 90) {
                lightAngle = -90;
            }
        } else if (lightAngle <= -80 || lightAngle >= 80) {
            float factor = 1 - (float) (Math.abs(lightAngle) - 80) / 10.0f;
            directionalLight.setIntensity(factor);
            directionalLight.getColor().y = Math.max(factor, 0.9f);
            directionalLight.getColor().z = Math.max(factor, 0.5f);
        } else {
            directionalLight.setIntensity(1);
            directionalLight.getColor().x = 1;
            directionalLight.getColor().y = 1;
            directionalLight.getColor().z = 1;
        }
        double angRad = Math.toRadians(lightAngle);
        directionalLight.getDirection().x = (float) Math.sin(angRad);
        directionalLight.getDirection().y = (float) Math.cos(angRad);
    }

    @Override
    public void render(Window window) {
        renderer.render(window, camera, gameItems, ambientLight, pointLight, directionalLight);
    }

    @Override
    public void cleanup() {
        renderer.cleanup();
        for (GameItem gameItem : gameItems) {
            gameItem.getMesh().cleanUp();
        }
    }
    
    public void action_arc(){
    	Calendar cal = Calendar.getInstance();
    	int time = cal.get(Calendar.SECOND);

    	//	Translation\ 	
    	if(time - seconds > 3 && time - seconds < 15){
    		Vector3f t = gameItems[0].getPosition();
    		gameItems[0].setPosition(t.x+3 * TRANSLATE_STEP, t.y, t.z + 3 * TRANSLATE_STEP);
    	} else if(time - seconds >= 15){
    		Vector3f t = gameItems[0].getPosition();
    		gameItems[0].setPosition(t.x - 5 * TRANSLATE_STEP, t.y, t.z + 10 * TRANSLATE_STEP);
    	}
    	
    	//	Rotation
    	if(time - seconds > 3 && time - seconds < 5){
			Vector3f curr = gameItems[0].getRotation();
    		gameItems[0].setRotation(curr.x, curr.y, curr.z + 3 * ROTATION_STEP);
    	} else if (time - seconds >= 5 && time - seconds < 7){
    		Vector3f curr = gameItems[0].getRotation();
    		gameItems[0].setRotation(curr.x , curr.y, curr.z - 3 * ROTATION_STEP);
    	} else if(time - seconds >= 7 && time - seconds < 9){
			Vector3f curr = gameItems[0].getRotation();
    		gameItems[0].setRotation(curr.x, curr.y, curr.z + 6 * ROTATION_STEP);
    	} else if(time - seconds >= 9 && time - seconds < 15 ){
       		Vector3f t = gameItems[0].getPosition();
    		gameItems[0].setPosition(t.x - 5 * TRANSLATE_STEP, t.y - 3 * TRANSLATE_STEP, t.z + 5 * TRANSLATE_STEP);
    		Vector3f curr = gameItems[0].getRotation();
    		gameItems[0].setRotation(curr.x, curr.y, curr.z - 12 * ROTATION_STEP);
    	} else if(time - seconds >= 15 && time - seconds < 18 ){
    		Vector3f curr = gameItems[0].getRotation();
    		gameItems[0].setRotation(curr.x, curr.y, curr.z + 2 * ROTATION_STEP);
    		//	Pose change
    		gameItems[0].getMesh().translateMesh(new Vector3f(0f, 0f, 200f));
        	gameItems[0].getMesh().rotateMesh(new Vector3f(0, -1, 1f), 0.25f);
            gameItems[0].getMesh().translateMesh(new Vector3f(0f, 0f, -200f));
    	} else if(time - seconds >= 18 && time - seconds < 20 ){
    		Vector3f curr = gameItems[currentObj].getRotation();
    		gameItems[currentObj].setRotation(curr.x, curr.y, curr.z - 4 * ROTATION_STEP);
    	}
    }

    /*
     * This part controls the action of TIE-fighter #1
     */
    public void action_tie1(){
    	Calendar cal = Calendar.getInstance();
    	int time = cal.get(Calendar.SECOND);
    	
    	//	Translation\
    	if(time - seconds > 3 && time -seconds < 10){
    		Vector3f t = gameItems[1].getPosition();
    		gameItems[1].setPosition(t.x+3 * TRANSLATE_STEP, t.y, t.z + 3 * TRANSLATE_STEP);
    	} else if(time - seconds >= 10){
    		Vector3f t = gameItems[1].getPosition();
    		gameItems[1].setPosition(t.x - 5 * TRANSLATE_STEP, t.y - TRANSLATE_STEP, t.z + 10 * TRANSLATE_STEP);
    		
    	}
    	//	Rotation
    	if(time - seconds >= 5 && time - seconds < 8){
			Vector3f curr = gameItems[1].getRotation();
    		gameItems[1].setRotation(curr.x, curr.y, curr.z + ROTATION_STEP);
    	} else if (time - seconds >= 18 && time - seconds < 10){
    		Vector3f curr = gameItems[1].getRotation();
    		gameItems[1].setRotation(curr.x , curr.y, curr.z - ROTATION_STEP);
    	} else if(time - seconds >= 10 && time - seconds < 13 ){
    		Vector3f t = gameItems[1].getPosition();
    		gameItems[1].setPosition(t.x + 2 * TRANSLATE_STEP, t.y - 3 * TRANSLATE_STEP, t.z);
    		Vector3f curr = gameItems[1].getRotation();
    		gameItems[1].setRotation(curr.x, curr.y, curr.z+ 10 * ROTATION_STEP);
    	} else if(time - seconds >= 13){
    		//	Pose change
    		gameItems[1].getMesh().translateMesh(new Vector3f(0f, 0f, 220f));
        	gameItems[1].getMesh().rotateMesh(new Vector3f(0, 0, 1f), 0.5f);
            gameItems[1].getMesh().translateMesh(new Vector3f(0f, 0f, -220f));
    	}
    	
    }

    /*
     * This part controls the action of TIE-fighter #2
     */
    public void action_tie2(){
    	Calendar cal = Calendar.getInstance();
    	int time = cal.get(Calendar.SECOND);
    	
    	//	Translation\
    	if(time - seconds > 3 && time - seconds <= 12){
    		Vector3f t = gameItems[2].getPosition();
    		gameItems[2].setPosition(t.x + 7 * TRANSLATE_STEP, t.y - TRANSLATE_STEP, t.z + 5 * TRANSLATE_STEP);
    	} else if (time - seconds > 12){
    		Vector3f t = gameItems[2].getPosition();
    		gameItems[2].setPosition(t.x - 5 * TRANSLATE_STEP, t.y - TRANSLATE_STEP, t.z + 10 * TRANSLATE_STEP);
    	}
    	
    	//	Rotation
    	if(time - seconds > 5 && time - seconds < 10){
			Vector3f curr = gameItems[2].getRotation();
    		gameItems[2].setRotation(curr.x, curr.y, curr.z - ROTATION_STEP);
    	} else if (time - seconds >= 13 && time - seconds < 15){
    		Vector3f curr = gameItems[2].getRotation();
    		gameItems[2].setRotation(curr.x , curr.y, curr.z + ROTATION_STEP);
    	} else if(time - seconds >= 15 && time - seconds < 17){
			Vector3f curr = gameItems[2].getRotation();
    		gameItems[2].setRotation(curr.x, curr.y, curr.z - ROTATION_STEP);
    	} else if(time - seconds >= 17 && time - seconds < 20 ){
    		//	Pose change
    		gameItems[2].getMesh().translateMesh(new Vector3f(0f, 0f, 230f));
        	gameItems[2].getMesh().rotateMesh(new Vector3f(0, 0, 1f), 0.5f);
            gameItems[2].getMesh().translateMesh(new Vector3f(0f, 0f, -230f));
        }    	
    	
    }
    
    /*
     * This part controls the action of moon, in this case, rotate around itself
     */
    public void action_moon(){
    	gameItems[3].getMesh().translateMesh(new Vector3f(50.0f, 0f, 250f));
    	gameItems[3].getMesh().rotateMesh(new Vector3f(0, 1, 0.3f), 0.01f);
        gameItems[3].getMesh().translateMesh(new Vector3f(-50.0f, 0f, -250f));
    }
    
    
}
