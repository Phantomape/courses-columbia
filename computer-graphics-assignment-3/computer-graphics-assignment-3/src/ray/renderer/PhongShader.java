package ray.renderer;

import ray.light.PointLight;
import ray.math.Vector3;
import ray.misc.Color;
import ray.misc.IntersectionRecord;
import ray.misc.Ray;
import ray.misc.Scene;
import ray.sampling.SampleGenerator;

public class PhongShader implements Renderer {
    
    private double phongCoeff = 2.5;
    
    public PhongShader() { }
    
    public void setAlpha(double a) {
        phongCoeff = a;
    }
    
    @Override
    public void rayRadiance(Scene scene, Ray ray, SampleGenerator sampler,
            int sampleIndex, Color outColor) {
        // W4160 TODO (A)
        // Here you need to implement the basic phong reflection model to calculate
        // the color value (radiance) along the given ray. The output color value 
        // is stored in outColor. 
        // 
        // For such a simple rendering algorithm, you might not need Monte Carlo integration
        // In this case, you can ignore the input variable, sampler and sampleIndex.
    	
    	//	Following tutorial on http://www.cnblogs.com/Baesky/archive/2010/11/12/1876157.html and
    	//	http://blog.csdn.net/xueyedie1234/article/details/51455875#1-phong-
    	Vector3 N;	//	normal vector on the object surface
    	Vector3 V;	//	view vector
    	Vector3 L;	//	vector pointing to light source
    	Vector3 R;	//	reflected vector
		Color diffuse, specular;
		IntersectionRecord ir = new IntersectionRecord();

		if (scene.getFirstIntersection(ir, ray)) {
			outColor.set(0);

			L = new Vector3();

			R = new Vector3();

			N = new Vector3(ir.frame.w);
			N.normalize();

			Ray view = new Ray();
			V = new Vector3(view.direction);
			V.normalize();

			scene.getCamera().getRay(view, ir.texCoords.x, ir.texCoords.y);

			diffuse = new Color();
			specular = new Color();

			for (PointLight pl : scene.getPointLights()) {

				L.sub(pl.location, ir.frame.o);

				if (L.dot(N) > 0) {
					// find reflection direction
					double NL = L.dot(N);
					R.set(N);
					R.scale(2 * NL);
					R.sub(L);
					R.normalize();

					// add diffuse
					ir.surface.getMaterial().getBRDF(ir)
							.evaluate(ir.frame, L, R, diffuse);
					diffuse.scale(L.dot(N));
					diffuse.scale(pl.diffuse);
					outColor.add(diffuse);

					// add specular
					ir.surface.getMaterial().getBRDF(ir)
							.evaluate(ir.frame, L, R, specular);
					specular.scale(Math.pow(R.dot(V), phongCoeff));
					specular.scale(pl.specular);
					outColor.add(specular);
				}
			}
		} else {
			scene.getBackground().evaluate(ray.direction, outColor);
		}
		
    }
}
