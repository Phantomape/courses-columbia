package test;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import javafx.scene.image.Image;
import libnoiseforjava.exception.ExceptionInvalidParam;
import libnoiseforjava.module.Perlin;
import libnoiseforjava.util.ColorCafe;
import libnoiseforjava.util.ImageCafe;
import libnoiseforjava.util.NoiseMap;
import libnoiseforjava.util.NoiseMapBuilderPlane;
import libnoiseforjava.util.RendererImage;

public class test {

	public static void main(String[] args) throws ExceptionInvalidParam, IOException {
		// TODO Auto-generated method stub
		/*
		Perlin perlin = new Perlin();
		NoiseMap heightMap = new NoiseMap(640, 480);
		NoiseMapBuilderPlane heightMapBuilder = new NoiseMapBuilderPlane();
		heightMapBuilder.setSourceModule(perlin);
		heightMapBuilder.setDestNoiseMap(heightMap);
		heightMapBuilder.setDestSize(256, 256);
		heightMapBuilder.setBounds(1.0, 5.0, 1.0, 5.0);
		heightMapBuilder.build();
		RendererImage renderer = new RendererImage();
	  	ImageCafe image = new ImageCafe(256, 256);
	  	renderer.setSourceNoiseMap (heightMap);
	  	renderer.setDestImage (image);
	  	renderer.clearGradient ();
	    renderer.addGradientPoint (-1.0000, new ColorCafe (  0,   0, 128, 255)); // deeps
	    renderer.addGradientPoint (-0.2500, new ColorCafe (  0,   0, 255, 255)); // shallow
	    renderer.addGradientPoint ( 0.0000, new ColorCafe (  0, 128, 255, 255)); // shore
	    renderer.addGradientPoint ( 0.0625, new ColorCafe (240, 240,  64, 255)); // sand
	    renderer.addGradientPoint ( 0.1250, new ColorCafe ( 32, 160,   0, 255)); // grass
	    renderer.addGradientPoint ( 0.3750, new ColorCafe (224, 224,   0, 255)); // dirt
	    renderer.addGradientPoint ( 0.7500, new ColorCafe (128, 128, 128, 255)); // rock
	    renderer.addGradientPoint ( 1.0000, new ColorCafe (255, 255, 255, 255)); // snow
	    
	  	renderer.render();
	  	System.out.println("Rendering done.");
	  	//ColorCafe color = image.getValue(1, 0);
	  	BufferedImage img = new BufferedImage(256, 256,  BufferedImage.TYPE_INT_RGB);
	  	for(int i = 0; i < 256; i++)
	  		for(int j = 0; j < 256; j++){
	  			Color pixel = new Color(image.getValue(i, j).getRed(), image.getValue(i, j).getGreen(), image.getValue(i, j).getBlue());
	  			img.setRGB(i, j, pixel.getRGB());
	  		}
	  	
	  	File outputfile = new File("noise.png");
	  	ImageIO.write(img, "png", outputfile);
	  	System.out.println("Imaging done.");
	*/
	  	File file = new File("noise.png");
		try {
			BufferedImage image = ImageIO.read(file);
			for(int x = 0; x < 256; x++)
				for(int y = 0; y < 256; y++){

					int pixel = image.getRGB(x, y);   
					int red = (pixel >> 16) & 0x000000FF;
					int green = (pixel >>8 ) & 0x000000FF;
					int blue = (pixel) & 0x000000FF;
					System.out.println(x + "," + y + ":" + pixel);
		            System.out.println(red);
		            System.out.println(green);
		            System.out.println(blue);
		            System.out.println("-----");
		            
				}
            
     
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}

}
