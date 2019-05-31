package com.g4mesoft.composition.text.editable;

public interface ITextModel {

	public void addTextModelListener(ITextModelListener textModelListener);

	public void removeTextModelListener(ITextModelListener textModelListener);
	
	public int getLength();
	
	public void appendText(String text);
	
	public void insertText(int offset, String text);

	public void insertChars(int offset, int count, char[] buffer, int bufferOffset);
	
	public void insertChar(int offset, char c);
	
	public void removeText(int offset, int count);
	
	public String getText(int offset, int count);

	public void getChars(int offset, int count, char[] buffer, int bufferOffset);

	public char getChar(int offset);

}
