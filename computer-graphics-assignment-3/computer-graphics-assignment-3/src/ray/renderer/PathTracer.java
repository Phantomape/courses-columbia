package ray.renderer;

import ray.brdf.BRDF;
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

public abstract class PathTracer extends DirectOnlyRenderer {
	
	
	//Color brdf = new Color();
	Color irradiance = new Color();
	Color incRadiance = new Color();
	Ray sample = new Ray();
	
	IntersectionRecord lightIRec = new IntersectionRecord();
	LuminaireSamplingRecord lRec = new LuminaireSamplingRecord();
	
    protected int depthLimit = 5;
    protected int backgroundIllumination = 1;

    public void setDepthLimit(int depthLimit) { this.depthLimit = depthLimit; }
    public void setBackgroundIllumination(int backgroundIllumination) { this.backgroundIllumination = backgroundIllumination; }

    @Override
    public void rayRadiance(Scene scene, Ray ray, SampleGenerator sampler, int sampleIndex, Color outColor) {
    
        rayRadianceRecursive(scene, ray, sampler, sampleIndex, 0, outColor);
    }

    protected abstract void rayRadianceRecursive(Scene scene, Ray ray, SampleGenerator sampler, int sampleIndex, int level, Color outColor);

    public void gatherIllumination(Scene scene, Vector3 outDir, 
            IntersectionRecord iRec, SampleGenerator sampler, 
            int sampleIndex, int level, Color outColor) {
    	// W4160 TODO (B)
    	//
        // This method computes a Monte Carlo estimate of reflected radiance due to direct and/or indirect 
        // illumination.  It generates samples uniformly wrt. the projected solid angle measure:
        //
        //    f = brdf * radiance
        //    p = 1 / pi
        //    g = f / p = brdf * radiance * pi
    	// You need: 
    	//   1. Generate a random incident direction according to proj solid angle
    	//      pdf is constant 1/pi
    	//   2. Recursively find incident radiance from that direction
    	//   3. Estimate the reflected radiance: brdf * radiance / pdf = pi * brdf * radiance
    	//
    	// Here you need to use Geometry.squareToPSAHemisphere that you implemented earlier in this function
    	
    	//	generate random variable and get random incident direction
    
    	
    	//   1. Generate a random incident direction according to proj solid angle
    	Point2 seed = new Point2();
        sampler.sample(1, sampleIndex, seed); 
        Vector3 incDir = new Vector3();
    	Geometry.squareToPSAHemisphere(seed, incDir);
        iRec.frame.frameToCanonical(incDir);
        incDir.normalize();

    	Color brdf = new Color();
    	Color out = new Color();
    	Color indirect = new Color();
        
    	Material material = iRec.surface.getMaterial();
    	material.getBRDF(iRec).evaluate(iRec.frame, incDir, outDir, brdf);
            
            
            //scene.incidentRadiance(iRec.frame.o, incDir, out);
            
            //generate the new reflected light 
        Ray newRay = new Ray(iRec.frame.o, incDir);
        newRay.makeOffsetRay();
        // recursively compute incident radiance
        rayRadianceRecursive(scene, newRay, sampler, sampleIndex, level+1, indirect);
       
        //out.scale(brdf);
       // out.scale(Math.PI); 
        indirect.scale(brdf);
        indirect.scale(Math.PI); 
        out.add(indirect);
        outColor.add(out);
    	
 

    	/*
    	//   1. Generate a random incident direction according to proj solid angle
        sampler.sample(1, sampleIndex, seed); 
    	Geometry.squareToPSAHemisphere(seed, incDir);
        iRec.frame.frameToCanonical(incDir);
        incDir.normalize();
        
        sample.set(iRec.frame.o, incDir);
		sample.makeOffsetRay();
		
        if (scene.getFirstIntersection(lightIRec, sample) && lightIRec.surface.getMaterial().isEmitter()) {
			Material material = iRec.surface.getMaterial();
			material.getBRDF(iRec).evaluate(iRec.frame, incDir, outDir, brdf);
			scene.incidentRadiance(iRec.frame.o, incDir, incRadiance);
			incRadiance.scale(brdf);
			incRadiance.scale(Math.PI);
			sample = new Ray(iRec.frame.o, incDir);
			sample.makeOffsetRay();
			rayRadianceRecursive(scene, sample, sampler, sampleIndex, level + 1, irradiance);
            irradiance.scale(brdf);
            irradiance.scale(Math.PI);
            irradiance.add(incRadiance);
            outColor.add(irradiance);
		} else {
			outColor.set(0);
			//scene.getBackground().evaluate(ray.direction, outColor);
		}
        */

    }
    
    public void rayRadianceExt(Scene scene, Ray ray, IntersectionRecord iRec,
            SampleGenerator sampler, int sampleIndex, int level, Color outColor) {
    	LuminaireSamplingRecord lsr = new LuminaireSamplingRecord(); 
    	Color emittedRadiance = new Color();
    	Color gatherRadiance = new Color();
    	
    	Vector3 outDir = new Vector3();  
    	if(scene.getFirstIntersection(iRec, ray)){
    		//	1) compute the emitted light radiance from the current surface if the surface is a light surface
    		emittedRadiance(iRec, ray.direction, emittedRadiance);
    		outColor.set(emittedRadiance);
    		if(level <= depthLimit){
	    		gatherIllumination(scene, outDir, iRec, sampler, sampleIndex, level, gatherRadiance);
	    		outColor.add(gatherRadiance);
    		}
       	} else {
    		scene.getBackground().evaluate(ray.direction, outColor);
    	}

    }
}
