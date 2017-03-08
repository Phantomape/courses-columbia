package c2g2.game;

import org.joml.Vector2f;
import org.joml.Vector3f;


import static org.lwjgl.glfw.GLFW.*;

import java.util.Calendar;

import c2g2.engine.GameItem;
import c2g2.engine.IGameLogic;
import c2g2.engine.MouseInput;
import c2g2.engine.Scene;
import c2g2.engine.SkyBox;
import c2g2.engine.Terrain;
import c2g2.engine.Window;
import c2g2.engine.graph.Camera;
import c2g2.engine.graph.DirectionalLight;
import c2g2.engine.graph.Material;
import c2g2.engine.graph.Mesh;
import c2g2.engine.graph.OBJLoader;
import c2g2.engine.graph.PointLight;

public class TerrainGame implements IGameLogic {

	private int seconds = 0;
	
    private static final float MOUSE_SENSITIVITY = 0.2f;
    
    private static final float SCALE_STEP = 0.01f;
    
    private static final float TRANSLATE_STEP = 0.01f;
    
    private static final float ROTATION_STEP = 0.1f;

    private final Vector3f cameraInc;

    private final Renderer renderer;

    private final Camera camera;

    private GameItem[] gameItems;

    private Vector3f ambientLight;

    private PointLight pointLight;

    private DirectionalLight directionalLight;
    
    private Terrain terrain;

    private Scene scene;
    
    private float lightAngle;

    private static final float CAMERA_POS_STEP = 0.05f;
    
    private int currentObj;

    public TerrainGame() {
        renderer = new Renderer();
        camera = new Camera();
        cameraInc = new Vector3f(0.0f, 0.0f, 0.0f);
        lightAngle = -90;
        currentObj=0;
    }

    @Override
    public void init(Window window) throws Exception {
        renderer.init(window);
        scene = new Scene();
        float skyBoxScale = 50.0f;        
        float terrainScale = 10;
        int terrainSize = 3;
        float minY = -0.1f;
        float maxY = 0.1f;
        int textInc = 40;
        terrain = new Terrain(terrainSize, terrainScale, minY, maxY, "src/resources/textures/heightmap.png", "src/resources/textures/terrain.png", textInc);
        scene.setGameItems(terrain.getGameItems());
        

        SkyBox skyBox = new SkyBox("src/resources/models/skybox.obj", "src/resources/textures/skybox.png");
        skyBox.setScale(skyBoxScale);
        scene.setSkyBox(skyBox);
        

        camera.getPosition().x = 0.0f;

        camera.getPosition().y = 5.0f;

        camera.getPosition().z = 0.0f;

        camera.getRotation().x = 90;
        //
    }

    @Override
    public void input(Window window, MouseInput mouseInput) {

        cameraInc.set(0, 0, 0);

        if (window.isKeyPressed(GLFW_KEY_W)) {

            cameraInc.z = -1;

        } else if (window.isKeyPressed(GLFW_KEY_S)) {

            cameraInc.z = 1;

        }

        if (window.isKeyPressed(GLFW_KEY_A)) {

            cameraInc.x = -1;

        } else if (window.isKeyPressed(GLFW_KEY_D)) {

            cameraInc.x = 1;

        }

        if (window.isKeyPressed(GLFW_KEY_Z)) {

            cameraInc.y = -1;

        } else if (window.isKeyPressed(GLFW_KEY_X)) {

            cameraInc.y = 1;

        }
    }

    @Override
    public void update(float interval, MouseInput mouseInput) {
        // Update camera position
        camera.movePosition(cameraInc.x * CAMERA_POS_STEP, cameraInc.y * CAMERA_POS_STEP, cameraInc.z * CAMERA_POS_STEP);

        // Update camera based on mouse            
        if (mouseInput.isLeftButtonPressed()) {
            Vector2f rotVec = mouseInput.getDisplVec();
            System.out.println(rotVec);
            Vector3f curr = gameItems[0].getRotation();
            gameItems[0].setRotation(curr.x+ rotVec.x * MOUSE_SENSITIVITY, curr.y+rotVec.y * MOUSE_SENSITIVITY, 0);
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

}
