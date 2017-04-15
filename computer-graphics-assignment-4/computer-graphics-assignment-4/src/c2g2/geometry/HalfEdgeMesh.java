package c2g2.geometry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.joml.Vector3f;

import c2g2.engine.graph.Mesh;

/*
 * A mesh represented by HalfEdge data structure
 */
public class HalfEdgeMesh {

	private ArrayList<HalfEdge> halfEdges;
	private ArrayList<Face> faces;
	private Map<String, ArrayList<Integer>> map;
	private int vertexSize = 0;
	/*
	 * TODO:
	 * Convert this HalfEdgeMesh into an indexed triangle mesh
	 */
	public Mesh toMesh() {
		Set<Integer> vertexIdxSet = new HashSet<Integer>();
		//	change this part
		for(int i = 0; i < halfEdges.size()/3;i++){
			HalfEdge he0 = halfEdges.get(i * 3 + 0);
			HalfEdge he1 = halfEdges.get(i * 3 + 1);
			HalfEdge he2 = halfEdges.get(i * 3 + 2);
			int vertexIdx1 = he0.getNextV().idx;
			int vertexIdx2 = he1.getNextV().idx;
			int vertexIdx3 = he2.getNextV().idx;
			vertexIdxSet.add(vertexIdx1);
			vertexIdxSet.add(vertexIdx2);
			vertexIdxSet.add(vertexIdx3);
		}
        float[] posArr = new float[vertexIdxSet.size() * 3];
        float[] textCoordArr = new float[vertexIdxSet.size() * 2];
        float[] normArr = new float[vertexIdxSet.size() * 3];
        List<Integer> indiceList = new ArrayList<Integer>();
        
        //	process normArr and posArr
        for(int i = 0; i < halfEdges.size()/3; i++){
        	setArr(i, 0, posArr, textCoordArr, normArr, indiceList);
        	setArr(i, 1, posArr, textCoordArr, normArr, indiceList);
        	setArr(i, 2, posArr, textCoordArr, normArr, indiceList);
        }
        int[] indicesArr = new int[indiceList.size()];
        indicesArr = indiceList.stream().mapToInt((Integer v) -> v).toArray();
        
        Mesh mesh = new Mesh(posArr, textCoordArr, normArr, indicesArr);
        return mesh;
	}
	
	public void setArr(int i, int offset, float[] posArr, float[] normArr, float[] texsArr, List<Integer> indiceList){
		HalfEdge he = halfEdges.get(i * 3 + offset);
		int posIdx = he.getNextV().idx;
    	indiceList.add(posIdx);
    	posArr[posIdx * 3] = he.getNextV().pos.x;
    	posArr[posIdx * 3 + 1] = he.getNextV().pos.y;
    	posArr[posIdx * 3 + 2] = he.getNextV().pos.z;
    	normArr[posIdx * 3] = he.getNextV().norm.x;
    	normArr[posIdx * 3 + 1] = he.getNextV().norm.x;
    	normArr[posIdx * 3 + 2] = he.getNextV().norm.x;
    	texsArr[posIdx * 2] = 0;
    	texsArr[posIdx * 2 + 1] = 0;
    	indiceList.add(he.getNextV().idx);
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
		HalfEdge rev = edge.getFlipE();
		Face leftFace = edge.getlFace(), rightFace = rev.getlFace();
		HalfEdge leftUp = edge.getNextE().getFlipE(), leftDown = edge.getNextE().getNextE().getFlipE();
		HalfEdge rightUp = rev.getNextE().getFlipE(), rightDown = rev.getNextE().getNextE().getFlipE();
		Vertex k = getMergeVertex(edge.getNextV(), rev.getNextV());
		
		//	erase edges in left face and right face
		HalfEdge iter = leftFace.getHalfEdge();
		do{
			iter.terminate();
			iter = iter.getNextE();
		}while(iter != leftFace.getHalfEdge());
		iter = rightFace.getHalfEdge();
		do{
			iter.terminate();
			iter = iter.getNextE();
		}while(iter != rightFace.getHalfEdge());
		
		//	set reverse edges
		leftUp.setFlipE(leftDown);
		leftDown.setFlipE(leftUp);
		rightUp.setFlipE(rightDown);
		rightDown.setFlipE(rightUp);
		
		//	Change vertex
		Vertex up = edge.getNextV(), down = rev.getNextV();
		iter = up.getEdge();
		do{
			iter.setNextV(k);
			iter = iter.getNextE().getFlipE();
		}while(iter != up.getEdge());
		iter = down.getEdge();
		do{
			iter.setNextV(k);
			iter = iter.getNextE().getFlipE();
		}while(iter != down.getEdge());
		
		//	Clear half edges in global variable
		//clearTwoFaces(leftFace, rightFace);
		renewArray(edge);
	}
	public void renewArray(HalfEdge edge){
		
	}
	
	public void clearTwoFaces(Face left, Face right){
		int leftIdx = left.faceIdx, rightIdx = right.faceIdx;
		HalfEdge tmp = halfEdges.get(leftIdx * 3);
		tmp.terminate();
		halfEdges.set(leftIdx * 3, tmp);
		
		tmp = halfEdges.get(leftIdx * 3 + 1);
		tmp.terminate();
		halfEdges.set(leftIdx * 3 + 1, tmp);
		
		tmp = halfEdges.get(leftIdx * 3 + 2);
		tmp.terminate();
		halfEdges.set(leftIdx * 3 + 2, tmp);
		
		tmp = halfEdges.get(rightIdx * 3);
		tmp.terminate();
		halfEdges.set(rightIdx * 3, tmp);
		
		tmp = halfEdges.get(rightIdx * 3 + 1);
		tmp.terminate();
		halfEdges.set(rightIdx * 3 + 1, tmp);
		
		tmp = halfEdges.get(rightIdx * 3 + 2);
		tmp.terminate();
		halfEdges.set(rightIdx * 3 + 2, tmp);
	}
	
	public Vertex getMergeVertex(Vertex a, Vertex b){
		Vector3f pos = a.pos.add(b.pos).div(2);
		Vector3f norm = new Vector3f();
		int posIdx = vertexSize++;
		Vertex res = new Vertex(pos, norm, posIdx);
		return res;
	}
	
	public HalfEdgeMesh(Mesh mesh) {		
		float[] norms = mesh.getNorms();
		float[] pos = mesh.getPos();
		int[] inds = mesh.getInds();
		vertexSize = pos.length/3;
		
		halfEdges = new ArrayList<HalfEdge>();
		faces = new ArrayList<Face>();
		map = new HashMap<String, ArrayList<Integer>>();
		//	Process by face
		for(int i = 0; i < (inds.length / 3); i++){
			//	Construct vertexes
			Vertex v0 = createVertex(inds, pos, norms, i, 0);
			Vertex v1 = createVertex(inds, pos, norms, i, 1);
			Vertex v2 = createVertex(inds, pos, norms, i, 2);
			HalfEdge he0 = new HalfEdge(v0);
			HalfEdge he1 = new HalfEdge(v1);
			HalfEdge he2 = new HalfEdge(v2);
			v0.setEdge(he0);
			v1.setEdge(he1);
			v2.setEdge(he2);
			he0.setNextE(he1);
			he1.setNextE(he2);
			he2.setNextE(he0);
			Face f = new Face(he0, i);
			he0.setlFace(f);
			he1.setlFace(f);
			he2.setlFace(f);
			
			//	Add these edges and the face into global variable
			halfEdges.add(he0);
			halfEdges.add(he1);
			halfEdges.add(he2);
			faces.add(f);
			
			//	Construct mapping to find adjacent faces based on two vertexes
			int posIdx0 = inds[i * 3 + 0];
			int posIdx1 = inds[i * 3 + 1];
			int posIdx2 = inds[i * 3 + 2];
			String s0 = (posIdx0 < posIdx1) ? new String(posIdx0 + "-" + posIdx1) : new String(posIdx1 + "-" + posIdx0);
			String s1 = (posIdx1 < posIdx2) ? new String(posIdx1 + "-" + posIdx2) : new String(posIdx2 + "-" + posIdx1);
			String s2 = (posIdx2 < posIdx0) ? new String(posIdx2 + "-" + posIdx0) : new String(posIdx0 + "-" + posIdx2);
			processMapping(s0, i);
			processMapping(s1, i);
			processMapping(s2, i);
		}
		
		//	Adding reverse halfedges
		for(Face f : faces){
			Vertex v0 = f.getHalfEdge().getNextV();
			Vertex v1 = f.getHalfEdge().getNextE().getNextV();
			Vertex v2 = f.getHalfEdge().getNextE().getNextE().getNextV();
			//	Key for searching map
			addRevEdge(v0, v1, f);
			addRevEdge(v1, v2, f);
			addRevEdge(v2, v0, f);
		}
		System.out.println("Done");
		
		
	}
	
	public void addRevEdge(Vertex start, Vertex end, Face f){
		Face adjFace = null;
		String s = (start.idx < end.idx) ? new String(start.idx + "-" + end.idx) : new String(end.idx + "-" + start.idx);
		ArrayList<Integer> tmp;
		tmp = map.get(s);
		for(Integer j : tmp)
			if(j != f.faceIdx)
				adjFace = faces.get(j);
		//	Determine edge direction
		HalfEdge iter = f.getHalfEdge();
		boolean flag = false;
		Vertex orig, dest;
		do{
			if(iter.getNextV().idx == start.idx && iter.getNextE().getNextV().idx == end.idx)
				flag = true;
			iter = iter.getNextE();
		}while(iter != f.getHalfEdge());
		if(flag == true){
			orig = end;
			dest = start;
		}else{
			orig = start;
			dest = end;
		}
		
		//	Find corresponding half edge
		HalfEdge rev = adjFace.getHalfEdge();
		do{
			if(rev.getNextV().idx == dest.idx)
				break;
			rev = rev.getNextE();
		}while(rev != adjFace.getHalfEdge());
		HalfEdge inc = f.getHalfEdge();
		do{
			if(inc.getNextV().idx == orig.idx)
				break;
			inc = inc.getNextE();
		}while(inc != f.getHalfEdge());
		inc.setFlipE(rev);
		rev.setFlipE(inc);
		
	}
	
	public Vertex createVertex(int[] inds, float[] pos, float[] norms, int i, int offset){
		int posIdx = inds[i * 3 + offset];
		Vector3f position = new Vector3f(pos[posIdx * 3], pos[posIdx * 3 + 1], pos[posIdx * 3 + 2]);
		Vector3f norm = new Vector3f(norms[posIdx * 3], norms[posIdx * 3 + 1], norms[posIdx * 3 + 2]);
		Vertex v = new Vertex(position, norm, posIdx);
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
}
