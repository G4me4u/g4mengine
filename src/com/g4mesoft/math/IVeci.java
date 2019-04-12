package com.g4mesoft.math;

public interface IVeci<T extends IVeci<T>> {

	public T set(T other);

	public T set(int k);

	public T add(T other);
	
	public T add(int k);
	
	public T sub(T other);
	
	public T sub(int k);
	
	public T mul(T other);
	
	public T mul(int k);

	public T div(T other);
	
	public T div(int k);
	
	public int dot(T other);

	public int lengthSqr();
	
	public int length();
	
	public T normalize();
	
	public int sum();
	
	public int distManhattan(T other);

	public int distSqr(T other);
	
	public int dist(T other);

	public T copy();
	
	public boolean equals(T other);

}
