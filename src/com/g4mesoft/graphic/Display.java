package com.g4mesoft.graphic;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import com.g4mesoft.Application;

public class Display implements IViewport {

	private final DisplayConfig displayConfig;

	private JFrame frame;
	private DisplayCanvas canvas;
	
	private DisplayMode displayMode;
	private Point oldFrameLocation;
	private Dimension oldFrameSize;
	
	private boolean focused;
	
	private IRenderer2D renderer;
	private BufferStrategy bs;
	
	/*
	 * The currently active graphics object.
	 * If the display is not currently rendering
	 * the graphics object is null.
	 */
	private Graphics g;
	
	private boolean rendering;
	
	private boolean closeRequested;
	
	private Cursor emptyCursor;
	private Cursor prevCursor;
	
	public Display() { 
		this(DisplayConfig.DEFAULT_DISPLAY_CONFIG); 
	}
	
	public Display(DisplayConfig config) {
		if (config == null)
			throw new IllegalArgumentException("Display config must not be null!");
		
		displayConfig = config;
		
		initDisplay();
	}
	
	public static DisplayConfig readDisplayConfig(InputStream is) throws IOException {
		if (is == null)
			return DisplayConfig.DEFAULT_DISPLAY_CONFIG;
		
		try (Reader reader = new InputStreamReader(is, StandardCharsets.UTF_8)) {
			return DisplayConfig.loadConfigFile(reader);
		}
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
		
		closeRequested = false;
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				closeRequested = true;
			}
		});
		
		canvas.addFocusListener(new FocusListener() {
			@Override
			public void focusLost(FocusEvent e) {
				focused = false;
			}
			
			@Override
			public void focusGained(FocusEvent e) {
				focused = true;
			}
		});
		
		// Set frame icon, if it exists
		if (!DisplayConfig.NO_ICON_PATH.equals(displayConfig.iconPath)) {
			URL iconUrl = Display.class.getResource(displayConfig.iconPath);
			if (iconUrl != null) {
				BufferedImage icon = null;
				try {
					icon = ImageIO.read(iconUrl);
				} catch (IOException e) {
					Application.errorOccurred(e);
				}

				if (icon != null)
					setFrameIcon(icon);
			}
		}
		
		frame.setVisible(true);
		
		displayMode = DisplayMode.NORMAL;
		setDisplayMode(displayConfig.displayMode);

		renderer = new DefaultRenderer2D(this);
		
		canvas.requestFocus();
	}
	
	public void setFrameIcon(Image icon) {
		frame.setIconImage(icon);
		
		try {
			Class<?> appClazz = Class.forName("com.apple.eawt.Application");
			Object app = appClazz.getMethod("getApplication").invoke(null);
			appClazz.getMethod("setDockIconImage", Image.class).invoke(app, icon);
		} catch (Exception e1) {
			// We're not on mac
		}
	}
	
	public void setDisplayMode(DisplayMode displayMode) {
		if (frame == null || this.displayMode == displayMode) 
			return;

		// Disable fullscreen
		if (this.displayMode == DisplayMode.FULLSCREEN) {
			GraphicsDevice device = getGraphicsDevice();
			if (device != null)
				device.setFullScreenWindow(null);
		}
		
		boolean disableExtended = false;
		
		// Disable borderless
		if (this.displayMode == DisplayMode.FULLSCREEN_BORDERLESS) {
			frame.dispose();
			frame.setUndecorated(false);
			frame.setVisible(true);

			if (displayMode == DisplayMode.FULLSCREEN_WINDOWED) {
				// We're already in the correct
				// display state.
				this.displayMode = displayMode;
				return;
			}
			
			disableExtended = true;
		}
		
		// Disable fullscreen windowed
		if (this.displayMode == DisplayMode.FULLSCREEN_WINDOWED || disableExtended) {
			frame.setExtendedState(JFrame.NORMAL);
			frame.setResizable(displayConfig.resizable);
			
			if (oldFrameSize != null)
				frame.setSize(oldFrameSize);
			if (oldFrameLocation != null)
				frame.setLocation(oldFrameLocation);
		}
		
		if (displayMode != DisplayMode.NORMAL) {
			oldFrameLocation = frame.getLocation();
			oldFrameSize = frame.getSize();

			if (displayMode == DisplayMode.FULLSCREEN) {
				GraphicsDevice device = getGraphicsDevice();
				if (device != null)
					device.setFullScreenWindow(frame);
			} else {
				frame.setResizable(false);
				frame.setExtendedState(JFrame.MAXIMIZED_BOTH);

				if (displayMode == DisplayMode.FULLSCREEN_BORDERLESS) {
					frame.dispose();
					frame.setUndecorated(true);
					frame.setVisible(true);
				}
			}
		}
		
		this.displayMode = displayMode;
		
		if (canvas != null)
			canvas.requestFocus();
	}
	
	public DisplayMode getDisplayMode() {
		return displayMode;
	}
	
	private GraphicsDevice getGraphicsDevice() {
		return GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()[0];
	}
	
	public IRenderer2D startRendering() {
		if (frame == null || !frame.isShowing()) 
			return null;
		
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
		
		g = bs.getDrawGraphics();
		if (g == null || renderer == null)
			return null;
		
		if (!renderer.start(g))
			return null;
		
		rendering = true;
		return renderer;
	}
	
	public void stopRendering() {
		if (!rendering)
			throw new IllegalStateException("Already stopped rendering!");
		
		renderer.stop();
		
		g.dispose();
		g = null;
		
		// We have to make sure the canvas 
		// is displayable before showing the 
		// next frame. (The bufferstrategy
		// could have been invalidated).
		if (canvas.isDisplayable())
			bs.show();
		
		rendering = false;
	}
	
	public boolean isRendering() {
		return rendering;
	}
	
	public void setRenderer(IRenderer2D renderer) {
		if (renderer == null)
			throw new NullPointerException("Renderer is null!");
	
		if (rendering)
			throw new IllegalStateException("Cannot change renderer if display is already rendering!");
		
		if (this.renderer != null)
			this.renderer.dispose();
		
		this.renderer = renderer;
	}
	
	public IRenderer2D getRenderer() {
		return renderer;
	}
	
	public boolean isFullscreen() {
		return displayMode != DisplayMode.NORMAL;
	}
	
	public boolean isWindowed() {
		if (!isFullscreen())
			return true;
		
		return displayMode == DisplayMode.FULLSCREEN_WINDOWED;
	}

	public boolean isBorderless() {
		return displayMode == DisplayMode.FULLSCREEN_BORDERLESS;
	}
	
	public boolean isCloseRequested() {
		return closeRequested;
	}

	public void dispose() {
		if (frame != null) {
			frame.setVisible(false);
			frame.dispose();
		}
		
		frame = null;
		canvas = null;
		
		if (renderer != null) {
			renderer.dispose();
			renderer = null;
		}
	}
	
	@Override
	public int getX() {
		return 0;
	}

	@Override
	public int getY() {
		return 0;
	}
	
	@Override
	public int getWidth() {
		return canvas == null ? 0 : canvas.getWidth();
	}
	
	@Override
	public int getHeight() {
		return canvas == null ? 0 : canvas.getHeight();
	}

	public void registerKeyListener(KeyListener keyListener) {
		if (canvas == null || keyListener == null)
			return;
			
		// Make sure the keyListener doesn't exist
		for (KeyListener listener : canvas.getKeyListeners()) {
			if (listener.equals(keyListener)) {
				canvas.removeKeyListener(listener);
				break;
			}
		}
		
		canvas.addKeyListener(keyListener);
	}

	public void unregisterKeyListener(KeyListener keyListener) {
		canvas.removeKeyListener(keyListener);
	}

	public void registerMouseListener(MouseListener mouseListener) {
		if (canvas == null || mouseListener == null) 
			return;
		
		// Make sure the mouseListener doesn't exist
		for (MouseListener listener : canvas.getMouseListeners()) {
			if (listener.equals(mouseListener)) {
				canvas.removeMouseListener(listener);
				break;
			}
		}
		
		canvas.addMouseListener(mouseListener);
	}

	public void unregisterMouseListener(MouseListener mouseListener) {
		canvas.removeMouseListener(mouseListener);
	}

	public void registerMouseMotionListener(MouseMotionListener mouseMotionListener) {
		if (canvas == null || mouseMotionListener == null)
			return;
		
		// Make sure the mouseMotionListener doesn't exist
		for (MouseMotionListener listener : canvas.getMouseMotionListeners()) {
			if (listener.equals(mouseMotionListener)) {
				canvas.removeMouseMotionListener(listener);
				break;
			}
		}
		
		canvas.addMouseMotionListener(mouseMotionListener);
	}
	
	public void unregisterMouseMotionListener(MouseMotionListener mouseMotionListener) {
		canvas.removeMouseMotionListener(mouseMotionListener);
	}

	public Canvas getCanvas() {
		return canvas;
	}

	public boolean isVisible() {
		if (frame != null)
			return frame.isVisible();
		return displayConfig.displayVisible;
	}

	public boolean isFocused() {
		return focused;
	}

	public void enableCursor() {
		if (prevCursor != null) {
			canvas.setActualCursor(prevCursor);
			prevCursor = null;
		}
	}

	public void disableCursor() {
		if (emptyCursor == null) {
			BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
			emptyCursor = frame.getToolkit().createCustomCursor(img, new Point(), null);
		}

		if (prevCursor == null) {
			prevCursor = canvas.getCursor();
			canvas.setActualCursor(emptyCursor);
		}
	}
	
	@SuppressWarnings("serial")
	private class DisplayCanvas extends Canvas {

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
	
		@Override
		public void setCursor(Cursor cursor) {
			if (Display.this.prevCursor == null) {
				setActualCursor(cursor);
				return;
			}
			
			Display.this.prevCursor = cursor;
		}
		
		private void setActualCursor(Cursor cursor) {
			super.setCursor(cursor);
		}
	}
}
