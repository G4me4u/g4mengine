package com.g4mesoft.composition.text.editable;

import com.g4mesoft.composition.ui.TextFieldCompositionUI;

public class TextFieldComposition extends EditableTextComposition {

	public TextFieldComposition() {
		this(null);
	}
	
	public TextFieldComposition(String text) {
		if (text != null) {
			ITextModel textModel = getTextModel();
			textModel.insertText(0, text);
		}

		// Set UI
		setUI(new TextFieldCompositionUI());
	}
	
	public void setUI(TextFieldCompositionUI ui) {
		super.setUI(ui);
	}
	
	@Override
	public TextFieldCompositionUI getUI() {
		return (TextFieldCompositionUI)super.getUI();
	}
	
	public void setText(String text) {
		ITextModel textModel = getTextModel();
		if (textModel.getLength() != 0)
			textModel.removeText(0, textModel.getLength());
		if (text != null && !text.isEmpty())
			textModel.insertText(0, text);
	}
	
	public void appendText(String text) {
		if (text == null || text.isEmpty())
			return;

		getTextModel().appendText(text);
	}
	
	public String getText() {
		ITextModel textModel = getTextModel();
		return textModel.getText(0, textModel.getLength());
	}

	@Override
	protected ITextModel createDefaultModel() {
		return new SingleLineTextModel();
	}
}
