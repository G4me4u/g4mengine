package com.g4mesoft.graphic;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;

public interface IRenderer2D {

	public boolean start(BufferStrategy bs);

	public void stop();
	
	public boolean isRendering();

	public Graphics getGraphics();
	
	public void clear();

	public void setColor(Color color);

	public void drawGrid(int x, int y, int xc, int yc, int gw, int gh);

	public void drawRect(int x, int y, int width, int height);
	
	public void fillRect(int x, int y, int width, int height);
	
	public void drawLine(int x0, int y0, int x1, int y1);

	public void setOffsetX(int ox);
	
	public void setOffsetY(int oy);

	public void setOffset(int ox, int oy);
	
	public void translateX(int tx);

	public void translateY(int ty);

	public void translate(int tx, int ty);
	
	public int getWidth();
	
	public int getHeight();
}
