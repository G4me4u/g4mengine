package com.g4mesoft.input.key;

import com.g4mesoft.input.Input;

/**
 * When an overridden version of this class is initialized,
 * it should represent a given key or key-combination action.
 * Each key-action can be represented by different keys and
 * is therefore easily usable, if there are different combinations
 * or strokes of keys available for a single action.
 * <br>
 * Each initialized KeyInput has to be manually registered to the 
 * {@link com.g4mesoft.input.key.KeyInputListener KeyInputListener}
 * if used together with a display. A {@code KeyInput} instance can 
 * also be activated manually by calling the event functions 
 * {@link #keyPressed(int)}, {@link #keyReleased(int)} when a keyState 
 * changes and {@link #update()} when a tick has passed.
 * 
 * @author Christian
 * 
 * @see com.g4mesoft.input.key.KeyInputListener KeyInputListener
 */
public abstract class KeyInput extends Input {

	protected final String name;
	
	public KeyInput(String name) {
		this.name = name;
	}
	
	/**
	 * Should be called as the last thing every tick. Used for resetting
	 * old key-states and replacing them with new ones.
	 */
	public abstract void update();
	
	/**
	 * Resets all key-states in this key input. Useful, when the game
	 * is paused or if key-control is off.
	 */
	public abstract void reset();

	/**
	 * Should be called whenever a key with the given {@code keyCode}
	 * is pressed.
	 * 
	 * @param keyCode  -  An integer representing the {@code keyCode}
	 *                    of the given key. Key codes can be found in
	 *                    the {@link java.awt.event.KeyEvent KeyEvent} 
	 *                    class.
	 * 
	 * @see java.awt.event.KeyEvent KeyEvent
	 */
	public abstract void keyPressed(int keyCode);
	
	/**
	 * Should be called whenever a key with the given {@code keyCode}
	 * is released.
	 * 
	 * @param keyCode  -  An integer representing the {@code keyCode}
	 *                    of the given key. Key codes can be found in
	 *                    the {@link java.awt.event.KeyEvent KeyEvent} 
	 *                    class.
	 * 
	 * @see java.awt.event.KeyEvent KeyEvent
	 */
	public abstract void keyReleased(int keyCode);
	
	/**
	 * Returns true, for as long as the key(s) this key input 
	 * represents are pressed. Returns false after they're released 
	 * and until they're pressed again.
	 * 
	 * @return 	True, if this key input is pressed, false otherwise.
	 * 
	 * @see #wasPressed()
	 * @see #isClicked()
	 * @see #isReleased()
	 */
	public abstract boolean isPressed();
	
	/**
	 * Returns true / false, one update after the {@link #isPressed()}
	 * function returns either value. In other words, this function
	 * will always be one tick behind. This value is used for the
	 * {@link #isClicked()} and {@link #isReleased()} functions.
	 * 
	 * @return True, if this key input was pressed one tick ago, false
	 *         otherwise.
	 * 
	 * @see #isPressed()
	 * @see #isClicked()
	 * @see #isReleased()
	 */
	public abstract boolean wasPressed();
	
	/**
	 * Returns true, if this key input is clicked. This function is a
	 * basic wrapper and can be replaced with the following code snippet: 
	 * <pre>
	 * boolean clicked = (isPressed()) && (!wasPressed());
	 * </pre>
	 * This function will only return true for a single update making it
	 * useful for <i>"rising-edge"</i> based events.
	 * 
	 * @return True, the first tick this key input is pressed, false 
	 *         otherwise.
	 *  
	 * @see #isPressed()
	 * @see #wasPressed()
	 */
	public final boolean isClicked() {
		return (isPressed()) && (!wasPressed());
	}
	
	/**
	 * Returns true, if this key input was released. This function is a
	 * basic wrapper and can be replaced with the following code snippet:
	 * <pre>
	 * boolean released = (!isPressed()) && (wasPressed());
	 * </pre>
	 * This function will only return true for a single update making is 
	 * useful for <i>"falling-edge"</i> based events.
	 * 
	 * @return True, the first tick this key input is released, false
	 *         otherwise
	 * 
	 * @see #isPressed()
	 * @see #wasPressed()
	 */
	public final boolean isReleased() {
		return (!isPressed()) && (wasPressed());
	}
	
	/**
	 * @return The name of this key input. Can be useful for translations
	 *         and debugging.
	 */
	public final String getName() {
		return name;
	}
	
	@Override
	public boolean isActive() {
		return isPressed();
	}
}
