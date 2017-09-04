package com.g4mesoft.graphic;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Map;

import com.g4mesoft.util.FileUtil;

public final class DisplayConfig {

	public static final String DEFAULT_TITLE = "My Title";
	public static final int DEFAULT_WIDTH = 400;
	public static final int DEFAULT_HEIGHT = 400;
	public static final boolean DEFAULT_RESIZABLE = true;
	public static final boolean DEFAULT_CENTERED = true;
	public static final DisplayMode DEFAULT_DISPLAY_MODE = DisplayMode.NORMAL;
	
	public static final DisplayConfig DEFAULT_DISPLAY_CONFIG = new DisplayConfig(	
			DEFAULT_TITLE, 
			DEFAULT_WIDTH, 
			DEFAULT_HEIGHT, 
			DEFAULT_RESIZABLE, 
			DEFAULT_CENTERED, 
			DEFAULT_DISPLAY_MODE
	);
	
	public final String title;
	public final int preferredWidth;
	public final int preferredHeight;
	public final boolean resizable;
	public final boolean centered;
	public final DisplayMode displayMode;

	public DisplayConfig(String title, int preferredWidth, int preferredHeight, 
			boolean resizable, boolean centered, DisplayMode displayMode) {
		this.title = title;
		this.preferredWidth = preferredWidth;
		this.preferredHeight = preferredHeight;
		this.resizable = resizable;
		this.centered = centered;
		this.displayMode = displayMode == null ? DisplayMode.NORMAL : displayMode;
	}
	
	public static DisplayConfig loadConfigReader(File configFile) throws IOException {
		if (configFile == null)
			throw new NullPointerException("configFile is null!");
		if (!configFile.isFile())
			throw new RuntimeException("configFile either doesn't exist or is not a file!");
		return loadConfigFile(new FileReader(configFile));
	}

	public static DisplayConfig loadConfigFile(Reader configReader) throws IOException {
		if (configReader == null)
			throw new NullPointerException("configReader is null!");

		Map<String, String> configurations = FileUtil.readConfigFile(configReader, "=");

		String title = getString(configurations, "title", DEFAULT_TITLE);
		int preferredWidth = getInt(configurations, "preferredWidth", DEFAULT_WIDTH);
		int preferredHeight = getInt(configurations, "preferredHeight", DEFAULT_HEIGHT);
		boolean resizable = getBoolean(configurations, "resizable", DEFAULT_RESIZABLE);
		boolean centered = getBoolean(configurations, "centered", DEFAULT_CENTERED);
		DisplayMode displayMode = getDisplayMode(configurations, "displayMode", DEFAULT_DISPLAY_MODE);
		
		return new DisplayConfig(title, preferredWidth, preferredHeight, resizable, centered, displayMode);
	}
	
	private static String getString(Map<String, String> configurations, String key, String defaultValue) {
		String value = configurations.get(key);
		return value != null ? value : defaultValue;
	}
	
	private static int getInt(Map<String, String> configurations, String key, int defaultValue) {
		String value = configurations.get(key);
		if (value != null) {
			try {
				return Integer.parseInt(value);
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
		}
		return defaultValue;
	}

	private static boolean getBoolean(Map<String, String> configurations, String key, boolean defaultValue) {
		String value = configurations.get(key);
		if (value != null) {
			if (value.equalsIgnoreCase("false")) return false;
			if (value.equalsIgnoreCase("true")) return true;
		}
		return defaultValue;
	}
	
	private static DisplayMode getDisplayMode(Map<String, String> configurations, String key, DisplayMode defaultValue) {
		String value = configurations.get(key);
		if (value != null) {
			DisplayMode result = DisplayMode.parse(value);
			if (result != null) return result;
		}
		return defaultValue;
	}
	
	public enum DisplayMode {
		NORMAL(0, "normal"),
		FULLSCREEN(1, "fullscreen"),
		FULLSCREEN_WINDOWED(2, "fullscreen_windowed");
		
		private final int index;
		private final String name;
		
		private DisplayMode(int index, String name) {
			this.index = index;
			this.name = name;
		}
		
		public static DisplayMode parse(String name) {
			if (name == null) return null;
			
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
}
