package com.g4mesoft.graphics3d;

public class BasicVertexProvider implements IVertexProvider {

	private final Vertex3D[] vertices;
	private final int offset;
	private final int numVertices;
	private final Shape3D shape;
	
	private boolean drawing;
	private int drawVertexOffset;
	
	public BasicVertexProvider(Vertex3D[] vertices, Shape3D shape) {
		this(vertices, 0, vertices.length, shape);
	}

	public BasicVertexProvider(Vertex3D[] vertices, int offset, int numVertices, Shape3D shape) {
		if (vertices == null)
			throw new NullPointerException("vertices == null");
		if (shape == null)
			throw new NullPointerException("shape == null");
		
		if ((numVertices % shape.getVerticesPerShape()) != 0)
			throw new IllegalArgumentException("numVertices is not a multiple of " + shape.getVerticesPerShape());
		if (offset < 0)
			throw new ArrayIndexOutOfBoundsException(offset);
		if (offset + numVertices > vertices.length)
			throw new ArrayIndexOutOfBoundsException(vertices.length);
		
		this.vertices = vertices;
		this.offset = offset;
		this.numVertices = numVertices;
		this.shape = shape;
		
		drawing = false;
		drawVertexOffset = 0;
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

		Vertex3D vert = vertices[offset + drawVertexOffset];
		
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
