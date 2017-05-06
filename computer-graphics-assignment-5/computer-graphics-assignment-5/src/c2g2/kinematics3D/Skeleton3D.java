package c2g2.kinematics3D;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joml.Vector2d;
import org.joml.Vector3d;
import org.lwjgl.BufferUtils;

import c2g2.engine.Material;
import c2g2.kinematics.RigidLink2D;

public class Skeleton3D {
    private int vaoId;
    private List<Integer> vboIdList = new ArrayList<Integer>();
    private Material material;
	private Joint3D root = null;
	private int numJoints = 0;
	private ArrayList<Joint3D> joints = new ArrayList<Joint3D>(); 
	private Map<String, Double> lengths = new HashMap<String, Double>();
	private ArrayList<Joint3D> links = new ArrayList<Joint3D>();
	
    float[] positions = new float[]{
            -2.0f, -2.0f, 1.0f,
            -1.0f, -1.0f, 0.0f,
            -1.0f, -1.0f, 3.0f,
        };
        
	
	public Skeleton3D(Joint3D root){
		this.root = root;
	}
	
	public Skeleton3D(Skeleton3D s) {
		material = s.getMaterial();
		root = s.getRoot();
		joints = s.getJoints();
		lengths = s.getLengths();
		links = s.getLinks();
	}

	public ArrayList<Joint3D> getLinks() {
		return links;
	}

	public void init(){
		setIdx(root);
		setLen(root);
		setLinks(root);
	}
	
	private void setIdx(Joint3D root){
		root.idx = numJoints++;
		joints.add(root);
		if(root.child.size() == 0)
			root.isLeaf = true;
		for(int i = 0; i < root.child.size(); i++){
			setIdx(root.child.get(i));
		}
	}
	
	private void setLen(Joint3D root){
		for(int i = 0; i < root.child.size(); i++){
			Joint3D child = root.child.get(i);
			String key = root.idx + "-" + child.idx;
			Vector3d bone = new Vector3d(child.pos.x - root.pos.x, child.pos.y - root.pos.y, child.pos.z - root.pos.z);
			double value = bone.length();
			lengths.put(key, value);
			setLen(child);
		}
	}
	
	public void setLinks(Joint3D root){
		for(int i = 0; i < root.child.size(); i++){
			Joint3D child = root.child.get(i);
			links.add(root);
			links.add(child);
			setLinks(child);
		}
	}
	
	public void render() {
		//testRender();

		int len = links.size() * 3;
    	float[] pts = new float[len];
    	for(int i = 0; i < links.size(); i++){
    		pts[i * 3 + 0] = (float)links.get(i).pos.x;
    		pts[i * 3 + 1] = (float)links.get(i).pos.y;
    		pts[i * 3 + 2] = (float)links.get(i).pos.z;
    	}
		
    	glUniform4f(0, 0.5f,0f,0f,1.0f);
    	

        int vao = glGenVertexArrays();
        glBindVertexArray(vao);
        
        int vbo = glGenBuffers();
        vboIdList.add(vbo);
        FloatBuffer vertices = BufferUtils.createFloatBuffer(pts.length);
        vertices.put(pts);
        vertices.rewind();       
        glBindBuffer (GL_ARRAY_BUFFER, vbo);
        glBufferData (GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);
        glVertexAttribPointer (0, 3, GL_FLOAT, false, 0, 0);
        
        glEnableVertexAttribArray(0);

        // wipe the drawing surface clear
        //glClear (GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        // draw points 0-3 from the currently bound VAO with current in-use shader
        //glDrawArrays (GL_TRIANGLES, 0, positions.length/3);
        glDrawArrays (GL_LINES, 0, pts.length/3);
    	
	}
	
	@Deprecated
	private void testRender(){
    	
    	glUniform4f(0, 0.5f,0f,0f,1.0f);
    	
    	FloatBuffer vertices = BufferUtils.createFloatBuffer(positions.length);
        vertices.put(positions);
        // Rewind the vertices
        vertices.rewind();
       
        int vbo = glGenBuffers();
        int vao = glGenVertexArrays();

        glBindBuffer (GL_ARRAY_BUFFER, vbo);
        glBufferData (GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);
        
        glBindVertexArray(vao);

        glEnableVertexAttribArray (0);
        glBindBuffer (GL_ARRAY_BUFFER, vbo);
        glVertexAttribPointer (0, 3, GL_FLOAT, false, 0, 0);

        // wipe the drawing surface clear
        glClear (GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glBindVertexArray (vao);
        // draw points 0-3 from the currently bound VAO with current in-use shader
        //glDrawArrays (GL_TRIANGLES, 0, positions.length/3);
        glDrawArrays (GL_LINE_STRIP, 0, positions.length/3);
	}
	
    public int getVaoId() {
        return vaoId;
    }
    
    public ArrayList<Joint3D> getJoints(){
    	return joints;
    }
    
    public Map<String, Double> getLengths(){
    	return lengths;
    }
    
    public Joint3D getRoot(){
    	return root;
    }
    
    public Material getMaterial(){
    	return material;
    }
    
    public void setMaterial(Material m){
    	material = m;
    }

	public void show() {
		System.out.println("Skeleton:");
		Joint3D iter = root;
		System.out.println("(" + iter.pos.x + "," + iter.pos.y + "," + iter.pos.z + ")");
		dfs(iter);
	}
	
	private void dfs(Joint3D r){
		for(int i = 0; i < r.child.size(); i++){
			Joint3D j = r.child.get(i);
			System.out.println("(" + j.pos.x + "," + j.pos.y + "," + j.pos.z + ")");
			dfs(j);
		}
	}
}
