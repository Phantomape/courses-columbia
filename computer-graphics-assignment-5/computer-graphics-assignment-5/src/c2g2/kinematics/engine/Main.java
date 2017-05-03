package c2g2.kinematics.engine;

import c2g2.engine.GameEngine;
import c2g2.engine.GameLogic;
import c2g2.game.Game;

public class Main {

  
    public static void main(String[] args) {
    	//	2D scene
    	/*
        Scene scene = new Scene();
        scene.loadfromXML("src/resources/models/test.xml");
        Renderer r = new Renderer(scene);
        r.run();
        */
        //	3D scene
        try {
            boolean vSync = true;
            GameLogic gameLogic = new Game();
            //IGameLogic gameLogic = new TerrainGame();
            GameEngine engine = new GameEngine("GAME", 600, 480, vSync, gameLogic);
            engine.start();
        } catch (Exception excp) {
            excp.printStackTrace();
            System.exit(-1);
        }
    }

}