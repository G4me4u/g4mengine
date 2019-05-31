package com.g4mesoft.composition.text.editable;

import com.g4mesoft.graphic.IRenderer2D;

public interface ITextCaret {

	public void install(EditableTextComposition textComposition);

	public void uninstall(EditableTextComposition textComposition);

	public void update();

	public void render(IRenderer2D renderer, float dt);

	public void setCaretLocation(int location);
	
	public void setCaretDot(int dot);
	
	public void setCaretMark(int mark);
	
	public int getCaretLocation();
	
	public int getCaretDot();

	public int getCaretMark();
	
	public boolean hasCaretSelection();
	
	public void setBlinkRate(int blinkRate);
	
	public int getBlinkRate();
	
}
