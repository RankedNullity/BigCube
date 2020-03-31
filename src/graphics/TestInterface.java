package graphics;
import cube.*;
import java.util.regex.*;
import java.awt.event.*;
import java.awt.*;
import javax.swing.*;

/**
 * 2D test interface used for debugging the functionalities of FullStickerCube.
 * @author AGuo
 *
 */

public class TestInterface extends JFrame implements ActionListener{
	private static JFrame frame;
	private static JPanel panel;
	private static JMenuBar menubar;
	private static FullStickerCube cube;
	private static TestInterface t1;
	private static boolean[] radioChecks;
	private static boolean sliceTextCheck;
	
	public static void main(String[] args) {
		t1 = new TestInterface();
		frame = new JFrame("Cube");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		panel = new JPanel();
		
		menubar = new JMenuBar();
		JMenu dimensions = new JMenu("Dimension");
		JMenuItem d1 = new JMenuItem("2x2x2"); 
		JMenuItem d2 = new JMenuItem("3x3x3");
		JMenuItem d3 = new JMenuItem("4x4x4");
		dimensions.add(d1);
		dimensions.add(d2);
		dimensions.add(d3);
		d1.addActionListener(t1);
		d2.addActionListener(t1);
		d3.addActionListener(t1);
		menubar.add(dimensions);

		for (int i = 0; i < 6; i++) {
			JRadioButton rButton = new JRadioButton("Slice " + i, false);
			rButton.addActionListener(t1);
			rButton.setVisible(false);
			panel.add(rButton);
		}
		
		for (int i = 0; i < 6; i++) {
			JButton button = new JButton(GameCube.FACE_STRING[i]);
			button.addActionListener(t1);
			panel.add(button);
		}
		
		for (int i = 0; i < 6; i++) {
			JButton button = new JButton(GameCube.FACE_STRING[i] + " Alt");
			button.addActionListener(t1);
			panel.add(button);
		}
		
		JButton randomize = new JButton("Randomize");
		randomize.addActionListener(t1);
		panel.add(randomize);
		
		JButton sliceTextSwitch = new JButton("Slice Text On/Off");
		sliceTextSwitch.addActionListener(t1);
		panel.add(sliceTextSwitch);
		sliceTextCheck = false;
		
		radioChecks = new boolean[] {false, false, false, false, false, false};
		
		panel.setBackground(new Color(140, 180, 180));
		frame.setJMenuBar(menubar);
		frame.add(panel);
		frame.setSize(1000, 1000);
		frame.setVisible(true);
		
	}
	
	/*
	 * Custom class used for physically painting the cube using specs of FullStickerCube
	 * 
	 */
	private static class RectDraw extends JPanel {
		private int cubeDimension;
		private final int SQUARESIZE = 50;
		private int startX;
		private int startY;
		private int[][][] colorArray;
		private String[][][] debugArray;
		private boolean sliceTextSwitch;

		public RectDraw(FullStickerCube cube) {
			cubeDimension = cube.getSize();
			if (cubeDimension < 0) {
				throw new IllegalArgumentException("Cube dimension cannot be negative!");
			}
			colorArray = cube.getColorAsIntArray();
			debugArray = cube.getDebugArray();
			startX = 50;
			startY = 200;
			this.setBackground(new Color(140, 180, 180));

		}
		
		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			drawFace(g, startX, startY, colorArray[GameCube.LEFT], debugArray[GameCube.LEFT]);
			drawFace(g, startX + cubeDimension * SQUARESIZE, startY, colorArray[GameCube.FRONT], debugArray[GameCube.FRONT]);
			drawFace(g, startX + cubeDimension * SQUARESIZE, startY + cubeDimension * SQUARESIZE, colorArray[GameCube.DOWN], debugArray[GameCube.DOWN]);
			drawFace(g, startX + cubeDimension * SQUARESIZE, startY - cubeDimension * SQUARESIZE, colorArray[GameCube.UP], debugArray[GameCube.UP]);
			drawFace(g, startX + 2 * cubeDimension * SQUARESIZE, startY, colorArray[GameCube.RIGHT], debugArray[GameCube.RIGHT]);
			drawFace(g, startX + 3 * cubeDimension * SQUARESIZE, startY, colorArray[GameCube.BACK], debugArray[GameCube.BACK]);
		}
		
		@Override
		public Dimension getPreferredSize() {
			return new Dimension(1000, 1000);
		}
		
		private void drawFace(Graphics g, int x, int y, int[][] colors, String[][]debug) {
			/*
			 *  This is like O(n^2) but it's ok because cubeDimension likely < 5
			 * 
			 */
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
	}
	
	/*
	 * Updates radio buttons for slices, ensuring the correct number of options appear according to cube dimension
	 */
	private void updateSliceOptions(int n) {
		Component[] componentList = panel.getComponents();
		int count = 0;
		for (Component c : componentList) {
			if (c instanceof JRadioButton) {
				((JRadioButton) c).setSelected(false);
				if (count < n) {
					c.setVisible(true);
					count++;
				} else {
					c.setVisible(false);
					count++;
				}
			}
			if (count > 6) {
				break;
			}
		}
		for (int i = 0; i < radioChecks.length; i++) {
			radioChecks[i] = false;
		}
	}
	
	/*
	 * Apply moves to the cube and updates the displayed image
	 */
	
	private void move(int face, boolean clockwise) {
		for (int i = 0; i < radioChecks.length; i++) {
			if (radioChecks[i]) {
				System.out.println(i);
				cube.applyMove(face, i, clockwise);
			}
		}
		updateRect(new RectDraw(cube));
	}
	
	/*
	 * All functionalities of buttons
	 */

	@Override
	public void actionPerformed(ActionEvent e) {
		String s = e.getActionCommand();
		System.out.println(s);
		if (s.equals("2x2x2")) {
			cube = new FullStickerCube(2, false);
			updateSliceOptions(cube.getSize());
			RectDraw newrect = new RectDraw(cube);
			updateRect(newrect);
		} else if (s.equals("3x3x3")) {
			cube = new FullStickerCube(3, false);
			updateSliceOptions(cube.getSize());
			RectDraw newrect = new RectDraw(cube);
			updateRect(newrect);
		} else if (s.equals("4x4x4")) {
			cube = new FullStickerCube(4, false);
			updateSliceOptions(cube.getSize());
			RectDraw newrect = new RectDraw(cube);
			updateRect(newrect);
		}  else if (s.equals("Slice Text On/Off")) {
			sliceTextCheck = !sliceTextCheck;
			updateRect(new RectDraw(cube));
		} else if (Pattern.matches("Slice.*", s)) {
			int slice = Integer.parseInt(s.substring(s.length() - 1));
			radioChecks[slice] = !radioChecks[slice];
		} else if (Pattern.matches("Front.*", s)) {
			if (Pattern.matches(".*Alt", s)) {
				move(GameCube.FRONT, false);
			} else {
				move(GameCube.FRONT, true);
			}
		} else if (Pattern.matches("Left.*", s)) {
			if (Pattern.matches(".*Alt", s)) {
				move(GameCube.LEFT, false);
			} else {
				move(GameCube.LEFT, true);
			}	
		} else if (Pattern.matches("Up.*", s)) {
			if (Pattern.matches(".*Alt", s)) {
				move(GameCube.UP, false);
			} else {
				move(GameCube.UP, true);
			}
		} else if (Pattern.matches("Down.*", s)) {
			if (Pattern.matches(".*Alt", s)) {
				move(GameCube.DOWN, false);
			} else {
				move(GameCube.DOWN, true);
			}
		} else if (Pattern.matches("Right.*", s)) {
			if (Pattern.matches(".*Alt", s)) {
				move(GameCube.RIGHT, false);
			} else {
				move(GameCube.RIGHT, true);
			}
		} else if (Pattern.matches("Back.*", s)) {
			if (Pattern.matches(".*Alt", s)) {
				move(GameCube.BACK, false);
			} else {
				move(GameCube.BACK, true);
			}
		} else if (s.equals("Randomize")) {
			cube = new FullStickerCube(cube.getSize(), true);
			updateRect(new RectDraw(cube));
		}
		
	}
	
	
	/*
	 * Removes old display image of cube and replaces it with new, updated one.
	 */
	private void updateRect(RectDraw r) {
		Component[] componentList = panel.getComponents();
		for (Component c : componentList) {
			if (c instanceof RectDraw) {
				panel.remove(c);
				break;
			}
		}
		panel.add(r);
		panel.revalidate();
		panel.repaint();
	}
}
