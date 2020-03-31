package graphics;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.LineBorder;

import cube.FullStickerCube;
import cube.GameCube;
import graphics.scenes.scenes2D.Cube2DPanel;

public class MovePanel extends JPanel implements ActionListener {
	Cube2DPanel cubePanel;
	JRadioButton[] radioChecks;
	private boolean sliceTextCheck;
	
	public MovePanel(Cube2DPanel cubePanel) {
		this.cubePanel = cubePanel;
		this.setLayout(new BorderLayout());
		JPanel dimensions = new JPanel();
		JButton d1 = new JButton("2x2x2");
		JButton d2 = new JButton("3x3x3");
		JButton d3 = new JButton("4x4x4");
		d1.addActionListener(this);
		d2.addActionListener(this);
		d3.addActionListener(this);
		dimensions.add(d1);
		dimensions.add(d2);
		dimensions.add(d3);
		
		JPanel slices = new JPanel(new GridLayout(6, 1));
		radioChecks = new JRadioButton[6];
		
		for (int i = 0; i < 6; i++) {
			JRadioButton rButton = new JRadioButton("Slice " + i, false);
			rButton.addActionListener(this);
			rButton.setVisible(true);
			radioChecks[i] = rButton;
			slices.add(rButton);
			
		}
		int cubeSize = cubePanel.getCube().getSize();
		updateRadioChecks(cubeSize);
		
		JPanel moves = new JPanel();
		JPanel normalMoves = new JPanel(new GridLayout(6, 1));
		for (int i = 0; i < 6; i++) {
			JButton button = new JButton(GameCube.FACE_STRING[i]);
			button.addActionListener(this);
			normalMoves.add(button);
		}
		
		JPanel altMoves = new JPanel(new GridLayout(6, 1));
		for (int i = 0; i < 6; i++) {
			JButton button = new JButton(GameCube.FACE_STRING[i] + " Alt");
			button.addActionListener(this);
			altMoves.add(button);
		}
		
		JPanel miscPanel = new JPanel();
		JButton randomize = new JButton("Randomize");
		randomize.addActionListener(this);
		JButton sliceTextSwitch = new JButton("Slice Text On/Off");
		sliceTextSwitch.addActionListener(this);
		miscPanel.add(randomize);
		miscPanel.add(sliceTextSwitch);
		
		moves.add(normalMoves);
		moves.add(altMoves);
		
		this.setBorder(new LineBorder(Color.BLACK, 5));
		this.add(dimensions, BorderLayout.PAGE_START);
		this.add(slices, BorderLayout.LINE_START);
		this.add(moves, BorderLayout.LINE_END);
		this.add(miscPanel, BorderLayout.PAGE_END);
	}
	
	
	public Dimension getPreferredSize() {
		return new Dimension(250, 250);
	}
	
	private void move(int face, boolean clockwise) {
		for (int i = 0; i < radioChecks.length; i++) {
			if (radioChecks[i].isEnabled() && radioChecks[i].isSelected()) {
				cubePanel.move(face, i, clockwise);
			}
		}
	}
	
	private void updateRadioChecks(int n) {
		for (int i = 0; i < radioChecks.length; i++) {
			if (i < n) {
				radioChecks[i].setEnabled(true);
			} else {
				radioChecks[i].setEnabled(false);
			}
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String s = e.getActionCommand();
		if (s.equals("2x2x2")) {
			cubePanel.resizeCube(2);
			updateRadioChecks(2);
			
		} else if (s.equals("3x3x3")) {
			cubePanel.resizeCube(3);
			updateRadioChecks(3);
			
		} else if (s.equals("4x4x4")) {
			cubePanel.resizeCube(4);
			updateRadioChecks(4);
			
		}  else if (s.equals("Slice Text On/Off")) {
			cubePanel.updateSliceCheck();
			
		}  else if (Pattern.matches("Front.*", s)) {
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
			cubePanel.randomize();
		}
		
		
	}
}
