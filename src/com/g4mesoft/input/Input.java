package com.g4mesoft.input;

/**
 * An input class used as a template for simple
 * input operations. Useful for movement, with
 * entities or other simple input-based events.
 * 
 * @author Christian
 * 
 * @see com.g4mesoft.input.key.KeyInput KeyInput
 */
public abstract class Input {

	/**
	 * @return 	True, if this input is active, false
	 * 			otherwise
	 */
	public abstract boolean isActive();
	
	/**
	 * @return 	The time in milliseconds, when this
	 * 			input was activated.
	 */
	public abstract long getActivationTime();
}
