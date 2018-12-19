package com.g4mesoft.graphic;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

import com.g4mesoft.graphic.filter.IPixelFilter;

public class PixelRenderer2D implements IRenderer2D {

	protected final IViewport viewport;
	
	protected final int width;
	protected final int height;

	private final BufferedImage screen;
	protected final int[] pixels;

	private Graphics g;

	protected int offsetX;
	protected int offsetY;

	protected int color;
	private GColor backdropColor;
	
	public PixelRenderer2D(IViewport viewport, int width, int height) {
		this.viewport = viewport;
		this.width = width;
		this.height = height;
		
		screen = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		pixels = ((DataBufferInt)screen.getRaster().getDataBuffer()).getData();

		g = null;
		
		offsetX = 0;
		offsetY = 0;
		
		color = 0;
		backdropColor = GColor.BLACK;
	}

	@Override
	public boolean start(Graphics g) {
		return (this.g = g) != null;
	}

	@Override
	public void stop() {
		int dw = viewport.getWidth();
		int dh = viewport.getHeight();
		
		int pixelDensity = Math.min(dw / width, dh / height); 
		if (pixelDensity <= 0)
			pixelDensity = 1;
		
		int w = width * pixelDensity;
		int h = height * pixelDensity;
		int x = viewport.getX() + (dw - w) / 2;
		int y = viewport.getY() + (dh - h) / 2;

		if (dw > w || dh > h) {
			g.setColor(backdropColor.toAWTColor());
			g.fillRect(0, 0, dw, dh);
		}
		g.drawImage(screen, x, y, w, h, null);
		
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
		for (int i = 0; i < pixels.length; i++)
			pixels[i] = color;
	}
	
	public void setPixel(int x, int y, int color) {
		if (isInBounds(x, y))
			pixels[x + y * width] = color;
	}
	
	public int getPixel(int x, int y) {
		return isInBounds(x, y) ? pixels[x + y * width] : -1;
	}

	public boolean isInBounds(int x, int y) {
		return x >= 0 && x < width && y >= 0 && y < height;
	}
	
	@Override
	public void drawGrid(int x, int y, int gw, int gh, int xc, int yc) {
		if (gw == 0 || gh == 0)
			return;

		int y1 = y + yc * gh;
		
		int xp = x;
		for (int i = 0; i <= xc; i++) {
			drawVerticalLine(xp, y, y1);
			xp += gw;
		}

		int x1 = x + xc * gw;

		int yp = y;
		for (int i = 0; i <= yc; i++) {
			drawHorizontalLine(yp, x, x1);
			yp += gh;
		}
	}
	
	@Override
	public void drawRect(int x, int y, int width, int height) {
		int x1 = x + width;
		int y1 = y + height;
		
		// Left Right Top Bottom 
		// (offset handled by drawLine)
		drawVerticalLine(x, y + 1, y1);
		drawVerticalLine(x1, y, y1 - 1);
		drawHorizontalLine(y, x, x1 - 1);
		drawHorizontalLine(y1, x + 1, x1);
	}
	
	@Override
	public void fillRect(int x, int y, int width, int height) {
		// Handle offset
		x += offsetX;
		y += offsetY;
		
		int x0 = x < 0 ? 0 : x;

		int x1 = x + width;
		if (x1 > this.width)
			x1 = this.width;
		
		int y1 = y + height;
		if (y1 > this.height)
			y1 = this.height;

		int yp = y < 0 ? 0 : y;

		// x1 and y1 are exclusive.
		for ( ; yp < y1; yp++) {
			int i = x0 + yp * this.width;
			for (int xp = x0; xp < x1; xp++)
				pixels[i++] = color;
		}
	}
	
	@Override
	public void drawLine(int x0, int y0, int x1, int y1) {
		int dx = x1 - x0;
		if (dx == 0) {
			// Offset handled by drawVerticalLine
			drawVerticalLine(x0, y0, y1);
			return;
		}

		int dy = y1 - y0;
		if (dy == 0) {
			// Offset handled by drawHorizontalLine
			drawHorizontalLine(y0, x0, x1);
			return;
		}
		
		// If the line is diagonal we
		// draw the line using Bresenham's 
		// line algorithm.
		
		int xi;
		if (dx < 0) {
			xi = -1;
			dx = -dx;
		} else xi = 1;

		int yi;
		if (dy < 0) {
			yi = -1;
			dy = -dy;
		} else yi = 1;
		
		// Handle offset
		int xp = x0 + offsetX;
		int yp = y0 + offsetY;
		if (dx > dy) {
			int d = 2 * dy - dx;
			for (int i = 0; i <= dx; i++) {
				setPixel(xp, yp, color);
				
				if (d > 0) {
					yp += yi;
					d -= 2 * dx;
				}
				d += 2 * dy;
				xp += xi;
			}
		} else {
			int d = 2 * dx - dy;
			for (int i = 0; i <= dy; i++) {
				setPixel(xp, yp, color);
				
				if (d > 0) {
					xp += xi;
					d -= 2 * dy;
				}
				d += 2 * dx;
				yp += yi;
			}
		}
	}
	
	public void drawVerticalLine(int x, int y0, int y1) {
		// Handle offset
		x += offsetX;
		if (x < 0 || x >= width)
			return;

		y0 += offsetY;
		y1 += offsetY;
		
		if (y0 > y1) {
			int tmp = y0;
			y0 = y1;
			y1 = tmp;
		}

		// Make sure our line is in bounds
		if (y0 >= height || y1 < 0)
			return;
		
		if (y1 >= height) 
			y1 = height - 1;
		if (y0 < 0) 
			y0 = 0;

		// Set pixels
		int i = x + y0 * width;
		for (int yp = y0; yp <= y1; yp++) {
			pixels[i] = color;
			i += width;
		}
	}

	public void drawHorizontalLine(int y, int x0, int x1) {
		// Handle offset
		y += offsetY;
		if (y < 0 || y >= height)
			return;

		x0 += offsetX;
		x1 += offsetX;
		
		if (x0 > x1) {
			int tmp = x0;
			x0 = x1;
			x1 = tmp;
		}
		
		// Make sure our line is in bounds
		if (x0 >= width || x1 < 0)
			return;
		
		if (x1 >= width) 
			x1 = width - 1;
		if (x0 < 0) 
			x0 = 0;
		
		// Set pixels
		int i = x0 + y * width;
		for (int xp = x0; xp <= x1; xp++)
			pixels[i++] = color;
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
	public void drawString(String str, int x, int y) {
		Graphics g = screen.createGraphics();
		g.setFont(g.getFont());
		g.setColor(new Color(color));
		g.drawString(str, x, y);
		g.dispose();
	}
	
	public void applyFilter(IPixelFilter filter) {
		filter.filterPixels(pixels, width, height);
	}
	
	@Override
	public void setColor(GColor color) {
		this.color = color.getRGB();
	}
	
	public void setBackdropColor(GColor backdropColor) {
		this.backdropColor = backdropColor;
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
		offsetX = ox;
		offsetY = oy;
	}

	@Override
	public void translateX(int tx) {
		offsetX += tx;
	}

	@Override
	public void translateY(int ty) {
		offsetY += ty;
	}

	@Override
	public void translate(int tx, int ty) {
		offsetX += tx;
		offsetY += ty;
	}

	@Override
	public int getX() {
		return 0;
	}

	@Override
	public int getY() {
		return 0;
	}
	
	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public int getHeight() {
		return height;
	}
}
