package com.g4mesoft.math;

public class Mat2f implements IMatf<Mat2f> {

	public float m00, m10,
	             m01, m11;
	
	public Mat2f() {
		this(1.0f);
	}
	
	public Mat2f(float d) {
		toIdentity(d);
	}
	
	public Mat2f(float m00, float m10,
	             float m01, float m11) {
		
		set(m00, m10,
		    m01, m11);
	}

	@Override
	public Mat2f toIdentity() {
		return toIdentity(1.0f);
	}
	
	@Override
	public Mat2f toIdentity(float d) {
		return set( d  , 0.0f,
		           0.0f,  d  );
	}
	
	public Mat2f set(float m00, float m10,
	                 float m01, float m11) {
		
		this.m00 = m00;
		this.m10 = m10;
		
		this.m01 = m01;
		this.m11 = m11;
		
		return this;
	}
	
	@Override
	public Mat2f set(Mat2f other) {
		return set(other.m00, other.m10,
		           other.m01, other.m11);
	}

	public Mat2f scale(float s) {
		return scale(s, s);
	}

	public Mat2f scale(float sx, float sy) {
		return mul(new Mat2f().setScale(sx, sy));
	}

	public Mat2f setScale(float s) {
		return setScale(s, s);
	}

	public Mat2f setScale(float sx, float sy) {
		m00 = sx;
		m11 = sy;
		
		return this;
	}

	public Mat2f rotate(float angle) {
		float r = MathUtils.PI * angle / 180.0f;
		float c = MathUtils.cos(r);
		float s = MathUtils.sin(r);

		float n00 = m00 * c + m10 * s;
		float n01 = m01 * c + m11 * s;

		m10 = m10 * c - m00 * s;
		m11 = m11 * c - m01 * s;
		
		m00 = n00;
		m01 = n01;
		
		return this;
	}
	
	public Mat2f setRotation(float angle) {
		float r = MathUtils.PI * angle / 180.0f;
		float c = MathUtils.cos(r);
		float s = MathUtils.sin(r);

		m00 = c;
		m01 = s;

		m10 = -s;
		m11 = c;

		return this;
	}

	@Override
	public Mat2f mul(Mat2f right) {
		return mul(right, this);
	}

	@Override
	public Mat2f mul(Mat2f right, Mat2f dest) {
		float n00 = m00 * right.m00 + m10 * right.m01;
		float n01 = m01 * right.m00 + m11 * right.m01;

		float n10 = m00 * right.m10 + m10 * right.m11;
		dest.m11 = m01 * right.m10 + m11 * right.m11;

		dest.m00 = n00;
		dest.m01 = n01;
		
		dest.m10 = n10;
		
		return dest;
	}

	public Vec2f mul(Vec2f vec) {
		return mul(vec, new Vec2f());
	}

	public Vec2f mul(Vec2f vec, Vec2f dest) {
		float x = m00 * vec.x + m10 * vec.y; 

		dest.y = m01 * vec.x + m11 * vec.y;
		dest.x = x;
		
		return dest;
	}
	
	@Override
	public Mat2f invert() {
		return inverseCopy(this);
	}
	
	@Override
	public Mat2f inverseCopy() {
		return inverseCopy(new Mat2f());
	}

	@Override
	public Mat2f inverseCopy(Mat2f dest) {
		float det = m00 * m11 - m10 * m01;
		if (MathUtils.nearZero(det))
			return null;

		float detInv = 1.0f / det;
		
		return dest.set( m11 * detInv, -m10 * detInv, 
		                -m01 * detInv,  m00 * detInv);
	}

	@Override
	public Mat2f transpose() {
		return transpose(this);
	}

	@Override
	public Mat2f transpose(Mat2f dest) {
		return dest.set(m00, m01,
		                m10, m11);
	}

	@Override
	public Mat2f copy() {
		return copy(new Mat2f());
	}
	
	@Override
	public Mat2f copy(Mat2f dest) {
		return dest.set(m00, m10,
		                m01, m11);
	}

	@Override
	public String toString() {
		return String.format(
				"%f, %f,\n" + 
				"%f, %f\n" + 

				m00, m10,
				m01, m11
		);
	};
}
