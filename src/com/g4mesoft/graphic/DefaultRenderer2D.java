package com.g4mesoft.graphic;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;

public class DefaultRenderer2D implements IRenderer2D {

	private final Display display;

	private int offsetX;
	private int offsetY;
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

		offsetX = 0;
		offsetY = 0;
	}
	
	@Override
	public Graphics getGraphics() {
		return g;
	}

	@Override
	public void clear() {
		g.fillRect(0, 0, display.getWidth(), display.getHeight());
	}
	
	@Override
	public void drawGrid(int x, int y, int gw, int gh, int xc, int yc) {
		if (gw == 0) return;
		if (gh == 0) return;

		x += offsetX;
		y += offsetY;
		
		if (gw < 0) {
			x += gw * xc;
			gw = -gw;
		}
		if (gh < 0) {
			y += gh * yc;
			gh = -gh;
		}
		
		if (x >= display.getWidth() ||
			y >= display.getHeight()) return;
		
		int x1 = x + gw * xc;
		int y1 = y + gh * yc;
		for (int xl = x; xl <= x1; xl += gw) {
			if (xl < 0) continue;
			if (xl >= display.getWidth()) break;
			
			g.drawLine(xl, y, xl, y1);
		}

		for (int yl = y; yl <= y1; yl += gh) {
			if (yl < 0) continue;
			if (yl >= display.getHeight()) break;
			
			g.drawLine(x, yl, x1, yl);
		}
	}
	
	@Override
	public void drawRect(int x, int y, int width, int height) {
		int x1 = x + width;
		int y1 = y + height;
		
		// Offset handled by drawLine
		drawLine(x, y, x1, y);
		drawLine(x, y1, x1, y1);
		drawLine(x, y, x, y1);
		drawLine(x1, y, x1, y1);
	}
	
	@Override
	public void fillRect(int x, int y, int width, int height) {
		x += offsetX;
		y += offsetY;
		
		if (x + width < 0 || x >= display.getWidth())
			return;
		if (y + height < 0 || y >= display.getHeight())
			return;
		
		g.fillRect(x, y, width, height);
	}
	
	@Override
	public void drawLine(int x0, int y0, int x1, int y1) {
		x0 += offsetX;
		y0 += offsetY;
		x1 += offsetX;
		y1 += offsetY;
		
		int w = display.getWidth();
		if ((x0 < 0 && x1 < 0) || (x0 >= w && x1 >= w))
			return;
		int h = display.getHeight();
		if ((y0 < 0 && y1 < 0) || (y0 >= h && y1 >= h))
			return;
		
		g.drawLine(x0, y0, x1, y1);
	}

	@Override
	public void setColor(Color color) {
		g.setColor(color);
	}

	@Override
	public void setOffsetX(int ox) {
		offsetX = ox;
	}

	@Override
	public void setOffsetY(int oy) {
		offsetY = oy;
	}

	@Override
	public void setOffset(int ox, int oy) {
		setOffsetX(ox);
		setOffsetY(oy);
	}
	
	@Override
	public void translateX(int tx) {
		setOffsetX(offsetX + tx);
	}

	@Override
	public void translateY(int ty) {
		setOffsetY(offsetY + ty);
	}

	@Override
	public void translate(int tx, int ty) {
		setOffset(offsetX + tx, offsetY + ty);
	}
}
