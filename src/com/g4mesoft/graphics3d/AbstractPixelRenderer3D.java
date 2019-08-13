package com.g4mesoft.graphics3d;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

import com.g4mesoft.graphic.IViewport;
import com.g4mesoft.graphic.PixelRenderer2D;
import com.g4mesoft.math.MathUtils;
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
	
	protected Queue<Triangle3D> clipTriangle(TriangleCache cache, Queue<Triangle3D> trianglesToClip, Queue<Triangle3D> clippedTriangles) {
		for (int i = 0; i < clippingNormals.length; i++) {
			if (i != 0) {
				Queue<Triangle3D> tmp = trianglesToClip;
				trianglesToClip = clippedTriangles;
				clippedTriangles = tmp;
			}

			Vec4f clipNormal = clippingNormals[i];
			for (Triangle3D t : trianglesToClip)
				clipTriangle(t, clipNormal, cache, clippedTriangles);
			
			// Triangles would already be added to
			// the cache. Simply clear the queue.
			trianglesToClip.clear();
		}
		
		return clippedTriangles;
	}

	private void clipTriangle(Triangle3D t, Vec4f normal, TriangleCache cache, Queue<Triangle3D> clippedTriangles) {
		if (normal.dot(t.v0.pos) >= -t.v0.pos.w) {
			if (normal.dot(t.v1.pos) >= -t.v1.pos.w) {
				if (normal.dot(t.v2.pos) >= -t.v2.pos.w) {
					clippedTriangles.add(t);
				} else {
					clip2Inside(normal, t.v0, t.v1, t.v2, cache, clippedTriangles);
					cache.storeTriangle(t);
				}
			} else {
				if (normal.dot(t.v2.pos) >= -t.v2.pos.w) {
					clip2Inside(normal, t.v2, t.v0, t.v1, cache, clippedTriangles);
					cache.storeTriangle(t);
				} else {
					clip2Outside(normal, t.v0, t.v1, t.v2, cache, clippedTriangles);
					cache.storeTriangle(t);
				}
			}
		} else {
			if (normal.dot(t.v1.pos) >= -t.v1.pos.w) {
				if (normal.dot(t.v2.pos) >= -t.v2.pos.w) {
					clip2Inside(normal, t.v1, t.v2, t.v0, cache, clippedTriangles);
					cache.storeTriangle(t);
				} else {
					clip2Outside(normal, t.v1, t.v2, t.v0, cache, clippedTriangles);
					cache.storeTriangle(t);
				}
			} else {
				if (normal.dot(t.v2.pos) >= -t.v2.pos.w) {
					clip2Outside(normal, t.v2, t.v0, t.v1, cache, clippedTriangles);
					cache.storeTriangle(t);
				} else {
					cache.storeTriangle(t);
				}
			}
		}
	}
	
	private void clip2Inside(Vec4f n, Vertex3D in0, Vertex3D in1, Vertex3D out, TriangleCache cache, Queue<Triangle3D> clippedTriangles) {
		Triangle3D t0 = cache.getTriangle();
		Triangle3D t1 = cache.getTriangle();
		
		t0.v0.setVertex(in0);
		interpolateClippedVertex(n, out, in1, t0.v1);
		interpolateClippedVertex(n, out, in0, t0.v2);
		
		t1.v0.setVertex(in0);
		t1.v1.setVertex(in1);
		t1.v2.setVertex(t0.v1);
		
		clippedTriangles.add(t0);
		clippedTriangles.add(t1);
	}
	
	private void clip2Outside(Vec4f n, Vertex3D in, Vertex3D out0, Vertex3D out1, TriangleCache cache, Queue<Triangle3D> clippedTriangles) {
		Triangle3D t = cache.getTriangle();
		
		t.v0.setVertex(in);
		interpolateClippedVertex(n, out0, in, t.v1);
		interpolateClippedVertex(n, out1, in, t.v2);
		
		clippedTriangles.add(t);
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
	
	protected void transformAndCullTriangles(Queue<Triangle3D> triangles, TriangleCache cache) {
		float hw = 0.5f * (width + MathUtils.EPSILON);
		float hh = 0.5f * (height + MathUtils.EPSILON);
		
		Iterator<Triangle3D> itr = triangles.iterator();
		while (itr.hasNext()) {
			Triangle3D t = itr.next();
			
			t.v0.pos.x /= t.v0.pos.w;
			t.v0.pos.y /= t.v0.pos.w;

			t.v1.pos.x /= t.v1.pos.w;
			t.v1.pos.y /= t.v1.pos.w;

			t.v2.pos.x /= t.v2.pos.w;
			t.v2.pos.y /= t.v2.pos.w;

			if (cullEnabled) {
				// Calculate triangle orientation
				float d = (t.v1.pos.y - t.v0.pos.y) * (t.v2.pos.x - t.v1.pos.x) - 
				          (t.v1.pos.x - t.v0.pos.x) * (t.v2.pos.y - t.v1.pos.y);
			
				if (d >= 0.0f && cullFace == TriangleFace.BACK_FACE ||
					d <= 0.0f && cullFace == TriangleFace.FRONT_FACE) {
					
					itr.remove();
					cache.storeTriangle(t);

					continue;
				}
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
		}
	}
	
	protected void renderTriangles(Queue<Triangle3D> triangles, TriangleCache cache) {
		for (Triangle3D t : triangles)
			fillTriangle(t.v0, t.v1, t.v2, cache);
	}
	
	/*
	protected void fillTriangle(Vertex3D v0, Vertex3D v1, Vertex3D v2, TriangleCache cache) {
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

		int x0 = (int)v0.pos.x, y0 = (int)v0.pos.y;
		int x1 = (int)v1.pos.x, y1 = (int)v1.pos.y;
		int x2 = (int)v2.pos.x, y2 = (int)v2.pos.y;
		
		Triangle3D triangle = cache.getTriangle();
		Vertex3D i0 = triangle.v0;
		Vertex3D i1 = triangle.v1;
		Vertex3D i2 = triangle.v2;
		
		if (y0 != y1) {
			int dx0 = x2 - x0, dy0 = y2 - y0;
			int dx1 = x1 - x0, dy1 = y1 - y0;
			
			float a0 = (float)dx0 / dy0;
			float a1 = (float)dx1 / dy1;

			for (int y = y0; y <= y1; y++) {
				int dy = y - y0;

				int xs = (int)(a0 * dy) + x0;
				int xe = (int)(a1 * dy) + x0;
				
				interpolateVertex(v0, v2, (float)dy / dy0, i0);
				interpolateVertex(v0, v1, (float)dy / dy1, i1);
				
				if (xs > xe) {
					int tx = xe;
					xe = xs;
					xs = tx;
					
					Vertex3D tv = i0;
					i0 = i1;
					i1 = tv;
				}
				
				float dt2 = (xs == xe) ? 0.0f : 1.0f / (xe - xs);
				
				int index = xs + y * width;
				for (int x = xs; x <= xe; x++) {
					interpolateVertex(i0, i1, dt2 * (x - xs), i2);
					
					if (i2.pos.z <= depthBuffer[index]) {
						depthBuffer[index] = i2.pos.z;

						// Perspective correction of vertex data
						for (int i = 0; i < i2.data.length; i++)
							i2.data[i] /= i2.pos.w;
						pixels[index] = shader.fragment(i2);
					}
					
					index++;
				}
			}
		}

		int dx0 = x0 - x2, dy0 = y0 - y2;
		int dx1 = x1 - x2, dy1 = y1 - y2;
		
		float a0 = (dy0 != 0) ? (float)dx0 / dy0 : 0.0f;
		float a1 = (dy1 != 0) ? (float)dx1 / dy1 : 0.0f;
		
		float dt0 = (dy0 != 0) ? 1.0f / dy0 : 0.0f;
		float dt1 = (dy1 != 0) ? 1.0f / dy1 : 0.0f;
		
		for (int y = y1; y <= y2; y++) {
			int dy = y - y2;
			
			int xs = (int)(a0 * dy) + x2;
			int xe = (int)(a1 * dy) + x2;
			
			interpolateVertex(v2, v0, dt0 * dy, i0);
			interpolateVertex(v2, v1, dt1 * dy, i1);
			
			if (xs > xe) {
				int tmp = xe;
				xe = xs;
				xs = tmp;
				
				Vertex3D tv = i0;
				i0 = i1;
				i1 = tv;
			}

			float dt2 = (xs == xe) ? 0.0f : 1.0f / (xe - xs);

			int index = xs + y * width;
			for (int x = xs; x <= xe; x++) {
				interpolateVertex(i0, i1, dt2 * (x - xs), i2);

				if (depthBuffer[index] > i2.pos.z) {
					depthBuffer[index] = i2.pos.z;
					
					// Perspective correction of vertex data
					for (int i = 0; i < i2.data.length; i++)
						i2.data[i] /= i2.pos.w;
					pixels[index] = shader.fragment(i2);
				}
				
				index++;
			}
		}
		
		cache.storeTriangle(triangle);
	}*/
	
	protected void fillTriangle(Vertex3D v0, Vertex3D v1, Vertex3D v2, TriangleCache cache) {
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

		int x0 = (int)(v0.pos.x + 0.5f), y0 = (int)(v0.pos.y + 0.5f);
		int x1 = (int)(v1.pos.x + 0.5f), y1 = (int)(v1.pos.y + 0.5f);
		int x2 = (int)(v2.pos.x + 0.5f), y2 = (int)(v2.pos.y + 0.5f);
		
		Triangle3D triangle = cache.getTriangle();
		Vertex3D vertY0 = triangle.v0;
		Vertex3D vertY1 = triangle.v1;
		Vertex3D vertXY = triangle.v2;

		boolean negateX = (x1 - x0) * (y2 - y0) < (x2 - x0) * (y1 - y0);

		if (y0 != y1) {
			float dx0 = x2 - x0, dy0 = y2 - y0;
			float dx1 = x1 - x0, dy1 = y1 - y0;
			
			for (int y = y0; y != y1; y++) {
				float dy = y - y0 + 0.5f;

				interpolateVertex(v0, v2, dy / dy0, vertY0);
				interpolateVertex(v0, v1, dy / dy1, vertY1);

				int xs = MathUtils.round((dx0 * dy) / dy0) + x0;
				int xe = MathUtils.round((dx1 * dy) / dy1) + x0;
				
				if (negateX) {
					drawTriangleRow(xe, xs, y, vertY1, vertY0, vertXY);
				} else {
					drawTriangleRow(xs, xe, y, vertY0, vertY1, vertXY);
				}
			}
		}
		
		if (y1 != y2) {
			float dx0 = x0 - x2, dy0 = y0 - y2;
			float dx1 = x1 - x2, dy1 = y1 - y2;

			for (int y = y1; y != y2; y++) {
				float dy = y - y2 + 0.5f;

				interpolateVertex(v2, v0, dy / dy0, vertY0);
				interpolateVertex(v2, v1, dy / dy1, vertY1);

				int xs = MathUtils.round((dx0 * dy) / dy0) + x2;
				int xe = MathUtils.round((dx1 * dy) / dy1) + x2;

				if (negateX) {
					drawTriangleRow(xe, xs, y, vertY1, vertY0, vertXY);
				} else {
					drawTriangleRow(xs, xe, y, vertY0, vertY1, vertXY);
				}
			}
		}
		
		cache.storeTriangle(triangle);
	}
	
	private final void drawTriangleRow(int xs, int xe, int y, Vertex3D vertY0, Vertex3D vertY1, Vertex3D vertXY) {
		int diffX = xe - xs;
		int index = xs + y * width;
		for (int x = xs; x < xe; x++) {
			interpolateVertex(vertY0, vertY1, (float)(x - xs + 0.5f) / diffX, vertXY);

			if (vertXY.pos.z < depthBuffer[index]) {
				depthBuffer[index] = vertXY.pos.z;

				// Perspective correction of vertex data
				for (int i = 0; i < vertXY.data.length; i++)
					vertXY.data[i] /= vertXY.pos.w;
				pixels[index] = shader.fragment(vertXY);
			}
			
			index++;
		}
	}
	
	private final void interpolateVertex(Vertex3D v0, Vertex3D v1, float t, Vertex3D result) {
		result.pos.x = (v1.pos.x - v0.pos.x) * t + v0.pos.x;
		result.pos.y = (v1.pos.y - v0.pos.y) * t + v0.pos.y;
		result.pos.z = (v1.pos.z - v0.pos.z) * t + v0.pos.z;
		result.pos.w = (v1.pos.w - v0.pos.w) * t + v0.pos.w;

		for (int i = 0; i < result.data.length; i++)
			result.data[i] = (1.0f - t) * v0.data[i] + t * v1.data[i];
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
