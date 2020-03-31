package graphics;


import java.awt.Dimension;
import java.awt.FlowLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;

import cube.FullStickerCube;
import graphics.scenes.scenes2D.Cube2DPanel;
import graphics.scenes.scenes3D.GraphicsDemo3D;
import graphics.scenes.scenes3D.RubicksScene3D;


public class GraphicsMain {	
	public static void main(String[] args) {
		JFrame mainDisplay = new JFrame();
		
		boolean showCube = true;
		boolean showDemo = !showCube;
		
		int windowSize = 900;
		mainDisplay.setSize(windowSize + 20, windowSize + 20);
		JPanel container = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0)); 
		//container.setLayout(new BoxLayout(container, FlowLayout));
		/*for(int i = 1; i < 11; i++) {
			CubeScene3D s = new CubeScene3D(i, true, windowSize);
			s.setPreferredSize(new Dimension(windowSize, windowSize));
			container.add(s);
		}*/
		
		if(showDemo) {
			GraphicsDemo3D s2 = new GraphicsDemo3D(windowSize);
			s2.setPreferredSize(new Dimension(windowSize, windowSize));
			container.add(s2);
		}
		
		
		if(showCube) {
			RubicksScene3D s = new RubicksScene3D(15, true, windowSize);
			s.setPreferredSize(new Dimension(windowSize, windowSize));
			container.add(s);
			RubicksScene3D s2 = new RubicksScene3D(30, true, windowSize);
			s2.setPreferredSize(new Dimension(windowSize, windowSize));
			container.add(s2);
		}
		
		JPanel mainPanel = new JPanel();
		mainPanel.setPreferredSize(new Dimension(windowSize, windowSize));
		JPanel cmdPanel = new JPanel();
		JPanel cubePanel = new JPanel();
		Cube2DPanel c = new Cube2DPanel(new FullStickerCube(2));
		cubePanel.add(c);
		cmdPanel.add(new MovePanel(c));
		mainPanel.add(cmdPanel);
		mainPanel.add(cubePanel);
		mainDisplay.add(mainPanel);

		mainDisplay.add(container);
		mainDisplay.setTitle("Project BigCube");
		mainDisplay.setVisible(true);
		mainDisplay.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
	}
}
