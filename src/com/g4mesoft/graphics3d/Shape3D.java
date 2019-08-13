package com.g4mesoft.graphics3d;

public enum Shape3D {

	TRIANGLES(3),
	QUADS(4);
	
	private final int verticesPerShape;
	
	private Shape3D(int verticesPerShape) {
		this.verticesPerShape = verticesPerShape;
	}
	
	public int getVerticesPerShape() {
		return verticesPerShape;
	}
}
