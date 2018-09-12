package com.g4mesoft;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import com.g4mesoft.graphic.Display;
import com.g4mesoft.graphic.IExitable;
import com.g4mesoft.graphic.IRenderer2D;

public abstract class Application implements IExitable {

	private static final boolean DEFAULT_DEBUG = true;
	private static final float DEFAULT_TPS = 20.0f;
	private static final float DEFAULT_MIN_FPS = 60.0f;

	private static final String DISPLAY_CONFIG_LOCATION = "/config/display.txt";
	
	private Display display;
	
	private boolean running;
	private Timer timer;
	private float minimumFps;

// Abstract functions //
	
	private final String displayConfig;
	
	protected Application(String displayConfig) {
		this.displayConfig = displayConfig;
	}
	
	protected Application() {
		this(DISPLAY_CONFIG_LOCATION);
	}

	/**
	 * When this function is overridden, it should be in
	 * charge of ticking/updating necessary world objects,
	 * inputs or doing other necessary calculations. 
	 * <br>
	 * This function should not be used for drawing, as there
	 * is another method, {@code render(Renderer2D, float)}
	 * used for handling that. 
	 * <br><br>
	 * <b>NOTE:</b> this function will always be called with
	 * approximately the same interval (if the system isn't 
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
	 */
	protected void start() {
		// Running has to be set to true
		// before calling init. This is
		// done to make sure we're able
		// to call exit or stopRunning
		// before the during init.
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
	 * etc. 
	 * <br><br>
	 * <b>NOTE:</b><i> any sub-classes overriding this function should
	 * call {@code super.init()} to make sure the application starts 
	 * executing properly.</i>
	 */
	protected void init() {
		display = new Display(Application.class.getResourceAsStream(displayConfig), this);

		timer = new Timer(DEFAULT_TPS, DEFAULT_DEBUG);
		minimumFps = DEFAULT_MIN_FPS;
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
			timer.update();
			int missingTicks = timer.getMissingTicks();
			for (int i = 0; i < missingTicks; i++) {
				tick();
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
	 * Sets up the drawing before calling the overridden
	 * render function. This will start and stop rendering
	 * automatically.
	 * 
	 * @param dt	-	A constant representing how much 
	 * 					time has passed since previous tick.
	 * 
	 * @see #render(IRenderer2D, float) 
	 * @see #tick()
	 */
	private void draw(float dt) {
		IRenderer2D renderer = display.startRendering();
		if (renderer == null) return;
		render(renderer, dt);
		display.stopRendering();
	}
	
// Getter functions //
	
	/**
	 * @return The main display object
	 */
	public Display getDisplay() {
		return display;
	}
	
// Setter functions //
	
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
	
	public static void start(String[] args, Class<? extends Application> appClazz) {
		Constructor<?> defaultConstructor = null;
		try {
			defaultConstructor = appClazz.getDeclaredConstructor(new Class<?>[0]);
		} catch (NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
		
		if (defaultConstructor == null)
			return;
		
		Application app = null;
		try {
			app = (Application)defaultConstructor.newInstance(new Object[0]);
		} catch (InstantiationException e) {
			e.printStackTrace();
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
