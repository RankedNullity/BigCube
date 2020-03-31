package graphics.scenes.scenes2D;

import java.awt.Graphics;

import common.datastructures.concrete.DoubleLinkedList;
import common.datastructures.interfaces.IList;
import graphics.scenes.Scene;
import graphics.*;

public class Scene2D extends Scene{
	protected IList<Polygon2D> polys;
	
	
	public Scene2D(int screenWidth, int screenHeight, int maxFPS, boolean debug) {
		super(screenWidth, screenHeight, maxFPS, debug);
		polys = new DoubleLinkedList<>();
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void displayDebug(Graphics g) {
		// TODO Display all debug info on the scene. 
		
	}

	@Override
	protected void drawBackground(Graphics g) {
		// TODO Drawas the background of the scene. 
		
	}

	@Override
	protected void generateScene(int size) {
		// TODO Generates all objects in the scene. 
		
	}

	@Override
	protected boolean updateCamera() {
		// Change the camera. For 2d scenes, the camera never changes. 
		return false;
	}

	@Override
	protected boolean updateScene() {
		// TODO Change the scene
		return false;
	}

	@Override
	protected void updateDrawables() {
		// TODO Probably do nothing?
	}

	@Override
	protected void render(Graphics g) {
		for (Polygon2D p: polys) {
			p.drawPolygon(g);
		}
		
	}

}
