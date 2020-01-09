package com.g4mesoft.graphics3d;

public class BasicShader3D implements IShader3D {

	@Override
	public void prepareShader() {
	}

	@Override
	public void projectVertices(Triangle3D result, Vertex3D v0, Vertex3D v1, Vertex3D v2) {
		result.v0.pos.set(v0.pos);
		result.v1.pos.set(v1.pos);
		result.v2.pos.set(v2.pos);
	}

	@Override
	public boolean fragment(Vertex3D vert, Fragment3D fragment) {
		fragment.setRGB(0xFF, 0x00, 0xFF);
		return true;
	}

	@Override
	public int getOutputSize() {
		return 0;
	}
}