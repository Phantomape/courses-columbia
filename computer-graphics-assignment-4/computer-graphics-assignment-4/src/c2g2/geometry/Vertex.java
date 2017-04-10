package c2g2.geometry;

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
	private Vector3f pos = null;
	private Vector3f norm = null;
	
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
