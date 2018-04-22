package c2g2.animation;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import java.nio.FloatBuffer;
import java.util.ArrayList;

import org.joml.Vector3d;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import c2g2.kinematics3D.Skeleton3D;

public class AnimationClip {
	
	public boolean isRendering;
	
	public boolean isLooping;
	
	public double stride = 0.02;
	
	public ArrayList<AnimationSample> samples;
	
	//public ArrayList<ArrayList<Vector3d>> buf;
	
	public int numPoints;	//	number of points
	
	public float[][] buf;
	
	public AnimationClip(){
		isRendering = false;
		samples = new ArrayList<AnimationSample>();
		numPoints = 0;
	}

	public void init() {
		numPoints = samples.get(0).getLen()/3;
		buf = new float[(int) (1.0/stride) + 1][numPoints * 3];
		
		for(int idx = 0; idx < numPoints; idx++){
			System.out.println("Idx of Joint to be interpolated: " + idx);
			ArrayList<Vector3d> keyPoints = new ArrayList<Vector3d>();
			for(int j = 0; j < samples.size(); j++){
				keyPoints.add(samples.get(j).getPoints(idx));
			}
			for(int j = 0; j < keyPoints.size(); j++){
				System.out.println("time " + j + "(" + keyPoints.get(j).x + "," + keyPoints.get(j).y + "," + keyPoints.get(j).z + ")");
			}
			interpolate(keyPoints, idx);
			
			//	Test
			/*
			System.out.println("buf:");
			for(int i = 0; i < buf.length; i++){
				int j = idx;
				System.out.print("(" + buf[i][j * 3] + "," + buf[i][j * 3 + 1] + "," + buf[i][j * 3 + 2] + ")");
				System.out.println();
			}
			System.out.println();
			*/
		}
		

	}
	
	public void interpolate(ArrayList<Vector3d> keyPoints, int idx){
		for(int t = 0; t < buf.length; t++){
			Vector3d point = bezierCurve(keyPoints, 0 + t * stride);
			buf[t][idx * 3 + 0] = (float) point.x;
			buf[t][idx * 3 + 1] = (float) point.y;
			buf[t][idx * 3 + 2] = (float) point.z;
			System.out.println("point:" + idx + " at time:" + (t * stride) + " is: (" + point.x + "," + point.y + "," + point.z + ")");
		}
	}

	public void render(long t) {
		float[] pts = new float[numPoints * 3];
		System.out.println("Drawing:" + (int) (t % buf.length));
		pts = buf[(int) (t % buf.length)];
		
    	glUniform4f(0, 0.5f,0f,0f,1.0f);
    	
        int vao = glGenVertexArrays();
        glBindVertexArray(vao);
        
        int vbo = glGenBuffers();
        FloatBuffer vertices = BufferUtils.createFloatBuffer(pts.length);
        vertices.put(pts);
        vertices.rewind();       
        glBindBuffer (GL_ARRAY_BUFFER, vbo);
        glBufferData (GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);
        glVertexAttribPointer (0, 3, GL_FLOAT, false, 0, 0);
        
        glEnableVertexAttribArray(0);

        glDrawArrays (GL_LINES, 0, pts.length/3);       
	}
	
	private double getComb(double n, double i){
		double res = 1.0;
		for(int j = 1; j <= i; j++){
			res *= (double)(n - i + j);
			res /= (double)j;
		}
		return res;
	}

	private Vector3d bezierCurve(ArrayList<Vector3d> keyPoints, double t){
		double x = 0, y = 0, z = 0;
		for(int i = 0; i < keyPoints.size(); i++){
			double tmp1 = getComb(keyPoints.size() - 1, i);
			double tmp2 = Math.pow(t, i);
			double tmp3 = Math.pow(1 - t, keyPoints.size() - 1 - i);
			
			double effi = getComb(keyPoints.size() - 1, i) * Math.pow(t, i) * Math.pow(1 - t, keyPoints.size() - 1 - i);
			x += (keyPoints.get(i).x * effi) ;
			y += (keyPoints.get(i).y * effi) ;
			z += (keyPoints.get(i).z * effi) ;
		}
		Vector3d res = new Vector3d(x, y, z);
		return res;
		
	}
}
