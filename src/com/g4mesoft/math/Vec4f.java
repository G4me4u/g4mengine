package com.g4mesoft.math;

/**
 * A 3D vector consisting of x, y and z.
 *
 * @author Christian
 * 
 * @see com.g4mesoft.math.Vec2f Vec2f
 * @see com.g4mesoft.math.Vec3f Vec3f
 */
public class Vec4f {

	public float x;
	public float y;
	public float z;
	public float w;
	
	public Vec4f() {
		this(0.0f);
	}
	
	public Vec4f(float k) {
		x = y = z = w = k;
	}
	
	public Vec4f(Vec2f left, Vec2f right) {
		x = left.x;
		y = left.y;
		z = right.x;
		w = right.y;
	}
	
	public Vec4f(Vec2f left, float z, float w) {
		x = left.x;
		y = left.y;
		this.z = z;
		this.w = w;
	}
	
	public Vec4f(float x, float y, Vec2f right) {
		this.x = x;
		this.y = y;
		z = right.x;
		w = right.y;
	}
	
	public Vec4f(Vec3f left, float w) {
		x = left.x;
		y = left.y;
		z = left.z;
		this.w = w;
	}
	
	public Vec4f(float x, Vec3f right) {
		this.x = x;
		y = right.x;
		z = right.y;
		w = right.z;
	}

	public Vec4f(Vec4f other) {
		x = other.x;
		y = other.y;
		z = other.z;
		w = other.w;
	}
	
	public Vec4f(float x, float y, float z, float w) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
	}
	
	/**
	 * Set values x, y, z, w of this vector instance
	 * 
	 * @param x -	The new x value
	 * @param y -	The new y value
	 * @param z -	The new z value
	 * @param w -	The new w value
	 * 
	 * @return	This vector
	 * 
	 * @see #set(Vec4f)
	 * @see #set(float)
	 */
	public Vec4f set(float x, float y, float z, float w) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
		return this;
	}
	
	/**
	 * Set values x, y, z, w of this vector instance
	 * 
	 * @param other -	The new x, y, z and w values
	 * 					stored in another vector.
	 * 
	 * @return	This vector
	 * 
	 * @see #set(float, float, float, float)
	 * @see #set(float)
	 */
	public Vec4f set(Vec4f other) {
		x = other.x;
		y = other.y;
		z = other.z;
		w = other.w;
		return this;
	}
	
	/**
	 * Set values x, y, z, w of this vector instance
	 * 
	 * @param k	-	The new value of x, y, z and w
	 * <pre>
	 * x = y = z = w = k;
	 * </pre>
	 * 
	 * @return	This vector
	 * 
	 * @see #set(float, float, float, float)
	 * @see #set(Vec4f)
	 */
	public Vec4f set(float k) {
		x = y = z = w = k;
		return this;
	}
	
	/**
	 * Add a value to x, y, z and w
	 * 
	 * @param x	-	The value to add to the existing
	 * 				x value
	 * @param y	-	The value to add to the existing
	 * 				y value
	 * @param z	-	The value to add to the existing
	 * 				z value
	 * @param w	-	The value to add to the existing
	 * 				w value
	 * 
	 * @return This vector
	 * 
	 * @see #add(Vec4f)
	 * @see #add(float)
	 */
	public Vec4f add(float x, float y, float z, float w) {
		this.x += x;
		this.y += y;
		this.z += z;
		this.w += w;
		return this;
	}
	
	/**
	 * Add a value to x, y, z and w
	 * 
	 * @param other -	The vector to add to the
	 * 					existing x, y, z and w values.
	 * 
	 * @return This vector
	 * 
	 * @see #add(float, float, float, float)
	 * @see #add(float)
	 */
	public Vec4f add(Vec4f other) {
		x += other.x;
		y += other.y;
		z += other.z;
		w += other.w;
		return this;
	}
	
	/**
	 * Add a value to x, y, z and w
	 * 
	 * @param k	-	The value to add to x, y,
	 * 				z and w.
	 * 
	 * @return This vector
	 * 
	 * @see #add(float, float, float, float)
	 * @see #add(Vec4f)
	 */
	public Vec4f add(float k) {
		x += k;
		y += k;
		z += k;
		w += k;
		return this;
	}
	
	/**
	 * Subtract a value from x, y, z and w
	 * 
	 * @param x	-	The value to subtract from the 
	 * 				existing x value
	 * @param y	-	The value to subtract from the 
	 * 				existing y value
	 * @param z	-	The value to subtract from the 
	 * 				existing z value
	 * @param w	-	The value to subtract from the 
	 * 				existing w value
	 * 
	 * @return This vector
	 * 
	 * @see #sub(Vec4f)
	 * @see #sub(float)
	 */
	public Vec4f sub(float x, float y, float z, float w) {
		this.x -= x;
		this.y -= y;
		this.z -= z;
		this.w -= w;
		return this;
	}
	
	/**
	 * Subtract a value from x, y, z and w
	 * 
	 * @param other -	The vector to subtract from 
	 * 					the existing x, y, z and w values.
	 * 
	 * @return This vector
	 * 
	 * @see #sub(float, float, float, float)
	 * @see #sub(float)
	 */
	public Vec4f sub(Vec4f other) {
		x -= other.x;
		y -= other.y;
		z -= other.z;
		w -= other.w;
		return this;
	}
	
	/**
	 * Subtract a value from x, y, z and w
	 * 
	 * @param k	-	The value to subtract from 
	 * 				x, y, z and w.
	 * 
	 * @return This vector
	 * 
	 * @see #sub(float, float, float, float)
	 * @see #sub(Vec4f)
	 */
	public Vec4f sub(float k) {
		x -= k;
		y -= k;
		z -= k;
		w -= k;
		return this;
	}
	
	/**
	 * Multiply a value with x, y, z and w
	 * 
	 * @param x	-	The value to multiply with the 
	 * 				existing x value
	 * @param y	-	The value to multiply with the 
	 * 				existing y value
	 * @param z	-	The value to multiply with the 
	 * 				existing z value
	 * @param w	-	The value to multiply with the 
	 * 				existing w value
	 * 
	 * @return This vector
	 * 
	 * @see #mul(Vec4f)
	 * @see #mul(float)
	 */
	public Vec4f mul(float x, float y, float z, float w) {
		this.x *= x;
		this.y *= y;
		this.z *= z;
		this.w *= w;
		return this;
	}
	
	
	/**
	 * Multiply a value with x, y, z and w
	 * 
	 * @param other -	The vector to multiply with
	 * 					the existing x, y, z and w values.
	 * 
	 * @return This vector
	 * 
	 * @see #mul(float, float, float, float)
	 * @see #mul(float)
	 */
	public Vec4f mul(Vec4f other) {
		x *= other.x;
		y *= other.y;
		z *= other.z;
		w *= other.w;
		return this;
	}
	
	/**
	 * Multiply a value with x, y, z and w
	 * 
	 * @param k	-	The value to multiply with
	 * 				x, y, z and w.
	 * 
	 * @return This vector
	 * 
	 * @see #mul(float, float, float, float)
	 * @see #mul(Vec4f)
	 */
	public Vec4f mul(float k) {
		x *= k;
		y *= k;
		z *= k;
		w *= k;
		return this;
	}
	
	/**
	 * Divide the existing x, y, z and w values with
	 * the given.
	 * 
	 * @param x	-	The value to divide the existing 
	 * 				x value by.
	 * @param y	-	The value to divide the existing 
	 * 				y value by.
	 * @param z	-	The value to divide the existing 
	 * 				z value by.
	 * @param w	-	The value to divide the existing 
	 * 				w value by.
	 * 
	 * @return This vector
	 * 
	 * @see #div(Vec4f)
	 * @see #div(float)
	 */
	public Vec4f div(float x, float y, float z, float w) {
		this.x /= x;
		this.y /= y;
		this.z /= z;
		this.w /= w;
		return this;
	}
	
	/**
	 * Divide the existing x, y, z and w values with
	 * the given.
	 * 
	 * @param other	-	The vector used for division.
	 * 
	 * @return This vector
	 * 
	 * @see #div(float, float, float, float)
	 * @see #div(float)
	 */
	public Vec4f div(Vec4f other) {
		x /= other.x;
		y /= other.y;
		z /= other.z;
		w /= other.w;
		return this;
	}
	
	/**
	 * Divide the existing x, y, z and w values with
	 * the given.
	 * 
	 * @param k	-	The constant used to divide
	 * 				each component of this vector.
	 * 
	 * @return This vector
	 * 
	 * @see #div(float, float, float, float)
	 * @see #div(Vec4f)
	 */
	public Vec4f div(float k) {
		x /= k;
		y /= k;
		z /= k;
		w /= k;
		return this;
	}

	/**
	 * Returns the calculated dot product of this
	 * vector(left) dotted with {@code other}(right).
	 * 
	 * @param other	-	The other vector
	 * 
	 * @return The dotted value as a float.
	 */
	public float dot(Vec4f other) {
		return x * other.x + y * other.y + z * other.z + w * other.w;
	}
	
	/**
	 * Returns the calculated length squared of this
	 * vector.
	 * 
	 * @return The squared length of this vector.
	 * 
	 * @see #length()
	 */
	public float lengthSqr() {
		return x * x + y * y + z * z + w * w;
	}
	
	/**
	 * Returns the calculated length of this vector.
	 * 
	 * @return The length of this vector
	 * 
	 * @see #lengthSqr()
	 */
	public float length() {
		return (float)Math.sqrt(lengthSqr());
	}
	
	/**
	 * Normalizes this vector and returns it.
	 * 
	 * @return This vector
	 * 
	 * @see #div(float)
	 * @see #length()
	 */
	public Vec4f normalize() {
		return div(length());
	}
	
	/**
	 * @return	The sum of x, y, z and w
	 */
	public float sum() {
		return x + y + z + w;
	}
	
	/**
	 * Calculates the Manhattan distance to another 
	 * point, {@code other}.
	 * 
	 * @param other	-	The point to calculate the
	 * 					distance to.
	 * 
	 * @return	The Manhattan distance to the point.
	 * @see #distSqr(Vec4f)
	 * @see #dist(Vec4f)
	 */
	public float distManhattan(Vec4f other) {
		return	Math.abs(other.x - x) + 
				Math.abs(other.y - y) +
				Math.abs(other.z - z) +
				Math.abs(other.w - w);
	}
	
	/**
	 * Calculates the squared direct distance to 
	 * another point, {@code other}.
	 * 
	 * @param other	-	The point to calculate the
	 * 					distance to.
	 * 
	 * @return	The direct distance to the point 
	 * 			squared.
	 * @see #dist(Vec4f)
	 * @see #distManhattan(Vec4f)
	 */
	public float distSqr(Vec4f other) {
		return	(other.x - x) * (other.x - x) + 
				(other.y - y) * (other.y - y) +
				(other.z - z) * (other.z - z) +
				(other.w - w) * (other.w - w);
	}
	
	/**
	 * Calculates the direct distance to another 
	 * point, {@code other}.
	 * 
	 * @param other	-	The point to calculate the
	 * 					distance to.
	 * 
	 * @return	The direct distance to the point.
	 * @see #distSqr(Vec4f)
	 * @see #distManhattan(Vec4f)
	 */
	public float dist(Vec4f other) {
		return (int)Math.sqrt(distSqr(other));
	}
	
	public boolean equals(float x, float y, float z, float w) {
		return x == this.x &&
		       y == this.y &&
		       z == this.z &&
		       w == this.w;
	}

	public boolean equals(Vec4f other) {
		if (other == null)
			return false;
		
		return other.x == x &&
		       other.y == y &&
		       other.z == z &&
		       other.w == w;
	}
	
	@Override
	public boolean equals(Object other) {
		if (!(other instanceof Vec4f))
			return false;

		Vec4f otherVec = ((Vec4f)other);
		return otherVec.x == x && 
		       otherVec.y == y &&
		       otherVec.z == z &&
		       otherVec.w == w;
	}
}
