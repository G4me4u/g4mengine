package com.g4mesoft.input.key;

import java.util.Arrays;

/**
 * Used as a single-activated KeyInput extension. If either
 * of the given keyCodes are invoked, this KeyInput is represented
 * as being "pressed". If none of the keyCodes are pressed, this
 * KeyInput is not pressed and {@link #isPressed()} will return 
 * false.
 * <br><br>
 * A {@code KeySingleInput} representing either 'A' or 'D' can be
 * initialized as follows:
 * <pre>
 * KeyInput key = new KeySingleInput("A or D", KeyEvent.VK_A, KeyEvent.VK_D);
 * </pre>
 * <b>
 * NOTE: Each key has to be registered manually in an instance of the
 * {@link com.g4mesoft.input.key.KeyInputListener KeyInputListener}
 * to function properly.
 * </b>
 * @author Christian
 * 
 * @see com.g4mesoft.input.key.KeyInput KeyInput
 * @see com.g4mesoft.input.key.KeyInputListener KeyInputListener
 * @see com.g4mesoft.input.key.KeyComboInput KeyComboInput
 */
public class KeySingleInput extends KeyInput {

	private final int[] keyCodes;
	private final boolean[] keyStates;
	private boolean pressed;
	private boolean wasPressed;

	private long activationTime;
	
	public KeySingleInput(String name, int... keyCodes) {
		super(name);

		// Copy the arrays to make sure they don't change.
		this.keyCodes = Arrays.copyOf(keyCodes, keyCodes.length);

		// Create key-state arrays
		this.keyStates = new boolean[keyCodes.length];

		reset();
	}
	
	@Override
	public void update() {
		wasPressed = pressed;
		pressed = isKeyStatePressed(keyStates);
	}
	
	@Override
	public void reset() {
		for (int i = 0, len = keyStates.length; i < len; i++)
			keyStates[i] = false;
		
		pressed = false;
		wasPressed = false;
	}

	/**
	 * Used internally to set the state of a given key. If a key
	 * with the given {@code keyCode} does not exist, this function
	 * will change nothing, however it will still have to loop 
	 * through all keys to know.
	 * 
	 * @param keyCode  -  The keyCode representing the key
	 * @param state    -  The new state, which will be given to
	 *                    the key in the {@code keyStates} array.
	 * 
	 * @return True, if a key state changed, false otherwise.
	 * 
	 * @see #keyPressed(int) keyPressed(keyCode)
	 * @see #keyReleased(int) keyPressed(keyCode)
	 */
	private boolean setKeyState(int keyCode, boolean state) {
		for (int i = 0; i < keyCodes.length; i++) {
			if (keyCodes[i] == keyCode) {
				keyStates[i] = state;
				// Might exist as another key,
				// but if it does, it would be
				// a duplicate and not change
				// any outcome.
				return true;
			}
		}
		return false;
	}
	
	@Override
	public void keyPressed(int keyCode) {
		if (setKeyState(keyCode, true) && !pressed) {
			// Make sure to keep the state, as it
			// might change within the same tick
			wasPressed = pressed;
			pressed = isKeyStatePressed(keyStates);
			if (pressed)
				activationTime = System.currentTimeMillis();
		}
	}

	@Override
	public void keyReleased(int keyCode) {
		setKeyState(keyCode, false);
	}
	
	/**
	 * Used internally to determine if a one of the given keys are pressed. Each
	 * key will be represented by a boolean inside the {@code boolean[] keyStates}
	 * array.
	 * 
	 * @param keyStates  -  The array of keys to loop through.
	 * 
	 * @return True, if one of the keys are pressed, false otherwise.
	 */
	private boolean isKeyStatePressed(boolean[] keyStates) {
		for (int i = 0; i < keyStates.length; i++) {
			// A key was pressed, return true
			if (keyStates[i])
				return true;
		}

		// None of the keys were pressed
		return false;
	}

	@Override
	public boolean isPressed() {
		return pressed;
	}

	@Override
	public boolean wasPressed() {
		return wasPressed;
	}
	
	@Override
	public long getActivationTime() {
		return activationTime;
	}
}
