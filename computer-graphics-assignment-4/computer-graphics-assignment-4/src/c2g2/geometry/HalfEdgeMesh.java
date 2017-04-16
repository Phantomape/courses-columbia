package c2g2.geometry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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
		for(int i = 0; i < faces.size(); i++){
			//System.out.println("faceIdx" + i);
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
		System.out.println("Max Index:" + Collections.max(vertexIdxSet));
        float[] posArr = new float[vertexSize * 3];
        float[] textCoordArr = new float[vertexSize * 2];
        float[] normArr = new float[vertexSize * 3];
        List<Integer> indiceList = new ArrayList<Integer>();
        
        System.out.println("posArr.size():" + posArr.length);
        System.out.println("normArr.size():" + normArr.length);
        
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
	
	public void setArr(int i, int offset, float[] posArr, float[] texsArr, float[] normArr, List<Integer> indiceList){
		HalfEdge he = halfEdges.get(i * 3 + offset);
		int posIdx = he.getNextV().idx;
    	indiceList.add(posIdx);
    	posArr[posIdx * 3] = he.getNextV().pos.x;
    	posArr[posIdx * 3 + 1] = he.getNextV().pos.y;
    	posArr[posIdx * 3 + 2] = he.getNextV().pos.z;
    	normArr[posIdx * 3] = he.getNextV().norm.x;
    	normArr[posIdx * 3 + 1] = he.getNextV().norm.y;
    	normArr[posIdx * 3 + 2] = he.getNextV().norm.z;
    	texsArr[posIdx * 2] = 0;
    	texsArr[posIdx * 2 + 1] = 0;
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
		System.out.println("Vertex to be removed:" + vtx.idx);
		System.out.println("New dest vertex:" + vtx.getEdge().getFlipE().getNextV().idx);
		ArrayList<HalfEdge> edges = new ArrayList<HalfEdge>();
		HalfEdge inc = vtx.getEdge(), rev = inc.getFlipE();
		Vertex dest = rev.getNextV();
		HalfEdge leftUp = rev.getNextE().getFlipE(), leftDown = rev.getNextE().getNextE().getFlipE();
		HalfEdge rightUp = rev.getNextE(), rightDown = rev.getNextE().getNextE();
		Face leftFace = rev.getlFace(), rightFace = inc.getlFace();
		HalfEdge iter = inc;
		//	get all edges pointing to vtx
		do{
			edges.add(iter);
			iter = iter.getNextE().getFlipE();
		}while(iter != inc);
		
		//	erase edges in left face and right face
		iter = leftFace.getHalfEdge();
		System.out.print("Vertex in deleted face:");
		do{
			System.out.print(iter.getNextV().idx + "->");
			iter.terminate();
			iter = iter.getNextE();
		}while(iter != leftFace.getHalfEdge());
		System.out.print("Vertex in deleted face:");
		iter = rightFace.getHalfEdge();
		do{
			System.out.print(iter.getNextV().idx + "->");
			iter.terminate();
			iter = iter.getNextE();
		}while(iter != rightFace.getHalfEdge());
		System.out.println();
		//	set reverse edges
		leftUp.setFlipE(leftDown);
		leftDown.setFlipE(leftUp);
		rightUp.setFlipE(rightDown);
		rightDown.setFlipE(rightUp);
		
		//	iterate remainint edges pointing to vtx
		for(int i = 1; i < edges.size() - 1; i++){
			edges.get(i).setNextV(dest);
		}
		
		updateArray(leftFace.faceIdx, rightFace.faceIdx);
	}
	
	/*
	 * TODO:
	 * Collapse the given edge into a point.
	 * See the specification for the detailed requirement.
	 */
	public void collapseEdge(HalfEdge edge) {
		HalfEdge rev = edge.getFlipE();
		Face leftFace = edge.getlFace(), rightFace = rev.getlFace();
		Face leftUpFace = edge.getNextE().getFlipE().getlFace();
		Face leftDownFace = edge.getNextE().getNextE().getFlipE().getlFace();
		Face rightDownFace = rev.getNextE().getFlipE().getlFace(); 
		Face rightUpFace = rev.getNextE().getNextE().getFlipE().getlFace();
		HalfEdge leftUp = edge.getNextE().getFlipE(), leftDown = edge.getNextE().getNextE().getFlipE();
		HalfEdge rightUp = rev.getNextE().getNextE().getFlipE(), rightDown = rev.getNextE().getFlipE();
		Vertex k = getMergeVertex(edge.getNextV(), rev.getNextV());
		Vertex up = edge.getNextV(), down = rev.getNextV();
		HalfEdge iter;
		
		//	erase edges in left face and right face
		iter = leftFace.getHalfEdge();
		System.out.print("Vertex:");
		do{
			System.out.print(iter.getNextV().idx + "->");
			iter.terminate();
			iter = iter.getNextE();
		}while(iter != leftFace.getHalfEdge());
		System.out.println();
		System.out.print("Vertex:");
		iter = rightFace.getHalfEdge();
		do{
			System.out.print(iter.getNextV().idx + "->");
			iter.terminate();
			iter = iter.getNextE();
		}while(iter != rightFace.getHalfEdge());
		System.out.println();
		//	set reverse edges
		leftUp.setFlipE(leftDown);
		leftDown.setFlipE(leftUp);
		rightUp.setFlipE(rightDown);
		rightDown.setFlipE(rightUp);

		//	Change vertex
		iter = leftUp;
		while(iter.getNextV().idx == up.idx){
			iter.setNextV(k);
			iter = iter.getNextE().getFlipE();
		}
		iter = rightDown;
		while(iter.getNextV().idx == down.idx){
			iter.setNextV(k);
			iter = iter.getNextE().getFlipE();
		}		
		//	Clear half edges in global variable
		System.out.println("Before renewal:(size)" + halfEdges.size());
		updateArray(leftFace.faceIdx, rightFace.faceIdx);
		System.out.println("After renewal:(size)" + halfEdges.size());
	}
	
	public void updateArray(int a, int b){
		System.out.println("Remove face:" + a + "and face:" + b);
		halfEdges.clear();
		ArrayList<Face> newFaces = new ArrayList<Face>();
		
		//	Change faceIdx
		for(int i = 0, iend = faces.size(); i < iend; i++){
			//System.out.println("Before changing:(faceIdx)" + faces.get(i).faceIdx);
			if(faces.get(i).faceIdx == a || faces.get(i).faceIdx == b)
				continue;
			newFaces.add(faces.get(i));
			//System.out.println("After changing:(faceIdx)" + faces.get(i).faceIdx);
		}
		for(int i = 0, iend = newFaces.size(); i < iend; i++){
			newFaces.get(i).faceIdx = i;
		}
		faces = newFaces;
		
		//	Update halfEdges
		for(int i = 0; i < faces.size(); i++){
			//System.out.print("Face:" + faces.get(i).faceIdx);
			HalfEdge iter = faces.get(i).getHalfEdge();
			//System.out.print(", Vertex:");
			do{
				//System.out.print(iter.getNextV().idx + "->");
				if(iter.getNextV().idx == 11249)
					System.out.println("Error");
				halfEdges.add(iter);
				iter = iter.getNextE();
			}while(iter != faces.get(i).getHalfEdge());
			//System.out.println();
		}

	}
	
	public Vertex getMergeVertex(Vertex a, Vertex b){
		Vector3f pos = a.pos.add(b.pos).div(2);
		Vector3f norm = a.norm;	//	How to calculate?
		int posIdx = a.idx;
		Vertex res = new Vertex(pos, norm, posIdx);
		a.pos = pos;
		b.pos = pos;
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
		//displayMapping();
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
		
		displayProperty();		
	}
	
	public void displayProperty(){
		//	change this part
		System.out.println("HalfEdges:(size)" + halfEdges.size());
		System.out.println("Faces:(size)" + faces.size());
		
	}
	
	public void displayMapping(){
		for (Entry<String, ArrayList<Integer>> entry : map.entrySet()) {  
		    System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());  		  
		}  
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
