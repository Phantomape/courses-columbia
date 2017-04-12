package c2g2.geometry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.joml.Vector3f;

import c2g2.engine.graph.Mesh;

/*
 * A mesh represented by HalfEdge data structure
 */
public class HalfEdgeMesh {

	private ArrayList<HalfEdge> halfEdges;
	private ArrayList<Face> faces;
	private Map<String, ArrayList<Integer>> map;
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
		halfEdges = new ArrayList<HalfEdge>();
		faces = new ArrayList<Face>();
		map = new HashMap<String, ArrayList<Integer>>();
		for(int i = 0; i < (inds.length / 3); i++){
			//	Three index of vertex in each face
			int posIdx1 = inds[i * 3 + 0];
			int posIdx2 = inds[i * 3 + 1];
			int posIdx3 = inds[i * 3 + 2];
			Vector3f pos1 = new Vector3f(pos[posIdx1 * 3], pos[posIdx1 * 3 + 1], pos[posIdx1 * 3 + 2]);
			Vector3f pos2 = new Vector3f(pos[posIdx2 * 3], pos[posIdx2 * 3 + 1], pos[posIdx2 * 3 + 2]);
			Vector3f pos3 = new Vector3f(pos[posIdx3 * 3], pos[posIdx3 * 3 + 1], pos[posIdx3 * 3 + 2]);
			Vector3f norm1 = new Vector3f(norms[posIdx1 * 3], norms[posIdx1 * 3 + 1], norms[posIdx1 * 3 + 2]);
			Vector3f norm2 = new Vector3f(norms[posIdx2 * 3], norms[posIdx2 * 3 + 1], norms[posIdx2 * 3 + 2]);
			Vector3f norm3 = new Vector3f(norms[posIdx3 * 3], norms[posIdx3 * 3 + 1], norms[posIdx3 * 3 + 2]);
			Vertex v1 = new Vertex(pos1, norm1, posIdx1);
			Vertex v2 = new Vertex(pos2, norm2, posIdx2);
			Vertex v3 = new Vertex(pos3, norm3, posIdx3);
			//	Construct three halfedges in anti-clockwise direction
			HalfEdge he1 = new HalfEdge(v1);
			HalfEdge he2 = new HalfEdge(v2);
			HalfEdge he3 = new HalfEdge(v3);
			he1.setNextE(he2);he2.setNextE(he3);he3.setNextE(he1);
			Map<Integer, Integer> order = new HashMap<Integer, Integer>();
			order.put(posIdx1, posIdx2);
			order.put(posIdx2, posIdx3);
			order.put(posIdx3, posIdx1);
			//order.put(new Integer(posIdx1), new Integer(posIdx2));
			//order.put(new Integer(posIdx2), new Integer(posIdx3));
			//order.put(new Integer(posIdx3), new Integer(posIdx1));
			
			halfEdges.add(he1);halfEdges.add(he2);halfEdges.add(he3);
			Face f = new Face(he1, i, order, posIdx1);
			he1.setlFace(f);he2.setlFace(f);he3.setlFace(f);
			faces.add(f);
			//	Construct mapping: (v1, v3)->[f1, f2];
			String s1 = (posIdx1 < posIdx2) ? new String(posIdx1 + "-" + posIdx2) : new String(posIdx2 + "-" + posIdx1);
			String s2 = (posIdx2 < posIdx3) ? new String(posIdx2 + "-" + posIdx3) : new String(posIdx3 + "-" + posIdx2);
			String s3 = (posIdx3 < posIdx1) ? new String(posIdx3 + "-" + posIdx1) : new String(posIdx1 + "-" + posIdx3);
			int faceIdx = faces.size() - 1;
			processMapping(s1, faceIdx);
			processMapping(s2, faceIdx);
			processMapping(s3, faceIdx);
		}
		//	Adding reverse halfedges
		for(Face f : faces){
			HalfEdge start = f.getHalfEdge();
			int v1 = f.v0, v2 = f.v1, v3 = f.v2;
			//	Key for searching map
			addRevEdge(v1, v2, f);
			addRevEdge(v2, v3, f);
			addRevEdge(v3, v1, f);
		}
		System.out.println("Done");
		
	}
	
	public void processMapping(String s, int faceIdx){
		ArrayList<Integer> tmp;
		if(map.containsKey(s)){
			tmp = map.get(s);
		}else{
			tmp = new ArrayList<Integer>();
		}
		tmp.add(faceIdx);
		map.put(s, tmp);
	}
	
	public void addRevEdge(int start, int end, Face f){
		Face adjFace = null;
		String s = (start < end) ? new String(start + "-" + end) : new String(end + "-" + start);
		ArrayList<Integer> tmp;
		tmp = map.get(s);
		for(Integer j : tmp)
			if(j != f.getFaceIdx())
				adjFace = faces.get(j);
		//	Determine edge direction
		int dest = 0; 
		int orig = 0;
		if(f.order.get(start) == end){	//	the direction in f is from start to end
			dest = end;
			orig = start;
		}else{
			orig = end;
			dest = start;
		}
		//	Find corresponding half edge
		HalfEdge rev = adjFace.getHalfEdge();
		do{
			if(rev.getNextV().getVertexIdx() == dest)
				break;
			rev = rev.getNextE();
		}while(rev != adjFace.getHalfEdge());
		HalfEdge inc = f.getHalfEdge();
		do{
			if(inc.getNextV().getVertexIdx() == orig)
				break;
			inc = inc.getNextE();
		}while(inc != f.getHalfEdge());
		inc.setFlipE(rev);
		rev.setFlipE(inc);
		
	}
	
}
