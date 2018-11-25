package com.g4mesoft;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import com.g4mesoft.composition.Composition;
import com.g4mesoft.graphic.Display;
import com.g4mesoft.graphic.IExitable;
import com.g4mesoft.graphic.IRenderer2D;
import com.g4mesoft.input.key.KeyInputListener;
import com.g4mesoft.input.mouse.MouseInputListener;

public abstract class Application implements IExitable {

	private static final boolean DEFAULT_DEBUG = true;
	private static final float DEFAULT_TPS = 20.0f;
	private static final float DEFAULT_MIN_FPS = 60.0f;

	private static final String DISPLAY_CONFIG_LOCATION = "/config/display.txt";
	
	private Display display;
	
	private Composition composition;
	
	private boolean running;
	private Timer timer;
	private float minimumFps;
	
	private long ticksPassed;
	
	/*
	 * Width and height values of the renderer
	 * in the previous frame. Used to know when
	 * The renderer viewport has been resized. 
	 */
	private int oldRendererWidth;
	private int oldRendererHeight;
	
	/*
	 * Width and height values of the display
	 * in the previous frame.
	 */
	private int oldDisplayWidth;
	private int oldDisplayHeight;
	
	private KeyInputListener keyListener;
	private MouseInputListener mouseListener;

	private final String displayConfig;
	
	protected Application(String displayConfig) {
		this.displayConfig = displayConfig;
	}
	
	protected Application() {
		this(DISPLAY_CONFIG_LOCATION);
	}

// Abstract functions //

	/**
	 * When this function is overridden, it should be in
	 * charge of ticking/updating necessary world objects,
	 * inputs or doing other necessary calculations. 
	 * <br>
	 * This function should not be used for drawing, as there
	 * is another method, {@code render(IRenderer2D, float)}
	 * used for handling that. 
	 * <br><br>
	 * <b>NOTE:</b> this function will always be called with
	 * approximately the same time-interval (if the system isn't 
	 * overloaded). If you want to change the interval between 
	 * each tick, call {@code setTps(float)} with the desired 
	 * amount of ticks per second.
	 * 
	 * @see #setTps(float)
	 * @see #render(IRenderer2D, float)
	 */
	protected abstract void tick();

	/**
	 * When this function is overridden, it should be in
	 * charge of rendering to the display through the
	 * Renderer2D object.<br>
	 * The dt time constant can be used for 
	 * smoother drawing of moving objects. 
	 * <br><br>
	 * <b>Example <i>(no casting handled)</i>:</b>
	 * <pre>
	 * int xPixel = prevX + (x - prevX) * dt;
	 * int yPixel = prevY + (y - prevY) * dt;
	 * </pre>
	 * Where {@code prevX} and {@code prevY} represent the
	 * coordinates of an object or a point (in the previous 
	 * tick) to be drawn with a smooth motion onto the 
	 * display. The {@code x} and {@code y} coordinates are
	 * the current location of the object or point.
	 * 
	 * @param renderer	-	A renderer used for drawing to
	 * 						the display.
	 * @param dt		-	A constant representing how much 
	 * 						time has passed since previous 
	 * 						tick (0.0 - 1.0).
	 * 
	 * @see #setMinimumFps(float)
	 * @see #tick()
	 */
	protected abstract void render(IRenderer2D renderer, float dt);
	
// Overridable functions //
	
	/**
	 * This function should be overridden by sub-classes and
	 * used for general startup calls. This function will be
	 * called before {@code init()}.
	 * <br><br>
	 * <b>NOTE:</b><i> any sub-classes overriding this function should
	 * call {@code super.start()} to make sure the application starts 
	 * executing properly. Make sure to place that line of code at the
	 * end of the sub-implementation of this function, as this function 
	 * will cause the execution to pause until the game has stopped.</i>
	 * 
	 * @deprecated Use {@link #start(String[], Class)} instead.
	 */
	protected void start() {
		// Running has to be set to true
		// before calling #init(). This is
		// done to make sure we're able
		// to call exit or stopRunning
		// before or during initialization.
		running = true;
		
		init();
		startLoop();
		stop();
		
		System.exit(0);
	}
	
	/**
	 * This function should be overridden by sub-classes and
	 * used for general initialization. This implementation of
	 * the function will intialize the main display, the timer
	 * etc. If one wishes to enable or disable user input, this
	 * can be achieved by invoking the methods {@link #enableKeyInput()}
	 * or {@link #enableMouseInput()}. User input will be enabled
	 * by default and can be disabled using {@link #disableKeyInput()}
	 * or {@link #disableMouseInput()}.
	 * <br><br>
	 * <b>NOTE:</b><i> any sub-classes overriding this function should
	 * call {@code super.init()} to make sure the application starts 
	 * executing properly.</i>
	 */
	protected void init() {
		display = new Display(Application.class.getResourceAsStream(displayConfig));

		composition = null;
		
		timer = new Timer(DEFAULT_TPS, DEFAULT_DEBUG);
		minimumFps = DEFAULT_MIN_FPS;
		
		ticksPassed = 0;
	
		oldRendererWidth = 0;
		oldRendererHeight = 0;
	
		oldDisplayWidth = 0;
		oldDisplayHeight = 0;

		keyListener = null;
		mouseListener = null;

		enableKeyInput();
		enableMouseInput();
	}
	
	protected void stop() { }
	
// Runtime functions //
	
	/**
	 * Starts the main game loop. This function will use a timer
	 * for timing the ticks, and sleep x amount of time depending
	 * on the minimumFps field.
	 * 
	 * @see com.g4mesoft.Timer Timer
	 */
	private void startLoop() {
		timer.initTimer();
		while (running) {
			if (display.isCloseRequested()) {
				display.dispose();
				exit();
				// Continue to stop loop
				continue;
			}
			
			timer.update();
			int missingTicks = timer.getMissingTicks();
			for (int i = 0; i < missingTicks; i++) {
				update();
				timer.tickPassed();
			}

			if (display.isVisible()) {
				draw(timer.getDeltaTick());
				timer.framePassed();
			}

			timer.sleep(minimumFps);
		}
	}

	/**
	 * Sets up the update before calling the overridden
	 * tick function. This function is in charge of 
	 * updating important features such as the root ui
	 * composition. 
	 * 
	 * @see #tick()
	 * @see #render(IRenderer2D, float)
	 */
	private void update() {
		if (composition != null && composition.isValid())
			composition.update();
		
		tick();
		
		// The key- and mouse-input should
		// be updated after every tick.
		if (keyListener != null)
			keyListener.updateKeys();
		if (mouseListener != null)
			mouseListener.updateMouseButtons();
		
		ticksPassed++;
	}
	
	/**
	 * Sets up the drawing before calling the overridden
	 * render function. This will start and stop rendering
	 * automatically and check for changes in display and
	 * renderer viewport sizes. If a viewport has changed
	 * size, the functions {@link #displayResized(int, int)}
	 * and {@link #rendererResized(int, int)} will be
	 * invoked accordingly.
	 * 
	 * @param dt	-	A constant representing how much 
	 * 					time has passed since previous tick.
	 * 
	 * @see #render(IRenderer2D, float) 
	 * @see #tick()
	 */
	private void draw(float dt) {
		// Test if display has changed size.
		// This is useful, if the application
		// wants to change renderer before
		// starting the actual rendering.
		int displayWidth = display.getWidth();
		int displayHeight = display.getHeight();
		if (oldDisplayWidth != displayWidth || oldDisplayHeight != displayHeight) {
			// Display changed size
			displayResized(displayWidth, displayHeight);
			
			oldDisplayWidth = displayWidth;
			oldDisplayHeight = displayHeight;
		}
		
		IRenderer2D renderer = display.startRendering();
		if (renderer == null) 
			return;

		render(renderer, dt);
		
		int width = renderer.getWidth();
		int height = renderer.getHeight();
		if (oldRendererWidth != width || oldRendererHeight != height) {
			// The renderer has resized the viewport
			rendererResized(width, height);
			
			oldRendererWidth = width;
			oldRendererHeight = height;
		}

		if (composition != null) {
			if (composition.isRelayoutRequired())
				composition.layout(renderer);

			// Transformations may have changed
			// when calling #render(IRenderer2D, float)
			renderer.resetTransformations();
			
			composition.render(renderer, dt);
		}
		
		display.stopRendering();
	}
	
	/**
	 * Invoked during rendering, if the viewport size of the current 
	 * {@link com.g4mesoft.graphic.IRenderingContext2D IRenderingContext2D}
	 * has changed within the last frame.
	 * <br><br>
	 * <b>NOTE:</b><i> any sub-classes overriding this function should
	 * call {@code super.rendererResized(int, int)} to make sure the 
	 * application handles context resizing correctly. If one wants to
	 * change the renderer of the display, override and use the method
	 * {@link #displayResized(int, int)} instead.</i>
	 * 
	 * @param newWidth - The new width of the context viewport
	 * @param newHeight - The new height of the context viewport
	 * 
	 * @see #displayResized(int, int)
	 */
	protected void rendererResized(int newWidth, int newHeight) {
		if (composition != null) {
			// Invalidate composition. The resizing
			// will be handled by Composition#layout()
			composition.invalidate();
		}
	}

	/**
	 * Invoked during rendering, if the display viewport size has been
	 * changed. Such event could be caused by the user resizing the
	 * frame or when the display size was changed by the application.
	 * 
	 * <b>NOTE:</b><i> This function is called before rendering has been
	 * started, which makes it possible to change the renderer of the
	 * current display directly from this method.</i>
	 * 
	 * @param newWidth - The new width of the display.
	 * @param newHeight - The new height of the display.
	 * 
	 * @see #rendererResized(int, int)
	 */
	protected void displayResized(int newWidth, int newHeight) {
	}
	
	/**
	 * Enables the KeyInputListener to listen for key events on the 
	 * current display. Calling this function will replace the manual 
	 * work having to update the keys in the static class called
	 * {@link com.g4mesoft.input.key.KeyInputListener KeyInputListener}.
	 * The following code snippet could replace a call to this function:
	 * <pre>
	 * // Somewhere during initialization:
	 * KeyInputListener.getInstance().registerDisplay(getDisplay());
	 * ...
	 * // After every tick
	 * KeyInputListener.getInstance().updateKeys();
	 * </pre>
	 * The above code snippet can seem confusing at first, but is actually
	 * quite simple. Firstly, the KeyInputListener is registered to the
	 * display owned by this application. Since the KeyInputListener
	 * supports various key-input features (such as key-clicked events)
	 * the keys have to be updated after every tick has passed. This small 
	 * detail is very important for the functionality of the key-listener. 
	 * <br><br>
	 * <b>NOTE: </b><i>To simplify programs, it is advised that this 
	 * function is to be used instead of the above code snippet.<i>
	 * 
	 * @see #disableKeyInput()
	 * @see com.g4mesoft.input.key.KeyInputListener
	 */
	public void enableKeyInput() {
		keyListener = KeyInputListener.getInstance();
		keyListener.registerDisplay(display);
	}
	
	/**
	 * Disables the {@code KeyInputListener}. If the listener was not 
	 * enabled prior to calling this function, no action will occur. 
	 * If one wishes to re-enable to key-listener after disabling it, 
	 * it can be done by using the {@link #enableKeyInput()} function.
	 * 
	 * @see #enableKeyInput()
	 */
	public void disableKeyInput() {
		if (keyListener != null) {
			keyListener.unregisterDisplay(display);
			keyListener = null;
		}
	}

	/**
	 * Enables the {@link com.g4mesoft.input.mouse.MouseInputListener
	 * MouseInputListener} by registering it to the display owned by
	 * this application. Enabling the mouse input listener using this
	 * function will replace the manual work of having to call the
	 * {@link MouseInputListener#updateMouseButtons()} function after 
	 * every update in the {@link #tick()} method. For the sake of 
	 * simplicity it is adviced to use this function instead of calling 
	 * {@code MouseInputListener.registerDisplay(getDisplay())} manually.
	 * 
	 * @see #disableMouseInput()
	 */
	public void enableMouseInput() {
		mouseListener = MouseInputListener.getInstance();
		mouseListener.registerDisplay(display);
	}
	
	/**
	 * Disables the {@code MouseInputListener}. If the listener was not
	 * enabled prior to calling this function, no action will occur.
	 * If one wishes to re-enable the mouse-listener after disabling it,
	 * it can be achieved by using the {@link #enableMouseInput()} function.
	 * 
	 * @see #enableMouseInput()
	 */
	public void disableMouseInput() {
		if (mouseListener != null) {
			mouseListener.unregisterDisplay(display);
			mouseListener = null;
		}
	}
	
// Getter functions //
	
	/**
	 * @return The main display object
	 */
	public Display getDisplay() {
		return display;
	}
	
	/**
	 * @return The root composition of the application
	 */
	public Composition getRootComposition() {
		return composition;
	}
	
	/**
	 * @return The amount of ticks passed since
	 *         the application was initialized.
	 */
	public long getTicksPassed() {
		return ticksPassed;
	}
	
// Setter functions //
	
	/**
	 * Sets the root composition of this application. The root
	 * composition is the lowest ui element in the ui hierarchy.
	 * It should be noted, that the root composition is drawn on
	 * top of the application. In other words, the render function
	 * {@link #render(IRenderer2D, float)} is invoked before the
	 * {@link Composition#render(IRenderer2D, float)} function.
	 * Setting the root composition to null will be the equivalent
	 * to removing it from the application. By default, the root 
	 * composition is null. The root composition can be changed at
	 * any given time, and will be revalidated, when the function
	 * {@link #draw(float)} is called. - if the renderer size changes
	 * or the root composition was invalidated by another mean.
	 * 
	 * @param composition - The new root composition or null, if 
	 *                      the root composition should be removed.
	 * 
	 * @see #getRootComposition()
	 */
	public void setRootComposition(Composition composition) {
		if (composition != null && (composition.getParent() != null || composition.isValid()))
			throw new IllegalArgumentException("Composition is already bound to a parent!");
		
		if (this.composition != null)
			this.composition.invalidate();
		this.composition = composition;
	}
	
	/**
	 * Sets the tps in the timer used for ticking and
	 * drawing. Changing this constant will instantly
	 * change the speed, at which the game will run.
	 * <br>
	 * Calling this function will only directly change
	 * the ticks per second. Whether or not this function
	 * changes frames per second is unknown.
	 *  
	 * @param tps	-	The new constant used for timing
	 * 					the ticks per second.
	 * 
	 * @see #tick()
	 * @see #setMinimumFps(float)
	 */
	public void setTps(float tps) {
		timer.setTps(tps);
	}
	
	/**
	 * Sets the minimumFps in the timer used for ticking
	 * and drawing. If this constant is lower than the tps,
	 * the timer will not allow for such a low frame-rate,
	 * and compensate, to make sure the ticks per second is
	 * met.
	 * 
	 * @param minimumFps	-	The minimum frames per second.
	 * 
	 * @see #render(IRenderer2D, float)
	 * @see #setTps(float)
	 */
	public void setMinimumFps(float minimumFps) {
		this.minimumFps = minimumFps;
	}
	
	/**
	 * Sets the debugging state of the timer. Setting debug
	 * to false will disable tps and fps logging.
	 * 
	 * @param debug 	-	The new debug state
	 * 
	 * @see com.g4mesoft.Timer
	 */
	public void setDebug(boolean debug) {
		timer.setDebug(debug);
	}
	
	/**
	 * Sets running to false. The thread will terminate
	 * if the application is running at the time of the
	 * call to this method.
	 * 
	 * @see #start()
	 * @see #exit()
	 */
	public void stopRunning() {
		running = false;
	}
	
// Interface functions //
	
	/**
	 * Sets running to false. This will stop the main game
	 * loop and stop execution.
	 */
	@Override
	public void exit() {
		running = false;
	}
	
// Static functions //
	
	/**
	 * Constructs and starts the given application class. This
	 * method is the safest way to start an application, and will
	 * be compatible with future all versions of the engine. 
	 * Alternatively one could initialize and start the application 
	 * by doing the following:
	 * <pre>
	 * Application app = new YourApplication();
	 * app.start();
	 * </pre>
	 * Although the above code snippet may work in most situations, 
	 * it is not guaranteed to work reliably in future versions and
	 * is therefore deprecated. It is advised that this implementation 
	 * is to be used instead.
	 * <br><br>
	 * <b>NOTE:</b><i>The provided Application class implementation must
	 * contain a public empty default constructor, for the application to 
	 * be able to start. If the application is unable to find such 
	 * constructor an exception is thrown.</i>
	 * 
	 * @param args - The JVM arguments parsed from the main method.
	 * @param appClazz - The class of the application to be started.
	 * 
	 * @throws NoSuchMethodException If the default empty constructor was
	 *                               not found or is unavailable.
	 * @throws InstantiationException If the method was unable to construct
	 *                                a new instance of the application.
	 */
	public static void start(String[] args, Class<? extends Application> appClazz) throws NoSuchMethodException, InstantiationException {
		Constructor<?> defaultConstructor = null;
		try {
			defaultConstructor = appClazz.getDeclaredConstructor(new Class<?>[0]);
		} catch (NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
		
		if (defaultConstructor == null)
			throw new NoSuchMethodException("Unable to find default constructor");
			
		Application app = null;
		try {
			app = (Application)defaultConstructor.newInstance(new Object[0]);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		
		app.start();
	}
}
