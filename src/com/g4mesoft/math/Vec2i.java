package com.g4mesoft.math;

/**
 * A 2D vector consisting of x and y.
 *
 * @author Christian
 * 
 * @see com.g4mesoft.math.Vec3i Vec3i
 * @see com.g4mesoft.math.Vec4i Vec4i
 */
public class Vec2i {

	public int x;
	public int y;
	
	public Vec2i() {
		this(0);
	}
	
	public Vec2i(int k) {
		x = y = k;
	}

	public Vec2i(Vec2i other) {
		x = other.x;
		y = other.y;
	}
	
	public Vec2i(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	/**
	 * Set values x, y of this vector instance
	 * 
	 * @param x -	The new x value
	 * @param y -	The new y value
	 * 
	 * @return	This vector
	 * 
	 * @see #set(Vec2i)
	 * @see #set(int)
	 */
	public Vec2i set(int x, int y) {
		this.x = x;
		this.y = y;
		return this;
	}
	
	/**
	 * Set values x, y of this vector instance
	 * 
	 * @param other -	The new x and y values
	 * 					stored in another vector.
	 * 
	 * @return	This vector
	 * 
	 * @see #set(int, int)
	 * @see #set(int)
	 */
	public Vec2i set(Vec2i other) {
		x = other.x;
		y = other.y;
		return this;
	}
	
	/**
	 * Set values x, y of this vector instance
	 * 
	 * @param k	-	The new value of x and y
	 * <pre>
	 * x = y = k;
	 * </pre>
	 * 
	 * @return	This vector
	 * 
	 * @see #set(int, int)
	 * @see #set(Vec2i)
	 */
	public Vec2i set(int k) {
		x = y = k;
		return this;
	}
	
	/**
	 * Add a value to x and z
	 * 
	 * @param x	-	The value to add to the existing
	 * 				x value
	 * @param y	-	The value to add to the existing
	 * 				y value
	 * 
	 * @return This vector
	 * 
	 * @see #add(Vec2i)
	 * @see #add(int)
	 */
	public Vec2i add(int x, int y) {
		this.x += x;
		this.y += y;
		return this;
	}
	
	/**
	 * Add a value to x and y
	 * 
	 * @param other -	The vector to add to the
	 * 					existing x and y values.
	 * 
	 * @return This vector
	 * 
	 * @see #add(int, int)
	 * @see #add(int)
	 */
	public Vec2i add(Vec2i other) {
		x += other.x;
		y += other.y;
		return this;
	}
	
	/**
	 * Add a value to x and y
	 * 
	 * @param k	-	The value to add to x
	 *				and y.
	 * 
	 * @return This vector
	 * 
	 * @see #add(int, int)
	 * @see #add(Vec2i)
	 */
	public Vec2i add(int k) {
		x += k;
		y += k;
		return this;
	}
	
	/**
	 * Subtract a value from x and y
	 * 
	 * @param x	-	The value to subtract from the 
	 * 				existing x value
	 * @param y	-	The value to subtract from the 
	 * 				existing y value
	 * 
	 * @return This vector
	 * 
	 * @see #sub(Vec2i)
	 * @see #sub(int)
	 */
	public Vec2i sub(int x, int y) {
		this.x -= x;
		this.y -= y;
		return this;
	}
	
	/**
	 * Subtract a value from x and y
	 * 
	 * @param other -	The vector to subtract from 
	 * 					the existing x and y values.
	 * 
	 * @return This vector
	 * 
	 * @see #sub(int, int)
	 * @see #sub(int)
	 */
	public Vec2i sub(Vec2i other) {
		x -= other.x;
		y -= other.y;
		return this;
	}
	
	/**
	 * Subtract a value from x and y
	 * 
	 * @param k	-	The value to subtract from 
	 * 				x and y.
	 * 
	 * @return This vector
	 * 
	 * @see #sub(int, int)
	 * @see #sub(Vec2i)
	 */
	public Vec2i sub(int k) {
		x -= k;
		y -= k;
		return this;
	}
	
	/**
	 * Multiply a value with x and y
	 * 
	 * @param x	-	The value to multiply with the 
	 * 				existing x value
	 * @param y	-	The value to multiply with the 
	 * 				existing y value
	 * 
	 * @return This vector
	 * 
	 * @see #mul(Vec2i)
	 * @see #mul(int)
	 */
	public Vec2i mul(int x, int y) {
		this.x *= x;
		this.y *= y;
		return this;
	}
	
	
	/**
	 * Multiply a value with x and y
	 * 
	 * @param other -	The vector to multiply with
	 * 					the existing x and y values.
	 * 
	 * @return This vector
	 * 
	 * @see #mul(int, int)
	 * @see #mul(int)
	 */
	public Vec2i mul(Vec2i other) {
		x *= other.x;
		y *= other.y;
		return this;
	}
	
	/**
	 * Multiply a value with x and y
	 * 
	 * @param k	-	The value to multiply with
	 * 				x and y.
	 * 
	 * @return This vector
	 * 
	 * @see #mul(int, int)
	 * @see #mul(Vec2i)
	 */
	public Vec2i mul(int k) {
		x *= k;
		y *= k;
		return this;
	}
	
	/**
	 * Divide the existing x and y values with
	 * the given.
	 * 
	 * @param x	-	The value to divide the existing 
	 * 				x value by.
	 * @param y	-	The value to divide the existing 
	 * 				y value by.
	 * 
	 * @return This vector
	 * 
	 * @see #div(Vec2i)
	 * @see #div(int)
	 */
	public Vec2i div(int x, int y) {
		this.x /= x;
		this.y /= y;
		return this;
	}
	
	/**
	 * Divide the existing x and y values with
	 * the given.
	 * 
	 * @param other	-	The vector used for division.
	 * 
	 * @return This vector
	 * 
	 * @see #div(int, int)
	 * @see #div(int)
	 */
	public Vec2i div(Vec2i other) {
		x /= other.x;
		y /= other.y;
		return this;
	}
	
	/**
	 * Divide the existing x and y values with
	 * the given.
	 * 
	 * @param k	-	The constant used to divide
	 * 				each component of this vector.
	 * 
	 * @return This vector
	 * 
	 * @see #div(int, int)
	 * @see #div(Vec2i)
	 */
	public Vec2i div(int k) {
		x /= k;
		y /= k;
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
	public int dot(Vec2i other) {
		return x * other.x + y * other.y;
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
		return x * x + y * y;
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
	public Vec2i normalize() {
		return div(length());
	}
	
	/**
	 * @return	The sum of x and y
	 */
	public int sum() {
		return x + y;
	}
	
	/**
	 * Calculates the Manhattan distance to another 
	 * point, {@code other}.
	 * 
	 * @param other	-	The point to calculate the
	 * 					distance to.
	 * 
	 * @return	The Manhattan distance to the point.
	 * @see #distSqr(Vec2i)
	 * @see #dist(Vec2i)
	 */
	public int distManhattan(Vec2i other) {
		return	Math.abs(other.x - x) + 
				Math.abs(other.y - y);
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
	 * @see #dist(Vec2i)
	 * @see #distManhattan(Vec2i)
	 */
	public int distSqr(Vec2i other) {
		return	(other.x - x) * (other.x - x) + 
				(other.y - y) * (other.y - y);
	}
	
	/**
	 * Calculates the direct distance to another 
	 * point, {@code other}.
	 * 
	 * @param other	-	The point to calculate the
	 * 					distance to.
	 * 
	 * @return	The direct distance to the point.
	 * @see #distSqr(Vec2i)
	 * @see #distManhattan(Vec2i)
	 */
	public int dist(Vec2i other) {
		return (int)Math.sqrt(distSqr(other));
	}
	
	public boolean equals(int x, int y) {
		return x == this.x &&
		       y == this.y;
	}

	public boolean equals(Vec2i other) {
		if (other == null)
			return false;
		
		return other.x == x &&
		       other.y == y;
	}
	
	@Override
	public boolean equals(Object other) {
		if (!(other instanceof Vec2i))
			return false;

		Vec2i otherVec = ((Vec2i)other);
		return otherVec.x == x && 
		       otherVec.y == y;
	}
}
