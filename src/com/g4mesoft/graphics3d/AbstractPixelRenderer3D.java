package com.g4mesoft.graphics3d;

import java.util.LinkedList;
import java.util.Queue;

import com.g4mesoft.graphic.IViewport;
import com.g4mesoft.graphic.PixelRenderer2D;
import com.g4mesoft.math.Vec4f;

public abstract class AbstractPixelRenderer3D extends PixelRenderer2D {

	protected static final float FAR_DEPTH = 1.0f;
	
	protected IShader3D shader;

	protected final Vec4f[] clippingNormals;
	protected float[] depthBuffer;
	
	protected boolean cullEnabled;
	protected TriangleFace cullFace;
	
	public AbstractPixelRenderer3D(IViewport viewport, int width, int height) {
		super(viewport, width, height);
		
		shader = new BasicShader3D();
		
		clippingNormals = new Vec4f[] {
			// Z CLIP 
			new Vec4f( 0,  0,  1, 0),
			new Vec4f( 0,  0, -1, 0),
			// X CLIP
			new Vec4f( 1,  0,  0, 0),
			new Vec4f(-1,  0,  0, 0),
			// Y CLIP
			new Vec4f( 0,  1,  0, 0),
			new Vec4f( 0, -1,  0, 0)
		};
		
		cullEnabled = true;
		cullFace = TriangleFace.BACK_FACE;
	}

	@Override
	public void setSize(int width, int height) {
		super.setSize(width, height);
		
		depthBuffer = new float[width * height];
	}
	
	@Override
	public void clear() {
		int i = pixels.length;
		while (i-- != 0) {
			pixels[i] = color;
			depthBuffer[i] = FAR_DEPTH;
		}
	}
	
	public void clearDepth() {
		int i = depthBuffer.length;
		while (i-- != 0)
			depthBuffer[i] = FAR_DEPTH;
	}
	
	public void drawVertices(Vertex3D[] vertices) {
		drawVertices(vertices, 0, vertices.length, Shape3D.TRIANGLES);
	}

	public void drawVertices(Vertex3D[] vertices, Shape3D shape) {
		drawVertices(vertices, 0, vertices.length, shape);
	}

	public void drawVertices(Vertex3D[] vertices, int offset, int length) {
		drawVertices(vertices, offset, length, Shape3D.TRIANGLES);
	}
	
	public void drawVertices(float[] buffer, int vertexSize) {
		drawVertices(buffer, vertexSize, Shape3D.TRIANGLES);
	}

	public void drawVertices(float[] buffer, int vertexSize, Shape3D shape) {
		drawVertices(buffer, vertexSize, 0, buffer.length / vertexSize, shape);
	}

	public void drawVertices(float[] buffer, int vertexSize, int bufferOffset, int numVertices) {
		drawVertices(buffer, vertexSize, bufferOffset, numVertices, Shape3D.TRIANGLES);
	}

	public void drawVertices(Vertex3D[] vertices, int offset, int length, Shape3D shape) {
		drawVertices(new BasicVertexProvider(vertices, offset, length, shape));
	}
	
	public void drawVertices(float[] buffer, int vertexSize, int bufferOffset, int numVertices, Shape3D shape) {
		drawVertices(new FloatArrayVertexProvider(buffer, vertexSize, bufferOffset, numVertices, shape));
	}

	public abstract void drawVertices(IVertexProvider vertexProvider);
	
	protected boolean canPreCullTriangle(Triangle3D triangle) {
		if (triangle.v0.pos.z < -triangle.v0.pos.w)
			return false;
		if (triangle.v1.pos.z < -triangle.v1.pos.w)
			return false;
		if (triangle.v2.pos.z < -triangle.v2.pos.w)
			return false;
		
		float x0 = triangle.v0.pos.x / triangle.v0.pos.w;
		float y0 = triangle.v0.pos.y / triangle.v0.pos.w;
		
		float x1 = triangle.v1.pos.x / triangle.v1.pos.w;
		float y1 = triangle.v1.pos.y / triangle.v1.pos.w;

		float x2 = triangle.v2.pos.x / triangle.v2.pos.w;
		float y2 = triangle.v2.pos.y / triangle.v2.pos.w;
		
		// Calculate triangle orientation
		float d = (y1 - y0) * (x2 - x1) - 
		          (x1 - x0) * (y2 - y1);
		
		if (!Float.isFinite(d))
			return true;
		
		switch (cullFace) {
		case BACK_FACE:
			return d > 0;
		case FRONT_FACE:
			return d < 0;
		}
		
		return false;
	}
	
	protected void clipAndRenderTriangle(Triangle3D t, TriangleCache cache, int normalIndex) {
		if (normalIndex >= clippingNormals.length) {
			transformAndRenderTriangle(t, cache);
		} else {
			Vec4f normal = clippingNormals[normalIndex];
			
			if (normal.dot(t.v0.pos) >= -t.v0.pos.w) {
				if (normal.dot(t.v1.pos) >= -t.v1.pos.w) {
					if (normal.dot(t.v2.pos) >= -t.v2.pos.w) {
						clipAndRenderTriangle(t, cache, normalIndex + 1);
					} else {
						clip2Inside(t.v0, t.v1, t.v2, cache, normalIndex);
					}
				} else {
					if (normal.dot(t.v2.pos) >= -t.v2.pos.w) {
						clip2Inside(t.v2, t.v0, t.v1, cache, normalIndex);
					} else {
						clip2Outside(t.v0, t.v1, t.v2, cache, normalIndex);
					}
				}
			} else {
				if (normal.dot(t.v1.pos) >= -t.v1.pos.w) {
					if (normal.dot(t.v2.pos) >= -t.v2.pos.w) {
						clip2Inside(t.v1, t.v2, t.v0, cache, normalIndex);
					} else {
						clip2Outside(t.v1, t.v2, t.v0, cache, normalIndex);
					}
				} else {
					if (normal.dot(t.v2.pos) >= -t.v2.pos.w) {
						clip2Outside(t.v2, t.v0, t.v1, cache, normalIndex);
					} else {
						// All vertices are outside of view.
					}
				}
			}
		}
	}
	
	private void clip2Inside(Vertex3D in0, Vertex3D in1, Vertex3D out, TriangleCache cache, int normalIndex) {
		Vec4f n = clippingNormals[normalIndex];
		
		Triangle3D t0 = cache.getTriangle();
		Triangle3D t1 = cache.getTriangle();
		
		t0.v0.setVertex(in0);
		interpolateClippedVertex(n, out, in1, t0.v1);
		interpolateClippedVertex(n, out, in0, t0.v2);

		t1.v0.setVertex(in1);
		t1.v1.setVertex(t0.v1);
		t1.v2.setVertex(in0);

		clipAndRenderTriangle(t0, cache, normalIndex + 1);
		clipAndRenderTriangle(t1, cache, normalIndex + 1);
		
		cache.storeTriangle(t0);
		cache.storeTriangle(t1);
	}
	
	private void clip2Outside(Vertex3D in, Vertex3D out0, Vertex3D out1, TriangleCache cache, int normalIndex) {
		Vec4f n = clippingNormals[normalIndex];
		
		Triangle3D t = cache.getTriangle();
		
		t.v0.setVertex(in);
		interpolateClippedVertex(n, out0, in, t.v1);
		interpolateClippedVertex(n, out1, in, t.v2);
		
		clipAndRenderTriangle(t, cache, normalIndex + 1);

		cache.storeTriangle(t);
	}
	
	protected void interpolateClippedVertex(Vec4f n, Vertex3D outVertex, Vertex3D inVertex, Vertex3D result) {
		// Set result position to interpolation
		// difference temporarily.
		result.pos.x = inVertex.pos.x - outVertex.pos.x;
		result.pos.y = inVertex.pos.y - outVertex.pos.y;
		result.pos.z = inVertex.pos.z - outVertex.pos.z;
		result.pos.w = inVertex.pos.w - outVertex.pos.w;
		
		float w0 = outVertex.pos.w;
		float w1 = inVertex.pos.w;
		
		// Interpolation value. Limits the 
		// new w value to be equal to the
		// n.dot(result.pos) value.
		float t = (w0 + n.dot(outVertex.pos)) / (w0 - w1 - n.dot(result.pos));
		
		// Calculate new position
		result.pos.x = result.pos.x * t + outVertex.pos.x;
		result.pos.y = result.pos.y * t + outVertex.pos.y;
		result.pos.z = result.pos.z * t + outVertex.pos.z;
		result.pos.w = result.pos.w * t + outVertex.pos.w;
		
		for (int i = 0; i < result.data.length; i++)
			result.data[i] = (1.0f - t) * outVertex.data[i] + t * inVertex.data[i];
	}

	private void transformAndRenderTriangle(Triangle3D triangle, TriangleCache cache) {
		if (transformAndCullTriangle(triangle, cache))
			renderTriangle(triangle, cache);
	}
	
	private boolean transformAndCullTriangle(Triangle3D t, TriangleCache cache) {
		float hw = 0.5f * width;
		float hh = 0.5f * height;
			
		t.v0.pos.x /= t.v0.pos.w;
		t.v0.pos.y /= t.v0.pos.w;

		t.v1.pos.x /= t.v1.pos.w;
		t.v1.pos.y /= t.v1.pos.w;

		t.v2.pos.x /= t.v2.pos.w;
		t.v2.pos.y /= t.v2.pos.w;

		// Calculate triangle orientation
		float d = (t.v1.pos.y - t.v0.pos.y) * (t.v2.pos.x - t.v1.pos.x) - 
		          (t.v1.pos.x - t.v0.pos.x) * (t.v2.pos.y - t.v1.pos.y);

		if (cullEnabled) {
			if (d >= 0.0f && cullFace == TriangleFace.BACK_FACE ||
				d <= 0.0f && cullFace == TriangleFace.FRONT_FACE) {
				
				return false;
			}
		} else if (!Float.isFinite(d)) {
			return false;
		}

		// Transform to viewport space
		t.v0.pos.x = (t.v0.pos.x + 1.0f) * hw;
		t.v1.pos.x = (t.v1.pos.x + 1.0f) * hw;
		t.v2.pos.x = (t.v2.pos.x + 1.0f) * hw;

		t.v0.pos.y = (1.0f - t.v0.pos.y) * hh;
		t.v1.pos.y = (1.0f - t.v1.pos.y) * hh;
		t.v2.pos.y = (1.0f - t.v2.pos.y) * hh;

		// Normalize depth
		t.v0.pos.z /= t.v0.pos.w;
		t.v1.pos.z /= t.v1.pos.w;
		t.v2.pos.z /= t.v2.pos.w;
		
		// Transform to fit inside depth buffer
		// where near is 0.0 and far is 1.0.
		t.v0.pos.z = (t.v0.pos.z + 1.0f) * 0.5f;
		t.v1.pos.z = (t.v1.pos.z + 1.0f) * 0.5f;
		t.v2.pos.z = (t.v2.pos.z + 1.0f) * 0.5f;
		
		// Perspective correction of vertex data
		for (int i = 0; i < cache.vertexNumData; i++) {
			t.v0.data[i] /= t.v0.pos.w;
			t.v1.data[i] /= t.v1.pos.w;
			t.v2.data[i] /= t.v2.pos.w;
		}
		
		// Used for perspective correction 
		// during rendering of triangles.
		t.v0.pos.w = 1.0f / t.v0.pos.w;
		t.v1.pos.w = 1.0f / t.v1.pos.w;
		t.v2.pos.w = 1.0f / t.v2.pos.w;
		
		return true;
	}
	
	protected abstract void renderTriangle(Triangle3D triangle, TriangleCache cache);
	
	protected void fillTriangle(Vertex3D v0, Vertex3D v1, Vertex3D v2, TriangleCache cache, Fragment3D fragment) {
		if (v0.pos.y > v1.pos.y) {
			Vertex3D tmp = v1;
			v1 = v0;
			v0 = tmp;
		}

		if (v1.pos.y > v2.pos.y) {
			Vertex3D tmp = v1;
			v1 = v2;
			v2 = tmp;
		}
		
		if (v0.pos.y > v1.pos.y) {
			Vertex3D tmp = v1;
			v1 = v0;
			v0 = tmp;
		}

		int iy0 = (int)(v0.pos.y + 0.5f);
		int iy1 = (int)(v1.pos.y + 0.5f);
		int iy2 = (int)(v2.pos.y + 0.5f);
		
		Triangle3D triangle = cache.getTriangle();
		Vertex3D vertY0 = triangle.v0;
		Vertex3D vertY1 = triangle.v1;
		Vertex3D vertXY = triangle.v2;

		if (iy0 != iy1) {
			float dy0 = v2.pos.y - v0.pos.y;
			float dy1 = v1.pos.y - v0.pos.y;
			
			float dy = iy0 - v0.pos.y + 0.5f;
			
			for (int y = iy0; y != iy1; y++) {
				interpolateVertex(v0, v2, dy / dy0, vertY0);
				interpolateVertex(v0, v1, dy / dy1, vertY1);
				dy++;
				
				if (vertY1.pos.x < vertY0.pos.x) {
					drawTriangleRow(y, vertY1, vertY0, vertXY, fragment);
				} else {
					drawTriangleRow(y, vertY0, vertY1, vertXY, fragment);
				}
			}
		}
		
		if (iy1 != iy2) {
			float dy0 = v0.pos.y - v2.pos.y;
			float dy1 = v1.pos.y - v2.pos.y;

			float dy = iy1 - v2.pos.y + 0.5f;

			for (int y = iy1; y != iy2; y++) {
				interpolateVertex(v2, v0, dy / dy0, vertY0);
				interpolateVertex(v2, v1, dy / dy1, vertY1);
				dy++;

				if (vertY1.pos.x < vertY0.pos.x) {
					drawTriangleRow(y, vertY1, vertY0, vertXY, fragment);
				} else {
					drawTriangleRow(y, vertY0, vertY1, vertXY, fragment);
				}
			}
		}
		
		cache.storeTriangle(triangle);
	}
	
	private final void drawTriangleRow(int y, Vertex3D vertY0, Vertex3D vertY1, Vertex3D vertXY, Fragment3D fragment) {
		int xs = (int)(vertY0.pos.x + 0.5f);
		int xe = (int)(vertY1.pos.x + 0.5f);
		
		float dx0 = vertY1.pos.x - vertY0.pos.x;

		float dx = xs - vertY0.pos.x + 0.5f;
		
		int index = xs + y * width;
		for (int x = xs; x != xe; x++) {
			interpolateVertex(vertY0, vertY1, dx / dx0, vertXY);
			dx++;

			if (vertXY.pos.z <= depthBuffer[index]) {
				// Perspective correction of vertex data
				for (int i = 0; i < vertXY.data.length; i++)
					vertXY.data[i] /= vertXY.pos.w;
				
				fragment.setRGB(pixels[index]);
				if (shader.fragment(vertXY, fragment)) {
					depthBuffer[index] = vertXY.pos.z;
					pixels[index] = fragment.getRGB();
				}
			}
			
			index++;
		}
	}
	
	private final void interpolateVertex(Vertex3D v0, Vertex3D v1, float t, Vertex3D result) {
		float omt = 1.0f - t;
		
		result.pos.x = v1.pos.x * t + v0.pos.x * omt;
		result.pos.y = v1.pos.y * t + v0.pos.y * omt;
		result.pos.z = v1.pos.z * t + v0.pos.z * omt;
		result.pos.w = v1.pos.w * t + v0.pos.w * omt;

		for (int i = 0; i < result.data.length; i++)
			result.data[i] = v1.data[i] * t + v0.data[i] * omt;
	}
	
	public void setCullEnabled(boolean enabled) {
		cullEnabled = enabled;
	}

	public void setCullFace(TriangleFace face) {
		cullFace = face;
	}
	
	public void setShader(IShader3D shader) {
		this.shader = shader;
	}

	public IShader3D getShader() {
		return shader;
	}
	
	public float[] getDepthBuffer() {
		return depthBuffer;
	}
	
	@Override
	public void dispose() {
		super.dispose();
		
		shader = null;
		depthBuffer = null;
	}
	
	protected static class TriangleCache {
		
		private final int vertexNumData;
		private final Queue<Triangle3D> cache;

		public TriangleCache(int vertexNumData) {
			this.vertexNumData = vertexNumData;

			cache = new LinkedList<Triangle3D>();
		}
		
		public void storeTriangle(Triangle3D triangle) {
			cache.add(triangle);
		}

		public Triangle3D getTriangle() {
			Triangle3D t = cache.poll();
			return t != null ? t : new Triangle3D(vertexNumData);
		}
		
		public void clear() {
			cache.clear();
		}
	}
}
