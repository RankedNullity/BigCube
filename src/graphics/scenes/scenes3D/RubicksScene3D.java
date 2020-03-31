package graphics.scenes.scenes3D;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import cube.GameCube;
import cube.solvers.RandomSolver;
import cube.solvers.Solver;
import graphics.sceneObjects.Cube3D;
import graphics.sceneObjects.MegaCube;
import cube.Action;
import cube.FullStickerCube;
import math.linalg.lin3d.*;

public class RubicksScene3D extends Scene3D implements KeyListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/*------------------------------------------------------------ Fields ---------------------------------------------------*/
	private Solver mySolver;

	// Camera Properties
	private static final double CAMERA_ROTATION_INTERVAL = 0.005;
	private boolean[] keysHeld;

	// GameObject fields
	private GameCube gameCube;

	// Animation Variables
	private MegaCube graphicsCube;
	private boolean animationOn;
	private static final int ANIMATION_STEPS = 25;
	private static final double ROTATION_INTERVAL = Math.PI / (2 * ANIMATION_STEPS);
	private int currentStep;
	private Action currentAction;

	public RubicksScene3D(int cubeSize, boolean animations, int screenWidth) {
		super(screenWidth, screenWidth, 31, true);
		zoom = (0.5 * screenWidth) / (cubeSize);
		keysHeld = new boolean[4];
		gameCube = new FullStickerCube(cubeSize);

		lastRefresh = System.currentTimeMillis();
		this.animationOn = animations;
		if (animations) {
			currentStep = 0;
		}

		// Generating the initial viewPlane
		viewPlane = new Plane3d(Math.pow(cubeSize, 2), 0, 0, Lin3d.zBasis, Lin3d.yBasis);
		mySolver = new RandomSolver(gameCube, 192384);
		currentAction = mySolver.getBestAction();

		// Generating the Rubicks cube
		viewPlane.applyTransform(Lin3d.getRotationAroundY(-Math.PI / 4));
		viewPlane.applyTransform(Lin3d.getRotationAroundZ(Math.PI / 4));
		keysHeld[0] = true;
		keysHeld[1] = true;
		generateScene(cubeSize);
		updateDrawables();

	}

	public RubicksScene3D(int size) {
		this(size, true, 720);
	}

	/*------------------------------------------------------------ Graphics ---------------------------------------------------*/

	protected void drawBackground(Graphics g) {
		g.setColor(new Color(140, 180, 180));
		g.fillRect(0, 0, screenWidth, screenWidth);
		g.setColor(Color.black);
	}

	protected static final Color debugColor = new Color(80, 80, 80, 5);

	protected void displayDebug(Graphics g) {
		int startX = 40, y = 40, interval = 15;
		g.setColor(Color.LIGHT_GRAY);
		g.fillRect(startX, y - 10, 200, 6 * interval);
		g.setColor(Color.black);
		g.drawString("FPS: " + (int) drawFPS + " (Benchmark)", startX, y);
		y += interval;
		g.drawString("Current Camera Loc: (" + (int)getCameraLoc().getX() + ", " + (int)getCameraLoc().getY() + ", "
				+ (int)getCameraLoc().getZ() + ")", startX, y);
		y += interval;
		// g.drawString("Camera Radius: " + LinAlg.norm(getCameraLoc(), 2), 40, 80);
		g.drawString("Zoom: " + zoom, startX, y);
		y += interval;
		g.drawString("Cube Size: " + gameCube.getSize() + " x " + gameCube.getSize() + " x " + gameCube.getSize() + " ("
				+ (int) Math.pow(gameCube.getSize(), 3) + ")", startX, y);
		y += interval;
		g.drawString("SolverType: " + mySolver.getType(), startX, y);
		y += interval;
		g.drawString("Current Move: " + currentAction.toString(), startX, y);

	}

	protected boolean updateScene() {
		if (animationOn) {
			// Check animation steps, and update the scene accordingly.

			if (currentAction != null) {
				if (currentStep == ANIMATION_STEPS) {
					gameCube.applyMove(currentAction);
					graphicsCube.processAnimation(currentAction, -ROTATION_INTERVAL * (ANIMATION_STEPS), viewPlane,
							zoom, screenWidth, true);
					graphicsCube.updateColors(gameCube.getColorArray());
					currentStep = 0;
					currentAction = getNextAction();
				} else {
					currentStep++;
					graphicsCube.processAnimation(currentAction, ROTATION_INTERVAL, viewPlane, zoom, screenWidth, true);
				}
				return true;
			}

		} else {
			if (currentAction != null) {
				gameCube.applyMove(currentAction);
				graphicsCube.updateColors(gameCube.getColorArray());
				currentAction = getNextAction();
				return true;
			}
		}

		return false;
	}

	public Action getNextAction() {
		return mySolver.getBestAction();
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
				int direction = ((i > 1) ? 1 : -1);
				if (i % 2 == 0) {
					viewPlane.applyTransform(Lin3d.getRotationAroundY(direction * CAMERA_ROTATION_INTERVAL));
				} else {
					viewPlane.applyTransform(Lin3d.getRotationAroundZ(-1 * direction * CAMERA_ROTATION_INTERVAL));
				}
			}
		}
		return cameraMovement;
	}

	/**
	 * Returns the camera location.
	 * 
	 * @return location of the camera.
	 */
	public Vector3d getCameraLoc() {
		return this.viewPlane.getPoint();
	}

	public void generateScene(int size) {
		graphicsCube = new MegaCube(size, gameCube);
		sceneObjs.add(graphicsCube);
		updateDrawables();
	}

	@Override
	protected void updateDrawables() {
		graphicsCube.updateDrawable(viewPlane, zoom, screenWidth, true);
	}

	@Override
	protected void render(Graphics g) {
		graphicsCube.render(g);
	}

	/*------------------------------------------------------------ Controls ---------------------------------------------------*/

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
		// TODO Auto-generated method stub

	}

}
