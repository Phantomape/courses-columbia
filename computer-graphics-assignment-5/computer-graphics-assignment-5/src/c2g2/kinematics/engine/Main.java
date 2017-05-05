package c2g2.kinematics.engine;

import c2g2.engine.GameEngine;
import c2g2.engine.IGameLogic;
import c2g2.game.Game;
import c2g2.game.TestGame;


public class Main {
	
	public enum SceneType { SCENE_3D, SCENE_2D};
	
    public static void main(String[] args) {
    	//	2D scene
    	SceneType st = SceneType.SCENE_3D;
    	if(st == SceneType.SCENE_2D){
	        Scene scene = new Scene();
	        scene.loadfromXML("src/resources/models/test.xml");
	        Renderer r = new Renderer(scene);
	        r.run();
    	} 
    	else if(st == SceneType.SCENE_3D){
	        try {
	            boolean vSync = true;
	            IGameLogic gameLogic = new TestGame();
	            GameEngine engine = new GameEngine("GAME", 600, 480, vSync, gameLogic);
	            engine.start();
	        } catch (Exception excp) {
	            excp.printStackTrace();
	            System.exit(-1);
	        }
    	}
       
    }

}