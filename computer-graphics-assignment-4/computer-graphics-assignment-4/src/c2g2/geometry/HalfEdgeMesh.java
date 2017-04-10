package c2g2.geometry;

import java.util.ArrayList;

import c2g2.engine.graph.Mesh;

/*
 * A mesh represented by HalfEdge data structure
 */
public class HalfEdgeMesh {

	private ArrayList<HalfEdge> halfEdges;
	
	/*
	 * TODO:
	 * Convert this HalfEdgeMesh into an indexed triangle mesh
	 */
	public Mesh toMesh() {
		return null;
	}
	
	/*
	 * Remove the first vertex from the HalfEdgeMesh. 
	 * See the specification for the detailed requirement.
	 */
	public void removeFirstVertex(){
		Vertex vertex = halfEdges.get(0).getNextV();
		removeVertex(vertex);
	}
	
	public void collapseFirstEdge(){
		collapseEdge(halfEdges.get(0));
	}
	
	/*
	 * TODO:
	 * See the specification for the detailed requirement.
	 */
	public void removeVertex(Vertex vtx) {
		//student code
	}
	
	/*
	 * TODO:
	 * Collapse the given edge into a point.
	 * See the specification for the detailed requirement.
	 */
	public void collapseEdge(HalfEdge edge) {
		//student code
	}
	
	public HalfEdgeMesh(Mesh mesh) {
		float[] norms = mesh.getNorms();
		float[] pos = mesh.getPos();
		int[] inds = mesh.getInds();
		
		//please implement the constructor for HalfEdgeMesh
		
	}
	
	
}
