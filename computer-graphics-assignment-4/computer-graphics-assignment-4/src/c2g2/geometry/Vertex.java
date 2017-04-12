package c2g2.geometry;

import org.joml.Vector2f;
import org.joml.Vector3f;

/*
 * TODO:
 * 
 * Vertex referred by the HalfEdge data structure
 * 
 * Please add code if needed
 */
public class Vertex {
	
	private HalfEdge e = null;
	public Vector3f pos = null;
	public Vector2f tex = null;
	public Vector3f norm = null;
	private Integer vertexIdx = 0;
	
	public Vertex(Vector3f p, Vector3f n, Vector2f t, Integer v){
		tex = t;
		pos = p;
		norm = n;
		vertexIdx = v;
	}
	
	public Integer getVertexIdx(){
		return vertexIdx;
	}
	
	public void setEdge(HalfEdge e0){
		e=e0;
	}
	public boolean hasEdge(){
		return e!=null;
	}
	public HalfEdge getEdge(){
		return e;
	}
}
