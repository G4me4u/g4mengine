package com.g4mesoft.math;

/**
 * A 2D vector consisting of x and y.
 *
 * @author Christian
 * 
 * @see com.g4mesoft.math.Vec3f Vec3f
 * @see com.g4mesoft.math.Vec4f Vec4f
 */
public class Vec2f {

	public float x;
	public float y;
	
	public Vec2f() {
		this(0.0f);
	}
	
	public Vec2f(float k) {
		x = y = k;
	}

	public Vec2f(Vec2f other) {
		x = other.x;
		y = other.y;
	}
	
	public Vec2f(float x, float y) {
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
	 * @see #set(Vec2f)
	 * @see #set(float)
	 */
	public Vec2f set(float x, float y) {
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
	 * @see #set(float, float)
	 * @see #set(float)
	 */
	public Vec2f set(Vec2f other) {
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
	 * @see #set(float, float)
	 * @see #set(Vec2f)
	 */
	public Vec2f set(float k) {
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
	 * @see #add(Vec2f)
	 * @see #add(float)
	 */
	public Vec2f add(float x, float y) {
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
	 * @see #add(float, float)
	 * @see #add(float)
	 */
	public Vec2f add(Vec2f other) {
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
	 * @see #add(float, float)
	 * @see #add(Vec2f)
	 */
	public Vec2f add(float k) {
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
	 * @see #sub(Vec2f)
	 * @see #sub(float)
	 */
	public Vec2f sub(float x, float y) {
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
	 * @see #sub(float, float)
	 * @see #sub(float)
	 */
	public Vec2f sub(Vec2f other) {
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
	 * @see #sub(float, float)
	 * @see #sub(Vec2f)
	 */
	public Vec2f sub(float k) {
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
	 * @see #mul(Vec2f)
	 * @see #mul(float)
	 */
	public Vec2f mul(float x, float y) {
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
	 * @see #mul(float, float)
	 * @see #mul(float)
	 */
	public Vec2f mul(Vec2f other) {
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
	 * @see #mul(float, float)
	 * @see #mul(Vec2f)
	 */
	public Vec2f mul(float k) {
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
	 * @see #div(Vec2f)
	 * @see #div(float)
	 */
	public Vec2f div(float x, float y) {
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
	 * @see #div(float, float)
	 * @see #div(float)
	 */
	public Vec2f div(Vec2f other) {
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
	 * @see #div(float, float)
	 * @see #div(Vec2f)
	 */
	public Vec2f div(float k) {
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
	 * @return The dotted value as a float.
	 */
	public float dot(Vec2f other) {
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
	public float lengthSqr() {
		return x * x + y * y;
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
	public Vec2f normalize() {
		return div(length());
	}

	/**
	 * @return	The sum of x and y
	 */
	public float sum() {
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
	 * @see #distSqr(Vec2f)
	 * @see #dist(Vec2f)
	 */
	public float distManhattan(Vec2f other) {
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
	 * @see #dist(Vec2f)
	 * @see #distManhattan(Vec2f)
	 */
	public float distSqr(Vec2f other) {
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
	 * @see #distSqr(Vec2f)
	 * @see #distManhattan(Vec2f)
	 */
	public float dist(Vec2f other) {
		return (float)Math.sqrt(distSqr(other));
	}
	
	public boolean equals(float x, float y) {
		return x == this.x &&
		       y == this.y;
	}

	public boolean equals(Vec2f other) {
		if (other == null)
			return false;
		
		return other.x == x &&
		       other.y == y;
	}
	
	@Override
	public boolean equals(Object other) {
		if (!(other instanceof Vec2f))
			return false;

		Vec2f otherVec = ((Vec2f)other);
		return otherVec.x == x && 
		       otherVec.y == y;
	}
}
