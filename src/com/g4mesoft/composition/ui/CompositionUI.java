package com.g4mesoft.composition.ui;

import com.g4mesoft.composition.Composition;
import com.g4mesoft.graphic.IRenderer2D;
import com.g4mesoft.graphic.IRenderingContext2D;
import com.g4mesoft.math.Vec2i;

public abstract class CompositionUI {

	public abstract void bindUI(Composition composition);

	public abstract void unbindUI(Composition composition);
	
	public abstract void update();

	public abstract void render(IRenderer2D renderer, float dt);
	
	public abstract Vec2i getPreferredSize(IRenderingContext2D context);
}
