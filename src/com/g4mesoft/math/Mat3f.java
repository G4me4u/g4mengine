package com.g4mesoft.math;

public class Mat3f implements IMatf<Mat3f> {

	public float m00, m10, m20,
	             m01, m11, m21,
	             m02, m12, m22;
	
	public Mat3f() {
		this(1.0f);
	}
	
	public Mat3f(float d) {
		toIdentity(d);
	}

	public Mat3f(Vec3f r0, Vec3f r1, Vec3f r2) {
		this(r0, r1, r2, true);
	}

	public Mat3f(Vec3f v0, Vec3f v1, Vec3f v2, boolean rowMajor) {
		set(v0, v1, v2, rowMajor);
	}
	
	public Mat3f(float m00, float m10, float m20,
	             float m01, float m11, float m21,
	             float m02, float m12, float m22) {
		
		set(m00, m10, m20,
		    m01, m11, m21,
		    m02, m12, m22);
	}

	@Override
	public Mat3f toIdentity() {
		return toIdentity(1.0f);
	}
	
	@Override
	public Mat3f toIdentity(float d) {
		return set( d  , 0.0f, 0.0f,
		           0.0f,  d  , 0.0f,
		           0.0f, 0.0f,  d  );
	}
	
	public Mat3f set(Vec3f v0, Vec3f v1, Vec3f v2, boolean rowMajor) {
		if (rowMajor) {
			set(v0.x, v0.y, v0.z,
			    v1.x, v1.y, v1.z,
			    v2.x, v2.y, v2.z);
			return this;
		}
		
		set(v0.x, v1.x, v2.x,
		    v0.y, v1.y, v2.y,
		    v0.z, v1.z, v2.z);
		return this;
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
	
	@Override
	public Mat3f set(Mat3f other) {
		return set(other.m00, other.m10, other.m20,
		           other.m01, other.m11, other.m21,
		           other.m02, other.m12, other.m22);
	}

	public Mat3f scale(float s) {
		return scale(s, s, s);
	}

	public Mat3f scale(Vec3f s) {
		return scale(s.x, s.y, s.z);
	}
	
	public Mat3f scale(float sx, float sy, float sz) {
		return mul(new Mat3f().setScale(sx, sy, sz));
	}

	public Mat3f setScale(float s) {
		return setScale(s, s, s);
	}

	public Mat3f setScale(Vec3f s) {
		return setScale(s.x, s.y, s.z);
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
		float r = MathUtils.PI * angle / 180.0f;
		float c = MathUtils.cos(r);
		float s = MathUtils.sin(r);
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
	
	public Mat3f rotateX(float angle) {
		float r = MathUtils.PI * angle / 180.0f;
		float c = MathUtils.cos(r);
		float s = MathUtils.sin(r);
		
		float n10 = m10 * c + m20 * s;
		float n11 = m11 * c + m21 * s;
		float n12 = m12 * c + m22 * s;

		m20 = m20 * c - m10 * s;
		m21 = m21 * c - m11 * s;
		m22 = m22 * c - m12 * s;
		
		m10 = n10;
		m11 = n11;
		m12 = n12;
		
		return this;
	}
	
	public Mat3f rotateY(float angle) {
		float r = MathUtils.PI * angle / 180.0f;
		float c = MathUtils.cos(r);
		float s = MathUtils.sin(r);

		float n00 = m00 * c - m20 * s;
		float n01 = m01 * c - m21 * s;
		float n02 = m02 * c - m22 * s;

		m20 = m00 * s + m20 * c;
		m21 = m01 * s + m21 * c;
		m22 = m02 * s + m22 * c;
		
		m00 = n00;
		m01 = n01;
		m02 = n02;

		return this;
	}
	
	public Mat3f rotateZ(float angle) {
		float r = MathUtils.PI * angle / 180.0f;
		float c = MathUtils.cos(r);
		float s = MathUtils.sin(r);
		
		float n00 = m00 * c + m10 * s;
		float n01 = m01 * c + m11 * s;
		float n02 = m02 * c + m12 * s;

		m10 = m10 * c - m00 * s;
		m11 = m11 * c - m01 * s;
		m12 = m12 * c - m02 * s;

		m00 = n00;
		m01 = n01;
		m02 = n02;
		
		return this;
	}

	@Override
	public Mat3f mul(Mat3f right) {
		return mul(right, this);
	}

	@Override
	public Mat3f mul(Mat3f right, Mat3f dest) {
		float n00 = m00 * right.m00 + m10 * right.m01 + m20 * right.m02;
		float n01 = m01 * right.m00 + m11 * right.m01 + m21 * right.m02;
		float n02 = m02 * right.m00 + m12 * right.m01 + m22 * right.m02;

		float n10 = m00 * right.m10 + m10 * right.m11 + m20 * right.m12;
		float n11 = m01 * right.m10 + m11 * right.m11 + m21 * right.m12;
		float n12 = m02 * right.m10 + m12 * right.m11 + m22 * right.m12;

		float n20 = m00 * right.m20 + m10 * right.m21 + m20 * right.m22;
		float n21 = m01 * right.m20 + m11 * right.m21 + m21 * right.m22;
		float n22 = m02 * right.m20 + m12 * right.m21 + m22 * right.m22;

		return dest.set(n00, n10, n20,
		                n01, n11, n21,
		                n02, n12, n22);
	}

	public Vec3f mul(Vec3f vec) {
		return mul(vec, new Vec3f());
	}

	public Vec3f mul(Vec3f vec, Vec3f dest) {
		float x = m00 * vec.x + m10 * vec.y + m20 * vec.z; 
		float y = m01 * vec.x + m11 * vec.y + m21 * vec.z;
		
		dest.z = m02 * vec.x + m12 * vec.y + m22 * vec.z;
		dest.x = x;
		dest.y = y;
		
		return dest;
	}
	
	@Override
	public Mat3f invert() {
		return inverseCopy(this);
	}
	
	@Override
	public Mat3f inverseCopy() {
		return inverseCopy(new Mat3f());
	}
	
	@Override
	public Mat3f inverseCopy(Mat3f dest) {
		Vec3f al = new Vec3f(m00, m10, m20);
		Vec3f bl = new Vec3f(m01, m11, m21);
		Vec3f cl = new Vec3f(m02, m12, m22);
		
		Vec3f ar = new Vec3f(1.0f, 0.0f, 0.0f);
		Vec3f br = new Vec3f(0.0f, 1.0f, 0.0f);
		Vec3f cr = new Vec3f(0.0f, 0.0f, 1.0f);
		
		// First row check
		if (MathUtils.nearZero(al.x)) {
			if (!MathUtils.nearZero(bl.x)) {
				al.add(       1.0f, bl.y / bl.x, bl.z / bl.x);
				ar.add(br.x / bl.x, br.y / bl.x, br.z / bl.x);
			} else if (!MathUtils.nearZero(cl.x)) {
				al.add(       1.0f, cl.y / cl.x, cl.z / cl.x);
				ar.add(cr.x / cl.x, cr.y / cl.x, cr.z / cl.x);
			} else {
				return null; // Non-invertable
			}
		} else if (al.x != 1.0f) {
			ar.div(al.x);
			al.div(al.x);
		}
		
		if (bl.x != 0.0f) {
			br.sub(ar.x * bl.x, ar.y * bl.x, ar.z * bl.x);
			bl.sub(       bl.x, al.y * bl.x, al.z * bl.x);
		}
		if (cl.x != 0.0f) {
			cr.sub(ar.x * cl.x, ar.y * cl.x, ar.z * cl.x);
			cl.sub(       cl.x, al.y * cl.x, al.z * cl.x);
		}
		
		// Second row check
		if (MathUtils.nearZero(bl.y)) {
			if (!MathUtils.nearZero(cl.y)) {
				bl.add(cl.x / cl.y,        1.0f, cl.z / cl.y);
				br.add(cr.x / cl.y, cr.y / cl.y, cr.z / cl.y);
			} else {
				return null; // Non-invertable
			}
		} else if (bl.y != 1.0f) {
			br.div(bl.y);
			bl.div(bl.y);
		}
		
		if (al.y != 0.0f) {
			ar.sub(br.x * al.y, br.y * al.y, br.z * al.y);
			al.sub(       0.0f,        al.y, bl.z * al.y);
		}
		if (cl.y != 0.0f) {
			cr.sub(br.x * cl.y, br.y * cl.y, br.z * cl.y);
			cl.sub(       0.0f,        cl.y, bl.z * cl.y);
		}
		
		// Third row check
		if (MathUtils.nearZero(cl.z)) {
			return null; // Non-invertable
		} else if (cl.z != 1.0f) {
			cr.div(cl.z);
			//cl.div(cl.z);
		}
		
		// Subtracting from the left rows 
		// is not needed for the last pass.
		if (al.z != 0.0f) {
			ar.sub(cr.x * al.z, cr.y * al.z, cr.z * al.z);
			//al.sub(       0.0f,        0.0f,        al.z);
		}
		if (bl.z != 0.0f) {
			br.sub(cr.x * bl.z, cr.y * bl.z, cr.z * bl.z);
			//bl.sub(       0.0f,        0.0f,        bl.z);
		}

		// Return resulting rows in matrix
		// form.
		return dest.set(ar.x, ar.y, ar.z,
		                br.x, br.y, br.z,
		                cr.x, cr.y, cr.z);
	}

	@Override
	public Mat3f transpose() {
		return transpose(this);
	}

	@Override
	public Mat3f transpose(Mat3f dest) {
		float tmp;
		
		tmp = m01;
		dest.m01 = m10;
		dest.m10 = tmp;
		
		tmp = m02;
		dest.m02 = m20;
		dest.m20 = tmp;
		
		tmp = m12;
		dest.m12 = m21;
		dest.m21 = tmp;
		
		dest.m00 = m00;
		dest.m11 = m11;
		dest.m22 = m22;
		
		return dest;
	}
	
	@Override
	public Mat3f copy() {
		return copy(new Mat3f());
	}

	@Override
	public Mat3f copy(Mat3f dest) {
		return dest.set(m00, m10, m20,
		                m01, m11, m21,
		                m02, m12, m22);
	}

	public Vec3f getRow0(Vec3f dest) {
		return dest.set(m00, m10, m20);
	}

	public Vec3f getRow1(Vec3f dest) {
		return dest.set(m01, m11, m21);
	}

	public Vec3f getRow2(Vec3f dest) {
		return dest.set(m02, m12, m22);
	}

	public Vec3f getCol0(Vec3f dest) {
		return dest.set(m00, m01, m02);
	}
	
	public Vec3f getCol1(Vec3f dest) {
		return dest.set(m10, m11, m12);
	}
	
	public Vec3f getCol2(Vec3f dest) {
		return dest.set(m20, m21, m22);
	}
	
	public Mat3f setRow0(Vec3f r0) {
		m00 = r0.x;
		m10 = r0.y;
		m20 = r0.z;
		return this;
	}
	
	public Mat3f setRow1(Vec3f r1) {
		m01 = r1.x;
		m11 = r1.y;
		m21 = r1.z;
		return this;
	}

	public Mat3f setRow2(Vec3f r2) {
		m02 = r2.x;
		m12 = r2.y;
		m22 = r2.z;
		return this;
	}
	
	public Mat3f setCol0(Vec3f c0) {
		m00 = c0.x;
		m01 = c0.y;
		m02 = c0.z;
		return this;
	}
	
	public Mat3f setCol1(Vec3f c1) {
		m10 = c1.x;
		m11 = c1.y;
		m12 = c1.z;
		return this;
	}

	public Mat3f setCol2(Vec3f c2) {
		m20 = c2.x;
		m21 = c2.y;
		m22 = c2.z;
		return this;
	}
	
	@Override
	public String toString() {
		return String.format(
				"%f, %f, %f,\n" + 
				"%f, %f, %f,\n" + 
				"%f, %f, %f\n",

				m00, m10, m20,
				m01, m11, m21,
				m02, m12, m22
		);
	};
}
