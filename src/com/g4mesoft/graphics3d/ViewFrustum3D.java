package com.g4mesoft.graphics3d;

import com.g4mesoft.math.Mat4f;
import com.g4mesoft.math.MathUtils;
import com.g4mesoft.math.Vec3f;

public class ViewFrustum3D {

	private final Plane[] viewPlanes;
	
	public ViewFrustum3D() {
		viewPlanes = new Plane[6];
		
		for (int i = 0; i < 6; i++)
			viewPlanes[i] = new Plane();
	}
	
	/**
	 * Initializes the six planes (left, right, bottom, top, near, far), of the
	 * view frustum. This method has to be invoked before calling the functions
	 * {@link #pointInView(Vec3f)} and {@link #sphereInView(Vec3f, float)}. This
	 * method creates a copy of the projection-view matrix. This implies that
	 * the frustum has to be reinitialized after any change to the projection-
	 * view matrix.
	 * 
	 * @param pvm - the projection-view matrix
	 * 
	 * @see #pointInView(Vec3f)
	 * @see #pointInView(float, float, float)
	 * @see #sphereInView(Vec3f, float)
	 * @see #sphereInView(float, float, float, float)
	 */
	public void initFrustum(Mat4f pvm) {
		// LEFT RIGHT
		viewPlanes[0].set(pvm.m03 + pvm.m00, pvm.m13 + pvm.m10, pvm.m23 + pvm.m20, pvm.m33 + pvm.m30);
		viewPlanes[1].set(pvm.m03 - pvm.m00, pvm.m13 - pvm.m10, pvm.m23 - pvm.m20, pvm.m33 - pvm.m30);

		// BOTTOM TOP
		viewPlanes[2].set(pvm.m03 + pvm.m01, pvm.m13 + pvm.m11, pvm.m23 + pvm.m21, pvm.m33 + pvm.m31);
		viewPlanes[3].set(pvm.m03 - pvm.m01, pvm.m13 - pvm.m11, pvm.m23 - pvm.m21, pvm.m33 - pvm.m31);

		// NEAR FAR
		viewPlanes[4].set(pvm.m03 + pvm.m02, pvm.m13 + pvm.m12, pvm.m23 + pvm.m22, pvm.m33 + pvm.m32);
		viewPlanes[5].set(pvm.m03 - pvm.m02, pvm.m13 - pvm.m12, pvm.m23 - pvm.m22, pvm.m33 - pvm.m32);
	}
	
	/**
	 * Tests whether the given point is within the view-frustum. 
	 * <br><br>
	 * <b>NOTE: </b> the frustum has to be initialized using the member function
	 * {@link #initFrustum(Mat4f)} prior to calling this method.
	 * 
	 * @param point - the vector representing the (x, y, z) point to be tested.
	 * 
	 * @return True, if the point is within the view frustum, false otherwise.
	 * 
	 * @see #initFrustum(Mat4f)
	 */
	public boolean pointInView(Vec3f point) {
		return pointInView(point.x, point.y, point.z);
	}

	/**
	 * Tests whether the given point is within the view-frustum. 
	 * <br><br>
	 * <b>NOTE: </b> the frustum has to be initialized using the member function
	 * {@link #initFrustum(Mat4f)} prior to calling this method.
	 * 
	 * @param x - the x-coordinate of the point to be tested.
	 * @param y - the y-coordinate of the point to be tested.
	 * @param z - the z-coordinate of the point to be tested.
	 * 
	 * @return True, if the point is within the view frustum, false otherwise.
	 * 
	 * @see #initFrustum(Mat4f)
	 */
	public boolean pointInView(float x, float y, float z) {
		return sphereInView(x, y, z, 0.0f);
	}
	
	/**
	 * Tests whether any point on the given sphere is within the view frustum.
	 * The sphere is defined as a center point with a given radius.
	 * <br><br>
	 * <b>NOTE: </b> the frustum has to be initialized using the member function
	 * {@link #initFrustum(Mat4f)} prior to calling this method.
	 * 
	 * @param center - the center point of the sphere.
	 * @param radius - the radius of the sphere.
	 * 
	 * @return True, if a point on the sphere is within the view frustum, false
	 *         otherwise.
	 * 
	 * @see #initFrustum(Mat4f)
	 */
	public boolean sphereInView(Vec3f center, float radius) {
		return sphereInView(center.x, center.y, center.z, radius);
	}

	/**
	 * Tests whether any point on the given sphere is within the view frustum.
	 * The sphere is defined as a center point with a given radius.
	 * <br><br>
	 * <b>NOTE: </b> the frustum has to be initialized using the member function
	 * {@link #initFrustum(Mat4f)} prior to calling this method.
	 * 
	 * @param xc - the center x-coordinate of the sphere.
	 * @param yc - the center y-coordinate of the sphere.
	 * @param zc - the center z-coordinate of the sphere.
	 * @param radius - the radius of the sphere.
	 * 
	 * @return True, if a point on the sphere is within the view frustum, false
	 *         otherwise.
	 * 
	 * @see #initFrustum(Mat4f)
	 */
	public boolean sphereInView(float xc, float yc, float zc, float radius) {
		for (Plane plane : viewPlanes) {
			if (plane.a * xc + plane.b * yc + plane.c * zc + plane.d + radius <= 0)
				return false;
		}
		
		return true;
	}
	
	private class Plane {
		
		private float a;
		private float b;
		private float c;
		
		private float d;
		
		public Plane() {
			a = b = c = d = 0.0f;
		}
		
		/**
		 * Initializes a plane of the view frustum to the given values {@code a}
		 * , {@code b}, {@code c} and {@code d}. The plane follows the standard
		 * formula:
		 * <pre>
		 *     an * x + bn * y + cn * z + dn = 0
		 * </pre>
		 * Where the {@code an}, {@code bn}, {@code cn} and {@code dn} values
		 * are the normalized values given in the parameters. These values are
		 * calculated using the following formula:
		 * <pre>
		 *     float len = sqrt(a * a + b * b + c * c);
		 *     
		 *     float an = a / len;
		 *     float bn = b / len;
		 *     float cn = c / len;
		 *     float dn = d / len;
		 * </pre>
		 * 
		 * @param a - the x-coordinate of the plane's normal vector.
		 * @param b - the y-coordinate of the plane's normal vector.
		 * @param c - the z-coordinate of the plane's normal vector.
		 * @param d - the shortest distance from (0, 0, 0) to the plane. The
		 *            sign of this value depends on the direction of the normal.
		 * 
		 * @see ViewFrustum3D#initFrustum(Mat4f)
		 */
		public void set(float a, float b, float c, float d) {
			this.a = a;
			this.b = b;
			this.c = c;
			
			this.d = d;
			
			float lenSqr = a * a + b * b + c * c;
			if (lenSqr > MathUtils.EPSILON * MathUtils.EPSILON) {
				float s = 1.0f / MathUtils.sqrt(lenSqr);
				this.a *= s;
				this.b *= s;
				this.c *= s;
				
				this.d *= s;
			}
		}
	}
}
