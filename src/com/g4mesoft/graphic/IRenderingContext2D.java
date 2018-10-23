package com.g4mesoft.graphic;

import java.awt.geom.Rectangle2D;

public interface IRenderingContext2D {

	public boolean isRendering();

	public int getStringWidth(String str);
	
	public Rectangle2D getStringBounds(String str);
	
	public int getWidth();
	
	public int getHeight();
	
}
