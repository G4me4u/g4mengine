package com.g4mesoft.graphics3d;

import com.g4mesoft.math.IVecf;

public interface IShader3D {

	public void prepareShader();
	
	public void projectVertices(Triangle3D result, Vertex3D v0, Vertex3D v1, Vertex3D v2);

	public int fragment(Vertex3D vert);

	public int getOutputSize();
	
	default <T extends IVecf<T>>T reflect(T a, T n) {
		return reflect(a, n, n.copy());
	}

	default <T extends IVecf<T>>T reflect(T a, T n, T dest) {
		return dest.set(n).mul(-2.0f * a.dot(n) / n.lengthSqr()).add(a);
	}
}
