package com.g4mesoft.input.key;

import java.util.Arrays;

import com.g4mesoft.math.MathUtils;

/**
 * Used as a multi-activated KeyInput extension. If either
 * of the given key-combinations are active, this KeyInput is 
 * represented as being "pressed". If none of the key-combinations
 * are pressed, this KeyInput is not pressed and {@link #isPressed()} 
 * will return false. A key-combination is "pressed" when all keys
 * in the combo are active.
 * <br><br>
 * A {@code KeyComboInput} representing either 'SHIFT+A' or 'CTRL+D' can be
 * initialized as follows:
 * <pre>
 * KeyInput key = new KeyComboInput("SHIFT+A or CTRL+D", 
 * 	new int[] { KeyEvent.VK_SHIFT, KeyEvent.VK_A }, 
 * 	new int[] { KeyEvent.VK_CTRL,  KeyEvent.VK_D });
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
 * @see com.g4mesoft.input.key.KeySingleInput KeySingleInput
 */
public class KeyComboInput extends KeyInput {

	private final int[][] keyCombos;
	private final boolean[][] keyStates;
	private boolean pressed;
	private boolean wasPressed;
	
	private int[][] repeatCounts;
	
	private boolean keyStatesChanged;
	private int cachedRepeatCount;
	
	private long activationTime;
	
	public KeyComboInput(String name, int... keyCombo) {
		this(name, new int[][] { keyCombo });
	}

	public KeyComboInput(String name, int[][] keyCombos) {
		super(name);

		// Copy the arrays to make sure they don't change.
		this.keyCombos = new int[keyCombos.length][];
		
		// Create key-state arrays
		keyStates = new boolean[keyCombos.length][];
		repeatCounts = new int[keyCombos.length][];
		
		for (int i = 0; i < keyCombos.length; i++) {
			int[] keyCombo = keyCombos[i];

			this.keyCombos[i] = Arrays.copyOf(keyCombo, keyCombo.length);
			
			keyStates[i] = new boolean[keyCombo.length];
			repeatCounts[i] = new int[keyCombo.length];
		}
		
		reset();
	}
	
	@Override
	public void update() {
		if (keyStatesChanged) {
			wasPressed = pressed;
			pressed = isKeyStatePressed(keyStates);

			resetRepeatCounts();
		
			keyStatesChanged = false;
		}
	}

	@Override
	public void reset() {
		pressed = false;
		wasPressed = false;

		if (keyStatesChanged) {
			for (int i = 0, len = keyStates.length; i < len; i++) {
				boolean[] comboKeyStates = keyStates[i];
				for (int j = 0; j < comboKeyStates.length; j++) {
					comboKeyStates[j] = false;
				}
			}
	
			resetRepeatCounts();

			keyStatesChanged = false;
		}
	}
	
	private void resetRepeatCounts() {
		for (int i = 0; i < repeatCounts.length; i++) {
			int[] comboRepeatCounts = repeatCounts[i];
			for (int j = 0; j < comboRepeatCounts.length; j++) {
				comboRepeatCounts[j] = 0;
			}
		}
		
		cachedRepeatCount = 0;
	}

	/**
	 * Used internally to set the state of a given key. If a key
	 * with the given {@code keyCode} does not exist, this function
	 * will change nothing, however it will still have to loop 
	 * through all keys to know. Note that this also handles the
	 * recording of key repeatCounts, if and only if state is true.
	 * 
	 * @param keyCode  -  The keyCode representing the key
	 * @param state    -  The new state, which will be given to
	 *                    the key in the {@code keyStates} array.
	 * 
	 * @return True, if the a keyCode was found, false otherwise.
	 * 
	 * @see #keyPressed(int) keyPressed(keyCode)
	 * @see #keyReleased(int) keyPressed(keyCode)
	 */
	private boolean setKeyState(int keyCode, boolean state) {
		boolean changed = false;
		for (int i = 0, len = keyStates.length; i < len; i++) {
			int[] keyCombo = keyCombos[i];
			for (int j = 0; j < keyCombo.length; j++) {
				if (keyCombo[j] == keyCode) {
					keyStates[i][j] = state;
					if (state)
						repeatCounts[i][j]++;

					changed = true;
					
					// Might exist as another part 
					// of the key-combination.
					// break;
				}
			}
		}
		return changed;
	}
	
	@Override
	public void keyPressed(int keyCode) {
		if (setKeyState(keyCode, true)) {
			// repeatCount may have changed
			cachedRepeatCount = -1;
			keyStatesChanged = true;
			
			if (!pressed) {
				wasPressed = pressed;
				pressed = isKeyStatePressed(keyStates);
				if (pressed)
					activationTime = System.currentTimeMillis();
			}
		}
	}

	@Override
	public void keyReleased(int keyCode) {
		if (setKeyState(keyCode, false))
			keyStatesChanged = true;
	}
	
	/**
	 * Used internally to determine if a key-combination is pressed.
	 * Each key-combination will be in a single array inside an array
	 * of combinations {@code boolean[][] keyStates}
	 * 
	 * @param keyStates  -  The array of key-combinations to loop
	 *                      through.
	 * 
	 * @return 	True, if one of the key-combinations are pressed, false
	 * 			otherwise.
	 */
	private boolean isKeyStatePressed(boolean[][] keyStates) {
		for (int i = 0; i < keyStates.length; i++) {
			if (isKeyComboPressed(keyStates[i]))
				return true;
		}
		
		// None of the key-combinations were pressed
		return false;
	}

	/**
	 * Checks if all keyStates within the given combo are pressed.
	 * 
	 * @param comboStates - the keyStates of the given combo.
	 * 
	 * @return True, if all states are pressed, false otherwise.
	 */
	private boolean isKeyComboPressed(boolean[] comboStates) {
		for (int i = 0; i < comboStates.length; i++) {
			if (!comboStates[i])
				return false;
		}
		
		return true;
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

	@Override
	public int getRepeatCount() {
		if (cachedRepeatCount != -1)
			return cachedRepeatCount;
		
		int repeatCount = cachedRepeatCount = 0;
		for (int i = 0; i < repeatCounts.length; i++) {
			if (isKeyComboPressed(keyStates[i]))
				repeatCount += MathUtils.max(repeatCounts[i]);
		}
		
		// If the key was repeated during the search, the
		// cache may be invalid. Don't update the cache.
		if (cachedRepeatCount == -1)
			return repeatCount;

		cachedRepeatCount = repeatCount;
		return repeatCount;
	}
}
