package code;

import javax.swing.*;
import static com.jogamp.opengl.GL4.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.GLContext;
import com.jogamp.common.nio.Buffers;

public class Code extends JFrame implements GLEventListener
{	private GLCanvas myCanvas;
	private int renderingProgram;
	private int vao[] = new int[1];

	public Code()
	{	setTitle("Chapter 2 - program 5");
		setSize(400, 200);
		myCanvas = new GLCanvas();
		myCanvas.addGLEventListener(this);
		this.add(myCanvas);
		this.setVisible(true);
	}

	public void display(GLAutoDrawable drawable)
	{	GL4 gl = (GL4) GLContext.getCurrentGL();
		gl.glUseProgram(renderingProgram);
		gl.glDrawArrays(GL_TRIANGLES,0,3);
	}

	public void init(GLAutoDrawable drawable)
	{	GL4 gl = (GL4) GLContext.getCurrentGL();
		renderingProgram = Utils.createShaderProgram("code/vertShader.glsl", "code/fragShader.glsl");
		gl.glGenVertexArrays(vao.length, vao, 0);
		gl.glBindVertexArray(vao[0]);
	}

	public static void main(String[] args) { new Code(); }
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {}
	public void dispose(GLAutoDrawable drawable) {}
}