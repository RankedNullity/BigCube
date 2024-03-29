package cube;


import java.awt.Color;

import math.linalg.TrMatrix;

/**
 * Class for representing a rubicks cube using a minimal one-hot encoding,
 * which has the least amount of redundant information in a rubicks cube. 
 * @author Jaron Wang
 * @version 6-19-2019
 */
public class MinimalOneHotCube implements GameCube {
	private TrMatrix cube;
	private int size;
	/**
	 * Creates a cube that is n x n x n. 
	 * 
	 * @param n , n >= 2
	 */
	public MinimalOneHotCube(int n) {
		// First
	}
	
	

	@Override
	public int[][][] getColorAsIntArray() {
		// TODO Auto-generated method stub
		return null;
	}
	

	@Override
	public Color[][][] getColorArray() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void applyMove(int face, int slice, boolean clockwise) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void applyMove(Action move) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public int getSize() {
		// TODO Auto-generated method stub
		return size;
	}



	@Override
	public GameCube deepCopy() {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public GameCube move(int face, int slice, boolean clockwise) {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public GameCube move(Action action) {
		// TODO Auto-generated method stub
		return null;
	}




	
}
