package code;

import java.io.*;
import java.lang.Math;
import java.nio.*;
import java.util.*;
import java.awt.Color;
import javax.swing.*;
import static com.jogamp.opengl.GL4.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.*;
import com.jogamp.opengl.util.texture.*;
import com.jogamp.common.nio.Buffers;
import org.joml.*;

/*  Simulates drifting clouds.
    To view the skydome from the inside, move the camera to 0,2,0
	   and change the winding order to CW.
*/

public class Code extends JFrame implements GLEventListener
{
	private GLCanvas myCanvas;

	private int renderingProgram;
	private int[] vao = new int[1];
	private int[] vbo = new int[3];

	private HalfSphere halfSphere = new HalfSphere(48);
	private int numSphereVertices;
	
	private int skyTexture;
	
	private float cameraX, cameraY, cameraZ;
	private float objLocX, objLocY, objLocZ;
	
	// allocate variables for display() function
	private FloatBuffer vals = Buffers.newDirectFloatBuffer(16);
	private Matrix4f pMat = new Matrix4f();  // perspective matrix
	private Matrix4f vMat = new Matrix4f();  // view matrix
	private Matrix4f mMat = new Matrix4f();  // model matrix
	private Matrix4f mvMat = new Matrix4f(); // model-view matrix
	private int mvLoc, projLoc, dOffsetLoc;
	private float aspect;

	private int noiseTexture;
	private int noiseHeight= 200;
	private int noiseWidth = 200;
	private int noiseDepth = 200;
	private double[][][] noise = new double[noiseHeight][noiseWidth][noiseDepth];
	private java.util.Random random = new java.util.Random(12);
	
	private float rotAmt = 0.0f;
	private float d = 0.01f; // depth for 3rd dimension of 3D noise texture

	public Code()
	{	setTitle("Chapter 14 - program8");
		setSize(1000, 800);
		myCanvas = new GLCanvas();
		myCanvas.addGLEventListener(this);
		this.add(myCanvas);
		this.setVisible(true);
		Animator animator = new Animator(myCanvas);
		animator.start();
	}

	public void display(GLAutoDrawable drawable)
	{	GL4 gl = (GL4) GLContext.getCurrentGL();
		gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
		gl.glClear(GL_COLOR_BUFFER_BIT);
		gl.glClear(GL_DEPTH_BUFFER_BIT);
		
		gl.glUseProgram(renderingProgram);
		
		mvLoc = gl.glGetUniformLocation(renderingProgram, "mv_matrix");
		projLoc = gl.glGetUniformLocation(renderingProgram, "proj_matrix");
		dOffsetLoc = gl.glGetUniformLocation(renderingProgram, "d");
		
		vMat.identity().setTranslation(-cameraX,-cameraY,-cameraZ);
		
		mMat.identity();
		mMat.translate(objLocX, objLocY, objLocZ);
		rotAmt += 0.002f;
		mMat.rotateY(rotAmt);
		mMat.scale(4.0f, 4.0f, 4.0f);

		mvMat.identity();
		mvMat.mul(vMat);
		mvMat.mul(mMat);
		
		d += 0.00005f; if (d >= 0.99f) d = 0.01f;

		gl.glUniformMatrix4fv(mvLoc, 1, false, mvMat.get(vals));
		gl.glUniformMatrix4fv(projLoc, 1, false, pMat.get(vals));
		gl.glUniform1f(dOffsetLoc, d);
		
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[0]);
		gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(0);
		
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[1]);
		gl.glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(1);
		
		gl.glActiveTexture(GL_TEXTURE0);
		gl.glBindTexture(GL_TEXTURE_3D, skyTexture);
		
		gl.glEnable(GL_CULL_FACE);
		gl.glFrontFace(GL_CW);	// CW for inside, CCW for outside
		gl.glEnable(GL_DEPTH_TEST);
		gl.glDepthFunc(GL_LEQUAL);

		gl.glDrawArrays(GL_TRIANGLES, 0, numSphereVertices);
	}

	public void init(GLAutoDrawable drawable)
	{	GL4 gl = (GL4) GLContext.getCurrentGL();
		renderingProgram = Utils.createShaderProgram("code/vertShader.glsl", "code/fragShader.glsl");

		float aspect = (float) myCanvas.getWidth() / (float) myCanvas.getHeight();
		pMat.identity().setPerspective((float) Math.toRadians(50.0f), aspect, 0.1f, 1000.0f);

		setupVertices();
		cameraX = 0.0f; cameraY = 2.0f; cameraZ = 0.0f;	// 0,2,0 or 0,2,10
		objLocX = 0.0f; objLocY = 0.0f; objLocZ = 0.0f;
		
		generateNoise();	
		skyTexture = buildNoiseTexture();
	}
	
	private void setupVertices()
	{	GL4 gl = (GL4) GLContext.getCurrentGL();

		numSphereVertices = halfSphere.getIndices().length;
	
		int[] indices = halfSphere.getIndices();
		Vector3f[] vert = halfSphere.getVertices();
		Vector2f[] tex  = halfSphere.getTexCoords();
		Vector3f[] norm = halfSphere.getNormals();
		
		float[] pvalues = new float[indices.length*3];
		float[] tvalues = new float[indices.length*2];
		float[] nvalues = new float[indices.length*3];
		
		for (int i=0; i<indices.length; i++)
		{	pvalues[i*3] = (float) (vert[indices[i]]).x();
			pvalues[i*3+1] = (float) (vert[indices[i]]).y();
			pvalues[i*3+2] = (float) (vert[indices[i]]).z();
			tvalues[i*2] = (float) (tex[indices[i]]).x();
			tvalues[i*2+1] = (float) (tex[indices[i]]).y();
			nvalues[i*3] = (float) (norm[indices[i]]).x();
			nvalues[i*3+1]= (float)(norm[indices[i]]).y();
			nvalues[i*3+2]=(float) (norm[indices[i]]).z();
		}
		
		gl.glGenVertexArrays(vao.length, vao, 0);
		gl.glBindVertexArray(vao[0]);
		gl.glGenBuffers(3, vbo, 0);
		
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[0]);
		FloatBuffer vertBuf = Buffers.newDirectFloatBuffer(pvalues);
		gl.glBufferData(GL_ARRAY_BUFFER, vertBuf.limit()*4, vertBuf, GL_STATIC_DRAW);

		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[1]);
		FloatBuffer texBuf = Buffers.newDirectFloatBuffer(tvalues);
		gl.glBufferData(GL_ARRAY_BUFFER, texBuf.limit()*4, texBuf, GL_STATIC_DRAW);

		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[2]);
		FloatBuffer norBuf = Buffers.newDirectFloatBuffer(nvalues);
		gl.glBufferData(GL_ARRAY_BUFFER, norBuf.limit()*4,norBuf, GL_STATIC_DRAW);
	}

	// 3D Texture section
	
	private void fillDataArray(byte data[])
	{ for (int i=0; i<noiseHeight; i++)
	  { for (int j=0; j<noiseWidth; j++)
	    { for (int k=0; k<noiseDepth; k++)
	      {	float brightness = 1.0f - (float) turbulence(i,j,k,64) / 256.0f;
			Color c = new Color(brightness, brightness, 1.0f, 1.0f);
	        data[i*(noiseWidth*noiseHeight*4)+j*(noiseHeight*4)+k*4+0] = (byte) c.getRed();
	        data[i*(noiseWidth*noiseHeight*4)+j*(noiseHeight*4)+k*4+1] = (byte) c.getGreen();
	        data[i*(noiseWidth*noiseHeight*4)+j*(noiseHeight*4)+k*4+2] = (byte) c.getBlue();
	        data[i*(noiseWidth*noiseHeight*4)+j*(noiseHeight*4)+k*4+3] = (byte) 0;
	} } } }

	private int buildNoiseTexture()
	{	GL4 gl = (GL4) GLContext.getCurrentGL();

		byte[] data = new byte[noiseHeight*noiseWidth*noiseDepth*4];
		
		fillDataArray(data);

		ByteBuffer bb = Buffers.newDirectByteBuffer(data);

		int[] textureIDs = new int[1];
		gl.glGenTextures(1, textureIDs, 0);
		int textureID = textureIDs[0];

		gl.glBindTexture(GL_TEXTURE_3D, textureID);

		gl.glTexStorage3D(GL_TEXTURE_3D, 1, GL_RGBA8, noiseWidth, noiseHeight, noiseDepth);
		gl.glTexSubImage3D(GL_TEXTURE_3D, 0, 0, 0, 0,
				noiseWidth, noiseHeight, noiseDepth, GL_RGBA, GL_UNSIGNED_INT_8_8_8_8_REV, bb);
		
		gl.glTexParameteri(GL_TEXTURE_3D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);

		return textureID;
	}

	void generateNoise()
	{	for (int x=0; x<noiseHeight; x++)
		{	for (int y=0; y<noiseWidth; y++)
			{	for (int z=0; z<noiseDepth; z++)
				{	noise[x][y][z] = random.nextDouble();
	}	}	}	}
	
	double smoothNoise(double x1, double y1, double z1)
	{	//get fractional part of x, y, and z
		double fractX = x1 - (int) x1;
		double fractY = y1 - (int) y1;
		double fractZ = z1 - (int) z1;

		//neighbor values
		int x2 = ((int)x1 + noiseWidth + 1) % noiseWidth;
		int y2 = ((int)y1 + noiseHeight+ 1) % noiseHeight;
		int z2 = ((int)z1 + noiseDepth + 1) % noiseDepth;

		//smooth the noise by interpolating
		double value = 0.0;
		value += (1-fractX) * (1-fractY) * (1-fractZ) * noise[(int)x1][(int)y1][(int)z1];
		value += (1-fractX) * fractY     * (1-fractZ) * noise[(int)x1][(int)y2][(int)z1];
		value += fractX     * (1-fractY) * (1-fractZ) * noise[(int)x2][(int)y1][(int)z1];
		value += fractX     * fractY     * (1-fractZ) * noise[(int)x2][(int)y2][(int)z1];

		value += (1-fractX) * (1-fractY) * fractZ     * noise[(int)x1][(int)y1][(int)z2];
		value += (1-fractX) * fractY     * fractZ     * noise[(int)x1][(int)y2][(int)z2];
		value += fractX     * (1-fractY) * fractZ     * noise[(int)x2][(int)y1][(int)z2];
		value += fractX     * fractY     * fractZ     * noise[(int)x2][(int)y2][(int)z2];
		
		return value;
	}

	private double turbulence(double x, double y, double z, double size)
	{	double value = 0.0, initialSize = size, cloudQuant;
		while(size >= 0.9)
		{	value = value + smoothNoise(x/size, y/size, z/size) * size;
			size = size / 2.0;
		}
		cloudQuant = 110.0; // tunable quantity of clouds
		value = value/initialSize;
		value = 256.0 * logistic(value * 128.0 - cloudQuant);
		//value = 128.0 * value / initialSize;
		return value;
	}

	private double logistic(double x)
	{	double k = 0.2; // tunable haziness of clouds
		return (1.0/(1.0+Math.pow(2.718,-k*x)));
	}
	
	public static void main(String[] args) { new Code(); }
	public void dispose(GLAutoDrawable drawable) {}
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height)
	{	float aspect = (float) myCanvas.getWidth() / (float) myCanvas.getHeight();
		pMat.identity().setPerspective((float) Math.toRadians(50.0f), aspect, 0.1f, 1000.0f);
	}
}