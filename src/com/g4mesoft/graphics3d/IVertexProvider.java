package com.g4mesoft.graphics3d;

public interface IVertexProvider {

	public void prepareDraw();

	public boolean hasNext();
	
	public Vertex3D getNextVertex();
	
	public Shape3D getShape();
	
}
