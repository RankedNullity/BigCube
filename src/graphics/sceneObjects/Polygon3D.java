package graphics.sceneObjects;

import java.awt.Color;
import java.awt.Graphics;

import graphics.Polygon2D;
import math.linalg.*;
import math.linalg.lin3d.*;

/**
 * 3 Dimensional polygon class for use in graphics.
 * @author Jaron Wang
 *
 */
public class Polygon3D implements SceneObject {
	private Color c;
	private Vector3d[] vertices;
	private Polygon2D drawable;
	
	public Polygon3D(double[] x, double[] y, double[] z, Color c) {
		if (x.length != y.length || x.length != z.length || y.length != z.length) {
			throw new IllegalArgumentException("Illegal vertex arguments");
		}
		vertices = new Vector3d[x.length];
		for (int i = 0; i < x.length; i++) {
			vertices[i]  = new Vector3d(x[i], y[i], z[i]);
		}
		this.c = c; 
		drawable = new Polygon2D(vertices.length, c);
	}
	
	public Polygon3D(Vector3d[] vertices, Color c) {
		this.vertices = vertices;
		this.c = c;
	} 
	
	public Color getColor() {
		return c;
	}
	
	public void setColor(Color c) {
		this.c = c;
		drawable.setColor(c);
	}
	
	
	/**
	 * Applies the given transform to all the points on the polygon. (centered about the origin).
	 * @param transform
	 */
	public void applyTransform(Matrix transform) {
		for(int i = 0; i < vertices.length; i++) {
			vertices[i] = LinAlg.multiply(transform, vertices[i]).get3DVector();
		}
	}
	
	/**
	 * Returns the smallest Euclidean distance from this polygon to the point (x, y, z)
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	public double getClosestDistance(double x, double y, double z) {
		return getClosestDistance(new Vector3d(x, y, z));
	}
	
	/**
	 * Returns the vertices in this polygon. 
	 * @return
	 */
	public Vector3d[] getVertices() {
		return vertices;
	}
	
	
	/**
	 * Returns the smallest distance from this polygon to point p. 
	 * @param p
	 * @return
	 */
	public double getClosestDistance(Vector3d p) {
		double distance = Double.MAX_VALUE;
		for (int i = 0; i < vertices.length; i++) {
			double distanceToVertex = LinAlg.norm(LinAlg.elementWiseSubtraction(p, vertices[i]), 2);
			if (distanceToVertex < distance) {
				distance = distanceToVertex; 
			}
		}
		return distance; 
	}
	
	
	/**
	 * Returns the smallest distance from this polygon to the Plane p. 
	 * @param p
	 * @return
	 */
	public double getClosestDistance(Plane3d p) {
		double distance = Double.MAX_VALUE;
		for (int i = 0; i < vertices.length; i++) {
			double distanceToVertex = Lin3d.getDistance(p, vertices[i]);
			if (distanceToVertex < distance) {
				distance = distanceToVertex; 
			}
		}
		return distance; 
	}
	
	
	/**
	 * Returns the smallest distance from this polygon to the Plane p. 
	 * @param p
	 * @return
	 */
	public double getFarthestDistance(Plane3d p) {
		double distance = 0;
		for (int i = 0; i < vertices.length; i++) {
			double distanceToVertex = Lin3d.getDistance(p, vertices[i]);
			if (distanceToVertex > distance) {
				distance = distanceToVertex; 
			}
		}
		return distance; 
	}
	
	/**
	 * Returns the average distance from this polygon to the point (x, y, z)
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	public double getAvgDistance(double x, double y, double z) {
		return getAvgDistance(new Vector3d(x,y,z));
	}
	
	/**
	 * Returns the average distance from this polygon to the point p.
	 * @param p
	 * @return
	 */
	public double getAvgDistance(Vector3d p) {
		double total = 0;
		for (int i = 0; i < vertices.length; i++) {
			double distanceToVertex = LinAlg.norm(LinAlg.elementWiseSubtraction(p, vertices[i]), 2);
			total += distanceToVertex;
		}
		return total / vertices.length; 
	}
	

	/**
	 * Returns the average distance from this polygon to the plane p.
	 * @param p
	 * @return
	 */
	public double getAvgDistance(Plane3d p) {
		double total = 0;
		for (int i = 0; i < vertices.length; i++) {
			double distanceToVertex = Lin3d.getDistance(p, vertices[i]);
			total += distanceToVertex;
		}
		return total / vertices.length; 
	}

	/**
	 * Renders this polygon using g.
	 * @param g
	 */
	public void render(Graphics g) {
		drawable.drawPolygon(g);
	}
	
	/**
	 * Method for updating the drawables for this object.
	 * Should be called every time the object or camera changes, and
	 * once when initialized. 
	 * @param viewPlane View plane. 
	 */
	public void updateDrawable(Plane3d viewPlane, double zoom, int screenWidth, boolean lighting) {
		Vector3d cameraLoc = viewPlane.getPoint();
		
		//Vector3d rotationVector = rotationVector(viewPlane.getPoint(), Lin3d.origin);
		Vector3d viewb1 = viewPlane.getB1();
		Vector3d viewb2 = viewPlane.getB2();
		
		double[][] newPoints = new double[2][vertices.length];
		
		double[] drawableOrigin = pointToDrawable(cameraLoc, Lin3d.origin, Lin3d.origin, viewb1, viewb2);
		
		
		for (int i = 0; i < vertices.length; i++) {
			Vector3d point = vertices[i];
			double[] drawablePoint = pointToDrawable(cameraLoc, Lin3d.origin, point, viewb1, viewb2);
			
			newPoints[0][i] = (screenWidth / 2.0  - drawableOrigin[0]) + drawablePoint[0] * zoom;
			newPoints[1][i] = (screenWidth / 2.0  + drawableOrigin[1]) - drawablePoint[1] * zoom;
		}
		drawable.updatePolygon(newPoints[0], newPoints[1]);
		
		if (lighting) {
			calculateLighting(viewPlane);
		}
	}
	

	private static double[] pointToDrawable(Vector3d cameraLoc, Vector3d focus, Vector3d point, Vector3d b1, Vector3d b2) {
		Vector3d viewToPoint = Lin3d.elementwiseSubtract(point, cameraLoc);
		Vector3d newP = Lin3d.elementwiseAdd(cameraLoc, viewToPoint);
		double drawX = LinAlg.dotProduct(b2, point);
		double drawY = LinAlg.dotProduct(b1, point);
		return new double[] {drawX, drawY};
	}
	
	/**
	 * Outdated. For use for rotating a viewplane against a curvature and a non-constant focal point.
	 * @param cameraLoc
	 * @param focusPoint
	 * @return
	 */
	private static Vector3d rotationVector(Vector3d cameraLoc, Vector3d focusPoint) {
		double dx = Math.abs(cameraLoc.getX() - focusPoint.getX());
		double dy = Math.abs(cameraLoc.getY() - focusPoint.getY());
		double x = ((cameraLoc.getX() - focusPoint.getX() > 0) ? -1 : 1) * dy / (dy + dx); 
		double y = ((cameraLoc.getY() - focusPoint.getY() > 0) ? 1 : -1) * dx / (dy + dx);
		return new Vector3d(x, y, 0);
	}
	
	/**
	 * Calculates and updates the lighting for this object.
	 * @param viewPlane
	 */
	protected void calculateLighting(Plane3d viewPlane) {
		Vector3d normal = Lin3d.crossProduct(Lin3d.elementwiseSubtract(vertices[2] , vertices[0]), Lin3d.elementwiseSubtract(vertices[1] , vertices[0]));
		normal.normalize();
		if (LinAlg.norm(vertices[0], 2) > LinAlg.norm(Lin3d.elementwiseSubtract(vertices[0], normal), 2)) {
			normal = Lin3d.elementwiseSubtract(Lin3d.origin, normal);
			normal.normalize();
		}
		
		Vector3d viewDir = viewPlane.getNormal();
		double numer =  Math.acos(LinAlg.dotProduct(normal, viewDir));
		double denom = LinAlg.norm(viewDir, 2);
		double angle = numer / denom;
		//double angle = Math.acos(LinAlg.dotProduct(normal, viewPlane.getPoint())) / LinAlg.norm(viewPlane.getPoint(), 2);
		drawable.updateLighting(0.25 + 1 - Math.sqrt(Math.toDegrees(angle) / 180));
	}
	
	
}
