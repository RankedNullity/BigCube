package graphics.scenes.scenes3D;

import java.awt.Graphics;


import common.datastructures.concrete.ArrayHeap;
import common.datastructures.concrete.DoubleLinkedList;
import common.datastructures.interfaces.IList;
import common.datastructures.interfaces.IPriorityQueue;
import graphics.ObjectDistancePair;
import graphics.sceneObjects.*;
import graphics.scenes.Scene;
import math.linalg.lin3d.Plane3d;
import math.linalg.lin3d.Vector3d;

public abstract class Scene3D extends Scene {
	
	private static final long serialVersionUID = 1L; 
	
	
	protected IPriorityQueue<ObjectDistancePair> renderables;
	// --------------------------------------Scene properties-------------------------------------
	// List of all sceneObjects. Includes all types
	protected IList<SceneObject> sceneObjs;
	
	// list of all polygons not in cubes.
	protected IList<Polygon3D> polygons;
	
	// List of all cubes. 
	protected IList<Cube3D> cubes;
	
	// ------------------------------------Camera Properties---------------------------------------
	protected Plane3d viewPlane;
	protected double zoom;
	
	
	public Scene3D(int screenWidth, int screenHeight, int maxFPS, boolean debug) {
		super(screenWidth, screenHeight, maxFPS, debug);
		// Initializes all collections.
		polygons = new DoubleLinkedList<>();
		sceneObjs = new DoubleLinkedList<>();
		cubes = new DoubleLinkedList<>();
		renderables = new ArrayHeap<>();
	}
	 
	/**
	 * Adds p to the scene.
	 * @param p
	 */
	public void addPolygon(Polygon3D p) {
		polygons.add(p);
	}
	
	/**
	 * Uses g to draw all the debug details for the scene.
	 * @param g
	 */
	abstract protected void displayDebug(Graphics g);
	
	/**
	 * Uses g to draw the background.
	 * @param g
	 */
	abstract protected void drawBackground(Graphics g);
	
	/**
	 * Generates everything in the initial scene.
	 * @param size
	 */
	abstract protected void generateScene(int size);
	
	/**
	 * Return the location of the camera in 3d.
	 * @return
	 */
	abstract public Vector3d getCameraLoc();
	
	/**
	 * Attempts to update the camera (if any are needed)
	 * @return true if camera changed. Returns false otherwise.
	 */
	abstract protected boolean updateCamera();
	
	/**
	 * Attempts to update the objects in the scene. 
	 * @return true if anything was updated. Returns false otherwise.
	 */
	abstract protected boolean updateScene();
	
	/**
	 * Method which adds all the scene objects which want to be drawn into the priority queue to be rendered and updates how they are drawn.
	 * By default, this method updates and adds all of the objects to the queue.  
	 */
	protected void updateDrawables() {
		while (!sceneObjs.isEmpty()) {
			SceneObject currentPoly = sceneObjs.remove();
			currentPoly.updateDrawable(viewPlane, zoom, screenWidth, true);
			renderables.insert(new ObjectDistancePair(currentPoly, -currentPoly.getAvgDistance(viewPlane)));
		}
	}
	
	/**
	 * Uses g to render the objects which were put in the render queue.
	 * @param g
	 */
	protected void render(Graphics g) {
		while (!renderables.isEmpty()) {
			SceneObject p = renderables.removeMin().getObject();
			p.render(g);
			sceneObjs.insert(0, p);
		}
	}
	
}
