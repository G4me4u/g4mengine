package com.g4mesoft.math;

/**
 * A 3D vector consisting of x, y and z.
 *
 * @author Christian
 * 
 * @see com.g4mesoft.math.Vec2i Vec2i
 * @see com.g4mesoft.math.Vec3i Vec3i
 */
public class Vec4i {

	public int x;
	public int y;
	public int z;
	public int w;
	
	public Vec4i() {
		this(0);
	}
	
	public Vec4i(int k) {
		x = y = z = w = k;
	}
	
	public Vec4i(Vec2i left, Vec2i right) {
		x = left.x;
		y = left.y;
		z = right.x;
		w = right.y;
	}
	
	public Vec4i(Vec2i left, int z, int w) {
		x = left.x;
		y = left.y;
		this.z = z;
		this.w = w;
	}
	
	public Vec4i(int x, int y, Vec2i right) {
		this.x = x;
		this.y = y;
		z = right.x;
		w = right.y;
	}
	
	public Vec4i(Vec3i left, int w) {
		x = left.x;
		y = left.y;
		z = left.z;
		this.w = w;
	}
	
	public Vec4i(int x, Vec3i right) {
		this.x = x;
		y = right.x;
		z = right.y;
		w = right.z;
	}

	public Vec4i(Vec4i other) {
		x = other.x;
		y = other.y;
		z = other.z;
		w = other.w;
	}
	
	public Vec4i(int x, int y, int z, int w) {
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
	 * @see #set(Vec4i)
	 * @see #set(int)
	 */
	public Vec4i set(int x, int y, int z, int w) {
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
	 * @see #set(int, int, int, int)
	 * @see #set(int)
	 */
	public Vec4i set(Vec4i other) {
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
	 * @see #set(int, int, int, int)
	 * @see #set(Vec4i)
	 */
	public Vec4i set(int k) {
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
	 * @see #add(Vec4i)
	 * @see #add(int)
	 */
	public Vec4i add(int x, int y, int z, int w) {
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
	 * @see #add(int, int, int, int)
	 * @see #add(int)
	 */
	public Vec4i add(Vec4i other) {
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
	 * @see #add(int, int, int, int)
	 * @see #add(Vec4i)
	 */
	public Vec4i add(int k) {
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
	 * @see #sub(Vec4i)
	 * @see #sub(int)
	 */
	public Vec4i sub(int x, int y, int z, int w) {
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
	 * @see #sub(int, int, int, int)
	 * @see #sub(int)
	 */
	public Vec4i sub(Vec4i other) {
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
	 * @see #sub(int, int, int, int)
	 * @see #sub(Vec4i)
	 */
	public Vec4i sub(int k) {
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
	 * @see #mul(Vec4i)
	 * @see #mul(int)
	 */
	public Vec4i mul(int x, int y, int z, int w) {
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
	 * @see #mul(int, int, int, int)
	 * @see #mul(int)
	 */
	public Vec4i mul(Vec4i other) {
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
	 * @see #mul(int, int, int, int)
	 * @see #mul(Vec4i)
	 */
	public Vec4i mul(int k) {
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
	 * @see #div(Vec4i)
	 * @see #div(int)
	 */
	public Vec4i div(int x, int y, int z, int w) {
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
	 * @see #div(int, int, int, int)
	 * @see #div(int)
	 */
	public Vec4i div(Vec4i other) {
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
	 * @see #div(int, int, int, int)
	 * @see #div(Vec4i)
	 */
	public Vec4i div(int k) {
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
	 * @return The dotted value as an integer.
	 */
	public int dot(Vec4i other) {
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
	public int lengthSqr() {
		return x * x + y * y + z * z + w * w;
	}
	
	/**
	 * Returns the calculated length of this vector.
	 * 
	 * @return The length of this vector
	 * 
	 * @see #lengthSqr()
	 */
	public int length() {
		return (int)Math.sqrt(lengthSqr());
	}
	
	/**
	 * Normalizes this vector and returns it.
	 * 
	 * @return This vector
	 * 
	 * @see #div(int)
	 * @see #length()
	 */
	public Vec4i normalize() {
		return div(length());
	}
	
	/**
	 * @return	The sum of x, y, z and w
	 */
	public int sum() {
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
	 * @see #distSqr(Vec4i)
	 * @see #dist(Vec4i)
	 */
	public int distManhattan(Vec4i other) {
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
	 * @see #dist(Vec4i)
	 * @see #distManhattan(Vec4i)
	 */
	public int distSqr(Vec4i other) {
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
	 * @see #distSqr(Vec4i)
	 * @see #distManhattan(Vec4i)
	 */
	public int dist(Vec4i other) {
		return (int)Math.sqrt(distSqr(other));
	}
	
	@Override
	public boolean equals(Object other) {
		if (!(other instanceof Vec4i))
			return false;

		Vec4i otherVec = ((Vec4i)other);
		return otherVec.x == x && 
		       otherVec.y == y &&
		       otherVec.z == z &&
		       otherVec.w == w;
	}
}
