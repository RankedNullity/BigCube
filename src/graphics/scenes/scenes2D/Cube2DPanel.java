package graphics.scenes.scenes2D;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JPanel;

import cube.FullStickerCube;
import cube.GameCube;


public class Cube2DPanel extends JPanel{
	private final int SQUARESIZE = 50;
	private int startX;
	private int startY;
	private boolean sliceTextCheck;
	private FullStickerCube cube;

	public Cube2DPanel(FullStickerCube cube) {
		this.cube = cube;
		if (cube.getSize() < 0) {
			throw new IllegalArgumentException("Cube dimension cannot be negative!");
		}
		startX = 50;
		startY = 200;
		this.setBackground(new Color(140, 180, 180));

	}
	
	
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		drawFace(g, startX, startY, cube.getColorAsIntArray()[GameCube.LEFT], cube.getDebugArray()[GameCube.LEFT]);
		drawFace(g, startX + cube.getSize() * SQUARESIZE, startY, cube.getColorAsIntArray()[GameCube.FRONT], cube.getDebugArray()[GameCube.FRONT]);
		drawFace(g, startX + cube.getSize() * SQUARESIZE, startY + cube.getSize() * SQUARESIZE, cube.getColorAsIntArray()[GameCube.DOWN], cube.getDebugArray()[GameCube.DOWN]);
		drawFace(g, startX + cube.getSize() * SQUARESIZE, startY - cube.getSize() * SQUARESIZE, cube.getColorAsIntArray()[GameCube.UP], cube.getDebugArray()[GameCube.UP]);
		drawFace(g, startX + 2 * cube.getSize() * SQUARESIZE, startY, cube.getColorAsIntArray()[GameCube.RIGHT], cube.getDebugArray()[GameCube.RIGHT]);
		drawFace(g, startX + 3 * cube.getSize() * SQUARESIZE, startY, cube.getColorAsIntArray()[GameCube.BACK], cube.getDebugArray()[GameCube.BACK]);
	}
	
	@Override
	public Dimension getPreferredSize() {
		return new Dimension(500, 500);
	}
	
	private void drawFace(Graphics g, int x, int y, int[][] colors, String[][]debug) {
		/*
		 *  This is like O(n^2) but it's ok because cube.getSize() likely < 5
		 * 
		 */
		
		// Display slices if true
		if (sliceTextCheck) {
			for (int i = 0; i < colors.length; i++) {
				int x_prime = x;
				for (int j = 0; j < colors[0].length; j++) {
					String colorHexString = "#" + Integer.toHexString(colors[i][j]).toUpperCase();
					g.setColor(Color.decode(colorHexString));
					g.fillRect(x_prime, y, SQUARESIZE, SQUARESIZE);
					g.setColor(Color.black);
					g.drawRect(x_prime, y, SQUARESIZE, SQUARESIZE);
					g.drawString(debug[i][j], x_prime + 5, y + 30);
					x_prime += SQUARESIZE;
				}
				y += SQUARESIZE;
			}
		} else {
			for (int i = 0; i < colors.length; i++) {
				int x_prime = x;
				for (int j = 0; j < colors[0].length; j++) {
					String colorHexString = "#" + Integer.toHexString(colors[i][j]).toUpperCase();
					g.setColor(Color.decode(colorHexString));
					g.fillRect(x_prime, y, SQUARESIZE, SQUARESIZE);
					g.setColor(Color.black);
					g.drawRect(x_prime, y, SQUARESIZE, SQUARESIZE);
					x_prime += SQUARESIZE;
				}
				y += SQUARESIZE;
			}
		}
		
	}
	
	public void updateSliceCheck() {
		sliceTextCheck = !sliceTextCheck;
		this.revalidate();
		this.repaint();
	}
	
	public void resizeCube(int size) {
		cube = new FullStickerCube(size);
		this.revalidate();
		this.repaint();
	}
	
	public void move(int face, int slice, boolean clockwise) {
		cube.applyMove(face, slice, clockwise);
		System.out.println("Yeet");
		this.revalidate();
		this.repaint();
	}
	
	public FullStickerCube getCube() {
		return cube;
	}
	
	public void randomize() {
		cube.randomize();
		this.revalidate();
		this.repaint();
	}
}
