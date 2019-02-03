package com.g4mesoft.graphics3d;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

import com.g4mesoft.graphic.IViewport;
import com.g4mesoft.graphic.PixelRenderer2D;
import com.g4mesoft.math.MathUtils;
import com.g4mesoft.math.Vec4f;

public class PixelRenderer3D extends PixelRenderer2D {

	private IShader3D shader;

	private final Vec4f[] clippingNormals;
	private final float[] depthBuffer;
	
	private Queue<Triangle3D> trianglesToClip;
	private Queue<Triangle3D> clippedTriangles;
	
	private Vertex3D renderVertex0;
	private Vertex3D renderVertex1;
	private Vertex3D renderVertex2;
	
	private boolean cullEnabled;
	private TriangleFace cullFace;
	
	public PixelRenderer3D(IViewport viewport, int width, int height) {
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
		
		depthBuffer = new float[width * height];
		
		trianglesToClip = new LinkedList<Triangle3D>();
		clippedTriangles = new LinkedList<Triangle3D>();
		
		cullEnabled = true;
		cullFace = TriangleFace.BACK_FACE;
	}

	@Override
	public void clear() {
		super.clear();

		clearDepth();
	}
	
	public void clearDepth() {
		int i = depthBuffer.length;
		while (i-- != 0)
			depthBuffer[i] = 1.0f;
	}
	
	public void drawVertices(Vertex3D[] vertices) {
		if (vertices.length % 3 != 0)
			throw new IllegalArgumentException("Number of vertices is not a multiple of 3");
		
		shader.prepareShader();
		
		TriangleCache triangleCache = new TriangleCache(shader.getOutputSize());
		
		renderVertex0 = new Vertex3D(triangleCache.vertexNumData);
		renderVertex1 = new Vertex3D(triangleCache.vertexNumData);
		renderVertex2 = new Vertex3D(triangleCache.vertexNumData);
		
		int i = 0;
		while (i < vertices.length) {
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

			clipTriangle(triangleCache);
			transformAndCullTriangles(clippedTriangles, triangleCache);

			if (!clippedTriangles.isEmpty()) {
				renderTriangles(clippedTriangles);
				
				for (Triangle3D t : clippedTriangles)
					triangleCache.storeTriangle(t);
				clippedTriangles.clear();
			}
		}
		
		triangleCache.clear();
	}
	
	private boolean canPreCullTriangle(Triangle3D triangle) {
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
	
	private void clipTriangle(TriangleCache cache) {
		for (int i = 0; i < clippingNormals.length; i++) {
			if (i != 0) {
				Queue<Triangle3D> tmp = trianglesToClip;
				trianglesToClip = clippedTriangles;
				clippedTriangles = tmp;
			}

			Vec4f clipNormal = clippingNormals[i];
			for (Triangle3D t : trianglesToClip)
				clipTriangle(t, clipNormal, cache);
			
			// Triangles would already be added to
			// the cache. Simply clear the queue.
			trianglesToClip.clear();
			
		}
	}

	private void clipTriangle(Triangle3D t, Vec4f normal, TriangleCache cache) {
		if (normal.dot(t.v0.pos) >= -t.v0.pos.w) {
			if (normal.dot(t.v1.pos) >= -t.v1.pos.w) {
				if (normal.dot(t.v2.pos) >= -t.v2.pos.w) {
					clippedTriangles.add(t);
				} else {
					clip2Inside(normal, t.v0, t.v1, t.v2, cache);
					cache.storeTriangle(t);
				}
			} else {
				if (normal.dot(t.v2.pos) >= -t.v2.pos.w) {
					clip2Inside(normal, t.v2, t.v0, t.v1, cache);
					cache.storeTriangle(t);
				} else {
					clip2Outside(normal, t.v0, t.v1, t.v2, cache);
					cache.storeTriangle(t);
				}
			}
		} else {
			if (normal.dot(t.v1.pos) >= -t.v1.pos.w) {
				if (normal.dot(t.v2.pos) >= -t.v2.pos.w) {
					clip2Inside(normal, t.v1, t.v2, t.v0, cache);
					cache.storeTriangle(t);
				} else {
					clip2Outside(normal, t.v1, t.v2, t.v0, cache);
					cache.storeTriangle(t);
				}
			} else {
				if (normal.dot(t.v2.pos) >= -t.v2.pos.w) {
					clip2Outside(normal, t.v2, t.v0, t.v1, cache);
					cache.storeTriangle(t);
				} else {
					cache.storeTriangle(t);
				}
			}
		}
	}
	
	private void clip2Inside(Vec4f n, Vertex3D in0, Vertex3D in1, Vertex3D out, TriangleCache cache) {
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
	
	private void clip2Outside(Vec4f n, Vertex3D in, Vertex3D out0, Vertex3D out1, TriangleCache cache) {
		Triangle3D t = cache.getTriangle();
		
		t.v0.setVertex(in);
		interpolateClippedVertex(n, out0, in, t.v1);
		interpolateClippedVertex(n, out1, in, t.v2);
		
		clippedTriangles.add(t);
	}
	
	private void interpolateClippedVertex(Vec4f n, Vertex3D outVertex, Vertex3D inVertex, Vertex3D result) {
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
	
	private void transformAndCullTriangles(Queue<Triangle3D> triangles, TriangleCache cache) {
		float hw = 0.5f * (width - MathUtils.EPSILON);
		float hh = 0.5f * (height - MathUtils.EPSILON);
		
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
			
			// Used for perspective correction 
			// during rendering of triangles.
			t.v0.pos.w = 1.0f / t.v0.pos.w;
			t.v1.pos.w = 1.0f / t.v1.pos.w;
			t.v2.pos.w = 1.0f / t.v2.pos.w;
			
			// Perspective correction of vertex data
			for (int i = 0; i < cache.vertexNumData; i++) {
				t.v0.data[i] *= t.v0.pos.w;
				t.v1.data[i] *= t.v1.pos.w;
				t.v2.data[i] *= t.v2.pos.w;
			}
		}
	}
	
	private void renderTriangles(Queue<Triangle3D> triangles) {
		for (Triangle3D t : triangles)
			fillTriangle(t.v0, t.v1, t.v2);
	}
	
	private void fillTriangle(Vertex3D v0, Vertex3D v1, Vertex3D v2) {
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
		
		Vertex3D i0 = renderVertex0;
		Vertex3D i1 = renderVertex1;
		Vertex3D i2 = renderVertex2;
		
		if (y0 != y1) {
			int dx0 = x2 - x0, dy0 = y2 - y0;
			int dx1 = x1 - x0, dy1 = y1 - y0;
			
			float a0 = (float)dx0 / dy0;
			float a1 = (float)dx1 / dy1;

			float dt0 = 1.0f / dy0;
			float dt1 = 1.0f / dy1;
			for (int y = y0; y <= y1; y++) {
				int dy = y - y0;

				int xs = (int)(a0 * dy) + x0;
				int xe = (int)(a1 * dy) + x0;
				
				interpolateVertex(v0, v2, dt0 * dy, i0);
				interpolateVertex(v0, v1, dt1 * dy, i1);
				
				if (xs > xe) {
					int tx = xe;
					xe = xs;
					xs = tx;
					
					Vertex3D tv = i0;
					i0 = i1;
					i1 = tv;
				}
				
				float dt2 = (xs == xe) ? 0.0f : 1.0f / (xe - xs);
				for (int x = xs; x <= xe; x++) {
					interpolateVertex(i0, i1, dt2 * (x - xs), i2);
					
					int index = x + y * width;
					if (depthBuffer[index] > i2.pos.z) {
						depthBuffer[index] = i2.pos.z;

						// Perspective correction of vertex data
						for (int i = 0; i < i2.data.length; i++)
							i2.data[i] /= i2.pos.w;
						pixels[index] = shader.fragment(i2);
					}
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
			for (int x = xs; x <= xe; x++) {
				if (isInBounds(x, y)) {
					interpolateVertex(i0, i1, dt2 * (x - xs), i2);

					int index = x + y * width;
					if (depthBuffer[index] > i2.pos.z) {
						depthBuffer[index] = i2.pos.z;
						
						// Perspective correction of vertex data
						for (int i = 0; i < i2.data.length; i++)
							i2.data[i] /= i2.pos.w;
						pixels[index] = shader.fragment(i2);
					}
				}
			}
		}
	}
	
	private void interpolateVertex(Vertex3D v0, Vertex3D v1, float t, Vertex3D result) {
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
	
	public class TriangleCache {
		
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
