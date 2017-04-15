package c2g2.geometry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.joml.Vector2f;
import org.joml.Vector3f;

import c2g2.engine.graph.Mesh;
//import c2g2.engine.graph.OBJLoader.Face;

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
		Set<Integer> vertexIdxSet = new HashSet<Integer>();
		//	change this part
		for(int i = 0; i < halfEdges.size()/3;i++){
			HalfEdge he1 = halfEdges.get(i * 3 + 0), he2 = halfEdges.get(i * 3 + 1), he3 = halfEdges.get(i * 3 + 2);
			int vertexIdx1 = he1.getNextV().getVertexIdx(), vertexIdx2 = he2.getNextV().getVertexIdx(), vertexIdx3 = he3.getNextV().getVertexIdx();
			vertexIdxSet.add(vertexIdx1);
			vertexIdxSet.add(vertexIdx2);
			vertexIdxSet.add(vertexIdx3);
		}
		System.out.println("Vertex:" + vertexIdxSet.size());
        float[] posArr = new float[vertexIdxSet.size() * 3];
        float[] textCoordArr = new float[vertexIdxSet.size() * 2];
        float[] normArr = new float[vertexIdxSet.size() * 3];
        List<Integer> indiceList = new ArrayList<Integer>();
        
        //	Allocate posArr
        for(int i = 0; i < halfEdges.size()/3; i++){
        	setProperty(i, 0, posArr, textCoordArr, normArr, indiceList);
        	setProperty(i, 1, posArr, textCoordArr, normArr, indiceList);
        	setProperty(i, 2, posArr, textCoordArr, normArr, indiceList);
        }
        int[] indicesArr = new int[indiceList.size()];
        indicesArr = indiceList.stream().mapToInt((Integer v) -> v).toArray();
        
        Mesh mesh = new Mesh(posArr, textCoordArr, normArr, indicesArr);
        return mesh;
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
		//	Record edges to be deleted
		ArrayList<HalfEdge> toDel = new ArrayList<HalfEdge>();
		HalfEdge start = vtx.getEdge();
		Vertex dest = start.getFlipE().getNextV();
		do{
			toDel.add(start);
			start = start.getNextE().getFlipE();
		}while(start != vtx.getEdge());
		//	Change corresponding edges

		ArrayList<HalfEdge> overlap = new ArrayList<HalfEdge>();
		ArrayList<HalfEdge> innerOverlap = new ArrayList<HalfEdge>();
		for(int i = 0; i < toDel.size(); i++){
			//	Faces to be deleted
			HalfEdge iter = toDel.get(i);
			if(iter.getNextE().getNextV() == dest || iter.getFlipE().getNextV() == dest ){
				overlap.add(iter.getNextE().getNextE().getFlipE());
			}else{
				if(i == 1)
					innerOverlap.add(iter);
				if(i == toDel.size() - 1)
					innerOverlap.add(iter.getFlipE());
				
				Face f = iter.getlFace();
				iter.setNextV(dest);
				f.setHalfEdge(iter);
				// modify mapping 
				changeMapping(f, vtx, dest);
			}
		}
		overlap.get(0).setFlipE(innerOverlap.get(0));
		innerOverlap.get(0).setFlipE(overlap.get(0));
		overlap.get(1).setFlipE(innerOverlap.get(1));
		innerOverlap.get(1).setFlipE(overlap.get(1));
		//	Update faces and halfEdges
		
	}
	
	public void changeMapping(Face f, Vertex orig, Vertex next){
		int fromDest = f.order.get(orig.getVertexIdx());
		int toDest = f.order.get(fromDest);
		f.order.remove(orig.getVertexIdx());
		f.order.put(next.getVertexIdx(), fromDest);
		f.order.put(toDest, next.getVertexIdx());
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
		float[] texs = mesh.getTextco();
		float[] pos = mesh.getPos();
		int[] inds = mesh.getInds();
		halfEdges = new ArrayList<HalfEdge>();
		faces = new ArrayList<Face>();
		map = new HashMap<String, ArrayList<Integer>>();
		for(int i = 0; i < (inds.length / 3); i++){
			//	Three index of vertex in each face
			Vertex v1 = getProperty(inds, pos, norms, texs, i, 0);
			Vertex v2 = getProperty(inds, pos, norms, texs, i, 1);
			Vertex v3 = getProperty(inds, pos, norms, texs, i, 2);
			/*
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
			*/
			//	Construct three halfedges in anti-clockwise direction
			HalfEdge he1 = new HalfEdge(v1);
			v1.setEdge(he1);
			HalfEdge he2 = new HalfEdge(v2);
			v2.setEdge(he2);
			HalfEdge he3 = new HalfEdge(v3);
			v3.setEdge(he3);
			he1.setNextE(he2);he2.setNextE(he3);he3.setNextE(he1);
			Map<Integer, Integer> order = new HashMap<Integer, Integer>();
			int posIdx1 = inds[i * 3 + 0];
			int posIdx2 = inds[i * 3 + 1];
			int posIdx3 = inds[i * 3 + 2];
			order.put(posIdx1, posIdx2);
			order.put(posIdx2, posIdx3);
			order.put(posIdx3, posIdx1);
			
			halfEdges.add(he1);
			halfEdges.add(he2);
			halfEdges.add(he3);
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
			int v1 = f.v0, v2 = f.v1, v3 = f.v2;
			//	Key for searching map
			addRevEdge(v1, v2, f);
			addRevEdge(v2, v3, f);
			addRevEdge(v3, v1, f);
		}
		System.out.println("Done");
		
	}
	
	public void setProperty(int i, int offset, float[] posArr, float[] normArr, float[] texsArr, List<Integer> indiceList){
		HalfEdge he = halfEdges.get(i * 3 + offset);
    	posArr[i * 3] = he.getNextV().pos.x;
    	posArr[i * 3 + 1] = he.getNextV().pos.y;
    	posArr[i * 3 + 2] = he.getNextV().pos.z;
    	normArr[i * 3] = he.getNextV().norm.x;
    	normArr[i * 3 + 1] = he.getNextV().norm.x;
    	normArr[i * 3 + 2] = he.getNextV().norm.x;
    	texsArr[i * 2] = he.getNextV().tex.x;
    	texsArr[i * 2 + 1] = he.getNextV().tex.y;
    	indiceList.add(he.getNextV().getVertexIdx());
	}
	
	public Vertex getProperty(int[] inds, float[] pos, float[] norms, float[] texs, int i, int offset){
		int posIdx = inds[i * 3 + offset];
		Vector3f position = new Vector3f(pos[posIdx * 3], pos[posIdx * 3 + 1], pos[posIdx * 3 + 2]);
		Vector2f texture = new Vector2f(texs[posIdx * 2], texs[posIdx * 2 + 1]);
		Vector3f norm = new Vector3f(norms[posIdx * 3], norms[posIdx * 3 + 1], norms[posIdx * 3 + 2]);
		Vertex v = new Vertex(position, norm, texture, posIdx);
		return v;
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
