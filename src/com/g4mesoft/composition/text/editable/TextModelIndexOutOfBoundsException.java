package com.g4mesoft.composition.text.editable;

public class TextModelIndexOutOfBoundsException extends IndexOutOfBoundsException {
	private static final long serialVersionUID = -8048127724794232190L;

	public TextModelIndexOutOfBoundsException() {
		super();
	}

	public TextModelIndexOutOfBoundsException(String msg) {
		super(msg);
	}
	
	public TextModelIndexOutOfBoundsException(int index) {
		super("TextModel index out of bounds: " + index);
	}
}
