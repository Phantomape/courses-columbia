package c2g2.game;

import org.joml.Vector2d;
import org.joml.Vector2f;
import org.joml.Vector3d;
import org.joml.Vector3f;


import static org.lwjgl.glfw.GLFW.*;

import java.util.ArrayList;

import c2g2.engine.GameItem;
import c2g2.engine.IGameLogic;
import c2g2.engine.MouseInput;
import c2g2.engine.Window;
import c2g2.engine.XMLLoader;
import c2g2.kinematics.ForwardKinematics;
import c2g2.kinematics3D.ForwardKinematics3D;
import c2g2.kinematics3D.InverseKinematics3D;
import c2g2.kinematics3D.Joint3D;
import c2g2.kinematics3D.Skeleton3D;
import c2g2.animation.AnimationClip;
import c2g2.animation.AnimationSample;
import c2g2.engine.Camera;
import c2g2.engine.DirectionalLight;
import c2g2.engine.Material;
import c2g2.engine.Mesh;
import c2g2.engine.OBJLoader;
import c2g2.engine.PointLight;
import c2g2.engine.Timer;

public class TestGame implements IGameLogic{

    private static final float MOUSE_SENSITIVITY = 0.2f;

    private static final float SCALE_STEP = 0.01f;

    private static final float TRANSLATE_STEP = 0.01f;

    private static final float ROTATION_STEP = 0.3f;

    private final Vector3f cameraInc;

    private final DefaultRenderer renderer;

    private final Camera camera;

    private GameItem[] gameItems;

    private Vector3f ambientLight;

    private PointLight pointLight;

    private DirectionalLight directionalLight;
    
    private AnimationClip animationClip;
    
    private Skeleton3D currSkeleton;
    
    private Timer timer;

    private float lightAngle;

    private static final float CAMERA_POS_STEP = 0.05f;

    private int currentObj;

    public TestGame() {
        renderer = new DefaultRenderer();
        camera = new Camera();
        cameraInc = new Vector3f(0.0f, 0.0f, 0.0f);
        lightAngle = -90;
        currentObj=0;
        animationClip = new AnimationClip();
    }

    @Override
    public void init(Window window, Timer t) throws Exception {
        renderer.init(window);
        
        timer = t;
        
        float reflectance = 1f;
        Material material = new Material(new Vector3f(0.2f, 0.5f, 0.5f), reflectance);
        
        //	Mesh
        /*
        Mesh mesh = OBJLoader.loadMesh("src/resources/models/bunny.obj");
        mesh.setSkeleton();
        mesh.setMaterial(material);
        GameItem meshItem = new GameItem(mesh);
        meshItem.setPosition(0.0f, 0.0f, -2.0f);
        gameItems = new GameItem[]{meshItem};
        */
        
        
        //	Skeleton
        Skeleton3D skeleton = XMLLoader.loadXML("src/resources/models/object.xml");
        //Skeleton3D skeleton = XMLLoader.loadXML("src/resources/models/object.xml");
	    skeleton.init();
        skeleton.setMaterial(material);
        GameItem skeletonItem = new GameItem(skeleton);
        skeletonItem.setPosition(0.0f, 0.0f, -2.0f);
        
        Skeleton3D xaxis = XMLLoader.loadXML("src/resources/models/x.xml");
        xaxis.init();
        xaxis.setMaterial(new Material(new Vector3f(0.0f, 0.0f, 1.0f), reflectance));
        GameItem x_Axis = new GameItem(xaxis);
        x_Axis.setPosition(0.0f, 0.0f, -2.0f);
        
        Skeleton3D yaxis = XMLLoader.loadXML("src/resources/models/y.xml");
        yaxis.init();
        yaxis.setMaterial(new Material(new Vector3f(0.0f, 1.0f, 0.0f), reflectance));
        GameItem y_Axis = new GameItem(yaxis);
        y_Axis.setPosition(0.0f, 0.0f, -2.0f);
        
        Skeleton3D zaxis = XMLLoader.loadXML("src/resources/models/z.xml");
        zaxis.init();
        zaxis.setMaterial(new Material(new Vector3f(1.0f, 0.0f, 0.0f), reflectance));
        GameItem z_Axis = new GameItem(zaxis);
        z_Axis.setPosition(0.0f, 0.0f, -2.0f);
        
        
        gameItems = new GameItem[]{skeletonItem, x_Axis, y_Axis, z_Axis};
        currSkeleton = skeleton;

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

    	if(window.isKeyPressed(GLFW_KEY_A)){
    		ForwardKinematics3D fk = new ForwardKinematics3D(currSkeleton);
    		fk.init();
    		fk.selectParentJoint(0);
    		fk.selectChildJoint(1);
    		fk.updateStates(0.0, 1.0);
    		currSkeleton.showLinks();
    		System.out.println();
    	}
    	else if(window.isKeyPressed(GLFW_KEY_S)){
    		ForwardKinematics3D fk = new ForwardKinematics3D(currSkeleton);
    		fk.init();
    		fk.selectParentJoint(0);
    		fk.selectChildJoint(2);
    		fk.updateStates(0.0, 1.0);
    	}
    	else if(window.isKeyPressed(GLFW_KEY_D)){
    		ForwardKinematics3D fk = new ForwardKinematics3D(currSkeleton);
    		fk.init();
    		fk.selectParentJoint(2);
    		fk.selectChildJoint(3);
    		fk.updateStates(0.0, 1.0);
    	}
    	else if(window.isKeyPressed(GLFW_KEY_F)){
    		ForwardKinematics3D fk = new ForwardKinematics3D(currSkeleton);
    		fk.init();
    		fk.selectParentJoint(3);
    		fk.selectChildJoint(4);
    		fk.updateStates(0.0, 1.0);
    	}
    	else if(window.isKeyPressed(GLFW_KEY_G)){
    		ForwardKinematics3D fk = new ForwardKinematics3D(currSkeleton);
    		fk.init();
    		fk.selectParentJoint(2);
    		fk.selectChildJoint(5);
    		fk.updateStates(0.0, 1.0);
    	}    	
    	else if(window.isKeyPressed(GLFW_KEY_H)){
    		ForwardKinematics3D fk = new ForwardKinematics3D(currSkeleton);
    		fk.init();
    		fk.selectParentJoint(5);
    		fk.selectChildJoint(6);
    		fk.updateStates(0.0, 1.0);
    	}    	
    	else if(window.isKeyPressed(GLFW_KEY_J)){
    		ForwardKinematics3D fk = new ForwardKinematics3D(currSkeleton);
    		fk.init();
    		fk.selectParentJoint(2);
    		fk.selectChildJoint(7);
    		fk.updateStates(0.0, 1.0);
    	}    	
    	else if(window.isKeyPressed(GLFW_KEY_K)){
    		ForwardKinematics3D fk = new ForwardKinematics3D(currSkeleton);
    		fk.init();
    		fk.selectParentJoint(7);
    		fk.selectChildJoint(8);
    		fk.updateStates(0.0, 1.0);
    	}
    	else if(window.isKeyPressed(GLFW_KEY_L)){
    		ForwardKinematics3D fk = new ForwardKinematics3D(currSkeleton);
    		fk.init();
    		fk.selectParentJoint(8);
    		fk.selectChildJoint(9);
    		fk.updateStates(0.0, 1.0);
    	}    	
    	else if(window.isKeyPressed(GLFW_KEY_Q)){
    		ForwardKinematics3D fk = new ForwardKinematics3D(currSkeleton);
    		fk.init();
    		fk.selectParentJoint(7);
    		fk.selectChildJoint(10);
    		fk.updateStates(0.0, 1.0);
    	}    	
    	else if(window.isKeyPressed(GLFW_KEY_W)){
    		ForwardKinematics3D fk = new ForwardKinematics3D(currSkeleton);
    		fk.init();
    		fk.selectParentJoint(10);
    		fk.selectChildJoint(11);
    		fk.updateStates(0.0, 1.0);
    	}
    	else if(window.isKeyPressed(GLFW_KEY_E)){
    		ForwardKinematics3D fk = new ForwardKinematics3D(currSkeleton);
    		fk.init();
    		fk.selectParentJoint(0);
    		fk.selectChildJoint(1);
    		fk.updateStates(0.0, 1.0);
    	}
    	else if(window.isKeyPressed(GLFW_KEY_R)){
    		ForwardKinematics3D fk = new ForwardKinematics3D(currSkeleton);
    		fk.init();
    		fk.selectParentJoint(0);
    		fk.selectChildJoint(1);
    		fk.updateStates(1.0, 0.0);
    	}
    	else if(window.isKeyPressed(GLFW_KEY_T)){
    		ForwardKinematics3D fk = new ForwardKinematics3D(currSkeleton);
    		fk.init();
    		fk.selectParentJoint(1);
    		fk.selectChildJoint(2);
    		fk.updateStates(1.0, 0.0);
    	}  
    	else if(window.isKeyPressed(GLFW_KEY_Y)){
    		ForwardKinematics3D fk = new ForwardKinematics3D(currSkeleton);
    		fk.init();
    		fk.selectParentJoint(1);
    		fk.selectChildJoint(2);
    		fk.updateStates(0.0, 1.0);
    	}
    	else if(window.isKeyPressed(GLFW_KEY_Z)){
    		ForwardKinematics3D fk = new ForwardKinematics3D(currSkeleton);
    		fk.init();
    		fk.selectParentJoint(2);
    		fk.selectChildJoint(3);
    		fk.updateStates(0.0, 1.0);
    	}
    	else if(window.isKeyPressed(GLFW_KEY_X)){
    		ForwardKinematics3D fk = new ForwardKinematics3D(currSkeleton);
    		fk.init();
    		fk.selectParentJoint(2);
    		fk.selectChildJoint(3);
    		fk.updateStates(1.0, 0.0);
    	}
    	else if(window.isKeyPressed(GLFW_KEY_B)){
    		//	Select key frames
    		currSkeleton.show();
    		animationClip.samples.add(new AnimationSample(currSkeleton.getLinks(), timer.getTime()));
	        animationClip.numPoints = currSkeleton.getLinks().size();
    		try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
    	}
    	else if(window.isKeyPressed(GLFW_KEY_N)){
    		//	Start rendering animation
    		if(animationClip.samples.size() != 0){
	    		animationClip.isRendering = true;
	    		animationClip.init();
    		}else{
    			System.out.println("AnimationClip empty, press M to resume");
    		}
    	}
    	else if(window.isKeyPressed(GLFW_KEY_M)){
    		//	Resume selecting key frames
    		animationClip.isRendering = false;
    	}
    }

    @Override
    public void update(Window window, float interval, MouseInput mouseInput) {
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
		
        if (mouseInput.isRightButtonPressed()) {
        	//Vector2d pos = mouseInput.getCurrentPos();
        	Vector3d xyz = mouseInput.get3DCoord(window, renderer, camera, gameItems[0]);
        	//System.out.println("Pos in 2D:(" + pos.x + "," + pos.y + ")");
        	//System.out.println("Pos in 3D:(" + xyz.x * 2 + "," + xyz.y * 2 + "," + xyz.z + ")");
        	//xyz.x *= 2;
        	//xyz.y *= 2;
        	//Skeleton3D s = gameItems[0].getSkeleton();
        	Skeleton3D s = currSkeleton;
        	ArrayList<Joint3D> js = s.getJoints();
        	Joint3D end = null;
        	double dist = 100000;
        	for(int i = 0; i < js.size(); i++){
        		if(js.get(i).isLeaf){
        			Vector3d d = new Vector3d(js.get(i).pos);
        			d.sub(xyz);
        			if(d.length() < dist){
        				dist = d.length();
        				end = js.get(i);
        			}
        		}
        	}
        	System.out.println();

        	InverseKinematics3D ik = new InverseKinematics3D(s, end, xyz);
        	ik.updateStates();
        	currSkeleton = ik.getSkeleton();
        	
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
        renderer.render(window, camera, gameItems, ambientLight, pointLight, directionalLight, animationClip, timer);
    }

    @Override
    public void cleanup() {
        renderer.cleanup();
        for (GameItem gameItem : gameItems) {
            gameItem.getMesh().cleanUp();
        }
    }
}
