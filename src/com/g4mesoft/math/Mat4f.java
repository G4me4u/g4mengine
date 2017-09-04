package com.g4mesoft.math;

public class Mat4f {

	public float m00, m10, m20, m30,
				 m01, m11, m21, m31,
				 m02, m12, m22, m32,
				 m03, m13, m23, m33;
	
	public Mat4f() {
		this(1.0f);
	}
	
	public Mat4f(float d) {
		toIdentity(1.0f);
	}
	
	public Mat4f(float m00, float m10, float m20, float m30,
				 float m01, float m11, float m21, float m31,
				 float m02, float m12, float m22, float m32,
				 float m03, float m13, float m23, float m33) {
	
	set(m00, m10, m20, m30,
	    m01, m11, m21, m31,
	    m02, m12, m22, m32,
	    m03, m13, m23, m33);
}

	public Mat4f toIdentity() {
		return toIdentity(1.0f);
	}
	
	public Mat4f toIdentity(float d) {
		return set( d  , 0.0f, 0.0f, 0.0f,
				   0.0f,  d  , 0.0f, 0.0f,
				   0.0f, 0.0f,  d  , 0.0f,
				   0.0f, 0.0f, 0.0f,  d  );
	}
	
	public Mat4f set(float m00, float m10, float m20, float m30,
				     float m01, float m11, float m21, float m31,
				     float m02, float m12, float m22, float m32,
				     float m03, float m13, float m23, float m33) {
		
		this.m00 = m00;
		this.m10 = m10;
		this.m20 = m20;
		this.m30 = m30;
		
		this.m01 = m01;
		this.m11 = m11;
		this.m21 = m21;
		this.m31 = m31;
		
		this.m02 = m02;
		this.m12 = m12;
		this.m22 = m22;
		this.m32 = m32;

		this.m03 = m03;
		this.m13 = m13;
		this.m23 = m23;
		this.m33 = m33;
		
		return this;
	}

	public Mat4f toOrthographic(float left, float right, float bottom, float top, float near, float far) {
		toIdentity();

		m00 = 2.0f / (right - left);
		m11 = 2.0f / (top - bottom);
		m22 = 2.0f / (near - far);

		m30 = (left + right) / (left - right);
		m31 = (bottom + top) / (bottom - top);
		m32 = (far + near) / (far - near);

		return this;
	}

	public Mat4f toPerspective(float fov, float aspect, float near, float far) {
		toIdentity();

		float q = 1.0f / (float)Math.tan((float)Math.PI * fov / 360.0f);

		m00 = q / aspect;
		m11 = q;
		m22 = (near + far) / (near - far);
		m33 = 0.0f;

		m23 = -1.0f;
		m32 = (2.0f * near * far) / (near - far);

		return this;
	}

	public Mat4f translate(float tx, float ty, float tz) {
		return mul(new Mat4f().setTranslation(tx, ty, tz));
	}

	public Mat4f setTranslation(float tx, float ty, float tz) {
		m30 = tx;
		m31 = ty;
		m32 = tz;
		
		return this;
	}

	public Mat4f scale(float sx, float sy, float sz) {
		return mul(new Mat4f().setScale(sx, sy, sz));
	}

	public Mat4f setScale(float sx, float sy, float sz) {
		m00 = sx;
		m11 = sy;
		m22 = sz;
		
		return this;
	}

	public Mat4f rotate(float angle, float x, float y, float z) {
		return mul(new Mat4f().setRotation(angle, x, y, z));
	}
	
	public Mat4f setRotation(float angle, float x, float y, float z) {
		float r = (float)Math.PI * angle / 180.0f;
		float c = (float)Math.cos(r);
		float s = (float)Math.sin(r);
		float omc = 1.0f - c;

		float xy = x * y;
		float xz = x * z;
		float yz = y * z;
		
		float xs = x * s;
		float ys = y * s;
		float zs = z * s;
		
		m00 = x * x * omc + c;
		m01 = xy * omc + zs;
		m02 = xz * omc - ys;
		m03 = 0.0f;

		m10 = xy * omc - zs;
		m11 = y * y * omc + c;
		m12 = yz * omc + xs;
		m13 = 0.0f;

		m20 = xz * omc + ys;
		m21 = yz * omc - xs;
		m22 = z * z * omc + c;
		m23 = 0.0f;
		
		m30 = 0.0f;
		m31 = 0.0f;
		m32 = 0.0f;
		m33 = 1.0f;
		
		return this;
	}

	public Mat4f mul(Mat4f right) {
		float n00 = m00 * right.m00 + m10 * right.m01 + m20 * right.m02 + m30 * right.m03;
		float n01 = m01 * right.m00 + m11 * right.m01 + m21 * right.m02 + m31 * right.m03;
		float n02 = m02 * right.m00 + m12 * right.m01 + m22 * right.m02 + m32 * right.m03;
		float n03 = m03 * right.m00 + m13 * right.m01 + m23 * right.m02 + m33 * right.m03;

		float n10 = m00 * right.m10 + m10 * right.m11 + m20 * right.m12 + m30 * right.m13;
		float n11 = m01 * right.m10 + m11 * right.m11 + m21 * right.m12 + m31 * right.m13;
		float n12 = m02 * right.m10 + m12 * right.m11 + m22 * right.m12 + m32 * right.m13;
		float n13 = m03 * right.m10 + m13 * right.m11 + m23 * right.m12 + m33 * right.m13;

		float n20 = m00 * right.m20 + m10 * right.m21 + m20 * right.m22 + m30 * right.m23;
		float n21 = m01 * right.m20 + m11 * right.m21 + m21 * right.m22 + m31 * right.m23;
		float n22 = m02 * right.m20 + m12 * right.m21 + m22 * right.m22 + m32 * right.m23;
		float n23 = m03 * right.m20 + m13 * right.m21 + m23 * right.m22 + m33 * right.m23;

		m30 = m00 * right.m30 + m10 * right.m31 + m20 * right.m32 + m30 * right.m33;
		m31 = m01 * right.m30 + m11 * right.m31 + m21 * right.m32 + m31 * right.m33;
		m32 = m02 * right.m30 + m12 * right.m31 + m22 * right.m32 + m32 * right.m33;
		m33 = m03 * right.m30 + m13 * right.m31 + m23 * right.m32 + m33 * right.m33;

		m00 = n00;
		m01 = n01;
		m02 = n02;
		m03 = n03;

		m10 = n10;
		m11 = n11;
		m12 = n12;
		m13 = n13;

		m20 = n20;
		m21 = n21;
		m22 = n22;
		m23 = n23;

		return this;
	}

	public Vec4f mul(Vec4f vec) {
		Vec4f result = new Vec4f();

		result.x = m00 * vec.x + m10 * vec.y + m20 * vec.z + m30 * vec.w;
		result.y = m01 * vec.x + m11 * vec.y + m21 * vec.z + m31 * vec.w;
		result.z = m02 * vec.x + m12 * vec.y + m22 * vec.z + m32 * vec.w;
		result.w = m03 * vec.x + m13 * vec.y + m23 * vec.z + m33 * vec.w;

		return result;
	}

	public Mat4f copy() {
		return new Mat4f(m00, m10, m20, m30,
					     m01, m11, m21, m31,
					     m02, m12, m22, m32,
					     m03, m13, m23, m33);
	}

	@Override
	public String toString() {
		return String.format(
				"%d, %d, %d, %d,\n" + 
				"%d, %d, %d, %d,\n" + 
				"%d, %d, %d, %d,\n" + 
				"%d, %d, %d, %d\n",

				m00, m10, m20, m30,
				m01, m11, m21, m31,
				m02, m12, m22, m32,
				m03, m13, m23, m33
		);
	};
}
