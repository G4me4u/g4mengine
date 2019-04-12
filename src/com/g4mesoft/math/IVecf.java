package com.g4mesoft.math;

public interface IVecf<T extends IVecf<T>> {

	public T set(T other);

	public T set(float k);

	public T add(T other);
	
	public T add(float k);
	
	public T sub(T other);
	
	public T sub(float k);
	
	public T mul(T other);
	
	public T mul(float k);

	public T div(T other);
	
	public T div(float k);
	
	public float dot(T other);

	public float lengthSqr();
	
	public float length();
	
	public T normalize();
	
	public float sum();
	
	public float distManhattan(T other);

	public float distSqr(T other);
	
	public float dist(T other);

	public T copy();
	
	public boolean equals(T other);

}
