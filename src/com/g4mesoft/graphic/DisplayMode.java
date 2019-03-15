package com.g4mesoft.graphic;

public enum DisplayMode {
	
	NORMAL(0, "normal"),
	FULLSCREEN(1, "fullscreen"),
	FULLSCREEN_WINDOWED(2, "fullscreen_windowed"),
	FULLSCREEN_BORDERLESS(3, "fullscreen_borderless");
	
	private final int index;
	private final String name;
	
	private DisplayMode(int index, String name) {
		this.index = index;
		this.name = name;
	}
	
	public static DisplayMode parse(String name) {
		if (name == null)
			return null;
		
		for (DisplayMode displayMode : values()) {
			if (name.equalsIgnoreCase(displayMode.name))
				return displayMode;
		}
		
		return null;
	}
	
	public int getIndex() {
		return index;
	}
	
	public String getName() {
		return name;
	}
}
