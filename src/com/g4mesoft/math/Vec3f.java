package com.g4mesoft.math;

/**
 * A 3D vector consisting of x, y and z.
 *
 * @author Christian
 * 
 * @see com.g4mesoft.math.Vec2f Vec2f
 * @see com.g4mesoft.math.Vec4f Vec4f
 */
public class Vec3f implements IVecf<Vec3f> {

	public float x;
	public float y;
	public float z;
	
	public Vec3f() {
		this(0.0f);
	}
	
	public Vec3f(float k) {
		x = y = z = k;
	}
	
	public Vec3f(Vec2f left, float z) {
		x = left.x;
		y = left.y;
		this.z = z;
	}
	
	public Vec3f(float x, Vec2f right) {
		this.x = x;
		y = right.x;
		z = right.y;
	}

	public Vec3f(Vec3f other) {
		x = other.x;
		y = other.y;
		z = other.z;
	}
	
	public Vec3f(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	/**
	 * Set values x, y, z of this vector instance
	 * 
	 * @param x -	The new x value
	 * @param y -	The new y value
	 * @param z -	The new z value
	 * 
	 * @return	This vector
	 * 
	 * @see #set(Vec3f)
	 * @see #set(float)
	 */
	public Vec3f set(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
		return this;
	}
	
	/**
	 * Set values x, y, z of this vector instance
	 * 
	 * @param other -	The new x, y and z values
	 * 					stored in another vector.
	 * 
	 * @return	This vector
	 * 
	 * @see #set(float, float, float)
	 * @see #set(float)
	 */
	@Override
	public Vec3f set(Vec3f other) {
		x = other.x;
		y = other.y;
		z = other.z;
		return this;
	}
	
	/**
	 * Set values x, y, z of this vector instance
	 * 
	 * @param k	-	The new value of x, y and z
	 * <pre>
	 * x = y = z = k;
	 * </pre>
	 * 
	 * @return	This vector
	 * 
	 * @see #set(float, float, float)
	 * @see #set(Vec3f)
	 */
	@Override
	public Vec3f set(float k) {
		x = y = z = k;
		return this;
	}
	
	/**
	 * Add a value to x, y and z
	 * 
	 * @param x	-	The value to add to the existing
	 * 				x value
	 * @param y	-	The value to add to the existing
	 * 				y value
	 * @param z	-	The value to add to the existing
	 * 				z value
	 * 
	 * @return This vector
	 * 
	 * @see #add(Vec3f)
	 * @see #add(float)
	 */
	public Vec3f add(float x, float y, float z) {
		this.x += x;
		this.y += y;
		this.z += z;
		return this;
	}
	
	/**
	 * Add a value to x, y and z
	 * 
	 * @param other -	The vector to add to the
	 * 					existing x, y and z values.
	 * 
	 * @return This vector
	 * 
	 * @see #add(float, float, float)
	 * @see #add(float)
	 */
	@Override
	public Vec3f add(Vec3f other) {
		x += other.x;
		y += other.y;
		z += other.z;
		return this;
	}
	
	/**
	 * Add a value to x, y and z
	 * 
	 * @param k	-	The value to add to x, y
	 * 				and z.
	 * 
	 * @return This vector
	 * 
	 * @see #add(float, float, float)
	 * @see #add(Vec3f)
	 */
	@Override
	public Vec3f add(float k) {
		x += k;
		y += k;
		z += k;
		return this;
	}
	
	/**
	 * Subtract a value from x, y and z
	 * 
	 * @param x	-	The value to subtract from the 
	 * 				existing x value
	 * @param y	-	The value to subtract from the 
	 * 				existing y value
	 * @param z	-	The value to subtract from the 
	 * 				existing z value
	 * 
	 * @return This vector
	 * 
	 * @see #sub(Vec3f)
	 * @see #sub(float)
	 */
	public Vec3f sub(float x, float y, float z) {
		this.x -= x;
		this.y -= y;
		this.z -= z;
		return this;
	}
	
	/**
	 * Subtract a value from x, y and z
	 * 
	 * @param other -	The vector to subtract from 
	 * 					the existing x, y and z values.
	 * 
	 * @return This vector
	 * 
	 * @see #sub(float, float, float)
	 * @see #sub(float)
	 */
	@Override
	public Vec3f sub(Vec3f other) {
		x -= other.x;
		y -= other.y;
		z -= other.z;
		return this;
	}
	
	/**
	 * Subtract a value from x, y and z
	 * 
	 * @param k	-	The value to subtract from 
	 * 				x, y and z.
	 * 
	 * @return This vector
	 * 
	 * @see #sub(float, float, float)
	 * @see #sub(Vec3f)
	 */
	@Override
	public Vec3f sub(float k) {
		x -= k;
		y -= k;
		z -= k;
		return this;
	}
	
	/**
	 * Multiply a value with x, y and z
	 * 
	 * @param x	-	The value to multiply with the 
	 * 				existing x value
	 * @param y	-	The value to multiply with the 
	 * 				existing y value
	 * @param z	-	The value to multiply with the 
	 * 				existing z value
	 * 
	 * @return This vector
	 * 
	 * @see #mul(Vec3f)
	 * @see #mul(float)
	 */
	public Vec3f mul(float x, float y, float z) {
		this.x *= x;
		this.y *= y;
		this.z *= z;
		return this;
	}
	
	
	/**
	 * Multiply a value with x, y and z
	 * 
	 * @param other -	The vector to multiply with
	 * 					the existing x, y and z values.
	 * 
	 * @return This vector
	 * 
	 * @see #mul(float, float, float)
	 * @see #mul(float)
	 */
	@Override
	public Vec3f mul(Vec3f other) {
		x *= other.x;
		y *= other.y;
		z *= other.z;
		return this;
	}
	
	/**
	 * Multiply a value with x, y and z
	 * 
	 * @param k	-	The value to multiply with
	 * 				x, y and z.
	 * 
	 * @return This vector
	 * 
	 * @see #mul(float, float, float)
	 * @see #mul(Vec3f)
	 */
	@Override
	public Vec3f mul(float k) {
		x *= k;
		y *= k;
		z *= k;
		return this;
	}
	
	/**
	 * Divide the existing x, y and z values with
	 * the given.
	 * 
	 * @param x	-	The value to divide the existing 
	 * 				x value by.
	 * @param y	-	The value to divide the existing 
	 * 				y value by.
	 * @param z	-	The value to divide the existing 
	 * 				z value by.
	 * 
	 * @return This vector
	 * 
	 * @see #div(Vec3f)
	 * @see #div(float)
	 */
	public Vec3f div(float x, float y, float z) {
		this.x /= x;
		this.y /= y;
		this.z /= z;
		return this;
	}
	
	/**
	 * Divide the existing x, y and z values with
	 * the given.
	 * 
	 * @param other	-	The vector used for division.
	 * 
	 * @return This vector
	 * 
	 * @see #div(float, float, float)
	 * @see #div(float)
	 */
	@Override
	public Vec3f div(Vec3f other) {
		x /= other.x;
		y /= other.y;
		z /= other.z;
		return this;
	}
	
	/**
	 * Divide the existing x, y and z values with
	 * the given.
	 * 
	 * @param k	-	The constant used to divide
	 * 				each component of this vector.
	 * 
	 * @return This vector
	 * 
	 * @see #div(float, float, float)
	 * @see #div(Vec3f)
	 */
	@Override
	public Vec3f div(float k) {
		x /= k;
		y /= k;
		z /= k;
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
	@Override
	public float dot(Vec3f other) {
		return x * other.x + y * other.y + z * other.z;
	}
	
	/**
	 * Returns the calculated length squared of this
	 * vector.
	 * 
	 * @return The squared length of this vector.
	 * 
	 * @see #length()
	 */
	@Override
	public float lengthSqr() {
		return x * x + y * y + z * z;
	}
	
	/**
	 * Returns the calculated length of this vector.
	 * 
	 * @return The length of this vector
	 * 
	 * @see #lengthSqr()
	 */
	@Override
	public float length() {
		return MathUtils.sqrt(lengthSqr());
	}
	
	/**
	 * Returns the cross product between this vector(left)
	 * and {@code other}(right).
	 * 
	 * @param other	-	The vector to cross with this vector
	 * 
	 * @return The calculated cross product.
	 */
	public Vec3f cross(Vec3f other) {
		return new Vec3f(
			y * other.z - other.y * z,
			z * other.x - other.z * x,
			x * other.y - other.x * y
		);
	}

	/**
	 * Calculates the cross product between this vector (left)
	 * and {@code other} (right). The result of the product is
	 * stored in the {@code dst} parameter. The dst parameter
	 * can safely be set to either this vector or {@code other}.
	 * 
	 * @param other - The vector to cross with this vector.
	 * @param dst - The destination of the cross product result.
	 */
	public void cross(Vec3f other, Vec3f dst) {
		float cx = y * other.z - other.y * z;
		float cy = z * other.x - other.z * x;
		
		dst.z = x * other.y - other.x * y;
		dst.x = cx;
		dst.y = cy;
	}
	
	/**
	 * Normalizes this vector and returns it.
	 * 
	 * @return This vector
	 * 
	 * @see #div(float)
	 * @see #length()
	 */
	@Override
	public Vec3f normalize() {
		return div(length());
	}
	
	/**
	 * @return	The sum of x, y and z
	 */
	@Override
	public float sum() {
		return x + y + z;
	}
	
	/**
	 * Calculates the Manhattan distance to another 
	 * point, {@code other}.
	 * 
	 * @param other	-	The point to calculate the
	 * 					distance to.
	 * 
	 * @return	The Manhattan distance to the point.
	 * @see #distSqr(Vec3f)
	 * @see #dist(Vec3f)
	 */
	@Override
	public float distManhattan(Vec3f other) {
		return MathUtils.abs(other.x - x) + 
		       MathUtils.abs(other.y - y) +
		       MathUtils.abs(other.z - z);
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
	 * @see #dist(Vec3f)
	 * @see #distManhattan(Vec3f)
	 */
	@Override
	public float distSqr(Vec3f other) {
		return (other.x - x) * (other.x - x) + 
		       (other.y - y) * (other.y - y) +
		       (other.z - z) * (other.z - z);
	}
	
	/**
	 * Calculates the direct distance to another 
	 * point, {@code other}.
	 * 
	 * @param other	-	The point to calculate the
	 * 					distance to.
	 * 
	 * @return	The direct distance to the point.
	 * @see #distSqr(Vec3f)
	 * @see #distManhattan(Vec3f)
	 */
	@Override
	public float dist(Vec3f other) {
		return MathUtils.sqrt(distSqr(other));
	}
	
	@Override
	public Vec3f copy() {
		return new Vec3f(this);
	}
	
	public boolean equals(float x, float y, float z) {
		return x == this.x &&
		       y == this.y &&
		       z == this.z;
	}

	@Override
	public boolean equals(Vec3f other) {
		if (other == null)
			return false;
		return equals(other.x, other.y, other.z);
	}
	
	@Override
	public boolean equals(Object other) {
		if (!(other instanceof Vec3f))
			return false;
		return equals((Vec3f)other);
	}
}
