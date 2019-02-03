package com.g4mesoft.graphics3d;

public class Triangle3D {

	public final Vertex3D v0;
	public final Vertex3D v1;
	public final Vertex3D v2;
	
	public Triangle3D(int numData) {
		this.v0 = new Vertex3D(numData);
		this.v1 = new Vertex3D(numData);
		this.v2 = new Vertex3D(numData);
	}
}
