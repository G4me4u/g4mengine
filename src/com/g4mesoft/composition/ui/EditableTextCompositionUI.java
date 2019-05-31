package com.g4mesoft.composition.ui;

import java.awt.Rectangle;

import com.g4mesoft.graphic.IRenderingContext2D;

public abstract class EditableTextCompositionUI extends TextCompositionUI {

	public abstract Rectangle modelToView(IRenderingContext2D context, int location);

	public abstract int viewToModel(IRenderingContext2D context, int x, int y);

}
