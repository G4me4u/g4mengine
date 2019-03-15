package com.g4mesoft.graphic;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Map;

import com.g4mesoft.Application;
import com.g4mesoft.util.FileUtil;

public final class DisplayConfig {

	public static final String NO_ICON_PATH = "none";
	
	public static final String DEFAULT_TITLE = "My Title";
	public static final int DEFAULT_WIDTH = 400;
	public static final int DEFAULT_HEIGHT = 400;
	public static final int DEFAULT_MINIMUM_WIDTH = 200;
	public static final int DEFAULT_MINIMUM_HEIGHT = 200;
	public static final boolean DEFAULT_RESIZABLE = true;
	public static final boolean DEFAULT_CENTERED = true;
	public static final DisplayMode DEFAULT_DISPLAY_MODE = DisplayMode.NORMAL;
	public static final boolean DEFAULT_DISPLAY_VISIBLE = true;
	public static final String DEFAULT_ICON_PATH = NO_ICON_PATH;
	
	public static final DisplayConfig DEFAULT_DISPLAY_CONFIG = new DisplayConfig(	
			DEFAULT_TITLE, 
			DEFAULT_WIDTH, 
			DEFAULT_HEIGHT, 
			DEFAULT_MINIMUM_WIDTH,
			DEFAULT_MINIMUM_HEIGHT,
			DEFAULT_RESIZABLE, 
			DEFAULT_CENTERED, 
			DEFAULT_DISPLAY_MODE,
			DEFAULT_DISPLAY_VISIBLE,
			DEFAULT_ICON_PATH
	);
	
	public static final DisplayConfig INVISIBLE_DISPLAY_CONFIG = new DisplayConfig(	
			DEFAULT_TITLE, 
			DEFAULT_WIDTH, 
			DEFAULT_HEIGHT, 
			DEFAULT_MINIMUM_WIDTH,
			DEFAULT_MINIMUM_HEIGHT,
			DEFAULT_RESIZABLE, 
			DEFAULT_CENTERED, 
			DEFAULT_DISPLAY_MODE,
			false,
			DEFAULT_ICON_PATH
	);
	
	public final String title;
	public final int preferredWidth;
	public final int preferredHeight;
	public final int minimumWidth;
	public final int minimumHeight;
	public final boolean resizable;
	public final boolean centered;
	public final DisplayMode displayMode;
	public final boolean displayVisible;
	public final String iconPath;
	
	public DisplayConfig(String title, int preferredWidth, int preferredHeight,int minimumWidth, int minimumHeight, 
	                     boolean resizable, boolean centered, DisplayMode displayMode, boolean displayVisible, String iconPath) {
		this.title = title;
		this.preferredWidth = preferredWidth;
		this.preferredHeight = preferredHeight;
		this.minimumWidth = minimumWidth;
		this.minimumHeight = minimumHeight;
		this.resizable = resizable;
		this.centered = centered;
		this.displayMode = displayMode == null ? DisplayMode.NORMAL : displayMode;
		this.displayVisible = displayVisible;
		this.iconPath = iconPath;
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
		int minimumWidth = getInt(configurations, "minimumWidth", DEFAULT_MINIMUM_WIDTH);
		int minimumHeight = getInt(configurations, "minimumHeight", DEFAULT_MINIMUM_HEIGHT);
		boolean resizable = getBoolean(configurations, "resizable", DEFAULT_RESIZABLE);
		boolean centered = getBoolean(configurations, "centered", DEFAULT_CENTERED);
		DisplayMode displayMode = getDisplayMode(configurations, "displayMode", DEFAULT_DISPLAY_MODE);
		boolean displayVisible = getBoolean(configurations, "displayVisible", DEFAULT_DISPLAY_VISIBLE);
		String iconPath = getString(configurations, "icon", DEFAULT_ICON_PATH);
		
		return new DisplayConfig(title, preferredWidth, preferredHeight, minimumWidth, minimumHeight,
		                         resizable, centered, displayMode, displayVisible, iconPath);
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
				Application.errorOccurred(e);
			}
		}
		return defaultValue;
	}

	private static boolean getBoolean(Map<String, String> configurations, String key, boolean defaultValue) {
		String value = configurations.get(key);
		if (value != null) {
			if (value.equalsIgnoreCase("false"))
				return false;
			if (value.equalsIgnoreCase("true"))
				return true;
		}
		
		return defaultValue;
	}
	
	private static DisplayMode getDisplayMode(Map<String, String> configurations, String key, DisplayMode defaultValue) {
		String value = configurations.get(key);
		if (value != null) {
			DisplayMode result = DisplayMode.parse(value);
			if (result != null)
				return result;
		}
		return defaultValue;
	}
}
