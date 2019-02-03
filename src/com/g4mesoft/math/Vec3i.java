package com.g4mesoft.math;

/**
 * A 3D vector consisting of x, y and z.
 *
 * @author Christian
 * 
 * @see com.g4mesoft.math.Vec2i Vec2i
 * @see com.g4mesoft.math.Vec4i Vec4i
 */
public class Vec3i {

	public int x;
	public int y;
	public int z;
	
	public Vec3i() {
		this(0);
	}
	
	public Vec3i(int k) {
		x = y = z = k;
	}
	
	public Vec3i(Vec2i left, int z) {
		x = left.x;
		y = left.y;
		this.z = z;
	}
	
	public Vec3i(int x, Vec2i right) {
		this.x = x;
		y = right.x;
		z = right.y;
	}

	public Vec3i(Vec3i other) {
		x = other.x;
		y = other.y;
		z = other.z;
	}
	
	public Vec3i(int x, int y, int z) {
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
	 * @see #set(Vec3i)
	 * @see #set(int)
	 */
	public Vec3i set(int x, int y, int z) {
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
	 * @see #set(int, int, int)
	 * @see #set(int)
	 */
	public Vec3i set(Vec3i other) {
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
	 * @see #set(int, int, int)
	 * @see #set(Vec3i)
	 */
	public Vec3i set(int k) {
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
	 * @see #add(Vec3i)
	 * @see #add(int)
	 */
	public Vec3i add(int x, int y, int z) {
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
	 * @see #add(int, int, int)
	 * @see #add(int)
	 */
	public Vec3i add(Vec3i other) {
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
	 * @see #add(int, int, int)
	 * @see #add(Vec3i)
	 */
	public Vec3i add(int k) {
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
	 * @see #sub(Vec3i)
	 * @see #sub(int)
	 */
	public Vec3i sub(int x, int y, int z) {
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
	 * @see #sub(int, int, int)
	 * @see #sub(int)
	 */
	public Vec3i sub(Vec3i other) {
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
	 * @see #sub(int, int, int)
	 * @see #sub(Vec3i)
	 */
	public Vec3i sub(int k) {
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
	 * @see #mul(Vec3i)
	 * @see #mul(int)
	 */
	public Vec3i mul(int x, int y, int z) {
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
	 * @see #mul(int, int, int)
	 * @see #mul(int)
	 */
	public Vec3i mul(Vec3i other) {
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
	 * @see #mul(int, int, int)
	 * @see #mul(Vec3i)
	 */
	public Vec3i mul(int k) {
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
	 * @see #div(Vec3i)
	 * @see #div(int)
	 */
	public Vec3i div(int x, int y, int z) {
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
	 * @see #div(int, int, int)
	 * @see #div(int)
	 */
	public Vec3i div(Vec3i other) {
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
	 * @see #div(int, int, int)
	 * @see #div(Vec3i)
	 */
	public Vec3i div(int k) {
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
	 * @return The dotted value as an integer.
	 */
	public int dot(Vec3i other) {
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
	public int lengthSqr() {
		return x * x + y * y + z * z;
	}
	
	/**
	 * Returns the calculated length of this vector.
	 * 
	 * @return The length of this vector
	 * 
	 * @see #lengthSqr()
	 */
	public int length() {
		return (int)MathUtils.sqrt(lengthSqr());
	}
	
	/**
	 * Returns the cross product between this vector(left)
	 * and {@code other}(right).
	 * 
	 * @param other	-	The vector to cross with this vector
	 * 
	 * @return The calculated cross product.
	 */
	public Vec3i cross(Vec3i other) {
		return new Vec3i(
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
	public void cross(Vec3i other, Vec3i dst) {
		int cx = y * other.z - other.y * z;
		int cy = z * other.x - other.z * x;
		
		dst.z = x * other.y - other.x * y;
		dst.x = cx;
		dst.y = cy;
	}
	
	/**
	 * Normalizes this vector and returns it.
	 * 
	 * @return This vector
	 * 
	 * @see #div(int)
	 * @see #length()
	 */
	public Vec3i normalize() {
		return div(length());
	}
	
	/**
	 * @return	The sum of x, y and z
	 */
	public int sum() {
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
	 * @see #distSqr(Vec3i)
	 * @see #dist(Vec3i)
	 */
	public int distManhattan(Vec3i other) {
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
	 * @see #dist(Vec3i)
	 * @see #distManhattan(Vec3i)
	 */
	public int distSqr(Vec3i other) {
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
	 * @see #distSqr(Vec3i)
	 * @see #distManhattan(Vec3i)
	 */
	public int dist(Vec3i other) {
		return (int)MathUtils.sqrt(distSqr(other));
	}
	
	public boolean equals(int x, int y, int z) {
		return x == this.x &&
		       y == this.y &&
		       z == this.z;
	}

	public boolean equals(Vec3i other) {
		if (other == null)
			return false;
		
		return other.x == x &&
		       other.y == y &&
		       other.z == z;
	}
	
	@Override
	public boolean equals(Object other) {
		if (!(other instanceof Vec3i))
			return false;

		Vec3i otherVec = ((Vec3i)other);
		return otherVec.x == x && 
		       otherVec.y == y &&
		       otherVec.z == z;
	}
}
