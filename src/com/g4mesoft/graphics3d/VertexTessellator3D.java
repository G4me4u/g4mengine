package com.g4mesoft.graphics3d;

import java.util.Arrays;

import com.g4mesoft.math.Vec2f;
import com.g4mesoft.math.Vec3f;
import com.g4mesoft.math.Vec4f;

public class VertexTessellator3D {

	private static final int DEFAULT_INITIAL_CAPACITY = 16;
	
	public static final int LOCATION_X = 0;
	public static final int LOCATION_Y = 1;
	public static final int LOCATION_Z = 2;
	public static final int POSITION_SIZE = 3;
	
	private int vertexSize;
	private float[] vertices;
	private int numVertices;
	
	private int currentVertexOffset;
	
	public VertexTessellator3D(int numExtraData) {
		this(numExtraData, DEFAULT_INITIAL_CAPACITY);
	}
	
	public VertexTessellator3D(int numExtraData, int initialVertexCapacity) {
		if (numExtraData < 0)
			throw new IllegalArgumentException("numExtraData must be a non-negative integer");
		if (initialVertexCapacity <= 0)
			throw new IllegalArgumentException("initialVertexCapacity must be a positive integer");
		
		vertexSize = POSITION_SIZE + numExtraData;
		vertices = new float[initialVertexCapacity * vertexSize];
		numVertices = 0;
		
		currentVertexOffset = 0;
	}
	
	private void ensureCapacity(int verticesToAdd) {
		int minCapacity = (numVertices + verticesToAdd) * vertexSize;
		
		if (minCapacity > vertices.length) {
			// Double the capacity
			vertices = Arrays.copyOf(vertices, vertices.length * 2);
		}
	}
	
	public void addVertex(float x, float y, float z) {
		ensureCapacity(1);
		
		currentVertexOffset = getSize();
		vertices[currentVertexOffset + LOCATION_X] = x;
		vertices[currentVertexOffset + LOCATION_Y] = y;
		vertices[currentVertexOffset + LOCATION_Z] = z;
		
		numVertices++;
	}
	
	public void setExtraVec4(int location, Vec4f v) {
		setExtraVertexVec4(location, v.x, v.y, v.z, v.w);
	}
	
	public void setExtraVertexVec4(int location, float a, float b, float c, float d) {
		setExtraVertexData(location + 0, a);
		setExtraVertexData(location + 1, b);
		setExtraVertexData(location + 2, c);
		setExtraVertexData(location + 3, d);
	}

	public void setExtraVec3(int location, Vec3f v) {
		setExtraVertexVec3(location, v.x, v.y, v.z);
	}

	public void setExtraVertexVec3(int location, float a, float b, float c) {
		setExtraVertexData(location + 0, a);
		setExtraVertexData(location + 1, b);
		setExtraVertexData(location + 2, c);
	}

	public void setExtraVec2(int location, Vec2f v) {
		setExtraVertexVec2(location, v.x, v.y);
	}
	
	public void setExtraVertexVec2(int location, float a, float b) {
		setExtraVertexData(location + 0, a);
		setExtraVertexData(location + 1, b);
	}
	
	public void setExtraVertexData(int location, float v) {
		location += POSITION_SIZE;
		
		if (location >= vertexSize)
			throw new IndexOutOfBoundsException("Location out of bounds " + location);
		vertices[currentVertexOffset + location] = v;
	}
	
	public IVertexProvider getVertexProvider(Shape3D shape) {
		return new FloatArrayVertexProvider(getVertexData(), vertexSize, shape);
	}
	
	public float[] getVertexData() {
		return getVertexData(new float[getSize()], 0);
	}
	
	public float[] getVertexData(float[] buffer, int offset) {
		System.arraycopy(vertices, 0, buffer, offset, getSize());
		return buffer;
	}
	
	public int getSize() {
		return numVertices * vertexSize;
	}
	
	public int getNumVertices() {
		return numVertices;
	}
	
	public int getVertexSize() {
		return vertexSize;
	}
	
	public void clear() {
		numVertices = 0;
		currentVertexOffset = 0;
	}
}
