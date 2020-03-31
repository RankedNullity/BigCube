package graphics.scenes.scenes3D;


import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Iterator;
import java.util.Random;

import common.datastructures.concrete.*;
import common.datastructures.interfaces.*;
import graphics.ObjectDistancePair;
import graphics.sceneObjects.Cube3D;
import graphics.sceneObjects.Polygon3D;
import math.linalg.lin3d.*;

public class GraphicsDemo3D extends Scene3D implements KeyListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * TODO: Add a variable which keeps track of which scene this is, and stop
	 * camera controls when the current scene is not the active screen. Much much
	 * later (TM)
	 */

	// Stress Test. n = 100 . Random factor  = 0.003
	// Camera Properties
	private static final double CAMERA_ROTATION_INTERVAL = 0.01;
	private boolean[] keysHeld;

	private IList<Cube3D> cubes;
	private IList<Integer> cubeRotations;
	
	public static final double cubeSpacing = 0.1;

	private static final int ANIMATION_STEPS = 100;

	public GraphicsDemo3D(int cubeSize, int screenWidth) {
		super(screenWidth, screenWidth, 100, true);
		//MaxFPS = 4;
		zoom = (0.75 * screenWidth) / (cubeSize);
		keysHeld = new boolean[4];
		lastRefresh = System.currentTimeMillis();

		// Generating the initial viewPlane
		viewPlane = new Plane3d(Math.pow(cubeSize,  2), 0, 0, Lin3d.zBasis, Lin3d.yBasis);

		// Generating the rubicks cube
		viewPlane.applyTransform(Lin3d.getRotationAroundY(Math.PI / 4));
		viewPlane.applyTransform(Lin3d.getRotationAroundZ(Math.PI / 4));
		
		//keysHeld[0] = true;
		//keysHeld[1] = true;
		
		cubes = new DoubleLinkedList<>();
		cubeRotations = new ArrayList<>();
		
		generateScene(cubeSize);
	}
	
	public GraphicsDemo3D(int screenWidth) {
		this(100, screenWidth);
	}
	
	protected void drawBackground(Graphics g) {
		g.setColor(new Color(140, 180, 180));
		g.fillRect(0, 0, screenWidth, screenWidth);
		g.setColor(Color.black);
	}

	
	protected void displayDebug(Graphics g) {
		int startX = 40, y = 40, interval = 15;
		g.drawString("FPS: " + (int) drawFPS + " (Benchmark)", startX, y);
		y += interval;
		g.drawString("Current Camera Loc: (" + getCameraLoc().getX() + ", " + getCameraLoc().getY() + ", "
				+ getCameraLoc().getZ() + ")", startX, y);
		y += interval;
		//g.drawString("Camera Radius: " + LinAlg.norm(getCameraLoc(), 2), 40, 80);
		g.drawString("Zoom: " + zoom, startX, y);
		y += interval;
		g.drawString("# of Cubes: " + cubes.size(), startX, y);
	}
	

	protected boolean updateScene() {
		Iterator<Cube3D> iter = cubes.iterator();
		for (int i = 1; i <= cubeRotations.size(); i++) {
			Cube3D c = iter.next();
			int rotationValue = cubeRotations.get(i - 1);
			if (rotationValue != -1) {
				double rotationDir = ((rotationValue > 5) ? -1.0 : 1.0) / 50;
				double slowDown = 50;
				double baseSpeed = 50;
				switch(rotationValue % 6) {
					case 0:
						c.applyTransform(Lin3d.getRotationAroundX(rotationDir * (i % slowDown + baseSpeed)/ ANIMATION_STEPS));
						break;
					case 1:
						c.applyTransform(Lin3d.getRotationAroundY(rotationDir * (i % slowDown + baseSpeed)/ ANIMATION_STEPS));
						break;
					case 2:
						c.applyTransform(Lin3d.getRotationAroundZ(rotationDir *  (i % slowDown + baseSpeed)/ ANIMATION_STEPS));
						break;
					case 3:
						c.applyTransform(Lin3d.getRotationAroundX(rotationDir / 2.0 *  (i % slowDown + baseSpeed)  / ANIMATION_STEPS));
						c.applyTransform(Lin3d.getRotationAroundZ(rotationDir / 2.0 *  (i % slowDown + baseSpeed) / ANIMATION_STEPS));
						break;
					case 4:
						c.applyTransform(Lin3d.getRotationAroundX(rotationDir / 2.0 *  (i % slowDown + baseSpeed)  / ANIMATION_STEPS));
						c.applyTransform(Lin3d.getRotationAroundY(rotationDir / 2.0 *  (i % slowDown + baseSpeed) / ANIMATION_STEPS));
						break;
					case 5:
						c.applyTransform(Lin3d.getRotationAroundY(rotationDir / 2.0 *  (i % slowDown + baseSpeed) / ANIMATION_STEPS));
						c.applyTransform(Lin3d.getRotationAroundZ(rotationDir / 2.0 *  (i % slowDown + baseSpeed) / ANIMATION_STEPS));
						break;		
				}
			}
		}
		return true;
	}

	/**
	 * Updates the camera based on key presses.
	 * 
	 * @return
	 */
	protected boolean updateCamera() {
		boolean cameraMovement = false;
		for (int i = 0; i < keysHeld.length; i++) {
			if (keysHeld[i] && !keysHeld[(i + 2) % keysHeld.length]) {
				cameraMovement = true;
				int direction = ((i > 1) ? -1 : 1);
				if (i % 2 == 0) {
					viewPlane.applyTransform(Lin3d.getRotationAroundY(direction * CAMERA_ROTATION_INTERVAL));
				} else {
					viewPlane.applyTransform(Lin3d.getRotationAroundZ(direction * CAMERA_ROTATION_INTERVAL));
				}
			}
		}
		return cameraMovement;
	}


	/**
	 * Returns the camera location.
	 * 
	 * @return
	 */
	public Vector3d getCameraLoc() {
		return this.viewPlane.getPoint();
	}

	/**
	 * Generates the cubes in the scene.
	 */
	public void generateScene(int size) {
		double offSet = size % 2 == 1 ? -0.5: 0;
		int width = 1;
		int half = size / 2;
		Random r = new Random(5);
		// Draw the cubes.
		int count = 0;
		for (int x = - half; x < half + size % 2 ; x += width) {
			for (int y = - half; y < half + size % 2; y += width) {
				for (int z = - half; z < half + size % 2; z += width) {
					if ( x == y && y == z && z == 0) {
						Cube3D c = new Cube3D(x - width, y - width, z - width,  width * 2);
						Polygon3D[] faces = c.getFaces();
						for (int i = 0; i < faces.length; i++) {
							faces[i].setColor(Color.pink);
						}
						cubes.add(c);
						sceneObjs.add(c);
						cubeRotations.add(-1);
					} else if (r.nextDouble() < 0.003) {
						Cube3D c = new Cube3D(x + offSet, y + offSet, z + offSet, width);
						cubes.add(c);
						sceneObjs.add(c);
						cubeRotations.add(count++ % 12);
					}
					
				}
			}
		}
		updateDrawables();
	}

	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_W)
			keysHeld[0] = true;
		if (e.getKeyCode() == KeyEvent.VK_A)
			keysHeld[1] = true;
		if (e.getKeyCode() == KeyEvent.VK_S)
			keysHeld[2] = true;
		if (e.getKeyCode() == KeyEvent.VK_D)
			keysHeld[3] = true;
	}

	public void keyReleased(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_W)
			keysHeld[0] = false;
		if (e.getKeyCode() == KeyEvent.VK_A)
			keysHeld[1] = false;
		if (e.getKeyCode() == KeyEvent.VK_S)
			keysHeld[2] = false;
		if (e.getKeyCode() == KeyEvent.VK_D)
			keysHeld[3] = false;

	}

	@Override
	public void keyTyped(KeyEvent arg0) {
	}



}
