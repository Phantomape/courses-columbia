package ray.material;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;

import ray.brdf.BRDF;
import ray.brdf.Lambertian;
import ray.math.Vector3;
import ray.misc.Color;
import ray.misc.IntersectionRecord;
import ray.misc.LuminaireSamplingRecord;
import ray.renderer.DirectOnlyRenderer.TextureType;

public class Texture implements Material{
	
	Color textureColor = new Color();

	BRDF brdf = new Lambertian();
	
	public enum TextureType { PROCEDURAL_TEXTURE, IMAGE_TEXTURE, NONE_TEXTURE } 
	
	public Texture() { }

	public void setBRDF(BRDF brdf) { this.brdf = brdf; }

	public BRDF getBRDF(IntersectionRecord iRec) {
		return brdf;
	}

	public void emittedRadiance(LuminaireSamplingRecord lRec, Color outRadiance) {
		outRadiance.set(0, 0, 0);
	}

	public boolean isEmitter() {
		return false;
	}
	
	public Color getTextureColor(IntersectionRecord iRec){
		TextureType tex = TextureType.PROCEDURAL_TEXTURE;
		if(tex == TextureType.IMAGE_TEXTURE){
			File file;
			BufferedImage image;
    		if(iRec.texCoords.x != 0){
    			file = new File("noise.png");
    			try {
    				image = ImageIO.read(file);
    				//	get corresponding texture coordinates in the image coordinates
	    			int x = (int)(iRec.texCoords.x * image.getWidth());	// change 256 to image.width for general picture
	    			int y = (int)(iRec.texCoords.y * image.getHeight());
	                int pixel = image.getRGB(x, y);   
	                int r = (pixel >> 16) & 0x000000FF;
					int g = (pixel >>8 ) & 0x000000FF;
					int b = (pixel) & 0x000000FF;
					double red = (double)r/256;
					double blue = (double)b/256;
					double green = (double)g/256;
	                textureColor.set(red, green, blue);
    			} catch (IOException e) {
    				e.printStackTrace();
    			}
    		}
    	} else if(tex == TextureType.PROCEDURAL_TEXTURE){
			Random random = new Random();
    		if(iRec.texCoords.x != 0){	    			
    			int jump = (int)(iRec.texCoords.x * 100);
    			//System.out.println(random.nextDouble());
    			if(jump %10 < 5)
    				textureColor.set(1 - random.nextDouble(), 0, 0);
    			else
    				textureColor.set(0, 0, 1 - random.nextDouble());
    		}
    	} else if(tex == TextureType.NONE_TEXTURE){	//	direct illumination
    		return textureColor;
    	}
		return textureColor;
	}
	
	public boolean hasTexture(){
		return true;
	}

}
