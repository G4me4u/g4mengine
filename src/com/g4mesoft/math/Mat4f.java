package com.g4mesoft.math;

public class Mat4f {

	/**
	 * m[c][r], where c is column and r is row.
	 */
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
	
	public Mat4f set(Mat4f other) {
		return set(other.m00, other.m10, other.m20, other.m30,
		           other.m01, other.m11, other.m21, other.m31,
		           other.m02, other.m12, other.m22, other.m32,
		           other.m03, other.m13, other.m23, other.m33);
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

		float q = 1.0f / MathUtils.tan(MathUtils.PI * fov / 360.0f);

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
	
	public Mat4f rotateX(float angle) {
		float r = MathUtils.PI * angle / 180.0f;
		float c = MathUtils.cos(r);
		float s = MathUtils.sin(r);
		
		float n10 = m10 * c + m20 * s;
		float n11 = m11 * c + m21 * s;
		float n12 = m12 * c + m22 * s;
		float n13 = m13 * c + m23 * s;

		m20 = m20 * c - m10 * s;
		m21 = m21 * c - m11 * s;
		m22 = m22 * c - m12 * s;
		m23 = m23 * c - m13 * s;
		
		m10 = n10;
		m11 = n11;
		m12 = n12;
		m13 = n13;
		
		return this;
	}
	
	public Mat4f rotateY(float angle) {
		float r = MathUtils.PI * angle / 180.0f;
		float c = MathUtils.cos(r);
		float s = MathUtils.sin(r);

		float n00 = m00 * c - m20 * s;
		float n01 = m01 * c - m21 * s;
		float n02 = m02 * c - m22 * s;
		float n03 = m03 * c - m23 * s;

		m20 = m00 * s + m20 * c;
		m21 = m01 * s + m21 * c;
		m22 = m02 * s + m22 * c;
		m23 = m03 * s + m23 * c;
		
		m00 = n00;
		m01 = n01;
		m02 = n02;
		m03 = n03;

		return this;
	}
	
	public Mat4f rotateZ(float angle) {
		float r = MathUtils.PI * angle / 180.0f;
		float c = MathUtils.cos(r);
		float s = MathUtils.sin(r);
		
		float n00 = m00 * c + m10 * s;
		float n01 = m01 * c + m11 * s;
		float n02 = m02 * c + m12 * s;
		float n03 = m03 * c + m13 * s;

		m10 = m10 * c - m00 * s;
		m11 = m11 * c - m01 * s;
		m12 = m12 * c - m02 * s;
		m13 = m13 * c - m03 * s;

		m00 = n00;
		m01 = n01;
		m02 = n02;
		m03 = n03;
		
		return this;
	}

	public Mat4f mul(Mat4f right) {
		return mul(right, this);
	}

	public Mat4f mul(Mat4f right, Mat4f dest) {
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

		float n30 = m00 * right.m30 + m10 * right.m31 + m20 * right.m32 + m30 * right.m33;
		float n31 = m01 * right.m30 + m11 * right.m31 + m21 * right.m32 + m31 * right.m33;
		float n32 = m02 * right.m30 + m12 * right.m31 + m22 * right.m32 + m32 * right.m33;
		float n33 = m03 * right.m30 + m13 * right.m31 + m23 * right.m32 + m33 * right.m33;

		return dest.set(n00, n10, n20, n30,
		                n01, n11, n21, n31,
		                n02, n12, n22, n32,
		                n03, n13, n23, n33);
	}

	public Vec4f mul(Vec4f vec) {
		return mul(vec, new Vec4f());
	}
	
	public Vec4f mul(Vec4f vec, Vec4f dest) {
		float x = m00 * vec.x + m10 * vec.y + m20 * vec.z + m30 * vec.w;
		float y = m01 * vec.x + m11 * vec.y + m21 * vec.z + m31 * vec.w;
		float z = m02 * vec.x + m12 * vec.y + m22 * vec.z + m32 * vec.w;

		dest.w = m03 * vec.x + m13 * vec.y + m23 * vec.z + m33 * vec.w;
		dest.x = x;
		dest.y = y;
		dest.z = z;
		
		return dest;
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
	
	public Mat4f invert() {
		return inverseCopy(this);
	}

	public Mat4f inverseCopy() {
		return inverseCopy(new Mat4f());
	}

	public Mat4f inverseCopy(Mat4f dest) {
		Vec4f al = new Vec4f(m00, m10, m20, m30);
		Vec4f bl = new Vec4f(m01, m11, m21, m31);
		Vec4f cl = new Vec4f(m02, m12, m22, m32);
		Vec4f dl = new Vec4f(m03, m13, m23, m33);
		
		Vec4f ar = new Vec4f(1.0f, 0.0f, 0.0f, 0.0f);
		Vec4f br = new Vec4f(0.0f, 1.0f, 0.0f, 0.0f);
		Vec4f cr = new Vec4f(0.0f, 0.0f, 1.0f, 0.0f);
		Vec4f dr = new Vec4f(0.0f, 0.0f, 0.0f, 1.0f);
		
		// First row check
		if (MathUtils.nearZero(al.x)) {
			if (!MathUtils.nearZero(bl.x)) {
				al.add(       1.0f, bl.y / bl.x, bl.z / bl.x, bl.w / bl.x);
				ar.add(br.x / bl.x, br.y / bl.x, br.z / bl.x, br.w / bl.x);
			} else if (!MathUtils.nearZero(cl.x)) {
				al.add(       1.0f, cl.y / cl.x, cl.z / cl.x, cl.w / cl.x);
				ar.add(cr.x / cl.x, cr.y / cl.x, cr.z / cl.x, cr.w / cl.x);
			} else if (!MathUtils.nearZero(dl.x)) {
				al.add(       1.0f, dl.y / dl.x, dl.z / dl.x, dl.w / dl.x);
				ar.add(dr.x / dl.x, dr.y / dl.x, dr.z / dl.x, dr.w / dl.x);
			} else {
				return null; // Non-invertable
			}
		} else if (al.x != 1.0f) {
			ar.div(al.x);
			al.div(al.x);
		}
		
		if (bl.x != 0.0f) {
			br.sub(ar.x * bl.x, ar.y * bl.x, ar.z * bl.x, ar.w * bl.x);
			bl.sub(       bl.x, al.y * bl.x, al.z * bl.x, al.w * bl.x);
		}
		if (cl.x != 0.0f) {
			cr.sub(ar.x * cl.x, ar.y * cl.x, ar.z * cl.x, ar.w * cl.x);
			cl.sub(       cl.x, al.y * cl.x, al.z * cl.x, al.w * cl.x);
		}
		if (dl.x != 0.0f) {
			dr.sub(ar.x * dl.x, ar.y * dl.x, ar.z * dl.x, ar.w * dl.x);
			dl.sub(       dl.x, al.y * dl.x, al.z * dl.x, al.w * dl.x);
		}
		
		// Second row check
		if (MathUtils.nearZero(bl.y)) {
			if (!MathUtils.nearZero(cl.y)) {
				bl.add(cl.x / cl.y,        1.0f, cl.z / cl.y, cl.w / cl.y);
				br.add(cr.x / cl.y, cr.y / cl.y, cr.z / cl.y, cr.w / cl.y);
			} else if (!MathUtils.nearZero(dl.y)) {
				bl.add(dl.x / dl.y,        1.0f, dl.z / dl.y, dl.w / dl.y);
				br.add(dr.x / dl.y, dr.y / dl.y, dr.z / dl.y, dr.w / dl.y);
			} else {
				return null; // Non-invertable
			}
		} else if (bl.y != 1.0f) {
			br.div(bl.y);
			bl.div(bl.y);
		}
		
		if (al.y != 0.0f) {
			ar.sub(br.x * al.y, br.y * al.y, br.z * al.y, br.w * al.y);
			al.sub(       0.0f,        al.y, bl.z * al.y, bl.w * al.y);
		}
		if (cl.y != 0.0f) {
			cr.sub(br.x * cl.y, br.y * cl.y, br.z * cl.y, br.w * cl.y);
			cl.sub(       0.0f,        cl.y, bl.z * cl.y, bl.w * cl.y);
		}
		if (dl.y != 0.0f) {
			dr.sub(br.x * dl.y, br.y * dl.y, br.z * dl.y, br.w * dl.y);
			dl.sub(       0.0f,        dl.y, bl.z * dl.y, bl.w * dl.y);
		}
		
		// Third row check
		if (MathUtils.nearZero(cl.z)) {
			if (!MathUtils.nearZero(dl.z)) {
				cl.add(dl.x / dl.z, dl.y / dl.z,        1.0f, dl.w / dl.z);
				cr.add(dr.x / dl.z, dr.y / dl.z, dr.z / dl.z, dr.w / dl.z);
			} else {
				return null; // Non-invertable
			}
		} else if (cl.z != 1.0f) {
			cr.div(cl.z);
			cl.div(cl.z);
		}
		
		if (al.z != 0.0f) {
			ar.sub(cr.x * al.z, cr.y * al.z, cr.z * al.z, cr.w * al.z);
			al.sub(       0.0f,        0.0f,        al.z, cl.w * al.z);
		}
		if (bl.z != 0.0f) {
			br.sub(cr.x * bl.z, cr.y * bl.z, cr.z * bl.z, cr.w * bl.z);
			bl.sub(       0.0f,        0.0f,        bl.z, cl.w * bl.z);
		}
		if (dl.z != 0.0f) {
			dr.sub(cr.x * dl.z, cr.y * dl.z, cr.z * dl.z, cr.w * dl.z);
			dl.sub(       0.0f,        0.0f,        dl.z, cl.w * dl.z);
		}
		
		// Fourth row check
		if (MathUtils.nearZero(dl.w)) {
			return null; // Non-invertable
		} else if (dl.w != 1.0f) {
			dr.div(dl.w);
			//dl.div(dl.w);
		}
		
		// Subtracting from the left rows
		// is not needed for the last pass.
		if (al.w != 0.0f) {
			ar.sub(dr.x * al.w, dr.y * al.w, dr.z * al.w, dr.w * al.w);
			//al.sub(        0.0,         0.0,         0.0,        al.w);
		}
		if (bl.w != 0) {
			br.sub(dr.x * bl.w, dr.y * bl.w, dr.z * bl.w, dr.w * bl.w);
			//bl.sub(        0.0,         0.0,         0.0,        bl.w);
		}
		if (cl.w != 0) {
			cr.sub(dr.x * cl.w, dr.y * cl.w, dr.z * cl.w, dr.w * cl.w);
			//cl.sub(        0.0,         0.0,         0.0,        cl.w);
		}

		// Return resulting rows in matrix 
		// form.
		return dest.set(ar.x, ar.y, ar.z, ar.w,
		                br.x, br.y, br.z, br.w,
		                cr.x, cr.y, cr.z, cr.w,
		                dr.x, dr.y, dr.z, dr.w);
	}

	public Mat4f interpolate(Mat4f end, float t) {
		return interpolate(end, t, new Mat4f());
	}
	
	public Mat4f interpolate(Mat4f end, float t, Mat4f dest) {
		Vec3f qa = new Vec3f();
		Vec3f qb = new Vec3f();
		Vec3f qm = new Vec3f();
		
		qa.set(this.m00, this.m10, this.m20);
		qb.set(end.m00, end.m10, end.m20);
		slerp(qa, qb, qm, t);

		dest.m00 = qm.x;
		dest.m10 = qm.y;
		dest.m20 = qm.z;
		dest.m30 = (end.m30 - m30) * t + m30;

		qa.set(this.m01, this.m11, this.m21);
		qb.set(end.m01, end.m11, end.m21);
		slerp(qa, qb, qm, t);

		dest.m01 = qm.x;
		dest.m11 = qm.y;
		dest.m21 = qm.z;
		dest.m31 = (end.m31 - m31) * t + m31;

		qa.set(this.m02, this.m12, this.m22);
		qb.set(end.m02, end.m12, end.m22);
		slerp(qa, qb, qm, t);

		dest.m02 = qm.x;
		dest.m12 = qm.y;
		dest.m22 = qm.z;
		dest.m32 = (end.m32 - m32) * t + m32;

		dest.m03 = (end.m03 - m03) * t + m03;
		dest.m13 = (end.m13 - m13) * t + m13;
		dest.m23 = (end.m23 - m23) * t + m23;
		dest.m33 = (end.m33 - m33) * t + m33;
		
		return dest;
	}
	
	private Vec3f slerp(Vec3f qa, Vec3f qb, Vec3f qm, float t) {
		float cosTheta = qa.dot(qb);
		if (cosTheta >= 1.0f || cosTheta <= -1.0f)
			return qm.set(qa);

		float sinTheta = MathUtils.sqrt(1.0f - cosTheta * cosTheta);
		if (MathUtils.nearZero(sinTheta)) {
			return qm.set(qa.x * 0.5f + qb.x * 0.5f,
			              qa.y * 0.5f + qb.y * 0.5f,
			              qa.z * 0.5f + qb.z * 0.5f);
		}

		float theta = MathUtils.acos(cosTheta);
		float ra = MathUtils.sin((1.0f - t) * theta) / sinTheta;
		float rb = MathUtils.sin(t * theta) / sinTheta; 
		
		return qm.set(qa.x * ra + qb.x * rb,
		              qa.y * ra + qb.y * rb,
		              qa.z * ra + qb.z * rb);
	}
	
	public Mat4f transpose() {
		return transpose(this);
	}

	public Mat4f transpose(Mat4f dest) {
		return dest.set(m00, m01, m02, m03,
		                m10, m11, m12, m13,
		                m20, m21, m22, m23,
		                m30, m31, m32, m33);
	}

	public Mat4f copy() {
		return new Mat4f(m00, m10, m20, m30,
		                 m01, m11, m21, m31,
		                 m02, m12, m22, m32,
		                 m03, m13, m23, m33);
	}

	public void copy(Mat4f dst) {
		dst.set(m00, m10, m20, m30,
		        m01, m11, m21, m31,
		        m02, m12, m22, m32,
		        m03, m13, m23, m33);
	};
	
	@Override
	public String toString() {
		return String.format(
				"%f, %f, %f, %f,\n" + 
				"%f, %f, %f, %f,\n" + 
				"%f, %f, %f, %f,\n" + 
				"%f, %f, %f, %f\n",

				m00, m10, m20, m30,
				m01, m11, m21, m31,
				m02, m12, m22, m32,
				m03, m13, m23, m33);
	}
}
