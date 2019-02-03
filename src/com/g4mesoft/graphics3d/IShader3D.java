package com.g4mesoft.graphics3d;

public interface IShader3D {

	public void prepareShader();
	
	public void projectVertices(Triangle3D result, Vertex3D v0, Vertex3D v1, Vertex3D v2);

	public int fragment(Vertex3D vert);

	public int getOutputSize();
	
}
