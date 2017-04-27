package c2g2.kinematics.engine;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;
import static org.lwjgl.system.MemoryUtil.NULL;

import java.nio.FloatBuffer;
import java.util.ArrayList;

import org.joml.Vector2d;
import org.lwjgl.BufferUtils;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;


public class Renderer {
	 // We need to strongly reference callback instances.
    private GLFWErrorCallback errorCallback;
    private GLFWKeyCallback   keyCallback;
    private GLFWMouseButtonCallback mouseButtonCallback;

    // The window handle
    private long window;
    Scene mScene;
    
    private ArrayList<Float> ptlist;
    private ArrayList<Float> linelist;
    private boolean isClicked;
        
    public Renderer(Scene scene) {
		// TODO Auto-generated constructor stub
    	mScene = scene;
    	ptlist = new ArrayList<Float>();
    	linelist = new ArrayList<Float>();
    	isClicked = false;
	}

    public void run() {
    	
        System.out.println("Hello LWJGL " +  Version.getVersion() + "!");

        try {
            init();
            loop();

            // Release window and window callbacks
            glfwDestroyWindow(window);
            keyCallback.free();
        } finally {
            // Terminate GLFW and release the GLFWerrorfun
            glfwTerminate();
            errorCallback.free();
        }
    }

    private void init() {
        // Setup an error callback. The default implementation
        // will print the error message in System.err.
        glfwSetErrorCallback(errorCallback);

        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if ( glfwInit() != true )
            throw new IllegalStateException("Unable to initialize GLFW");

        // Configure our window
        glfwDefaultWindowHints(); // optional, the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, GL_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GL_TRUE); // the window will be resizable
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE);


        int WIDTH = 600;
        int HEIGHT = 600;

        // Create the window
        window = glfwCreateWindow(WIDTH, HEIGHT, "Hello World!", NULL, NULL);
        if ( window == NULL )
            throw new RuntimeException("Failed to create the GLFW window");

        // Setup a key callback. It will be called every time a key is pressed, repeated or released.
        glfwSetKeyCallback(window, keyCallback = new GLFWKeyCallback() {
            @Override
            public void invoke(long window, int key, int scancode, int action, int mods) {
                if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE ){
                    glfwSetWindowShouldClose(window, true); // We will detect this in our rendering loop
                }
                else if (key == GLFW_KEY_A) {
					System.out.println("A pressed.");
					//do whatever you want.
				}
                //add more key binding here.
                
            }
        });
       
        glfwSetMouseButtonCallback(window, mouseButtonCallback = new GLFWMouseButtonCallback() {
			@Override
			public void invoke(long arg0, int arg1, int arg2, int arg3) {
				if(arg2 == 1){
					isClicked = true;
				}
				if(arg2 == 0){
					isClicked = false;
				}
			
				
			}
		});
        
        glfwSetCursorPosCallback(window, new GLFWCursorPosCallback() {
			//implement your mouse callback function here.
			@Override
			public void invoke(long arg0, double arg1, double arg2) {
				// TODO Auto-generated method stub
				if(isClicked){
					//This is your cursor location in window coordinate, you need to convert it
					//into world coordinate in order to apply your algorithm.
					System.out.println("Cursor: "+Double.toString(arg1)+" "+Double.toString(arg2));
				}

			}
		});

        // Get the resolution of the primary monitor
        GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        // Center our window
        glfwSetWindowPos(
                window,
                (vidmode.width() - WIDTH) / 2,
                (vidmode.height() - HEIGHT) / 2
        );

        // Make the OpenGL context current
        glfwMakeContextCurrent(window);
        // Enable v-sync
        glfwSwapInterval(1);

        // Make the window visible
        glfwShowWindow(window);
    }

    private void loop() {
        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the ContextCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities();

        final String vertex_shader =
                "#version 330\n" +
                "in vec3 vp;\n" +
                "void main () {\n" +
                "  gl_Position = vec4 (vp, 1.0);\n" +
                "}";

        final String frag_shader =
                "#version 330\n"    +
                "uniform vec4 uColor;"+
                "out vec4 frag_colour;" +
                "void main () {"         +
                "  frag_colour = uColor;" +
                "}";

        int shader_programme = glCreateProgram();


        int vertexShaderID = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(vertexShaderID, vertex_shader);
        glCompileShader (vertexShaderID);

        if(glGetShaderi(vertexShaderID, GL_COMPILE_STATUS) == 0){
            System.err.println(glGetShaderInfoLog(vertexShaderID, 1024));
            System.exit(1);
        }

        glAttachShader (shader_programme, vertexShaderID);

        int fragmentShaderID = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(fragmentShaderID, frag_shader);
        glCompileShader (fragmentShaderID);

        if(glGetShaderi(fragmentShaderID, GL_COMPILE_STATUS) == 0){
            System.err.println(glGetShaderInfoLog(fragmentShaderID, 1024));
            System.exit(1);
        }

        glAttachShader (shader_programme, fragmentShaderID);

        glLinkProgram (shader_programme);

        if(glGetProgrami(shader_programme, GL_LINK_STATUS) == 0){
            System.err.println(glGetProgramInfoLog(shader_programme, 1024));
            System.exit(1);
        }

        while ( glfwWindowShouldClose(window) == false ) {
        	ptlist.clear();
        	linelist.clear();
        	renderSkeleton();
        	drawlinelist(shader_programme);
        	drawcirclelist(shader_programme);
        	
            // update other events like input handling
            glfwPollEvents ();
            // put the stuff we've been drawing onto the display
            glfwSwapBuffers (window);

        }
    }
    
    private void renderSkeleton(){
    	ArrayList<Vector2d> pts = mScene.getJointPos();
    	for(int i=0; i<pts.size(); i=i+2){
    		Vector2d p0 = pts.get(i);
    		Vector2d p1 = pts.get(i+1);
    		addCircle((float)p0.x, (float)p0.y, 0.05f);
    		addCircle((float)p1.x, (float)p1.y, 0.05f);
    		addLine((float)p0.x, (float)p0.y, (float)p1.x, (float)p1.y, 0.03f);
    	}
    }
    
    private void addLine(float p0x, float p0y, float p1x, float p1y, float width) {
		float dx = p1x-p0x;
		float dy = p1y-p0y;
		float ll = (float)Math.sqrt((float)dx*dx+dy*dy);
		dx = dx / ll * width;
		dy = dy / ll * width;
		glUniform4f(0, 0f,0.5f,0f,1.0f);
		addLinePoint(p0x+dy, p0y-dx, 0f);
		addLinePoint(p0x-dy, p0y+dx, 0f);
		addLinePoint(p1x+dy, p1y-dx, 0f);
		
		addLinePoint(p0x-dy, p0y+dx, 0f);
		addLinePoint(p1x+dy, p1y-dx, 0f);
		addLinePoint(p1x-dy, p1y+dx, 0f);
	}
    
    private void addCircle(float cx, float cy, float r){
    	int num=36;
    	for(int i=0; i<num; i++){
    		addPoint(cx, cy, 0f);
    		float p1x = (float) (Math.cos((float)i*Math.PI*2/(float)num)*r)+cx;
    		float p1y = (float) (Math.sin((float)i*Math.PI*2/(float)num)*r)+cy;
    		addPoint(p1x, p1y, 0f);
    		float p2x = (float) (Math.cos((float)(i+1)*Math.PI*2/(float)num)*r)+cx;
    		float p2y = (float) (Math.sin((float)(i+1)*Math.PI*2/(float)num)*r)+cy;
    		addPoint(p2x, p2y, 0f);
    	}
    }
    
    private void addPoint(float x, float y, float z){
    	ptlist.add(x);
    	ptlist.add(y);
    	ptlist.add(z);
    }
    
    private void addLinePoint(float x, float y, float z) {
    	linelist.add(x);
    	linelist.add(y);
    	linelist.add(z);
	}
    
    private void drawcirclelist(int shader_programme) {
    	float[] pts = new float[ptlist.size()];
    	
    	for(int i=0; i<ptlist.size(); i++){
    		pts[i] = ptlist.get(i);
    	}
    	
    	glUniform4f(0, 0.5f,0.5f,0f,1.0f);

    	
    	FloatBuffer vertices = BufferUtils.createFloatBuffer(pts.length);
        vertices.put(pts);
        // Rewind the vertices
        vertices.rewind();
       

        int vbo = glGenBuffers();
        int vao = glGenVertexArrays();

        glBindBuffer (GL_ARRAY_BUFFER, vbo);
        glBufferData (GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);
        
        glBindVertexArray(vao);

        glEnableVertexAttribArray (0);
        glBindBuffer (GL_ARRAY_BUFFER, vbo);
        glVertexAttribPointer (0, 3, GL_FLOAT, false, 0, 0);

        // wipe the drawing surface clear
        glUseProgram (shader_programme);
        glBindVertexArray (vao);
        // draw points 0-3 from the currently bound VAO with current in-use shader
        glDrawArrays (GL_TRIANGLES, 0, pts.length/3);
	}
    
    private void drawlinelist(int shader_programme) {
    	float[] pts = new float[linelist.size()];
    	
    	for(int i=0; i<linelist.size(); i++){
    		pts[i] = linelist.get(i);
    	}
    	
    	glUniform4f(0, 0.5f,0f,0f,1.0f);
    	
    	FloatBuffer vertices = BufferUtils.createFloatBuffer(pts.length);
        vertices.put(pts);
        // Rewind the vertices
        vertices.rewind();
       

        int vbo = glGenBuffers();
        int vao = glGenVertexArrays();

        glBindBuffer (GL_ARRAY_BUFFER, vbo);
        glBufferData (GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);
        
        glBindVertexArray(vao);

        glEnableVertexAttribArray (0);
        glBindBuffer (GL_ARRAY_BUFFER, vbo);
        glVertexAttribPointer (0, 3, GL_FLOAT, false, 0, 0);

        // wipe the drawing surface clear
        glClear (GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glUseProgram (shader_programme);
        glBindVertexArray (vao);
        // draw points 0-3 from the currently bound VAO with current in-use shader
        glDrawArrays (GL_TRIANGLES, 0, pts.length/3);
	}
}
