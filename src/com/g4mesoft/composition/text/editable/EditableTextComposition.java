package com.g4mesoft.composition.text.editable;

import java.util.ArrayList;
import java.util.List;

import com.g4mesoft.composition.text.TextComposition;
import com.g4mesoft.composition.ui.EditableTextCompositionUI;
import com.g4mesoft.graphic.GColor;

public abstract class EditableTextComposition extends TextComposition implements ITextModelListener {

	private ITextModel textModel;
	private final List<ICompositionModelListener> modelListeners;

	private boolean editable;
	private GColor caretColor;
	private ITextCaret caret;
	
	private GColor selectionTextColor;
	private GColor selectionBackgroundColor;
	
	public EditableTextComposition() {
		textModel = createDefaultModel();
		if (textModel == null)
			throw new NullPointerException("Default text model is null");

		modelListeners = new ArrayList<ICompositionModelListener>();
		
		editable = true;
		caret = null;

		textModel.addTextModelListener(this);
	}
	
	@Override
	public EditableTextCompositionUI getUI() {
		return (EditableTextCompositionUI)super.getUI();
	}
	
	public void addModelListener(ICompositionModelListener listener) {
		modelListeners.add(listener);
	}

	public void removeModelListener(ICompositionModelListener listener) {
		modelListeners.remove(listener);
	}
	
	protected abstract ITextModel createDefaultModel();
	
	public void setTextModel(ITextModel textModel) {
		if (textModel == null)
			throw new IllegalArgumentException("Text model can not be null!");
		
		this.textModel.removeTextModelListener(this);
		this.textModel = textModel;
		textModel.addTextModelListener(this);
		invokeModelChangeEvent();
		
		requestRelayout(true);
	}
	
	private void invokeModelChangeEvent() {
		for (ICompositionModelListener listener : modelListeners)
			listener.modelChanged(this);
	}
	
	public ITextModel getTextModel() {
		return textModel;
	}
	
	public void setCaret(ITextCaret caret) {
		if (this.caret != null)
			this.caret.uninstall(this);
	
		this.caret = caret;
		
		if (caret != null)
			caret.install(this);
	}
	
	public ITextCaret getCaret() {
		return caret;
	}
	
	public void setCaretColor(GColor caretColor) {
		this.caretColor = caretColor;
	}
	
	public GColor getCaretColor() {
		return caretColor == null ? getTextColor() : caretColor;
	}
	
	public void setSelectionTextColor(GColor selectionTextColor) {
		this.selectionTextColor = selectionTextColor;
	}

	public GColor getSelectionTextColor() {
		return selectionTextColor;
	}
	
	public void setSelectionBackgroundColor(GColor selectionBackgroundColor) {
		this.selectionBackgroundColor = selectionBackgroundColor;
	}

	public GColor getSelectionBackgroundColor() {
		return selectionBackgroundColor;
	}
	
	public void setEditable(boolean editable) {
		this.editable = editable;
	}
	
	public boolean isEditable() {
		return editable;
	}

	@Override
	public void textInserted(ITextModel model, int offset, int count) {
		requestRelayout(true);
	}

	@Override
	public void textRemoved(ITextModel model, int offset, int count) {
		requestRelayout(true);
	}
}
