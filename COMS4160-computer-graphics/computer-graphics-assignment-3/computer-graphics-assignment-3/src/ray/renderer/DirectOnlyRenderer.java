package ray.renderer;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;

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
	private LuminaireSamplingRecord lsr = new LuminaireSamplingRecord(); 
	private Vector3 L = new Vector3();
	File file;
	BufferedImage image;
	int pixel;
	int x, y;
	int r, g, b;
	double red, green, blue;
	public enum TextureType { PROCEDURAL_TEXTURE, IMAGE_TEXTURE, NONE_TEXTURE } 

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
    	
    	/*
    	 * Select texture type for rendering
    	 */
    	TextureType tType = TextureType.NONE_TEXTURE;
    	if(tType == TextureType.IMAGE_TEXTURE){
    		IntersectionRecord ir = new IntersectionRecord();
			if(scene.getFirstIntersection(ir, ray)){
	    		if(ir.texCoords.x != 0){
	    			file = new File("noise.png");
	    			try {
	    				image = ImageIO.read(file);
		    			x = (int)(ir.texCoords.x * 256);	// change 256 to image.width for general picture
		    			y = (int)(ir.texCoords.y * 256);
		                pixel = image.getRGB(x, y);   
		                r = (pixel >> 16) & 0x000000FF;
						g = (pixel >>8 ) & 0x000000FF;
						b = (pixel) & 0x000000FF;
						red = (double)r/256;
						blue = (double)b/256;
						green = (double)g/256;
		                outColor.set(red, green, blue);
	    			} catch (IOException e) {
	    				e.printStackTrace();
	    			}
	    		}
	    		else{
	    			Color emittedRadiance = new Color();
		    		emittedRadiance(ir, ray.direction, emittedRadiance);
		    		sampler.sample(1, sampleIndex, seed);
		    		Color directRadiance = new Color();
		    		Vector3 outDir = new Vector3();
		    		outDir.set(ray.direction);
		    		outDir.scale(-1);
		    		direct.directIllumination(scene, L, outDir, ir, seed, directRadiance);
		    		outColor.set(emittedRadiance);
		    		outColor.add(directRadiance);
	    		}
	       	} else {
	    		scene.getBackground().evaluate(ray.direction, outColor);
	    	}
    	} else if(tType == TextureType.PROCEDURAL_TEXTURE){
    		IntersectionRecord ir = new IntersectionRecord();
    		if(scene.getFirstIntersection(ir, ray)){
    			Random random = new Random();
	    		if(ir.texCoords.x != 0){	    			
	    			int jump = (int)(ir.texCoords.x * 100);
	    			//System.out.println(random.nextDouble());
	    			if(jump %10 < 5)
	    				outColor.set(1 - random.nextDouble(), 0, 0);
	    			else
	    				outColor.set(0, 0, 1 - random.nextDouble());
	    		}
	    		else{
	    			Color emittedRadiance = new Color();
		    		emittedRadiance(ir, ray.direction, emittedRadiance);
		    		sampler.sample(1, sampleIndex, seed);
		    		Color directRadiance = new Color();
		    		Vector3 outDir = new Vector3();
		    		outDir.set(ray.direction);
		    		outDir.scale(-1);
		    		direct.directIllumination(scene, L, outDir, ir, seed, directRadiance);
		    		outColor.set(emittedRadiance);
		    		outColor.add(directRadiance);
	    		}
	       	} else {
	    		scene.getBackground().evaluate(ray.direction, outColor);
	    	}
    	} else if(tType == TextureType.NONE_TEXTURE){	//	direct illumination
    		IntersectionRecord ir = new IntersectionRecord();
    		if(scene.getFirstIntersection(ir, ray)){
    			Color emittedRadiance = new Color();
	    		this.emittedRadiance(ir, ray.direction, emittedRadiance);
	    		sampler.sample(1, sampleIndex, seed);
	    		Color directRadiance = new Color();
	    		Vector3 outDir = new Vector3();
	    		outDir.set(ray.direction);
	    		outDir.scale(-1);
	    		outDir.normalize();
	    		direct.directIllumination(scene, L, outDir, ir, seed, directRadiance);
	    		outColor.set(emittedRadiance);
	    		outColor.add(directRadiance);
    		} else {
    			//scene.getBackground().evaluate(ray.direction, outColor);
    			outColor.set(0.0);
    		}
    	}

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
    	
		Material m = iRec.surface.getMaterial();
		if(m.isEmitter()){
			lsr.set(iRec);
			lsr.emitDir.set(dir);
			lsr.emitDir.scale(-1);
			iRec.surface.getMaterial().emittedRadiance(lsr, outColor);
		} else {
			outColor.set(0.0);
		}
    }
}
