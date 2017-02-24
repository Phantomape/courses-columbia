package c2g2.engine.graph;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import org.joml.Matrix3f;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryUtil;

public class Mesh {

    private int vaoId;

    private List<Integer> vboIdList;

    private int vertexCount;

    private Material material;
    
    private float[] pos;
    private float[] textco;
    private float[] norms;
    private int[] inds;
    
    
    public Mesh(){
    	this(new float[]{-0.5f,-0.5f,-0.5f,-0.5f,-0.5f,0.5f,-0.5f,0.5f,-0.5f,-0.5f,0.5f,0.5f,0.5f,-0.5f,-0.5f,0.5f,-0.5f,0.5f,0.5f,0.5f,-0.5f,0.5f,0.5f,0.5f}, 
    			new float[]{0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f}, 
    			new float[]{0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f}, 
    			new int[]{0,6,4,0,2,6,0,3,2,0,1,3,2,7,6,2,3,7,4,6,7,4,7,5,0,4,5,0,5,1,1,5,7,1,7,3});
    }
    
    public void setMesh(float[] positions, float[] textCoords, float[] normals, int[] indices){
    	pos = positions;
    	textco = textCoords;
    	norms = normals;
    	inds = indices;
    	FloatBuffer posBuffer = null;
        FloatBuffer textCoordsBuffer = null;
        FloatBuffer vecNormalsBuffer = null;
        IntBuffer indicesBuffer = null;
        System.out.println("create mesh:");
        System.out.println("v: "+positions.length+" t: "+textCoords.length+" n: "+normals.length+" idx: "+indices.length);
        try {
            vertexCount = indices.length;
            vboIdList = new ArrayList<Integer>();

            vaoId = glGenVertexArrays();
            glBindVertexArray(vaoId);

            // Position VBO
            int vboId = glGenBuffers();
            vboIdList.add(vboId);
            posBuffer = MemoryUtil.memAllocFloat(positions.length);
            posBuffer.put(positions).flip();
            glBindBuffer(GL_ARRAY_BUFFER, vboId);
            glBufferData(GL_ARRAY_BUFFER, posBuffer, GL_STATIC_DRAW);
            glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);

            // Texture coordinates VBO
            vboId = glGenBuffers();
            vboIdList.add(vboId);
            textCoordsBuffer = MemoryUtil.memAllocFloat(textCoords.length);
            textCoordsBuffer.put(textCoords).flip();
            glBindBuffer(GL_ARRAY_BUFFER, vboId);
            glBufferData(GL_ARRAY_BUFFER, textCoordsBuffer, GL_STATIC_DRAW);
            glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);

            // Vertex normals VBO
            vboId = glGenBuffers();
            vboIdList.add(vboId);
            vecNormalsBuffer = MemoryUtil.memAllocFloat(normals.length);
            vecNormalsBuffer.put(normals).flip();
            glBindBuffer(GL_ARRAY_BUFFER, vboId);
            glBufferData(GL_ARRAY_BUFFER, vecNormalsBuffer, GL_STATIC_DRAW);
            glVertexAttribPointer(2, 3, GL_FLOAT, false, 0, 0);

            // Index VBO
            vboId = glGenBuffers();
            vboIdList.add(vboId);
            indicesBuffer = MemoryUtil.memAllocInt(indices.length);
            indicesBuffer.put(indices).flip();
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vboId);
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_STATIC_DRAW);

            glBindBuffer(GL_ARRAY_BUFFER, 0);
            glBindVertexArray(0);
        } finally {
            if (posBuffer != null) {
                MemoryUtil.memFree(posBuffer);
            }
            if (textCoordsBuffer != null) {
                MemoryUtil.memFree(textCoordsBuffer);
            }
            if (vecNormalsBuffer != null) {
                MemoryUtil.memFree(vecNormalsBuffer);
            }
            if (indicesBuffer != null) {
                MemoryUtil.memFree(indicesBuffer);
            }
        }
    }

    public void setMesh(float[] positions, float[] textCoords, int[] indices){
    	pos = positions;
    	textco = textCoords;
    	inds = indices;
    	FloatBuffer posBuffer = null;
        FloatBuffer textCoordsBuffer = null;
        FloatBuffer vecNormalsBuffer = null;
        IntBuffer indicesBuffer = null;
        System.out.println("create mesh:");
        System.out.println("v: "+positions.length+" t: "+textCoords.length+" idx: "+indices.length);
        try {
            vertexCount = indices.length;
            vboIdList = new ArrayList<Integer>();

            vaoId = glGenVertexArrays();
            glBindVertexArray(vaoId);

            // Position VBO
            int vboId = glGenBuffers();
            vboIdList.add(vboId);
            posBuffer = MemoryUtil.memAllocFloat(positions.length);
            posBuffer.put(positions).flip();
            glBindBuffer(GL_ARRAY_BUFFER, vboId);
            glBufferData(GL_ARRAY_BUFFER, posBuffer, GL_STATIC_DRAW);
            glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);

            // Texture coordinates VBO
            vboId = glGenBuffers();
            vboIdList.add(vboId);
            textCoordsBuffer = MemoryUtil.memAllocFloat(textCoords.length);
            textCoordsBuffer.put(textCoords).flip();
            glBindBuffer(GL_ARRAY_BUFFER, vboId);
            glBufferData(GL_ARRAY_BUFFER, textCoordsBuffer, GL_STATIC_DRAW);
            glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);

            // Index VBO
            vboId = glGenBuffers();
            vboIdList.add(vboId);
            indicesBuffer = MemoryUtil.memAllocInt(indices.length);
            indicesBuffer.put(indices).flip();
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vboId);
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_STATIC_DRAW);

            glBindBuffer(GL_ARRAY_BUFFER, 0);
            glBindVertexArray(0);
        } finally {
            if (posBuffer != null) {
                MemoryUtil.memFree(posBuffer);
            }
            if (textCoordsBuffer != null) {
                MemoryUtil.memFree(textCoordsBuffer);
            }
            if (vecNormalsBuffer != null) {
                MemoryUtil.memFree(vecNormalsBuffer);
            }
            if (indicesBuffer != null) {
                MemoryUtil.memFree(indicesBuffer);
            }
        }
    }
    
    public Mesh(float[] positions, float[] textCoords, float[] normals, int[] indices) {
    	setMesh(positions, textCoords, normals, indices);        
    }
    
    public Mesh(float[] positions, float[] textCoords, int[] indices) {
    	setMesh(positions, textCoords, indices);        
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public int getVaoId() {
        return vaoId;
    }

    public int getVertexCount() {
        return vertexCount;
    }

    public void render() {
        // Draw the mesh
        glBindVertexArray(getVaoId());
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        glEnableVertexAttribArray(2);

        glDrawElements(GL_TRIANGLES, getVertexCount(), GL_UNSIGNED_INT, 0);

        // Restore state
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glDisableVertexAttribArray(2);
        glBindVertexArray(0);
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    public void cleanUp() {
        glDisableVertexAttribArray(0);

        // Delete the VBOs
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        for (int vboId : vboIdList) {
            glDeleteBuffers(vboId);
        }

        // Delete the VAO
        glBindVertexArray(0);
        glDeleteVertexArrays(vaoId);
    }
    
    public void scaleMesh(float sx, float sy, float sz){
    	cleanUp(); //clean up buffer
    	//Reset position of each point
    	//Do not change textco, norms, inds
    	//student code 
    	for (int i = 0; i < pos.length/3; i++) {
    		pos[i * 3] = pos[i * 3] * sx;
    		pos[i * 3 + 1] = pos[i * 3 + 1] * sy;
    		pos[i * 3 + 2] = pos[i * 3 + 2] * sz;
		}   	
    	setMesh(pos, textco, norms, inds);
    }
    
    public void translateMesh(Vector3f trans){
    	cleanUp();
    	//reset position of each point
    	//Do not change textco, norms, inds
    	//student code
    	for(int i=0; i< pos.length/3; i++){
    		pos[i * 3] = pos[i * 3] + trans.x;
    		pos[i * 3 + 1] = pos[i * 3 + 1] + trans.y;
    		pos[i * 3 + 2] = pos[i * 3 + 2] + trans.z;
    	}
    	setMesh(pos, textco, norms, inds);
    }
    
    public void rotateMesh(Vector3f axis, float angle){
    	cleanUp();
    	//reset position of each point
    	//Do not change textco, norms, inds
    	//student code
    	float k = (float)Math.cos(Math.toRadians(angle));
    	float j = (float)Math.sin(Math.toRadians(angle));
    	for(int i=0; i< pos.length/3; i++){
    		Vector3f v = new Vector3f(pos[i * 3], pos[i * 3 + 1], pos[i * 3 + 2]);
    		Vector3f v_1 = new Vector3f(pos[i * 3] * k, pos[i * 3 + 1] * k, pos[i * 3 + 2] * k);    		
    		Vector3f tmp = new Vector3f();
    		axis.cross(v, tmp);
    		Vector3f v_2 = new Vector3f(tmp.x * j, tmp.y * j, tmp.z * j);
    		float phi = 0.0f;
    		phi = v.dot(axis) * (1 - k);
    		Vector3f v_3 = new Vector3f(axis.x * phi, axis.y * phi, axis.z * phi);
    		Vector3f v_rot = v_1.add(v_2).add(v_3);
    		pos[i * 3] = v_rot.x;
    		pos[i * 3 + 1] = v_rot.y;
    		pos[i * 3 + 2] = v_rot.z;
    		
    	}
    	setMesh(pos, textco, norms, inds);
    }
    
    public void reflectMesh(Vector3f p, Vector3f n){
    	cleanUp();
    	//reset position of each point
    	//Do not change textco, norms, inds
    	//student code
    	n.normalize();
    	for(int i=0; i< pos.length/3; i++){
    		/*
    		Vector3f x = new Vector3f(pos[i * 3] - p.x, pos[i * 3 + 1] - p.y, pos[i * 3 + 2] - p.z);
    		
    		Matrix3f H = new Matrix3f();
    		H.m00(1 - n.x * n.x);H.m01(1 - n.x * n.y);H.m02(1 - n.x * n.z);
    		H.m10(1 - n.y * n.x);H.m11(1 - n.y * n.y);H.m12(1 - n.y * n.z);
    		H.m20(1 - n.z * n.x);H.m21(1 - n.z * n.y);H.m22(1 - n.z * n.z);
    		
    		Vector3f res = x.mul(H);
    		pos[i * 3] = res.x;
    		pos[i * 3 + 1] = res.y;
    		pos[i * 3 + 2] = res.z;
    		*/
    		
    		Vector3f p2x = new Vector3f(pos[i * 3] - p.x, pos[i * 3 + 1] - p.y, pos[i * 3 + 2] - p.z);
    		float c = 1 - 2 * (float)Math.cos(p2x.dot(n) / p2x.length());
    		Vector3f p2y = new Vector3f(p2x.x * c, p2x.y * c, p2x.z * c);
    		pos[i * 3] = p2y.x + p.x;
    		pos[i * 3 + 1] = p2y.y + p.y;
    		pos[i * 3 + 2] = p2y.z + p.z;
    	}
    	setMesh(pos, textco, norms, inds);
    }
}
