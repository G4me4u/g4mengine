package com.g4mesoft.math;

public interface IMatf<T extends IMatf<T>> {

	public T toIdentity();

	public T toIdentity(float d);

	public T set(T other);

	public T mul(T right);

	public T mul(T right, T dest);

	public T invert();

	public T inverseCopy();

	public T inverseCopy(T dest);

	public T transpose();
	
	public T transpose(T dest);
	
	public T copy();

	public T copy(T dest);
	
}
