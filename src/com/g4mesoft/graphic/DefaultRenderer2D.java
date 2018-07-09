package com.g4mesoft.graphic;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;

public class DefaultRenderer2D implements Renderer2D {

	private final Display display;

	private Graphics g;
	
	public DefaultRenderer2D(Display display) {
		this.display = display;
	}
	
	public void clear(Color color) {
		g.setColor(color);
		g.fillRect(0, 0, display.getWidth(), display.getHeight());
	}
	
	@Override
	public boolean start(BufferStrategy bs) {
		g = bs.getDrawGraphics();
		return g != null;
	}

	@Override
	public void stop() {
		g.dispose();
		g = null;
	}
	
	@Override
	public Graphics getGraphics() {
		return g;
	}

	@Override
	public void drawGrid(int x, int y, int gw, int gh, int xc, int yc) {
		if (gw == 0) return;
		if (gh == 0) return;

		if (gw < 0) {
			x += gw * xc;
			gw = -gw;
		}
		if (gh < 0) {
			y += gh * yc;
			gh = -gh;
		}
		
		if (x < 0 || 
			y < 0 || 
			x >= display.getWidth() ||
			y >= display.getHeight()) return;
		
		int x1 = x + gw * xc;
		int y1 = y + gh * yc;
		for (int xl = x; xl <= x1; xl += gw) {
			if (x < 0) break;
			if (x >= display.getWidth()) break;
			
			drawLine(xl, y, xl, y1);
		}

		for (int yl = y; yl <= y1; yl += gh) {
			if (y < 0) break;
			if (y >= display.getHeight()) break;
			
			drawLine(x, yl, x1, yl);
		}
	}
	
	@Override
	public void drawRect(int x, int y, int width, int height) {
		int x1 = x + width;
		int y1 = y + height;
		
		drawLine(x, y, x1, y);
		drawLine(x, y1, x1, y1);
		drawLine(x, y, x, y1);
		drawLine(x1, y, x1, y1);
	}
	
	@Override
	public void fillRect(int x, int y, int width, int height) {
		g.fillRect(x, y, width, height);
	}
	
	@Override
	public void drawLine(int x0, int y0, int x1, int y1) {
		g.drawLine(x0, y0, x1, y1);
	}

	@Override
	public void setColor(Color color) {
		g.setColor(color);
	}
}
