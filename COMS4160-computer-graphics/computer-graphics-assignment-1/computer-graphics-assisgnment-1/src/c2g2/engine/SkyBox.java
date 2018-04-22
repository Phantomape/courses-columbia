package c2g2.engine;


import c2g2.engine.GameItem;
import c2g2.engine.graph.Material;
import c2g2.engine.graph.Mesh;
import c2g2.engine.graph.OBJLoader;
import c2g2.engine.graph.Texture;

public class SkyBox extends GameItem {



    public SkyBox(String objModel, String textureFile) throws Exception {
        super();
        Mesh skyBoxMesh = OBJLoader.loadMesh(objModel);
        Texture skyBoxtexture = new Texture(textureFile);
        skyBoxMesh.setMaterial(new Material(skyBoxtexture, 0.0f));
        setMesh(skyBoxMesh);
        setPosition(0, 0, 0);
    }

}