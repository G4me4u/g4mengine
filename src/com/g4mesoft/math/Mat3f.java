package com.g4mesoft.math;

public class Mat3f {

	public float m00, m10, m20,
				 m01, m11, m21,
				 m02, m12, m22;
	
	public Mat3f() {
		this(1.0f);
	}
	
	public Mat3f(float d) {
		toIdentity(d);
	}
	
	public Mat3f(float m00, float m10, float m20,
				 float m01, float m11, float m21,
				 float m02, float m12, float m22) {
		
		set(m00, m10, m20,
		    m01, m11, m21,
		    m02, m12, m22);
	}

	public Mat3f toIdentity() {
		return toIdentity(1.0f);
	}
	
	public Mat3f toIdentity(float d) {
		return set( d  , 0.0f, 0.0f,
				   0.0f,  d  , 0.0f,
				   0.0f, 0.0f,  d  );
	}
	
	public Mat3f set(float m00, float m10, float m20,
				     float m01, float m11, float m21,
				     float m02, float m12, float m22) {
		
		this.m00 = m00;
		this.m10 = m10;
		this.m20 = m20;
		
		this.m01 = m01;
		this.m11 = m11;
		this.m21 = m21;
		
		this.m02 = m02;
		this.m12 = m12;
		this.m22 = m22;
		
		return this;
	}
	

	public Mat3f scale(float sx, float sy, float sz) {
		return mul(new Mat3f().setScale(sx, sy, sz));
	}

	public Mat3f setScale(float sx, float sy, float sz) {
		m00 = sx;
		m11 = sy;
		m22 = sz;
		
		return this;
	}

	public Mat3f rotate(float angle, float x, float y, float z) {
		return mul(new Mat3f().setRotation(angle, x, y, z));
	}
	
	public Mat3f setRotation(float angle, float x, float y, float z) {
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

		m10 = xy * omc - zs;
		m11 = y * y * omc + c;
		m12 = yz * omc + xs;

		m20 = xz * omc + ys;
		m21 = yz * omc - xs;
		m22 = z * z * omc + c;
		
		return this;
	}

	public Mat3f mul(Mat3f right) {
		float n00 = m00 * right.m00 + m10 * right.m01 + m20 * right.m02;
		float n01 = m01 * right.m00 + m11 * right.m01 + m21 * right.m02;
		float n02 = m02 * right.m00 + m12 * right.m01 + m22 * right.m02;

		float n10 = m00 * right.m10 + m10 * right.m11 + m20 * right.m12;
		float n11 = m01 * right.m10 + m11 * right.m11 + m21 * right.m12;
		float n12 = m02 * right.m10 + m12 * right.m11 + m22 * right.m12;

		m20 = m00 * right.m20 + m10 * right.m21 + m20 * right.m22;
		m21 = m01 * right.m20 + m11 * right.m21 + m21 * right.m22;
		m22 = m02 * right.m20 + m12 * right.m21 + m22 * right.m22;

		m00 = n00;
		m01 = n01;
		m02 = n02;

		m10 = n10;
		m11 = n11;
		m12 = n12;

		return this;
	}

	public Vec3f mul(Vec3f vec) {
		return new Vec3f(
			m00 * vec.x + m10 * vec.y + m20 * vec.z,
			m01 * vec.x + m11 * vec.y + m21 * vec.z,
			m02 * vec.x + m12 * vec.y + m22 * vec.z
		);
	}

	public Mat3f copy() {
		return new Mat3f(m00, m10, m20,
					     m01, m11, m21,
					     m02, m12, m22);
	}

	@Override
	public String toString() {
		return String.format(
				"%d, %d, %d,\n" + 
				"%d, %d, %d,\n" + 
				"%d, %d, %d\n",

				m00, m10, m20,
				m01, m11, m21,
				m02, m12, m22
		);
	};
}
