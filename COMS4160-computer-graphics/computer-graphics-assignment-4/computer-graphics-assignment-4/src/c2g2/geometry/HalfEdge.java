package c2g2.geometry;

/*
 * The main class that implements the half-edge structure.
 * TODO:
 * Please add more code if needed
 */
public class HalfEdge {
	
	// A pointer to the next halfEdge 
	private HalfEdge nextE = null;
	// A pointer to the HalfEdge that is along the opposite direction
	private HalfEdge flipE = null;
	// A pointer to the next vertex
	private Vertex nextV = null;
	// A pointer to the left face
	private Face lFace=null;
	
	public HalfEdge(Vertex v){
		nextV = v;
	}
	
	public Face getlFace(){
		return lFace;
	}
	
	public HalfEdge getNextE(){
		return nextE;
	}
	
	public HalfEdge getFlipE(){
		return flipE;
	}
	
	public Vertex getNextV(){
		return nextV;
	}
	
	public void setlFace(Face f){
		lFace = f;
	}
	
	public void setNextE(HalfEdge e){
		nextE = e;
	}
	
	public void setFlipE(HalfEdge e){
		flipE = e;
	}
	
	public void setNextV(Vertex v) {
		nextV = v;
	}
	
	public void terminate(){
		flipE = null;
		nextV = null;
		lFace=null;
	}
}
