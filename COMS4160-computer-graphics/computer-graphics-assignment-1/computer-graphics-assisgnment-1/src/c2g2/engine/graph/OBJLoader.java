package c2g2.engine.graph;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import java.util.ArrayList;
import java.util.List;

import org.joml.Vector2f;
import org.joml.Vector3f;


public class OBJLoader {
    public static Mesh loadMesh(String fileName) throws Exception {
    	//// --- student code ---
        float[] positions = null;
        float[] textCoords = null;
        float[] norms = null;
        int[] indices = null;
        
        List<Vector3f> pos = new ArrayList<Vector3f>();
        List<Vector2f> text = new ArrayList<Vector2f>();
        List<Vector3f> normals = new ArrayList<Vector3f>();
        List<Integer> ind = new ArrayList<Integer>();
        int count = 0;
        //    	Reading .obj file by line
        FileReader reader = null, readerNew = null;
    	try{
    		reader = new FileReader(fileName);

			BufferedReader br = new BufferedReader(reader);
			String line = null;
	
			while((line = br.readLine()) != null){
				if(line.length() == 0)	continue;
				//System.out.println(line);
				String[] parts = line.split(" +");
				if(parts[0].equals("v")){	//	Geometric vertices
					pos.add(new Vector3f(Float.parseFloat(parts[1]), Float.parseFloat(parts[2]), Float.parseFloat(parts[3])));
				} else if(parts[0].equals("vt")){	//	Texture
					text.add(new Vector2f(Float.parseFloat(parts[1]), Float.parseFloat(parts[2])));
				} else if(parts[0].equals("vn")){
					normals.add(new Vector3f(Float.parseFloat(parts[1]), Float.parseFloat(parts[2]), Float.parseFloat(parts[3])));
				}
			}
			if(textCoords == null)	textCoords = new float[pos.size() * 2];
			if(norms == null)	norms = new float[pos.size() * 3];
			br.close();
			
			readerNew = new FileReader(fileName);
			br = new BufferedReader(readerNew);
			String lineNew = null;
			count = 0;
			while((lineNew = br.readLine()) != null){
				if(lineNew.length() == 0)	continue;
				//System.out.println(line);
				String[] parts = lineNew.split(" +");
				if(parts[0].equals("f")){
					String[] v1 = parts[1].split("/");
					String[] v2 = parts[2].split("/");
					String[] v3 = parts[3].split("/");
					process(ind, v1, text, normals, textCoords, norms);
					process(ind, v2, text, normals, textCoords, norms);
					process(ind, v3, text, normals, textCoords, norms);
				}
			}
			br.close();
			
	    	if(positions == null)	positions = new float[pos.size() * 3];
			loadVertices(positions, pos);
	    	if(indices == null)	indices = new int[ind.size()];
			loadIndices(indices, ind);
			System.out.println("Successfully loaded obj file.");
    	} catch(FileNotFoundException e){
    		e.printStackTrace();
    	}
    	

        //your task is to read data from an .obj file and fill in those arrays.
        //the data in those arrays should use following format.
        //positions[0]=v[0].position.x positions[1]=v[0].position.y positions[2]=v[0].position.z positions[3]=v[1].position.x ...
        //textCoords[0]=v[0].texture_coordinates.x textCoords[1]=v[0].texture_coordinates.y textCoords[2]=v[1].texture_coordinates.x ...
        //norms[0]=v[0].normals.x norms[1]=v[0].normals.y norms[2]=v[0].normals.z norms[3]=v[1].normals.x...
        //indices[0]=face[0].ind[0] indices[1]=face[0].ind[1] indices[2]=face[0].ind[2] indices[3]=face[1].ind[0]...(assuming all the faces are triangle face)
        return new Mesh(positions, textCoords, norms, indices);
    }
    
    private static void process(List<Integer> ind, String[] v, List<Vector2f> text, List<Vector3f> normals, float[] textCoords, float[] norms){
    	int index = Integer.parseInt(v[0]) - 1;
    	ind.add(index);
    	if(text.size() != 0){
    		Vector2f texture = text.get(Integer.parseInt(v[1]) - 1);
        	textCoords[index * 2] = texture.x;
        	textCoords[index * 2 + 1] = texture.y;
    	}
    	Vector3f normal = normals.get(Integer.parseInt(v[2]) - 1);
    	norms[index * 3] = normal.x;
    	norms[index * 3 + 1] = normal.y;
    	norms[index * 3 + 2] = normal.z;
    }
    
    private static void loadVertices(float[] positions, List<Vector3f> pos){
    	int i = 0;
    	for(Vector3f v : pos){
    		positions[i++] = v.x;
    		positions[i++] = v.y;
    		positions[i++] = v.z;
    	}
    }
    
    private static void loadIndices(int[] indices, List<Integer> ind){
    	int j = 0;
    	for(Integer i : ind)
    		indices[j++] = i;
    }
    
}
