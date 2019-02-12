package com.g4mesoft.graphic;

import java.awt.geom.Rectangle2D;

public interface IRenderingContext2D extends IViewport {

	public boolean isRendering();

	public int getCharWidth(char c);

	public int getFontHeight();

	public int getStringWidth(String str);
	
	public Rectangle2D getStringBounds(String str);
	
	public void dispose();
	
}
