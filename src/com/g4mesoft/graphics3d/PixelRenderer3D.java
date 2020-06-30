package com.g4mesoft.graphics3d;

import com.g4mesoft.graphic.IViewport;

public class PixelRenderer3D extends AbstractPixelRenderer3D {

	private final Fragment3D fragment;
	
	public PixelRenderer3D(IViewport viewport, int width, int height) {
		super(viewport, width, height);
		
		fragment = new Fragment3D();
	}
	
	@Override
	public void drawVertices(IVertexProvider vertexProvider) {
		shader.prepareShader();
		
		TriangleCache triangleCache = new TriangleCache(shader.getOutputSize());
		vertexProvider.prepareDraw();
		
		switch (vertexProvider.getShape()) {
		case TRIANGLES:
			while (vertexProvider.hasNext()) {
				Vertex3D v0 = vertexProvider.getNextVertex();
				Vertex3D v1 = vertexProvider.getNextVertex();
				Vertex3D v2 = vertexProvider.getNextVertex();
				
				drawTriangleVertices(triangleCache, v0, v1, v2);
			}
			
			break;
		case QUADS:
			while (vertexProvider.hasNext()) {
				Vertex3D v0 = vertexProvider.getNextVertex();
				Vertex3D v1 = vertexProvider.getNextVertex();
				Vertex3D v2 = vertexProvider.getNextVertex();
				Vertex3D v3 = vertexProvider.getNextVertex();

				drawTriangleVertices(triangleCache, v0, v1, v2);
				drawTriangleVertices(triangleCache, v0, v2, v3);
			}
			
			break;
		
		default:
			throw new IllegalArgumentException("Shape not supported!");
		}
		
		triangleCache.clear();
	}
	
	private void drawTriangleVertices(TriangleCache triangleCache, Vertex3D v0, Vertex3D v1, Vertex3D v2) {
		Triangle3D triangle = triangleCache.getTriangle();
		shader.projectVertices(triangle, v0, v1, v2);
		if (cullEnabled && canPreCullTriangle(triangle)) {
			triangleCache.storeTriangle(triangle);
			return;
		}
		
		clipAndRenderTriangle(triangle, triangleCache, 0);
		
		triangleCache.storeTriangle(triangle);
	}

	@Override
	protected void renderTriangle(Triangle3D triangle, TriangleCache cache) {
		fillTriangle(triangle.v0, triangle.v1, triangle.v2, cache, fragment);
	}
}
