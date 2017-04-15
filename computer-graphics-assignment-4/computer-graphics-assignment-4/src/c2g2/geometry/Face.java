package c2g2.geometry;

import java.util.Map;

public class Face {
	private HalfEdge e;
	private int faceIdx;
	public Map<Integer, Integer> order;
	public int v0, v1, v2;
	
	public Face(HalfEdge e0, int idx, Map<Integer, Integer> o, int vIdx){
		e = e0;
		faceIdx = idx;
		order = o;
		v0 = o.get(vIdx);
		v1 = o.get(v0);
		v2 = o.get(v1);
	}

	public HalfEdge getHalfEdge(){
		return e;
	}
	
	public int getFaceIdx(){
		return faceIdx;
	}
	
	public void setHalfEdge(HalfEdge tmp){
		e = tmp;
	}
}
