package com.g4mesoft.graphics3d;

public class FloatArrayVertexProvider implements IVertexProvider {

	private static final int MINIMUM_VERTEX_SIZE = 3;
	private static final int OFFSET_POS_X = 0;
	private static final int OFFSET_POS_Y = 1;
	private static final int OFFSET_POS_Z = 2;
	
	private final float[] buffer;
	private final int vertexSize;
	
	private final int bufferOffset;
	private final int numVertices;
	private final Shape3D shape;

	private boolean drawing;
	private int drawVertexOffset;
	private Vertex3D[] drawBuffer;
	
	public FloatArrayVertexProvider(float[] buffer, int vertexSize, Shape3D shape) {
		this(buffer, vertexSize, 0, buffer.length / vertexSize, shape);
	}
	
	public FloatArrayVertexProvider(float[] buffer, int vertexSize, int bufferOffset, int numVertices, Shape3D shape) {
		if (buffer == null)
			throw new NullPointerException("buffer == null");
		if (shape == null)
			throw new NullPointerException("shape == null");
		
		if (vertexSize < MINIMUM_VERTEX_SIZE)
			throw new IllegalArgumentException("Minimum vertex size is " + MINIMUM_VERTEX_SIZE);
		if ((numVertices % shape.getVerticesPerShape()) != 0)
			throw new IllegalArgumentException("numVertices is not a multiple of " + shape.getVerticesPerShape());
		if (bufferOffset < 0)
			throw new ArrayIndexOutOfBoundsException(bufferOffset);
		if (bufferOffset + numVertices * vertexSize > buffer.length)
			throw new ArrayIndexOutOfBoundsException(buffer.length);
	
		this.buffer = buffer;
		this.vertexSize = vertexSize;
		
		this.bufferOffset = bufferOffset;
		this.numVertices = numVertices;
		this.shape = shape;
		
		drawing = false;
		drawVertexOffset = 0;
		
		drawBuffer = new Vertex3D[shape.getVerticesPerShape()];
		for (int i = 0; i < drawBuffer.length; i++)
			drawBuffer[i] = new Vertex3D(vertexSize - MINIMUM_VERTEX_SIZE);
	}

	@Override
	public void prepareDraw() {
		if (drawing)
			throw new IllegalStateException("Already drawing!");
		
		if (numVertices > 0) {
			drawVertexOffset = 0;
			drawing = true;
		}
	}

	@Override
	public boolean hasNext() {
		return drawing && drawVertexOffset < numVertices;
	}

	@Override
	public Vertex3D getNextVertex() {
		if (!drawing)
			throw new IllegalStateException("Not drawing!");
		
		int drawBufferOffset = drawVertexOffset % shape.getVerticesPerShape();
		Vertex3D vert = drawBuffer[drawBufferOffset];
		
		int offset = drawVertexOffset * vertexSize + this.bufferOffset;
		float x = buffer[offset + OFFSET_POS_X];
		float y = buffer[offset + OFFSET_POS_Y];
		float z = buffer[offset + OFFSET_POS_Z];
		vert.pos.set(x, y, z, 1.0f);
		
		for (int i = MINIMUM_VERTEX_SIZE; i < vertexSize; i++)
			vert.storeFloat(i - MINIMUM_VERTEX_SIZE, buffer[offset + i]);
		
		drawVertexOffset++;
		if (drawVertexOffset >= numVertices)
			drawing = false;
		
		return vert;
	}

	@Override
	public Shape3D getShape() {
		return shape;
	}
}
