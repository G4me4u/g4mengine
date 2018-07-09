package com.g4mesoft.graphic;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;

public interface Renderer2D {

	public boolean start(BufferStrategy bs);

	public void stop();
	
	public Graphics getGraphics();
	
	public void drawGrid(int x, int y, int gw, int gh, int xc, int yc);

	public void drawRect(int x, int y, int width, int height);
	
	public void fillRect(int x, int y, int width, int height);
	
	public void drawLine(int x0, int y0, int x1, int y1);
	
	public void setColor(Color color);
}
