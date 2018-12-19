package com.g4mesoft.graphic;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;

public class DefaultRenderer2D implements IRenderer2D {

	private static Font defaultFont = null;
	
	private final IViewport viewport;

	private int offsetX;
	private int offsetY;
	private Graphics g;
	
	public DefaultRenderer2D(IViewport viewport) {
		this.viewport = viewport;
	
		if (defaultFont == null)
			defaultFont = new Font("Segoe UI Light", Font.PLAIN, 28);
	}
	
	public void clear(GColor color) {
		g.setColor(color.toAWTColor());
		g.fillRect(0, 0, getWidth(), getHeight());
	}
	
	@Override
	public boolean start(Graphics g) {
		this.g = g;
		
		g.setFont(defaultFont);
		((Graphics2D)g).setRenderingHint(
				RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);
		return g != null;
	}

	@Override
	public void stop() {
		g = null;

		resetTransformations();
	}
	
	@Override
	public boolean isRendering() {
		return g != null;
	}
	
	@Override
	public Graphics getGraphics() {
		return g;
	}

	@Override
	public void resetTransformations() {
		offsetX = 0;
		offsetY = 0;
	}
	
	@Override
	public void clear() {
		g.fillRect(0, 0, getWidth(), getHeight());
	}

	@Override
	public void setColor(GColor color) {
		g.setColor(color.toAWTColor());
	}
	
	@Override
	public void drawGrid(int x, int y, int gw, int gh, int xc, int yc) {
		if (gw == 0 || gh == 0) 
			return;

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
		
		int width = getWidth();
		int height = getHeight();
		
		if (x >= width ||
			y >= height) return;
		
		int x1 = x + gw * xc;
		int y1 = y + gh * yc;
		for (int xl = x; xl <= x1; xl += gw) {
			if (xl < 0) continue;
			if (xl >= width) break;
			
			g.drawLine(xl, y, xl, y1);
		}

		for (int yl = y; yl <= y1; yl += gh) {
			if (yl < 0) continue;
			if (yl >= height) break;
			
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
		
		if (x + width < 0 || x >= getWidth())
			return;
		if (y + height < 0 || y >= getHeight())
			return;
		
		g.fillRect(x, y, width, height);
	}
	
	@Override
	public void drawLine(int x0, int y0, int x1, int y1) {
		x0 += offsetX;
		y0 += offsetY;
		x1 += offsetX;
		y1 += offsetY;
		
		int w = getWidth();
		if ((x0 < 0 && x1 < 0) || (x0 >= w && x1 >= w))
			return;
		int h = getHeight();
		if ((y0 < 0 && y1 < 0) || (y0 >= h && y1 >= h))
			return;
		
		g.drawLine(x0, y0, x1, y1);
	}
	
	@Override
	public void drawString(String str, int x, int y) {
		x += offsetX;
		y += offsetY;
		
		g.drawString(str, x, y);
	}
	
	@Override
	public int getCharWidth(char c) {
		return g.getFontMetrics().charWidth(c);
	}
	
	@Override
	public int getFontHeight() {
		return g.getFontMetrics().getHeight();
	}

	@Override
	public int getStringWidth(String str) {
		return g.getFontMetrics().stringWidth(str);
	}
	
	@Override
	public Rectangle2D getStringBounds(String str) {
		return g.getFontMetrics().getStringBounds(str, g);
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
	
	@Override
	public int getX() {
		return viewport.getX();
	}

	@Override
	public int getY() {
		return viewport.getY();
	}
	
	@Override
	public int getWidth() {
		return viewport.getWidth();
	}
	
	@Override
	public int getHeight() {
		return viewport.getHeight();
	}
}
