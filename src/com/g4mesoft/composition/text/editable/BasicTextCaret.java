package com.g4mesoft.composition.text.editable;

import java.awt.Rectangle;

import com.g4mesoft.Application;
import com.g4mesoft.composition.CompositionUtils;
import com.g4mesoft.composition.ui.EditableTextCompositionUI;
import com.g4mesoft.graphic.GColor;
import com.g4mesoft.graphic.IRenderer2D;
import com.g4mesoft.graphic.IRenderingContext2D;
import com.g4mesoft.input.key.KeyComboInput;
import com.g4mesoft.input.key.KeyInput;
import com.g4mesoft.input.key.KeySingleInput;
import com.g4mesoft.input.mouse.MouseButtonInput;
import com.g4mesoft.input.mouse.MouseInputListener;
import com.g4mesoft.math.MathUtils;
import com.sun.glass.events.KeyEvent;

/**
 * A basic text caret used by the user to navigate the text area on which this
 * caret is installed. This text caret will handle almost all navigational
 * properties of a modern text caret. This includes navigating by arrow keys,
 * selection modifiers and much more. To see all the functionality of the user
 * selection and navigational tools, see the setter-methods for these inputs.
 * <br><br>
 * The caret itself is defined by two numbers; the dot and the mark. To query
 * these, use the methods {@link #getCaretDot()} and {@link #getCaretMark()} 
 * respectively. The caret dot represents the location in the document at which
 * the cursor itself is located, usually represented by a vertical line. This
 * vertical line will always be located just before the index of the character
 * that it points to. For example will {@code dot = 0} represent the position
 * before the first character in the view. If the user decides to create a
 * selection, this will be represented by the {@code mark}. The selection itself
 * is <i>not</i> painted in this text caret and should therefore be handled 
 * elsewhere. To see the full functionality of the {@code mark} and how it is
 * activated by the user see {@link #setSelectionModifierKey(KeyInput)}.
 * <br><br>
 * To install this text caret onto an {@code EditableTextComposition} one can
 * use the following code-snippet:
 * <pre>
 * ...
 * {@literal //} Initialize a new text caret
 * BasicTextCaret caret = new BasicTextCaret();
 * {@literal //} Set caret properties. For example the width = 4
 * caret.setWidth(4);
 * {@literal //} Install the caret on the specified
 * {@literal //} editable text composition
 * textComposition.setCaret(caret);
 * ...
 * </pre>
 * The above code will automatically set and install the caret of the specified
 * {@link com.g4mesoft.composition.text.editable.EditableTextComposition
 * EditableTextComposition}.
 * 
 * @author Christian
 * 
 * @see #setNavigateForwardKey(KeyInput)
 * @see #setNavigateBackwardKey(KeyInput)
 * @see #setSelectionModifierKey(KeyInput)
 * @see #setSelectAllKey(KeyInput)
 * @see #setHomeKey(KeyInput)
 * @see #setEndKey(KeyInput)
 * @see #setNavigateMouseButton(MouseButtonInput)
 */
public class BasicTextCaret implements ITextCaret, ITextModelListener {

	private static final int DEFAULT_BLINK_RATE = 500;
	private static final int DEFAULT_CARET_WIDTH = 2;
	private static final int DEFAULT_CARET_INSETS = 0;
	
	private static KeyInput sharedNavigateForwardKey = null;
	private static KeyInput sharedNavigateBackwardKey = null;
	
	private static KeyInput sharedSelectionModifierKey = null;
	private static KeyInput sharedSelectAllKey = null;
	
	private static KeyInput sharedHomeKey = null;
	private static KeyInput sharedEndKey = null;

	private EditableTextComposition textComposition;
	
	private int dot;
	private int mark;
	
	private int caretInsets;
	private int caretWidth;
	
	private long lastFrame;
	private int blinkRate;
	private int blinkTimer;
	
	private KeyInput navigateForwardKey;
	private KeyInput navigateBackwardKey;
	
	private boolean selectionModifier;
	private KeyInput selectionModifierKey;
	private KeyInput selectAllKey;
	
	private KeyInput homeKey;
	private KeyInput endKey;
	
	private MouseButtonInput navigateMouseButton;
	
	private boolean navigateToPoint;
	private boolean wasNavigateToPoint;
	private int navigateX;
	private int navigateY;
	
	public BasicTextCaret() {
		dot = mark = 0;
		
		caretWidth = DEFAULT_CARET_WIDTH;
		caretInsets = DEFAULT_CARET_INSETS;
		
		blinkRate = DEFAULT_BLINK_RATE;
		lastFrame = -1L;
		
		ensureValidSharedNavigationKeys();
		
		navigateForwardKey = sharedNavigateForwardKey;
		navigateBackwardKey = sharedNavigateBackwardKey;
		
		selectionModifierKey = sharedSelectionModifierKey;
		selectAllKey = sharedSelectAllKey;
		
		homeKey = sharedHomeKey;
		endKey = sharedEndKey;
		
		navigateMouseButton = MouseInputListener.MOUSE_LEFT;
	}
	
	/**
	 * Ensures that all the default keys are initialized and registered
	 * correctly. 
	 */
	private static void ensureValidSharedNavigationKeys() {
		if (sharedNavigateForwardKey == null) {
			sharedNavigateForwardKey = new KeySingleInput("caretForward", KeyEvent.VK_RIGHT);
			Application.addKey(sharedNavigateForwardKey);
		}

		if (sharedNavigateBackwardKey == null) {
			sharedNavigateBackwardKey = new KeySingleInput("caretBackward", KeyEvent.VK_LEFT);
			Application.addKey(sharedNavigateBackwardKey);
		}
		
		if (sharedSelectionModifierKey == null) {
			sharedSelectionModifierKey = new KeySingleInput("caretSelectModifier", KeyEvent.VK_SHIFT);
			Application.addKey(sharedSelectionModifierKey);
		}

		if (sharedSelectAllKey == null) {
			sharedSelectAllKey = new KeyComboInput("caretSelectAll", KeyEvent.VK_CONTROL, KeyEvent.VK_A);
			Application.addKey(sharedSelectAllKey);
		}
		
		if (sharedHomeKey == null) {
			sharedHomeKey = new KeyComboInput("caretHome", KeyEvent.VK_HOME);
			Application.addKey(sharedHomeKey);
		}

		if (sharedEndKey == null) {
			sharedEndKey = new KeyComboInput("caretEnd", KeyEvent.VK_END);
			Application.addKey(sharedEndKey);
		}
	}
	
	@Override
	public void install(EditableTextComposition textComposition) {
		if (this.textComposition != null)
			throw new IllegalStateException("Caret already bound!");
	
		this.textComposition = textComposition;
		
		ITextModel textModel = textComposition.getTextModel();
		textModel.addTextModelListener(this);
	
		dot = textModel.getLength();
		mark = dot;
	}

	@Override
	public void uninstall(EditableTextComposition textComposition) {
		if (this.textComposition == null)
			throw new IllegalStateException("Caret not bound!");
	
		this.textComposition = null;
		
		textComposition.getTextModel().removeTextModelListener(this);
	}

	@Override
	public void update() {
		selectionModifier = false;
		
		handleKeyInput();
		handleMouseInput();
	}
	
	/**
	 * Handles all navigational key-inputs that are added to this text caret. If
	 * one wishes to see the functionality of these keys, and change them to, 
	 * they can be set by the following setter-methods:
	 * 
	 * @see #setNavigateForwardKey(KeyInput)
	 * @see #setNavigateBackwardKey(KeyInput)
	 * @see #setSelectionModifierKey(KeyInput)
	 * @see #setSelectAllKey(KeyInput)
	 * @see #setHomeKey(KeyInput)
	 * @see #setEndKey(KeyInput)
	 */
	protected void handleKeyInput() {
		if (selectionModifierKey.isPressed())
			selectionModifier = true;
		
		if (navigateForwardKey.isPressed())
			navigateByAmount(navigateForwardKey.getRepeatCount(), false);
		if (navigateBackwardKey.isPressed())
			navigateByAmount(navigateBackwardKey.getRepeatCount(), true);
		
		if (homeKey.isClicked())
			navigateToLocation(0);
		if (endKey.isClicked())
			navigateToLocation(textComposition.getTextModel().getLength());
		
		if (selectAllKey.isClicked())
			setSelection(0, textComposition.getTextModel().getLength());
	}
	
	/**
	 * Handles all mouse input for this text caret. This is for example handling
	 * a case where the user clicks on the text area and hence sets the current
	 * location of the caret to the given location. If the user decides to drag
	 * the mouse cursor along the text-area, the selectionModifier will be set
	 * to active, and the text from the original click location to the current
	 * will be selected.
	 * 
	 * @see #setNavigateMouseButton(MouseButtonInput)
	 */
	protected void handleMouseInput() {
		if (wasNavigateToPoint && !navigateToPoint && navigateMouseButton.isPressed()) {
			int dx = MouseInputListener.getInstance().getX();

			// We allow dragging outside of the text area. Just
			// make sure the original y-coordinate was inside.
			int cy = navigateMouseButton.getClickY();

			// When dragging the user will be selecting.
			selectionModifier = true;

			navigateToPoint = true;
			navigateX = dx;
			navigateY = cy;
		}
		
		if (navigateMouseButton.isClicked()) {
			int cx = navigateMouseButton.getClickX();
			int cy = navigateMouseButton.getClickY();
			
			if (textComposition.isInBounds(cx, cy)) {
				navigateToPoint = true;
				navigateX = cx;
				navigateY = cy;
			}
		}

		wasNavigateToPoint = navigateToPoint;
	}
	
	/**
	 * Navigates the model either forward or backward by the given amount
	 * depending on the given {@code backward} parameter. If the parameter is
	 * true, then the navigation will be backward, otherwise it will be forward.
	 * If the caret currently has a selection, but the selectionModifier is not
	 * active, then the cursor will be set to the backward / forward location
	 * of the selection, again depending on the backward parameter.
	 * 
	 * @param amount - a positive integer defining the amount of units to
	 *                 navigate the model by.
	 * @param backward - a parameter defining whether the navigation should be
	 *                   backward or forward.
	 */
	protected void navigateByAmount(int amount, boolean backward) {
		if (amount <= 0)
			return;
		
		if (!selectionModifier && hasCaretSelection()) {
			if (backward) {
				setCaretLocation(MathUtils.min(dot, mark));
			} else {
				setCaretLocation(MathUtils.max(dot, mark));
			}
			
			amount--;
		}

		if (amount != 0)
			navigateToLocation(backward ? (dot - amount) : (dot + amount));
	}
	
	/**
	 * Navigates the cursor to the given location. If the selectionModifier is
	 * currently active, this function will only set the dot location.
	 * 
	 * @param location - the new location of the caret, or the dot, if the
	 *                   selectionModifier is not active.
	 */
	protected void navigateToLocation(int location) {
		if (selectionModifier) {
			setCaretDot(location);
		} else {
			setCaretLocation(location);
		}
	}
	
	/**
	 * Navigates the caret to the specified point.
	 * 
	 * @param context - the current graphics context
	 * 
	 * @param navX - the x-position of the navigation point
	 * @param navY - the y-position of the navigation point
	 * 
	 * @see #handleMouseInput()
	 */
	protected void navigateToPoint(IRenderingContext2D context, int navX, int navY) {
		EditableTextCompositionUI editableUI = textComposition.getUI();
			
		int indexOffset = 0;
		if (selectionModifier) {
			if (navX < textComposition.getX()) {
				navX = textComposition.getX();
				indexOffset = -1;
			} else if (navX > textComposition.getX() + textComposition.getWidth()) {
				navX = textComposition.getX() + textComposition.getWidth();
				indexOffset = 1;
			}
		}
		
		if (editableUI != null && textComposition.isInBounds(navX, navY)) {
			int navigationIndex = editableUI.viewToModel(context, navX, navY);
			
			if (navigationIndex != -1)
				navigateToLocation(navigationIndex + indexOffset);
		}
	}
	
	@Override
	public void render(IRenderer2D renderer, float dt) {
		long now = System.currentTimeMillis();
		if (lastFrame != -1L)
			blinkTimer += (int)MathUtils.min(blinkRate, now - lastFrame);
		lastFrame = now;

		if (blinkTimer <= blinkRate) {
			paintCaret(renderer, dt);
		} else if (blinkTimer >= blinkRate << 1) {
			blinkTimer %= blinkRate << 1;
		}

		// Navigation should occur after we've rendered
		// the caret, since the model has no chance to
		// update to any navigation changes within the
		// same frame.
		if (navigateToPoint) {
			navigateToPoint(renderer, navigateX, navigateY);
			navigateToPoint = false;
		}
	}
	
	/**
	 * Paints the graphical caret to the given renderer. The width and insets of
	 * the caret are specified by the methods {@link #setCaretWidth(int)} and
	 * {@link #setCaretInsets(int)}. If the caret color is not set by the text
	 * area, and is therefore null, then the color will be the color of the text
	 * in the text area.
	 * 
	 * @param renderer - the current graphics renderer / context
	 * @param dt - the delta tick parameter for animation interpolation
	 */
	protected void paintCaret(IRenderer2D renderer, float dt) {
		EditableTextCompositionUI editableUI = textComposition.getUI();
		if (editableUI == null)
			return;

		Rectangle bounds = editableUI.modelToView(renderer, dot);
		if (bounds != null) {
			int x = CompositionUtils.getBoundedX(bounds.x, textComposition);
			int y = bounds.y + caretInsets;

			int height = bounds.height - caretInsets * 2;
			if (height > 0) {
				GColor caretColor = textComposition.getCaretColor();
				if (caretColor == null)
					caretColor = textComposition.getTextColor();
				
				renderer.setColor(caretColor);
				renderer.fillRect(x, y, caretWidth, height);
			}
		}
	}
	
	/**
	 * Calculates a bounded location in the text area, meaning that if the given
	 * location is outside of the text model, then it will be clamped to ensure
	 * a valid caret location.
	 * 
	 * @param location - the location to be clamped within the text model bounds
	 * 
	 * @return A bounded location found by clamping the given {@code location}.
	 */
	private int getBoundedLocation(int location) {
		if (location <= 0) {
			return 0;
		} else {
			ITextModel model = textComposition.getTextModel();
			if (location > model.getLength())
				return model.getLength();
		}
		
		return location;
	}

	@Override
	public void setCaretLocation(int location) {
		location = getBoundedLocation(location);
		
		if (location != dot || location != mark) {
			dot = mark = location;
			blinkTimer = 0;
		}
	}
	
	@Override
	public void setCaretDot(int dot) {
		dot = getBoundedLocation(dot);
		
		if (dot != this.dot) {
			this.dot = dot;
			blinkTimer = 0;
		}
	}

	@Override
	public void setCaretMark(int mark) {
		// Don't update blinkTimer when setting
		// the caret mark.
		this.mark = getBoundedLocation(mark);
	}

	@Override
	public int getCaretLocation() {
		return dot;
	}
	
	@Override
	public int getCaretDot() {
		return dot;
	}

	@Override
	public int getCaretMark() {
		return mark;
	}
	
	/**
	 * Sets the selection of the caret to the specified dot and mark locations.
	 * 
	 * @param dot - the dot at which the selection ends
	 * @param mark - the mark at which the selection begins.
	 */
	private void setSelection(int dot, int mark) {
		setCaretDot(dot);
		setCaretMark(mark);
	}
	
	@Override
	public boolean hasCaretSelection() {
		return dot != mark;
	}

	@Override
	public void textInserted(ITextModel model, int offset, int count) {
		if (offset <= dot)
			setCaretDot(dot + count);
		if (offset <= mark)
			setCaretMark(mark + count);
	}

	@Override
	public void textRemoved(ITextModel model, int offset, int count) {
		if (offset == dot)
			blinkTimer = 0;
		
		if (offset + count < dot) {
			setCaretDot(dot - count);
		} else if (offset < dot) {
			setCaretDot(offset);
		}

		if (offset + count < mark) {
			setCaretMark(mark - count);
		} else if (offset < mark) {
			setCaretMark(offset);
		}
	}

	/**
	 * Sets the blink rate to the specified amount of milliseconds. A full cycle
	 * of the caret blinking will be double the amount given in this method. The
	 * default value of this parameter is {@code 500}ms.
	 * 
	 * @param blinkRate - the new blink rate of this caret in milliseconds.
	 * 
	 * @throws IllegalArgumentException if the blinkRate is non-positive.
	 */
	@Override
	public void setBlinkRate(int blinkRate) {
		if (blinkRate <= 0)
			throw new IllegalArgumentException("blinkRate <= 0");
		
		this.blinkRate = blinkRate;
		blinkTimer = 0;
	}

	@Override
	public int getBlinkRate() {
		return blinkRate;
	}
	
	/**
	 * Sets the graphical width of the caret to the specified width. The default
	 * value of this parameter is {@code 2}.
	 * 
	 * @param width - the new width of the graphical caret.
	 * 
	 * @throws IllegalArgumentException if the given {@code width} is negative.
	 */
	public void setCaretWidth(int width) {
		if (width < 0)
			throw new IllegalArgumentException("Caret width is negative!");

		caretWidth = width;
	}
	
	/**
	 * @return The width of the graphical caret.
	 */
	public int getCaretWidth() {
		return caretWidth;
	}
	
	/**
	 * Sets the caret insets to the specified amount. The insets define the
	 * amount of pixels on the top and bottom of the caret that wont be rendered
	 * when painting the caret. If one wishes to disable these insets, they
	 * should set this value to zero. If this value is too large, the caret will
	 * not be rendered. The default value of this parameter is {@code 0}.
	 * 
	 * @param insets - a non-negative integer defining the new caret insets
	 */
	public void setCaretInsets(int insets) {
		if (insets < 0)
			throw new IllegalArgumentException("Caret insets are negative!");
		
		caretInsets = insets;
	}
	
	/**
	 * @return The insets of the graphical caret.
	 */
	public int getCaretInsets() {
		return caretInsets;
	}
	
	/**
	 * Sets the navigate forward key of this text caret to the given key-input.
	 * The navigate forward key allows the user to move the caret forward in the
	 * text model. The default key-input is defined as a single key-input
	 * {@link com.g4mesoft.input.key.KeySingleInput KeySingleInput} with keycode
	 * {@link java.awt.event.KeyEvent#VK_RIGHT VK_RIGHT}.
	 * 
	 * @param navigateForwardKey - the new navigate forward key
	 * 
	 * @throws NullPointerException if the given {@code navigateForwardKey} is
	 *                              null
	 */
	public void setNavigateForwardKey(KeyInput navigateForwardKey) {
		if (navigateForwardKey == null)
			throw new NullPointerException("navigateForwardKey is null");
		this.navigateForwardKey = navigateForwardKey;
	}

	/**
	 * Sets the navigate backward key of this text caret to the given key-input.
	 * The navigate backward key allows the user to move the caret backward in
	 * the text model. The default key-input is defined as a single key-input
	 * {@link com.g4mesoft.input.key.KeySingleInput KeySingleInput} with keycode
	 * {@link java.awt.event.KeyEvent#VK_LEFT VK_LEFT}.
	 * 
	 * @param navigateBackwardKey - the new navigate backward key
	 * 
	 * @throws NullPointerException if the given {@code navigateBackwardKey} is
	 *                              null
	 */
	public void setNavigateBackwardKey(KeyInput navigateBackwardKey) {
		if (navigateBackwardKey == null)
			throw new NullPointerException("navigateBackwardKey is null");
		this.navigateBackwardKey = navigateBackwardKey;
	}

	/**
	 * Sets the selection modifier key of this text caret to the given key-input.
	 * The selection modifier key allows the user to make selections. In short,
	 * when the user presses this button, the caret will remember the current
	 * caret location. This <i>mark</i> will then serve as a starting point for
	 * the caret selection. When the caret is then moved by navigating forward
	 * or backward, the mark will stay put and form a text selection. By default
	 * this is a {@link com.g4mesoft.input.key.KeySingleInput KeySingleInput}
	 * with a keycode of {@link java.awt.event.KeyEvent#VK_SHIFT VK_SHIFT}.
	 * 
	 * @param selectionModifierKey - the new selection modifier key
	 * 
	 * @throws NullPointerException if the given {@code selectionModifierKey} is
	 *                              null
	 */
	public void setSelectionModifierKey(KeyInput selectionModifierKey) {
		if (selectionModifierKey == null)
			throw new NullPointerException("selectionModifierKey is null");
		this.selectionModifierKey = selectionModifierKey;
	}

	/**
	 * Sets the select all key of this text caret to the given key-input. The 
	 * select all key allows the user to select all the text in the text area
	 * with the simple press of a (or more) key(s). When the user presses this
	 * button, the caret will set the dot to zero and the mark to the end of the
	 * model. The default key-input is defined as a combination key-input
	 * {@link com.g4mesoft.input.key.KeyComboInput KeyComboInput} with the 
	 * keycodes {@link java.awt.event.KeyEvent#VK_CONTROL VK_CONTROL} and 
	 * {@link java.awt.event.KeyEvent#VK_A VK_A}.
	 * 
	 * @param selectAllKey - the new select all key
	 * 
	 * @throws NullPointerException if the given {@code selectAllKey} is null
	 */
	public void setSelectAllKey(KeyInput selectAllKey) {
		if (selectAllKey == null)
			throw new NullPointerException("selectAllKey is null");
		this.selectAllKey = selectAllKey;
	}
	
	/**
	 * Sets the home key of this text caret to the given key-input. The home key
	 * allows the user to navigate to the beginning of the text model with the
	 * simple press of a (or more) key(s). If the selection modifier key is down
	 * whilst pressing this key, all the text from the current location of the
	 * cursor to the beginning of the text area will be selected. The default
	 * key-input is a {@link com.g4mesoft.input.key.KeyComboInput KeyComboInput}
	 * with the keycode {@link java.awt.event.KeyEvent#VK_HOME VK_HOME}.
	 * 
	 * @param homeKey - the new home key
	 * 
	 * @throws NullPointerException if the given {@code homeKey} is null
	 */
	public void setHomeKey(KeyInput homeKey) {
		if (homeKey == null)
			throw new NullPointerException("homeKey is null");
		this.homeKey = homeKey;
	}

	/**
	 * Sets the end key of this text caret to the given key-input. The end key
	 * allows the user to navigate to the end of the text model with the simple
	 * press of a (or more) key(s). If the selection modifier key is down whilst
	 * pressing this key, all the text from the current location of the cursor
	 * to the end of the text area will be selected. The default key-input is a
	 * {@link com.g4mesoft.input.key.KeyComboInput KeyComboInput} with the
	 * keycode {@link java.awt.event.KeyEvent#VK_END VK_END}.
	 * 
	 * @param endKey - the new end key
	 * 
	 * @throws NullPointerException if the given {@code endKey} is null
	 */
	public void setEndKey(KeyInput endKey) {
		if (endKey == null)
			throw new NullPointerException("endKey is null");
		this.endKey = endKey;
	}
	
	/**
	 * Sets the mouse navigation button of this text caret to the given mouse
	 * button input. This navigational tool allows the user to set the current
	 * location of the caret as well as select text within the model. The
	 * default value used to navigate the text area with mouse input is given by
	 * {@link com.g4mesoft.input.mouse.MouseInputListener#MOUSE_LEFT MOUSE_LEFT}
	 * 
	 * @param navigateMouseButton - the new navigational mouse button
	 * 
	 * @throws NullPointerException if the given {@code navigateMouseButton} is
	 *                              null
	 */
	public void setNavigateMouseButton(MouseButtonInput navigateMouseButton) {
		if (navigateMouseButton == null)
			throw new NullPointerException("navigateMouseButton is null");
		this.navigateMouseButton = navigateMouseButton;
	}
}
