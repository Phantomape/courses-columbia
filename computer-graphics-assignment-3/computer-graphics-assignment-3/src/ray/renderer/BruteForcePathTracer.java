package ray.renderer;

import ray.material.Material;
import ray.math.Vector3;
import ray.misc.Color;
import ray.misc.IntersectionRecord;
import ray.misc.LuminaireSamplingRecord;
import ray.misc.Ray;
import ray.misc.Scene;
import ray.sampling.SampleGenerator;

public class BruteForcePathTracer extends PathTracer {

	
    /**
     * @param scene
     * @param ray
     * @param sampler
     * @param sampleIndex
     * @param outColor
     */
    protected void rayRadianceRecursive(Scene scene, Ray ray, 
            SampleGenerator sampler, int sampleIndex, int level, Color outColor) {
    	// W4160 TODO (B)
    	//
        // Find the visible surface along the ray, then add emitted and reflected radiance
        // to get the resulting color.
    	//
    	// If the ray depth is less than the limit (depthLimit), you need
    	// 1) compute the emitted light radiance from the current surface if the surface is a light surface
    	// 2) reflected radiance from other lights and objects. You need recursively compute the radiance
    	//    hint: You need to call gatherIllumination(...) method.
    	

    	
    	if(level == 0)
    		outColor.set(0);
    	
    	if(level == this.depthLimit){
    		outColor.set(0);
    		return;
    	}
    	IntersectionRecord iRec = new IntersectionRecord();
		if (scene.getFirstIntersection(iRec, ray)) 
		{
			//	Normal global illumination
			Vector3 outDir = new Vector3();
			outDir.set(ray.direction); 
            outDir.scale(-1.0);
            outDir.normalize();
        	
            Color emittedRadiance = new Color();
            emittedRadiance(iRec, ray.direction, emittedRadiance);
            outColor.set(emittedRadiance);
        	
        	Color gatherRadiance = new Color();
            gatherIllumination(scene, outDir, iRec, sampler, sampleIndex, level, gatherRadiance);
            outColor.add(gatherRadiance);
        	return;
		} else{
			//scene.getBackground().evaluate(ray.direction, outColor);
			outColor.set(0);
		}

    	
    }
}
