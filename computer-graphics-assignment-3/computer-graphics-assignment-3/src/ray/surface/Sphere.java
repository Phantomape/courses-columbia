package ray.surface;

import ray.accel.AxisAlignedBoundingBox;
import ray.material.Material;
import ray.math.Geometry;
import ray.math.Point2;
import ray.math.Point3;
import ray.math.Vector3;
import ray.misc.IntersectionRecord;
import ray.misc.LuminaireSamplingRecord;
import ray.misc.Ray;

/**
 * Represents a sphere as a center and a radius.
 *
 * @author ags
 */
public class Sphere extends Surface {
    
    /** Material for this sphere. */
    protected Material material = Material.DEFAULT_MATERIAL;
    
    /** The center of the sphere. */
    protected final Point3 center = new Point3();
    
    /** The radius of the sphere. */
    protected double radius = 1.0;
    
    /**
     * Default constructor, creates a sphere at the origin with radius 1.0
     */
    public Sphere() {        
    }
    
    /**
     * The explicit constructor. This is the only constructor with any real code
     * in it. Values should be set here, and any variables that need to be
     * calculated should be done here.
     *
     * @param newCenter The center of the new sphere.
     * @param newRadius The radius of the new sphere.
     * @param newMaterial The material of the new sphere.
     */
    public Sphere(Vector3 newCenter, double newRadius, Material newMaterial) {        
        material = newMaterial;
        center.set(newCenter);
        radius = newRadius;
        updateArea();
    }
    
    public void updateArea() {
    	area = 4 * Math.PI * radius*radius;
    	oneOverArea = 1. / area;
    }
    
    /**
     * @see ray1.surface.Surface#getMaterial()
     */
    public Material getMaterial() {
        return material;
    }
    
    /**
     * @see ray1.surface.Surface#setMaterial(ray1.material.Material)
     */
    public void setMaterial(Material material) {
        this.material = material;
    }
    
    /**
     * Returns the center of the sphere in the input Point3
     * @param outPoint output space
     */
    public void getCenter(Point3 outPoint) {        
        outPoint.set(center);        
    }
    
    /**
     * @param center The center to set.
     */
    public void setCenter(Point3 center) {        
        this.center.set(center);        
    }
    
    /**
     * @return Returns the radius.
     */
    public double getRadius() {
        return radius;
    }
    
    /**
     * @param radius The radius to set.
     */
    public void setRadius(double radius) {
        this.radius = radius;
        updateArea();
    }
    
    public boolean chooseSamplePoint(Point3 p, Point2 seed, LuminaireSamplingRecord lRec) {
        Geometry.squareToSphere(seed, lRec.frame.w);
        lRec.frame.o.set(center);
        lRec.frame.o.scaleAdd(radius, lRec.frame.w);
        lRec.frame.initFromW();
        lRec.pdf = oneOverArea;
        lRec.emitDir.sub(p, lRec.frame.o);
        return true;
    }
        
    /**
     * @see ray1.surface.Surface#intersect(ray1.misc.IntersectionRecord,
     *      ray1.misc.Ray)
     */
    public boolean intersect(IntersectionRecord outRecord, Ray ray) {
        // W4160 TODO (A)
    	// In this function, you need to test if the given ray intersect with a sphere.
    	// You should look at the intersect method in other classes such as ray.surface.Triangle.java
    	// to see what fields of IntersectionRecord should be set correctly.
    	// The following fields should be set in this function
    	//     IntersectionRecord.t
    	//     IntersectionRecord.frame
    	//     IntersectionRecord.surface
    	//
    	// Note: Although a ray is conceptually a infinite line, in practice, it often has a length,
    	//       and certain rendering algorithm relies on the length. Therefore, here a ray is a 
    	//       segment rather than a infinite line. You need to test if the segment is intersect
    	//       with the sphere. Look at ray.misc.Ray.java to see the information provided by a ray.
    	double xc = center.x;
    	double yc = center.y;
    	double zc = center.z;
    	
    	double ecx = ray.origin.x - xc;
    	double ecy = ray.origin.y - yc;
    	double ecz = ray.origin.z - zc;
    	
    	double A = ray.direction.x * ray.direction.x + ray.direction.y * ray.direction.y + ray.direction.z * ray.direction.z;
    	double B = 2 * (ray.direction.x * ecx + ray.direction.y * ecy + ray.direction.z * ecz);
    	double C = - radius * radius + (ecx * ecx + ecy * ecy + ecz * ecz);
    	
    	double D = B * B - 4 * A * C;
    	double t = 0;
    	
    	if(D < 0)	
    		return false;
    	else if(D == 0){
    		t = -B /(2 * A);
        	if(t < ray.start || t > ray.end)
        		return false;
    	} else {
    		double t1 = (- B - Math.sqrt(D)) / (2 * A);
    		double t2 = (- B - Math.sqrt(D)) /(2 * A);
    		boolean validt1 = (t1 < ray.start || t1 > ray.end) ? false : true;
    		boolean validt2 = (t2 < ray.start || t2 > ray.end) ? false : true;
    		if(!validt1 && !validt2)
    			return false;
    		else if(!validt1 && validt2)
    			t = t2;
    		else if(validt1 && !validt2)
    			t = t1;
    		else
    			t = t1 > t2 ? t2 : t1;
    	}
    	
        // Fill out the record
        outRecord.t = t;
        outRecord.surface = this;

        ray.evaluate(outRecord.frame.o, t);
        outRecord.frame.w.sub(outRecord.frame.o, this.center);
        outRecord.frame.w.normalize();
        outRecord.frame.initFromW();
        
        double x = (ray.origin.x + t * ray.direction.x - xc)/radius;
        double y = (ray.origin.y + t * ray.direction.y - yc)/radius;
        double z = (ray.origin.z + t * ray.direction.z - zc)/radius;
        double theta = Math.acos(y);
        double phi = Math.atan2(z * radius, x * radius);
        if (phi < 0)
          phi += 2 * Math.PI;
        double u = phi / (2 * Math.PI);
        double v = (Math.PI - theta)/Math.PI;
        outRecord.texCoords = new Point2(u, v);
        return true;
    }
    
    /**
     * @see Object#toString()
     */
    public String toString() {
        return "sphere " + center + " " + radius + " " + material + " end";
    }
    
    /**
     * @see ray1.surface.Surface#addToBoundingBox(ray1.surface.accel.AxisAlignedBoundingBox)
     */
    public void addToBoundingBox(AxisAlignedBoundingBox inBox) {
        inBox.add(center.x - radius, center.y - radius, center.z - radius);
        inBox.add(center.x + radius, center.y + radius, center.z + radius);        
    }
    
}
