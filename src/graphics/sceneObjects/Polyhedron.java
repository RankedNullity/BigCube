package graphics.sceneObjects;


import java.awt.Graphics;
import java.util.Iterator;

import common.datastructures.concrete.ArrayHeap;
import common.datastructures.concrete.KVPair;
import common.datastructures.concrete.dictionaries.ChainedHashDictionary;
import common.datastructures.interfaces.IDictionary;
import common.datastructures.interfaces.IPriorityQueue;
import graphics.ObjectDistancePair;
import math.linalg.LinAlg;
import math.linalg.Matrix;
import math.linalg.lin3d.Lin3d;
import math.linalg.lin3d.Plane3d;
import math.linalg.lin3d.Vector3d;

public class Polyhedron implements SceneObject {
	protected Polygon3D[] faces;
	protected int maxVertexDegree, minVertexDegree;
	protected int[] visibleFaces;
	
	public Polyhedron(Polygon3D[] faces) {
		this.faces = faces;
		updateInfo(); 
		visibleFaces = new int[maxVertexDegree];
	}
	
	// Default Empty constructor. Should only be used when inheriting from this superclass. 
	protected Polyhedron() {
		
	}
	
	/**
	 * Calculates the max and min vertex and the unique set of vertices in the polyhedron. 
	 * @throws IllegalArgumentException if the polyhedron is not closed.
	 */
	protected void updateInfo() {
		IDictionary<Vector3d, Integer> dict = new ChainedHashDictionary();
		for (int i = 0; i < faces.length; i++) {
			Polygon3D face = faces[i];
			Vector3d[] vertices = face.getVertices();
			for (int j = 0; j < vertices.length; i++) {
				Vector3d vertex = vertices[j];
				if (!dict.containsKey(vertex)) {
					dict.put(vertex, 0);
				}
				dict.put(vertex, dict.get(vertex) + 1);
			}
		}
		minVertexDegree = Integer.MAX_VALUE;
		maxVertexDegree = 0;
		Iterator<KVPair<Vector3d, Integer>> iter = dict.iterator();
		while(iter.hasNext()) {
			KVPair<Vector3d, Integer> current = iter.next();
			int value = current.getValue();
			if(value < minVertexDegree) {
				minVertexDegree = value;
			} else if (value > maxVertexDegree) {
				maxVertexDegree = value;
			}
		}
		
		if (minVertexDegree < 3) {
			throw new IllegalArgumentException("This is not a valid Polyhedron.");
		}
	}
	
	/**
	 * Updates the parts of the polyhedron which needs to be drawn. 
	 * @param viewPlane
	 * @param zoom
	 * @param screenWidth. Screen width in pixels. Assumes equal screen height.
	 * @param lighting. Whether or not to render lighting effects. 
	 */
	public void updateDrawable(Plane3d viewPlane, double zoom, int screenWidth, boolean lighting) {
		IPriorityQueue<ObjectDistancePair> pq = new ArrayHeap<>();

		for (int i = 0; i < faces.length; i++) {
			pq.insert(new ObjectDistancePair(faces[i], faces[i].getAvgDistance(viewPlane), i));
		}
		int count = maxVertexDegree - 1;
		while(!pq.isEmpty() && count >= 0) {
			ObjectDistancePair current = pq.removeMin();
			Polygon3D p = (Polygon3D) current.getObject();
			visibleFaces[count--] = current.getIndex();
			p.updateDrawable(viewPlane, zoom, screenWidth, lighting);
		}
	}
	
	
	public double getClosestDistance(Vector3d point) {
		double distance = Double.MAX_VALUE;
		for(int i = 0; i < faces.length; i++) {
			double distanceToVertex = faces[i].getClosestDistance(point);
			if (distanceToVertex < distance) {
				distance = distanceToVertex;
			}
		}
		
		return distance;
	}
	
	public double getClosestDistance(Plane3d p) {
		double distance = Double.MAX_VALUE;
		for (int i = 0; i < faces.length; i++) {
			double distanceToVertex = faces[i].getClosestDistance(p);
			if (distanceToVertex < distance) {
				distance = distanceToVertex; 
			}
		}
		return distance; 
	}
	
	public double getClosestDistance(double x, double y, double z) {
		return getClosestDistance(new Vector3d(x, y, z));
	}
	
	
	/**
	 * Gets the average distance from this polyhedron to the specified point.
	 * @param point
	 * @return
	 */
	public double getAvgDistance(Vector3d point) {
		double distance = 0.0;
		for(int i = 0; i < faces.length; i++) {
			distance += faces[i].getAvgDistance(point);
		}
		
		return distance / faces.length;
	}
	
	
	/**
	 * Gets the average distance from this polyhedron to the specified point.
	 * @param point
	 * @return
	 */
	public double getAvgDistance(Plane3d plane) {
		double distance = 0.0;
		for(int i = 0; i < faces.length; i++) {
			distance += faces[i].getAvgDistance(plane);
		}
		
		return distance / faces.length; 
	}
	
	/**
	 * Gets the average distance from this polyhedron to the specified point (x, y, z). Also calculates and stores drawableFaces
	 * because these operations can be done in parallel to reduce computation. 
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	public double getAvgDistance(double x, double y, double z) {
		return getAvgDistance(new Vector3d(x,y,z));
	}
	
	public int[] getDrawableIndices() {
		return visibleFaces;
	}
	
	public Polygon3D[] getFaces() {
		return faces;
	}
	
	public void applyTransform(Matrix transform) {
		if (transform.getColumns() != 3 || transform.getRows() != 3) {
			throw new IllegalArgumentException(" Not a valid transform: R3 -> R3");
		}
		for (int i = 0; i < faces.length; i++) {
			faces[i].applyTransform(transform);
		}
	}

	@Override
	public void render(Graphics g) {
		for(int i = 0; i < visibleFaces.length; i++) {
			if(visibleFaces[i] != -1)
				faces[visibleFaces[i]].render(g);
		}
	}
}