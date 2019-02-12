package com.g4mesoft.graphics3d;

import com.g4mesoft.math.Vec2f;
import com.g4mesoft.math.Vec3f;
import com.g4mesoft.math.Vec4f;

public class Vertex3D {

	public final Vec4f pos;
	public final float[] data;
	
	public Vertex3D(int numData) {
		this.pos = new Vec4f();
		this.data = new float[numData];
	}

	public Vertex3D(Vec4f pos) {
		this(pos, 0);
	}

	public Vertex3D(Vec4f pos, int numData) {
		this.pos = pos;
		this.data = new float[numData];
	}

	public void storeFloat(int location, float input) {
		data[location] = input;
	}

	public float loadFloat(int location) {
		return data[location];
	}
	
	public void storeVec2f(int location, Vec2f input) {
		data[location + 0] = input.x;
		data[location + 1] = input.y;
	}

	public void loadVec2f(int location, Vec2f output) {
		output.x = data[location + 0];
		output.y = data[location + 1];
	}
	
	public void storeVec3f(int location, Vec3f input) {
		data[location + 0] = input.x;
		data[location + 1] = input.y;
		data[location + 2] = input.z;
	}

	public void loadVec3f(int location, Vec3f output) {
		output.x = data[location + 0];
		output.y = data[location + 1];
		output.z = data[location + 2];
	}

	public void storeVec4f(int location, Vec4f input) {
		data[location + 0] = input.x;
		data[location + 1] = input.y;
		data[location + 2] = input.z;
		data[location + 2] = input.w;
	}
	
	public void loadVec4f(int location, Vec4f output) {
		output.x = data[location + 0];
		output.y = data[location + 1];
		output.z = data[location + 2];
		output.w = data[location + 2];
	}

	public void setVertex(Vertex3D other) {
		pos.set(other.pos);
		
		for (int i = data.length - 1; i >= 0; i--)
			data[i] = other.data[i];
	}
	
	public int getNumData() {
		return data.length;
	}
}
