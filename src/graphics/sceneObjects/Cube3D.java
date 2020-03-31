package graphics.sceneObjects;

import java.awt.Color;

import cube.GameCube;
import math.linalg.Matrix;
import math.linalg.lin3d.Plane3d;
import math.linalg.lin3d.Vector3d;

public class Cube3D extends Polyhedron {
	private Vector3d center;
	private double width;
	public Cube3D(double x, double y, double z, double width) { 
		faces = new Polygon3D[6];
		this.width = width;
		faces[GameCube.DOWN] = new Polygon3D(new double[] { x, x + width, x + width, x },
								 new double[] { y, y, y + width, y + width },
								 new double[] { z, z, z, z }, Color.GRAY);
		faces[GameCube.UP] = new Polygon3D(new double[] { x, x + width, x + width, x },
								 new double[] { y, y, y + width, y + width },
							 	new double[] { z + width, z + width, z + width, z + width }, Color.GRAY);
		faces[GameCube.LEFT] = new Polygon3D(new double[] { x, x, x + width, x + width }, 
								 new double[] { y, y, y, y },
							 	 new double[] { z, z + width, z + width, z }, Color.GRAY); 
		faces[GameCube.FRONT] = new Polygon3D(new double[] { x + width, x + width, x + width, x + width },
								new double[] { y, y, y + width, y + width },
								new double[] { z, z + width, z + width, z }, Color.GRAY);
		faces[GameCube.RIGHT] = new Polygon3D(new double[] { x, x, x + width, x + width },
								new double[] { y + width, y + width, y + width, y + width },
								new double[] { z, z + width, z + width, z }, Color.GRAY);
		faces[GameCube.BACK] = new Polygon3D(new double[] { x, x, x, x }, 
								new double[] { y, y, y + width, y + width },
								new double[] { z, z + width, z + width, z }, Color.GRAY);
		minVertexDegree = maxVertexDegree = 3;
		visibleFaces = new int[maxVertexDegree];
		
	}

	/**
	 * Changes the color of face[i] to c. 
	 * @param c
	 * @param i
	 */
	void setColor(Color c, int i) {
		faces[i].setColor(c);
	}
	
	/**
	 * Removes all faces from visible. 
	 */
	void clearVisibles() {
		for (int i = 0; i < visibleFaces.length; i++) {
			visibleFaces[i] = -1;
		}
	}
	
	/**
	 * Manually udpates the visible value at index, and updates that drawable using the necessary parameters. 
	 * @see updateDrawables(viewPlane, zoom, screenWidth, lighting)
	 * @param visibles
	 * @param index
	 * @param viewPlane
	 * @param zoom
	 * @param screenWidth
	 * @param lighting
	 */
	void manualUpdateDrawables(int visibles, int index, Plane3d viewPlane, double zoom, int screenWidth, boolean lighting) {
		faces[visibles].updateDrawable(viewPlane, zoom, screenWidth, lighting);
		visibleFaces[index] = visibles;
	}
	
	public double getWidth() {
		return width;
	}
	
	public double getAvgDistance(Plane3d p) {
		double d1 = faces[GameCube.FRONT].getAvgDistance(p);
		double d2 = faces[GameCube.BACK].getAvgDistance(p);
		return (d1 + d2) / 2;
	}
	
	public double getClosestDistance(Plane3d p) {
		double d1 = faces[GameCube.FRONT].getAvgDistance(p);
		double d2 = faces[GameCube.BACK].getAvgDistance(p);
		return d1 > d2 ? d2 : d1;
	}
}
