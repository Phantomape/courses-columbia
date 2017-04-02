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


/**
 * This class computes direct illumination at a surface by the simplest possible approach: it estimates
 * the integral of incident direct radiance using Monte Carlo integration with a uniform sampling
 * distribution.
 * 
 * The class has two purposes: it is an example to serve as a starting point for other methods, and it
 * is a useful base class because it contains the generally useful <incidentRadiance> function.
 * 
 * @author srm, Changxi Zheng (at Columbia)
 */
public class ProjSolidAngleIlluminator extends DirectIlluminator {
    
    
    public void directIllumination(Scene scene, Vector3 incDir, Vector3 outDir, 
            IntersectionRecord iRec, Point2 seed, Color outColor) {

        // W4160 TODO (A)
    	// This method computes a Monte Carlo estimate of reflected radiance due to direct illumination.  It
        // generates samples uniformly wrt. the projected solid angle measure:
        //
        //    f = brdf * radiance
        //    p = 1 / pi
        //    g = f / p = brdf * radiance * pi
        //
        // The same code could be interpreted as an integration wrt. solid angle, as follows:
        //
        //    f = brdf * radiance * cos_theta
        //    p = cos_theta / pi
        //    g = f / p = brdf * radiance * pi
    	// 
    	// As a hint, here are a few steps when I code this function
    	// 1. Generate a random incident direction according to proj solid angle
        //    pdf is constant 1/pi
    	
    	
    	
    	//	randome incident direction
    	Geometry.squareToPSAHemisphere(seed, incDir);
    	
    	iRec.frame.frameToCanonical(incDir);
    	incDir.normalize();
 
    	//	normal vector ar the intersection point is w, calculate outDir according 
    	//	to outDir = 2 * normal * (incDir·normal) - incDir
    	Vector3 normal = new Vector3();
    	double tmp = incDir.dot(normal);
    	normal.set(iRec.frame.w);
    	normal.normalize();
    	outDir.set(normal);
    	outDir.scale(2 * tmp);
    	outDir.sub(incDir);
    	outDir.normalize();
    	
    	Color brdf = new Color();
    	Color irradiance = new Color();
    	Ray sample = new Ray();
    	LuminaireSamplingRecord lRec = new LuminaireSamplingRecord();
    	IntersectionRecord lightIRec = new IntersectionRecord();
		sample.set(iRec.frame.o, incDir);
		sample.makeOffsetRay();
		if (scene.getFirstIntersection(lightIRec, sample) && lightIRec.surface.getMaterial().isEmitter()) {
			/*
			 * if our surface is directly illuminated, calculate the rendering
			 * equation terms
			 */

			/* get BRDF */
			Material m = iRec.surface.getMaterial();
			m.getBRDF(iRec).evaluate(iRec.frame, incDir, outDir, brdf);

			// 2. Find incident radiance from that direction
			lightIRec.surface.getMaterial().emittedRadiance(lRec, irradiance);
			//irradiance.scale(iRec.frame.w.dot(incDir));

			// 3. Estimate reflected radiance using brdf * radiance / pdf = pi * brdf * radiance
			outColor.set(1.0);
			outColor.scale(brdf);
			outColor.scale(irradiance);
			outColor.scale(Math.PI);

		} else {

			/* otherwise, there is no illumination from this sample direction */
			outColor.set(0);
		}
    }
    
}
