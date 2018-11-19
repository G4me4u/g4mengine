package com.g4mesoft.graphic;

import java.awt.Color;
import java.awt.Graphics;

public interface IRenderer2D extends IRenderingContext2D {

	public boolean start(Graphics g);

	public void stop();

	public Graphics getGraphics();
	
	public void resetTransformations();
	
	public void clear();

	public void setColor(Color color);

	public void drawGrid(int x, int y, int xc, int yc, int gw, int gh);

	public void drawRect(int x, int y, int width, int height);
	
	public void fillRect(int x, int y, int width, int height);
	
	public void drawLine(int x0, int y0, int x1, int y1);

	public void drawString(String str, int x, int y);
	
	public void setOffsetX(int ox);
	
	public void setOffsetY(int oy);

	public void setOffset(int ox, int oy);
	
	public void translateX(int tx);

	public void translateY(int ty);

	public void translate(int tx, int ty);
	
}
