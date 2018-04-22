package c2g2.geometry;

import java.util.Comparator;

public class Face {
	private HalfEdge e;
	
	public int faceIdx;
	
	public Face(HalfEdge e0, int idx){
		e = e0;
		faceIdx = idx;
	}
	
	public HalfEdge getHalfEdge(){
		return e;
	}
}

