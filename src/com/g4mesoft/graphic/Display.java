package com.g4mesoft.graphic;

import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import javax.swing.JFrame;

import com.g4mesoft.graphic.DisplayConfig.DisplayMode;

public class Display {

	private final DisplayConfig displayConfig;
	private final Exitable exitable;

	private JFrame frame;
	private DisplayCanvas canvas;
	
	private boolean fullscreen;
	private boolean windowed;
	private Renderer2D renderer;
	
	private boolean rendering;
	
	public Display(Exitable exitable) { 
		this(null, exitable); 
	}
	
	public Display(InputStream configInputStream, Exitable exitable) {
		DisplayConfig displayConfig = null;
		
		if (configInputStream != null) {
			try {
				displayConfig = DisplayConfig.loadConfigFile(
						new InputStreamReader(configInputStream, StandardCharsets.UTF_8));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		if (displayConfig == null)
			displayConfig = DisplayConfig.DEFAULT_DISPLAY_CONFIG;
	
		this.displayConfig = displayConfig;
		this.exitable = exitable;
		
		initDisplay();
	}
	
	private void initDisplay() {
		frame = new JFrame(displayConfig.title);
		frame.setResizable(displayConfig.resizable);

		canvas = new DisplayCanvas();
		canvas.setPreferredSize(new Dimension(
				displayConfig.preferredWidth, 
				displayConfig.preferredHeight
		));
		
		frame.add(canvas);
		frame.pack();

		if (displayConfig.centered)
			frame.setLocationRelativeTo(null);
		
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				Display.this.dispose();
			}
		});
		
		frame.setVisible(true);
		
		fullscreen = false;
		if (displayConfig.displayMode != DisplayMode.NORMAL) {
			toggleFullscreen(displayConfig.displayMode == DisplayMode.FULLSCREEN_WINDOWED);
		}

		renderer = new Renderer2D(this);
		
		canvas.requestFocus();
	}
	
	private void toggleFullscreen(boolean windowed) {
		if (frame == null) return;
		
		fullscreen = !fullscreen;

		if (fullscreen) {
			this.windowed = windowed;
			if (this.windowed) {
				frame.setResizable(false);
				frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
			} else {
				GraphicsDevice device = getGraphicsDevice();
				if (device != null)
					device.setFullScreenWindow(frame);
			}
		} else {
			if (this.windowed) {
				frame.setExtendedState(JFrame.NORMAL);
				frame.setResizable(displayConfig.resizable);
			} else {
				GraphicsDevice device = getGraphicsDevice();
				if (device != null)
					device.setFullScreenWindow(null);
			}
		}
	}
	
	private GraphicsDevice getGraphicsDevice() {
		return GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()[0];
	}
	
	public Renderer2D startRendering() {
		if (frame == null || !frame.isShowing()) return null;
		
		BufferStrategy bs = canvas.getBufferStrategy();
		if (bs == null) {
			canvas.createBufferStrategy(3);
			return null;
		}
		
		Renderer2D renderer = initRenderer(bs);
		rendering = (renderer != null);
		return renderer;
	}
	
	private Renderer2D initRenderer(BufferStrategy bs) {
		if (bs == null) return null;
		return renderer.start(bs);
	}
	
	public void stopRendering() {
		if (!rendering)
			throw new IllegalStateException("Already stopped rendering!");
		
		BufferStrategy bs = renderer.stop();
		bs.show();
		
		rendering = false;
	}
	
	public boolean isRendering() {
		return rendering;
	}
	
	public Renderer2D getRenderer() {
		return rendering ? renderer : null;
	}
	
	public boolean isFullscreen() {
		return fullscreen;
	}
	
	public boolean isWindowed() {
		return (!fullscreen) || (fullscreen && windowed);
	}

	private void dispose() {
		frame.setVisible(false);
		frame.dispose();
		
		frame = null;
		canvas = null;
		
		exitable.exit();
	}
	
	public int getWidth() {
		return canvas.getWidth();
	}
	
	public int getHeight() {
		return canvas.getHeight();
	}

	public void registerKeyListener(KeyListener keyListener) {
		// Make sure the keyListener doesn't exist
		for (KeyListener listener : canvas.getKeyListeners()) {
			if (listener.equals(keyListener)) {
				canvas.removeKeyListener(listener);
				break;
			}
		}
		
		canvas.addKeyListener(keyListener);
	}
	
	@SuppressWarnings("serial")
	private static class DisplayCanvas extends Canvas {

		/*
		 * The super implementation of the following
		 * functions can lower performance on repaint, 
		 * and are not needed for this application.
		 */
		
		@Override
		public void paint(Graphics g) { }

		@Override
		public void update(Graphics g) { }
		
		@Override
		public void repaint(long tm, int x, int y, int width, int height) { }
	}
}
