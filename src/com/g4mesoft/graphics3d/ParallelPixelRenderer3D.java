package com.g4mesoft.graphics3d;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import com.g4mesoft.graphic.IViewport;

public class ParallelPixelRenderer3D extends AbstractPixelRenderer3D {

	private static final long MAX_SHUTDOWN_WAIT = 5000L;
	
	private int numThreads;
	private ExecutorService workerService;
	
	private boolean rendererDisposed;
	
	private final List<Future<?>> activeTasks;
	
	public ParallelPixelRenderer3D(IViewport viewport, int width, int height, int numThreads) {
		super(viewport, width, height);

		this.numThreads = numThreads;
		workerService = Executors.newFixedThreadPool(numThreads);
	
		rendererDisposed = false;
		
		activeTasks = new LinkedList<Future<?>>();
	}

	private void finishTasks() {
		// Wait for tasks to complete
		for (Future<?> task : activeTasks) {
			try {
				task.get();
			} catch (InterruptedException | ExecutionException e) {
			}
		}
	}

	@Override
	public void drawVertices(Vertex3D[] vertices, int offset, int length) {
		shader.prepareShader();
		
		VertexDrawingJob drawingJob = new VertexDrawingJob(vertices, offset, length);

		for (int i = 0; i < numThreads; i++)
			activeTasks.add(workerService.submit(new VertexWorkerTask(drawingJob, i * 3)));
	
		finishTasks();
	}
	
	@Override
	public void dispose() {
		rendererDisposed = true;

		if (!rendererDisposed) {
			workerService.shutdownNow();
	
			try {
				workerService.awaitTermination(MAX_SHUTDOWN_WAIT, TimeUnit.MILLISECONDS);
			} catch (InterruptedException e) {
			}
	
			super.dispose();
		}
	}
	
	private class VertexWorkerTask implements Runnable {
		
		private final VertexDrawingJob drawingJob;
		private int vertexOffset;

		private final Queue<Triangle3D> trianglesToClip;
		private final Queue<Triangle3D> clippedTriangles;
		
		private final TriangleCache cache;
		
		public VertexWorkerTask(VertexDrawingJob drawingJob, int vertexOffset) {
			this.drawingJob = drawingJob;
			this.vertexOffset = vertexOffset;

			trianglesToClip = new LinkedList<Triangle3D>();
			clippedTriangles = new LinkedList<Triangle3D>();
		
			cache = new TriangleCache(shader.getOutputSize());
		}
		
		@Override
		public void run() {
			while (!rendererDisposed) {
				Triangle3D triangle = drawingJob.getTriangleToDraw(vertexOffset, cache);
				if (triangle == null)
					break;

				vertexOffset += numThreads * 3;
				
				if (cullEnabled && canPreCullTriangle(triangle)) {
					cache.storeTriangle(triangle);
					continue;
				}
				
				trianglesToClip.add(triangle);
				triangle = null;

				Queue<Triangle3D> trianglesToDraw = clipTriangle(cache, trianglesToClip, clippedTriangles);
				transformAndCullTriangles(trianglesToDraw, cache);

				if (!trianglesToDraw.isEmpty()) {
					renderTriangles(trianglesToDraw, cache);
					
					for (Triangle3D t : trianglesToDraw)
						cache.storeTriangle(t);
					trianglesToDraw.clear();
				}
			}
			
			cache.clear();
		}
	}
	
	private class VertexDrawingJob {
		
		private final Vertex3D[] vertices;

		private final int offset;
		private final int end;

		public VertexDrawingJob(Vertex3D[] vertices, int offset, int length) {
			if (length % 3 != 0)
				throw new IllegalArgumentException("Number of vertices is not a multiple of 3");

			if (offset < 0)
				throw new ArrayIndexOutOfBoundsException(offset);
			
			int end = offset + length;
			if (end > vertices.length)
				throw new ArrayIndexOutOfBoundsException(vertices.length);
			
			this.vertices = vertices;
			this.offset = offset;
			this.end = end;
		}
		
		protected Triangle3D getTriangleToDraw(int vertexOffset, TriangleCache cache) {
			// Returning null will cause the
			// workers to wait for another
			// drawing job.
			if (vertexOffset >= end)
				return null;

			int offset = this.offset + vertexOffset;
			Vertex3D v0 = vertices[offset + 0];
			Vertex3D v1 = vertices[offset + 1];
			Vertex3D v2 = vertices[offset + 2];

			Triangle3D triangle = cache.getTriangle();
			shader.projectVertices(triangle, v0, v1, v2);
			
			return triangle;
		}
	}
}
