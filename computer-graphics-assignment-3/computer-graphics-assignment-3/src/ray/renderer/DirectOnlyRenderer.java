package ray.renderer;

import ray.material.Material;
import ray.math.Geometry;
import ray.math.Point2;
import ray.math.Vector3;
import ray.misc.Color;
import ray.misc.IntersectionRecord;
import ray.misc.LuminaireSamplingRecord;
import ray.misc.Ray;
import ray.misc.Scene;
import ray.sampling.SampleGenerator;

/**
 * A renderer that computes radiance due to emitted and directly reflected light only.
 * 
 * @author cxz (at Columbia)
 */
public class DirectOnlyRenderer implements Renderer {
	private Point2 seed = new Point2();
	private IntersectionRecord ir = new IntersectionRecord();
	private LuminaireSamplingRecord lsr = new LuminaireSamplingRecord(); 
	private Color er = new Color();
	private Color dr = new Color();
	private Vector3 L = new Vector3();
	private Vector3 R = new Vector3();
    /**
     * This is the object that is responsible for computing direct illumination.
     */
    DirectIlluminator direct = null;
        
    /**
     * The default is to compute using uninformed sampling wrt. projected solid angle over the hemisphere.
     */
    public DirectOnlyRenderer() {
        this.direct = new ProjSolidAngleIlluminator();
    }
    
    
    /**
     * This allows the rendering algorithm to be selected from the input file by substituting an instance
     * of a different class of DirectIlluminator.
     * @param direct  the object that will be used to compute direct illumination
     */
    public void setDirectIlluminator(DirectIlluminator direct) {
        this.direct = direct;
    }

    
    public void rayRadiance(Scene scene, Ray ray, SampleGenerator sampler, int sampleIndex, Color outColor) {
        // W4160 TODO (A)
    	// In this function, you need to implement your direct illumination rendering algorithm
    	//
    	// you need:
    	// 1) compute the emitted light radiance from the current surface if the surface is a light surface
    	// 2) direct reflected radiance from other lights. This is by implementing the function
    	//    ProjSolidAngleIlluminator.directIlluminaiton(...), and call direct.directIllumination(...) in this
    	//    function here.
    	//	Find whther there is intersection
    	
    	if(scene.getFirstIntersection(ir, ray)){
    		//	emmitted light radiance
    		emittedRadiance(ir, ray.direction, er);
    		//	sampling
    		sampler.sample(1, sampleIndex, seed);
    		direct.directIllumination(scene, L, R, ir, seed, dr);
    		
    		//	set color
    		outColor.set(er);
    		outColor.add(dr);
       	} else {
    		scene.getBackground().evaluate(ray.direction, outColor);
    	}
    	
    	/*
    	        IntersectionRecord iRec = new IntersectionRecord();
    	 
    	         if (scene.getFirstIntersection(iRec, ray)) {
    	 
    	             Color emittedRadiance = new Color();
    	             emittedRadiance(iRec, ray.direction, emittedRadiance);
    	             Point2 directSeed = new Point2();
    	             sampler.sample(1, sampleIndex, directSeed);     // this random variable is for incident direction
    	 
    	             // Generate a random incident direction
    	             Vector3 L = new Vector3();
    	             Geometry.squareToPSAHemisphere(directSeed, L);
    	             iRec.frame.frameToCanonical(L);
    	 
    	             Vector3 N = new Vector3(iRec.frame.w);
    	             N.normalize();
    	 
    	             //find reflection direction
    	             Vector3 R = new Vector3();
    	             R.set(N);
    	             R.scale(2 * L.dot(N));
    	             R.sub(L);
    	             R.normalize();
    	 
    	             Color directRadiance = new Color();
    	             direct.directIllumination(scene, L, R, iRec, directSeed, directRadiance);
    	 
    	             outColor.set(emittedRadiance);
    	             outColor.add(directRadiance);
    	         } else {
    	             scene.getBackground().evaluate(ray.direction, outColor);
    	         }
    	         */
    }

    
    /**
     * Compute the radiance emitted by a surface.
     * @param iRec      Information about the surface point being shaded
     * @param dir          The exitant direction (surface coordinates)
     * @param outColor  The emitted radiance is written to this color
     */
    protected void emittedRadiance(IntersectionRecord iRec, Vector3 dir, Color outColor) {
    	// W4160 TODO (A)
        // If material is emitting, query it for emission in the relevant direction.
        // If not, the emission is zero.
    	// This function should be called in the rayRadiance(...) method above
    	
		Material m = ir.surface.getMaterial();
		if(m.isEmitter()){
			lsr.set(ir);
			//Vector3 tmp = new Vector3(-1 * dir.x, -1 * dir.y, -1 * dir.z);
			//lsr.emitDir.set(tmp);
			lsr.emitDir.set(dir);
			lsr.emitDir.scale(-1);
			ir.surface.getMaterial().emittedRadiance(lsr, outColor);
		} else {
			outColor.set(0.0);
		}
		/*
                LuminaireSamplingRecord lRec = new LuminaireSamplingRecord();
    	            lRec.set(iRec);
    	             lRec.emitDir.set(dir);
    	             lRec.emitDir.scale(-1);
    	 iRec.surface.getMaterial().emittedRadiance(lRec, outColor);
    	 */
    }
}
