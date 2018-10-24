package com.g4mesoft.graphic;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import com.g4mesoft.graphic.DisplayConfig.DisplayMode;

public class Display {

	private final Object renderingLock = new Object();
	
	private final DisplayConfig displayConfig;
	private final IExitable exitable;

	private JFrame frame;
	private DisplayCanvas canvas;
	
	private boolean fullscreen;
	private boolean windowed;
	private IRenderer2D renderer;
	private BufferStrategy bs;
	
	private boolean rendering;
	
	public Display(IExitable exitable) { 
		this(null, exitable); 
	}
	
	public Display(InputStream configInputStream, IExitable exitable) {
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
		if (!displayConfig.displayVisible)
			return;
		
		frame = new JFrame(displayConfig.title);
		frame.setResizable(displayConfig.resizable);

		canvas = new DisplayCanvas();
		canvas.setPreferredSize(new Dimension(
				displayConfig.preferredWidth, 
				displayConfig.preferredHeight
		));
		canvas.setMinimumSize(new Dimension(
				displayConfig.minimumWidth,
				displayConfig.minimumHeight
		));
		
		frame.setLayout(new BorderLayout());
		frame.add(canvas, BorderLayout.CENTER);
		frame.pack();
		
		// A bug with minimum size in JFrame (and java.awt.Canvas). 
		// It has to be set by the user. Too bad... The following
		// is a hack (we know the minimum size wont change)
		frame.setMinimumSize(canvas.getMinimumSize());

		if (displayConfig.centered)
			frame.setLocationRelativeTo(null);
		
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				Display.this.dispose();
			}
		});
		
		// Set frame icon, if it exists
		if (!DisplayConfig.NO_ICON_PATH.equals(displayConfig.iconPath)) {
			BufferedImage icon = null;
			try {
				icon = ImageIO.read(Display.class.getResource(displayConfig.iconPath));
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			if (icon != null)
				frame.setIconImage(icon);
		}
		
		frame.setVisible(true);
		
		fullscreen = false;
		if (displayConfig.displayMode != DisplayMode.NORMAL) {
			toggleFullscreen(displayConfig.displayMode == DisplayMode.FULLSCREEN_WINDOWED);
		}

		renderer = new DefaultRenderer2D(this);
		
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
	
	public IRenderer2D startRendering() {
		if (frame == null || !frame.isShowing()) return null;
		
		synchronized (renderingLock) {
			if (rendering)
				throw new IllegalStateException("Already started rendering!");
			
			bs = canvas.getBufferStrategy();
			if (bs == null) {
				// We have to make sure the canvas
				// is displayable. It could have been
				// disposed or removed before this call.
				if (canvas.isDisplayable())
					canvas.createBufferStrategy(3);
				return null;
			}
			
			if (renderer == null || !renderer.start(bs))
				return null;
			rendering = true;
			return renderer;
		}
	}
	
	public void stopRendering() {
		synchronized (renderingLock) {
			if (!rendering)
				throw new IllegalStateException("Already stopped rendering!");
			
			renderer.stop();
			
			// We have to make sure the canvas 
			// is displayable before showing the 
			// next frame. (The bufferstrategy
			// could have been invalidated).
			if (canvas.isDisplayable())
				bs.show();
			
			rendering = false;
		}
	}
	
	public boolean isRendering() {
		return rendering;
	}
	
	public void setRenderer(IRenderer2D renderer) {
		if (renderer == null)
			throw new IllegalArgumentException("Renderer is null!");
	
		synchronized (renderingLock) {
			if (rendering)
				throw new IllegalStateException("Cannot change renderer if display is already rendering!");
			this.renderer = renderer;
		}
	}
	
	public IRenderer2D getRenderer() {
		return rendering ? renderer : null;
	}
	
	public boolean isFullscreen() {
		return fullscreen;
	}
	
	public boolean isWindowed() {
		return (!fullscreen) || (fullscreen && windowed);
	}

	private void dispose() {
		if (frame != null) {
			frame.setVisible(false);
			frame.dispose();
		}
		
		frame = null;
		canvas = null;
		
		if (exitable != null)
			exitable.exit();
	}
	
	public int getWidth() {
		return canvas == null ? 0 : canvas.getWidth();
	}
	
	public int getHeight() {
		return canvas == null ? 0 : canvas.getHeight();
	}

	public void registerKeyListener(KeyListener keyListener) {
		if (canvas == null) return;
			
		// Make sure the keyListener doesn't exist
		for (KeyListener listener : canvas.getKeyListeners()) {
			if (listener.equals(keyListener)) {
				canvas.removeKeyListener(listener);
				break;
			}
		}
		
		canvas.addKeyListener(keyListener);
	}
	
	public Canvas getCanvas() {
		return canvas;
	}

	public boolean isVisible() {
		if (frame != null)
			return frame.isVisible();
		return displayConfig.displayVisible;
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
