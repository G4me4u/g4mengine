package com.g4mesoft.input.key;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;

import com.g4mesoft.graphic.Display;

/**
 * A KeyInputListener used for registering and handling keyEvents
 * sent by the {@link java.awt.EventQueue EventQueue}. When a keyEvent
 * is registered, the key with the given keyCode will be activated.
 * <br>
 * Keys can be registered as a {@link com.g4mesoft.input.key.KeyInput KeyInput}
 * object using the {@link #addKey(KeyInput)} function.
 * <br><br>
 * <b>
 * NOTE: the KeyInputListener instance has to be added to a display manually.
 * This can be done with the following code snippet:
 * </b>
 * <pre>
 * KeyInputListener.getInstance().registerDisplay(display);
 * </pre>
 * 
 * @author Christian
 *
 * @see com.g4mesoft.input.key.KeyInput KeyInput
 * @see #registerDisplay(Display)
 */
public class KeyInputListener implements KeyListener {

	private static KeyInputListener instance;
	
	private List<KeyInput> keys;
	private List<KeyTypedInput> typedKeys;
	
	private KeyInputListener() {
		keys = new ArrayList<KeyInput>();
		typedKeys = new ArrayList<KeyTypedInput>();
	}
	
	@Override 
	public void keyTyped(KeyEvent e) {
		char keyChar = e.getKeyChar();
		for (KeyTypedInput typedKey : typedKeys)
			typedKey.keyTyped(keyChar);
	}

	@Override
	public void keyPressed(KeyEvent e) {
		int keyCode = e.getKeyCode();
		for (KeyInput key : keys)
			key.keyPressed(keyCode);
	}

	@Override
	public void keyReleased(KeyEvent e) {
		int keyCode = e.getKeyCode();
		for (KeyInput key : keys)
			key.keyReleased(keyCode);
	}
	
	/**
	 * Invokes the update functions of each key registered in
	 * this {@code KeyInputListener}.
	 */
	public void updateKeys() {
		for (KeyInput key : keys)
			key.update();
		for (KeyTypedInput typedKey : typedKeys)
			typedKey.update();
	}
	
	/**
	 * Invokes the reset functions of each key registered in
	 * this {@code KeyInputListener}.
	 */
	public void resetKeys() {
		for (KeyInput key : keys)
			key.reset();
		for (KeyTypedInput typedKey : typedKeys)
			typedKey.reset();
	}
	
	/**
	 * Adds a new key to this {@code KeyInputListener}. If the
	 * given key already exists in the key registry, the key
	 * will not be added, and this function will return false.
	 * 
	 * @param key  -  The key to be registered in the registry.
	 * 
	 * @return True, if the key registry changed, false otherwise.
	 * 
	 * @see com.g4mesoft.input.key.KeyInput KeyInput
	 */
	public boolean addKey(KeyInput key) {
		if (keys.contains(key))
			return false;
		return keys.add(key);
	}
	
	/**
	 * Removes an existing key from this {@code KeyInputListener}.
	 * If the key doesn't exist and the registry didn't change,
	 * this function returns false.
	 * 
	 * @param key  -  The key to be removed from the registry.
	 * 
	 * @return True, if the key registry changed, false otherwise.
	 * 
	 * @see com.g4mesoft.input.key.KeyInput KeyInput
	 */
	public boolean removeKey(KeyInput key) {
		return keys.remove(key);
	}
	
	/**
	 * TODO: Documentation
	 * 
	 * @param typedKey
	 * @return
	 */
	public boolean addTypedKey(KeyTypedInput typedKey) {
		if (typedKeys.contains(typedKey))
			return false;
		return typedKeys.add(typedKey);
	}
	
	/**
	 * TODO: Documentation
	 * 
	 * @param typedKey
	 * @return
	 */
	public boolean removeTypedKey(KeyTypedInput typedKey) {
		return typedKeys.remove(typedKey);
	}
	
	/**
	 * Registers this {@code KeyInputListener} to the given Display.
	 * This function is a simple wrapper and can be replaced by the
	 * following code snippet:
	 * <pre>
	 * display.registerKeyListener(KeyInputListener.getInstance());
	 * </pre>
	 * <b>
	 * NOTE: unless handled elsewhere one must update the keys at
	 * the end of each tick using the following code snippet: 
	 * </b>
	 * <pre>
	 * KeyInputListener.getInstance().updateKeys();
	 * </pre>
	 * 
	 * @param display  -  The display which this {@code KeyInputListener}
	 *                    should be registered by.
	 */
	public void registerDisplay(Display display) {
		display.registerKeyListener(this);
	}
	
	/**
	 * Returns an instance of the {@code KeyInputListener}. If the
	 * static instance is not yet defined, this function initiate a 
	 * new instance and return it in future calls to this method.
	 * There is only ever one instance of the KeyInputListener. Multiple
	 * calls to this function will return the same instance.
	 * 
	 * @return A static instance of the {@code KeyInputListener}
	 */
	public static KeyInputListener getInstance() {
		if (instance == null)
			instance = new KeyInputListener();
		return instance;
	}
}
