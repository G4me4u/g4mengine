package com.g4mesoft;

import com.g4mesoft.graphic.Display;
import com.g4mesoft.graphic.Exitable;
import com.g4mesoft.graphic.Renderer2D;

public abstract class Application implements Exitable {

	private static final boolean DEBUG = true;
	private static final float TPS = 20.0f;
	private static final float MIN_FPS = 60.0f;

	private static final String DISPLAY_CONFIG_LOCATION = "/config/display.txt";
	
	private Display display;
	
	private boolean running;
	private Timer timer;
	private float minimumFps;

// Abstract functions //
	
	protected Application() { }

	/**
	 * When this function is overriden, it should be in
	 * charge of ticking/updating neccesary world objects,
	 * inputs or doing other necessary calculations. 
	 * <br>
	 * This function should not be used for drawing, as there
	 * is another method, {@code render(Renderer2D, float)}
	 * used for handling that. 
	 * <br><br>
	 * <b>NOTE:</b> this function will always be called with
	 * approx. the same interval (if the system isn't overloaded).
	 * If you want to change the interval between each tick, call
	 * {@code setTps(float)} with the desired ticks per second.
	 * 
	 * @see #setTps(float)
	 * @see #render(Renderer2D, float)
	 */
	protected abstract void tick();

	/**
	 * When this function is overriden, it should be in
	 * charge for rendering to the display through the
	 * Renderer2D object.<br>
	 * The dt time constant can be used for 
	 * smoother drawing of moving objects. 
	 * <br><br>
	 * <b>Example <i>(no casting handled)</i>:</b>
	 * <pre>
	 * int xPixel = prevX + (x - prevX) * dt;
	 * int yPixel = prevY + (y - prevY) * dt;
	 * </pre>
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
	protected abstract void render(Renderer2D renderer, float dt);
	
// Overridable functions //
	
	/**
	 * This function should be overridden by sub-classes and
	 * used for general startup calls. This function will be
	 * called before init().
	 * <br><br>
	 * <b>NOTE:</b><i> any sub-classes overriding this function should
	 * call {@code super.start()} to make sure the application starts 
	 * executing properly. Make sure to place that line of code at the
	 * end of the sub-implementation of this function, as this function 
	 * will cause the execution to pause until the game has stopped.</i>
	 */
	protected void start() {
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
		display = new Display(Application.class.getResourceAsStream(DISPLAY_CONFIG_LOCATION), this);

		timer = new Timer(TPS, DEBUG);
		minimumFps = MIN_FPS;
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
		running = true;
		
		timer.initTimer();
		while (running) {
			timer.update();
			int missingTicks = timer.getMissingTicks();
			for (int i = 0; i < missingTicks; i++) {
				tick();
				timer.tickPassed();
			}
			draw(timer.getDeltaTick());
			timer.framePassed();
			timer.sleep(minimumFps);
		}
	}

	/**
	 * Sets up the drawing before calling the overriden
	 * render function. This will start and stop rendering
	 * automatically.
	 * 
	 * @param dt	-	A constant representing how much 
	 * 					time has passed since previous tick.
	 * 
	 * @see #render(Renderer2D, float) 
	 * @see #tick()
	 */
	private void draw(float dt) {
		Renderer2D renderer = display.startRendering();
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
	 * the timer will not allow for such a low framerate,
	 * and compensate, to make sure the ticks per second is
	 * met.
	 * 
	 * @param minimumFps	-	The minimum frames per second.
	 * 
	 * @see #render(Renderer2D, float)
	 * @see #setTps(float)
	 */
	public void setMinimumFps(float minimumFps) {
		this.minimumFps = minimumFps;
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
}
