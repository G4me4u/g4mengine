package com.g4mesoft.graphics3d;

import java.util.LinkedList;
import java.util.Queue;

import com.g4mesoft.graphic.IViewport;

public class PixelRenderer3D extends AbstractPixelRenderer3D {

	private Queue<Triangle3D> trianglesToClip;
	private Queue<Triangle3D> clippedTriangles;
	
	public PixelRenderer3D(IViewport viewport, int width, int height) {
		super(viewport, width, height);
		
		trianglesToClip = new LinkedList<Triangle3D>();
		clippedTriangles = new LinkedList<Triangle3D>();
	}
	
	@Override
	public void drawVertices(Vertex3D[] vertices, int offset, int length) {
		if (length % 3 != 0)
			throw new IllegalArgumentException("Number of vertices is not a multiple of 3");
		
		if (offset < 0)
			throw new ArrayIndexOutOfBoundsException(offset);
		
		int end = length + offset;
		if (end > vertices.length)
			throw new ArrayIndexOutOfBoundsException(vertices.length);
		
		shader.prepareShader();
		
		TriangleCache triangleCache = new TriangleCache(shader.getOutputSize());
		
		int i = offset;
		while (i < end) {
			Vertex3D v0 = vertices[i++];
			Vertex3D v1 = vertices[i++];
			Vertex3D v2 = vertices[i++];
			
			Triangle3D triangle = triangleCache.getTriangle();
			shader.projectVertices(triangle, v0, v1, v2);
			if (cullEnabled && canPreCullTriangle(triangle)) {
				triangleCache.storeTriangle(triangle);
				continue;
			}
			
			trianglesToClip.add(triangle);
			triangle = null;

			Queue<Triangle3D> trianglesToDraw = clipTriangle(triangleCache, trianglesToClip, clippedTriangles);
			transformAndCullTriangles(trianglesToDraw, triangleCache);

			if (!trianglesToDraw.isEmpty()) {
				renderTriangles(trianglesToDraw, triangleCache);
				
				for (Triangle3D t : trianglesToDraw)
					triangleCache.storeTriangle(t);
				trianglesToDraw.clear();
			}
		}
		
		triangleCache.clear();
	}
	
	@Override
	public void dispose() {
		super.dispose();
		
		clippedTriangles = null;
		trianglesToClip = null;
	}
}
