package c2g2.engine;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.joml.Vector2d;
import org.joml.Vector2f;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import c2g2.kinematics.Joint2D;
import c2g2.kinematics.LinkConnection2D;
import c2g2.kinematics.RevoluteJoint2D;
import c2g2.kinematics.RigidLink2D;
import c2g2.kinematics3D.Joint3D;
import c2g2.kinematics3D.Segment;
import c2g2.kinematics3D.Skeleton3D;

public class XMLLoader {
	private static Joint3D root = null;
    public static Skeleton3D loadXML(String fileName) throws Exception {
		try {	
	         File inputFile = new File(fileName);
	         DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
	         DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
	         Document doc = dBuilder.parse(inputFile);
	         
	         doc.getDocumentElement().normalize();
	         System.out.println(doc.getDocumentElement().getNodeName());
	 		 NodeList nList = doc.getElementsByTagName("root");
	         Element e = (Element)nList.item(0);
			 double x = Double.parseDouble(e.getAttribute("x"));
			 double y = Double.parseDouble(e.getAttribute("y"));
			 double z = Double.parseDouble(e.getAttribute("z"));
			 root = new Joint3D(new Vector3d(x, y, z));
			 dfsLoader(e, root, 1);
			 Skeleton3D s = new Skeleton3D(root);
			 return s;
	      } catch (Exception e) {
	         e.printStackTrace();
	      }
    	return null;
    }
    
    private static void dfsLoader(Element e, Joint3D root, int level){
    	NodeList nList = e.getElementsByTagName("joint" + level);
    	if(nList.getLength() == 0){
    		return;
    	}
		System.out.println("list length: "+Integer.toString(nList.getLength()));
		for(int i=0; i< nList.getLength(); i++) {
			Element cElement = (Element)nList.item(i);
			if(cElement==null){
				continue;
			}
			double x = Double.parseDouble(cElement.getAttribute("x"));
			double y = Double.parseDouble(cElement.getAttribute("y"));
			double z = Double.parseDouble(cElement.getAttribute("z"));
			Joint3D joint = new Joint3D(new Vector3d(x, y, z));
			joint.parent = root;
			root.child.add(joint);
			dfsLoader(cElement, joint, level + 1);
		}

    }
    
}
