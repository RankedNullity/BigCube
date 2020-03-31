package graphics.sceneObjects;

import java.awt.Graphics;

import math.linalg.Matrix;
import math.linalg.lin3d.Plane3d;
import math.linalg.lin3d.Vector3d;

public interface SceneObject {
	public void updateDrawable(Plane3d viewPlane, double zoom, int screenWidth, boolean lighting);
	public double getAvgDistance(Vector3d point);
	public double getAvgDistance(Plane3d plane);
	public double getAvgDistance(double x, double y, double z);
	
	public double getClosestDistance(Vector3d point);
	public double getClosestDistance(Plane3d plane);
	public double getClosestDistance(double x, double y, double z);
	public void applyTransform(Matrix m);
	public void render(Graphics g);
}
