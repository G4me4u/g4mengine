# G4mEngine

A lightweight general purpose 2D game engine for practice and simple game
implementations in Java. Using no external libraries.

## Table of content

[Installation](#installation)
* [Before installation](#before-installation)
* [Download project](#download-project)
* [Import and compile](#import-and-compile)

[Usage and examples](#usage-and-examples)
* [Simple program example](#simple-program-example)
* [Capturing user input](#capturing-user-input)

## Installation

There are several ways you can add G4mEngine to your project. But before we get
to any of that there are several things you need to make sure of. I will be going
through the simplest way to add Java to your project.

### Before installation
Before you install the engine you should make sure that you have the following
software installed on your computer. G4mEngine is a lightweight Java project and
does not need any third party (except for Java JRE) software to work, however it
is still advised that the following is to be installed:

* Java SDK and JRE version 1.8 or above should be
  [installed](https://www.oracle.com/technetwork/java/javase/downloads/index.html).
* Eclipse IDE or any other integrated development environment for Java should
  be [installed](https://www.eclipse.org/downloads/).

It is possible to use the project without the use of an IDE, but as there are
resource files and many class files, it is advised to use an IDE. The project is
originally written utilizing features in the Eclipse IDE, but other IDE's such as
IntelliJ may also work if the project is imported as an Eclipse project.

### Download project
G4mEngine is a very lightweight implementation, and is therefore very easy to add
to any new or ongoing Java project without adding too much complexity. Firstly you'll
need to download the project on [GitHub](https://github.com/G4me4u/g4mengine). The
entire master zip should be downloaded. Alternatively, you can fork the project and
pull it to your computer using git. Just make sure you have the entire repository on
your computer. If you downloaded the ZIP, you should un-zip the project into a new
folder.

### Import and compile
Once you've downloaded the project you have to compile the Java src-files. Because the
engine is so lightweight, it is not pre-compiled like many other projects, but that
shouldn't be an issue. If you're using Eclipse all you have to do is import G4mEngine
as an existing project. This can be done by selecting *File->Import...->General->Existing
Projects into Workspace*, browsing the root directory of the newly created folder with
the project files and clicking *Finish*. The Eclipse project wizard will automatically
set up the project with the necessary resources. Any other IDE should be nearly the same.
In IntelliJ you should select the *Import Eclipse project* option. And that's it! You can
now compile and use the project as a dependency to any of your own Java projects!

If you want to use G4mEngine as a library jar file (and not a project dependency) you
can do so by compiling the G4mEngine source files into a single jar file. This is done
in the Eclipse IDE by selecting the *File->Export...->Java->JAR file* option.

**NOTE:** *If the project is not imported as an Eclipse project you should make sure
that the /res folder is selected as a resource folder, and the contents are added to the
root of the jar output file.*

## Usage and examples
Once G4mEngine is added to your project, you should be able to access all the nice
features of the engine. If you're unable to access the features repeat the Installation
guide.

### Simple program example
Like with any other program, it is always a good idea to create a simple Hello World
application before getting into the more advanced stuff. Even though I know it's boring,
let us try to get a pink square to be drawn on a display with a white background. This
process should take around 5-10 minutes (no time at all) to complete depending on skill.
Again, I advice using the Eclipse IDE for doing projects with the engine, but it is not
strictly necessary (I promise I wont mention it again).

* [Creating our Application class](#creating-our-application-class)
* [Drawing to the canvas](#drawing-to-the-canvas)
* [Display configuration](#display-configuration)

#### Creating our Application class
The first thing we'll need to do is create a new class, which extends the Application
class in the G4mEngine. The Application class can be seen as the main class of the
program. It is here where the main loop will be managed and functions like *tick* and
*render* will be called - but you don't have to worry about that too much. It should be
noted, that the Application class is abstract and has to be sub-classed by your own
implementation.

```java
package com.mydomain.firstapp;

import com.g4mesoft.Application;
import com.g4mesoft.graphic.IRenderer2D;

public class MyApplication extends Application {

	/**
	 * The sub-implementation of Application must
	 * contain an empty default constructor. This
	 * is very important!
	 */
	public MyApplication() {
		// Leave this empty. Unless you know
		// what you're doing. Thanks! :)

		// If you wish to initialize variables
		// you should use the #init() instead.
	}

	@Override
	public void init() {
		super.init();

		// Put some initialization code here.
	}

	@Override
	public void tick() {
		// This should be in charge of all
		// movement and updates made to the
		// program elements. Tick is called
		// with a set interval (default 20
		// ticks per second) which can be
		// changed using #setTps(float).
	}

	@Override
	public void render(IRenderer2D renderer, float dt) {
		// This should be in charge of most
		// if not all drawing to the display.
		// The frames per second is not a set
		// interval, but the minimum fps can
		// be changed using #setMinFps(float).
	}

	public static void main(String[] args) throws Exception {
		// Starts the application on
		// the main thread.
		Application.start(args, MyApplication.class);
	}
}
```

The code above will work, but since we're not drawing anything onto the screen, the
triple-buffered canvas will simply blink in the colors black and white. This can be
fixed by clearing and drawing to the rendering-context every frame. The context is
passed through to the render function in the form of an *IRenderer2D*. All rendering
should be handled by a rendering-context, and it is therefore only adviced to draw
onto the display in the render method, or in other cases when the application supplies
an *IRenderer2D*.

#### Drawing to the canvas
As mentioned earlier we want to make a *Hello world* program, which draws a pink square
onto a white background. This can be done using the *render(IRenderer2D, float)*
function. The following code snippet will draw a 100x100 square onto the center of the
canvas.

```java
@Override
public void render(IRenderer2D renderer, float dt) {
	// Clear viewport to white.
	renderer.setColor(GColor.WHITE);
	renderer.clear();

	// Get dimensions of viewport.
	int w = renderer.getWidth();
	int h = renderer.getHeight();

	renderer.setColor(GColor.PINK);

	// Fill 100x100 square at the
	// center of the viewport.
	renderer.fillRect((w - 100) / 2, (h - 100) / 2, 100, 100);
}
```

**NOTE:** *For the abovewritten code to work, you will have to import
com.g4mesoft.graphic.GColor at the top of the class.*

```java
import com.g4mesoft.graphic.GColor;
```
And that's how simple it is to get a program to run! But it's not the end of our little
example.

#### Display configuration
There's one small, but important, step that we've skipped. Where did we ever set the size
and title of the display? Well - we didn't. The Application didn't find any configuration
for the display, and it therefore chose the default values. Hence you'll see "My Title" as
the title of the 400x400 window. These are all default values. You can change them by
creating a *display.txt* file inside of a folder called *config* or simply by supplying the
*Application* constructor with a path to the display configuration. A display config file
could look as follows:

```
title=Hello World

preferredWidth=720
preferredHeight=540

resizable=true
centered=true

displayMode=normal
displayVisible=true

icon=none
```

There are also other options such as *minimumWidth* and *minimumHeight*. If a value is
not specified in the *config/display.txt* file then the default value for that property
will be used. An example of all the parameters, what they do and what their default values
are can be found in the [config/display-default.txt](res/config/display-default.txt) file.
It should be noted that the display config file has to be a resource which gets added to
the jar itself.

##### Altering display after startup
If one wishes to change the display-mode or other properties of the display after the app
has started, it is possible to do so using the display class. Any instance of *Application*
will have the getter method *getDisplay()* which will return an instance of the display.
The following code snippet will change the display-mode of the display to a borderless
fullscreen window:

```java
getDisplay().setDisplayMode(DisplayMode.FULLSCREEN_BORDERLESS);
```

Other useful functions related to the display can be found in the source code located in
[Display.java](src/com/g4mesoft/graphic/Display.java).

### Capturing user input
There are several ways to capture input from the user in G4mEngine. The currently supported
ways of capturing input is via. the *KeyInputListener* and *MouseInputListener*, which as
their names suggest capture key-input and mouse-input respectively. Other ways to capture
input (such as from a joystick or others) may be added in the future. All user input is
enabled by default and can be disabled using an instance of the application class.

* [Keyboard input](#keyboard-input)

#### Keyboard input
Keyboard input comes in multiple form factors. In fact there are three different ways to
capture key-input.

* KeySingleInput - activated by a single key press.
* KeyComboInput - activated by a combination of keys pressed.
* KeyTypedInput - a way of recording the typed unicode key-inputs.

It is important to note that key input should only be captured during the tick method call.
If the keys are captured during rendering it could lead to unexpected behaviour.

##### Simple key detection
The first and simplest way to capture key-input is using the *KeySingleInput*. The source code
can be found in [KeySingleInput.java](src/com/g4mesoft/input/key/KeySingleInput.java).
This class is used to detect when either of a set of provided keys is pressed. For example a
key that detects when the 'A' or 'D' key is pressed can be initialized as follows:

```java
KeyInput key = new KeySingleInput("A or D", KeyEvent.VK_A, KeyEvent.VK_D);
```

Constructing a *KeyInput* which tracks only the 'A' key can be done by simply discluding
the *KeyEvent.VK_D* part of the constructor.

When a *KeyInput* has been constructed, it can be added to the *KeyInputListener* where
the key will be ready to listen for key-events sent by the display (if the key inputs
are enabled in the *Application*). The following code snippet will add the key to the
*KeyInputListener*:

```java
KeyInputListener.getInstance().addKey(key);
```

Likewise a key can also be removed from the *KeyInputListener* by calling the member function
*removeKey(KeyInput)*.

##### Key combinations
There are also other types of key inputs. In some cases it can be useful to have a key that
is only activated when the user presses multiple keys at once. For this purpose we have the
[KeyComboInput](src/com/g4mesoft/input/key/KeyComboInput.java) implementation. The
*KeyComboInput* is in many ways like the *KeySingleInput* but instead of tracking a single
key it will track multiple combinations of keys. For example making a key-combination
which tracks either 'SHIFT+A' or 'CTRL+D' can be initialized as follows:

```java
KeyInput key = new KeyComboInput("SHIFT+A or CTRL+D",
        new int[] { KeyEvent.VK_SHIFT, KeyEvent.VK_A },
        new int[] { KeyEvent.VK_CTRL,  KeyEvent.VK_D });
```

The *KeyComboInput* can be added and removed from the *KeyInputListener* in the same manner
as the *KeySingleInput*.

##### Example of registering keys
From my own experience, I've found that having all keys in a single class can be a good idea.
For example if you want a game where you have the UP, LEFT, DOWN and RIGHT keys as either
'WASD' or the arrow keys could be achieved as follows:

```java
package com.mydomain.input;

import java.awt.event.KeyEvent;

import com.g4mesoft.input.key.KeyInput;
import com.g4mesoft.input.key.KeyInputListener;
import com.g4mesoft.input.key.KeySingleInput;

/**
 * We use the 'final' keyword, so
 * this class can't be sub-classed.
 */
public final class Keyboard {

	/*
	 * Initialize movement keys.
	 */

	public static final KeyInput UP    = regKey("up",    KeyEvent.VK_UP,    KeyEvent.VK_W);
	public static final KeyInput LEFT  = regKey("left",  KeyEvent.VK_LEFT,  KeyEvent.VK_A);
	public static final KeyInput DOWN  = regKey("down",  KeyEvent.VK_DOWN,  KeyEvent.VK_S);
	public static final KeyInput RIGHT = regKey("right", KeyEvent.VK_RIGHT, KeyEvent.VK_D);

	private Keyboard() {
		// Private constructor. We want no
		// instances of this class.
	}

	/**
	 * A simple wrapper to register keys statically.
	 */
	private static KeyInput regKey(String name, int... keyCodes) {
		KeyInput key = new KeySingleInput(name, keyCodes);
		KeyInputListener.getInstance().addKey(key);
		return key;
	}
}
```

Using the above way of registering keys makes it possible to easily use the keys in any other
class by simply using the static *KeyInput* fields as follows:

```java
public void tick() {
	if (Keyboard.UP.isClicked()) {
		System.out.println("Up has been clicked!");
	}

	if (Keyboard.LEFT.isPressed()) {
		System.out.println("Left is pressed / held!");
	}

	if (Keyboard.DOWN.isReleased()) {
		System.out.println("Down was released. Nice!");
	}
}
```

More information about the *KeyInput* methods (like *isClicked()* etc.) can be found in the
source code documentation. See [KeyInput.java](src/com/g4mesoft/input/key/KeyInput.java).

##### Typed key input
The last way of capturing user input is by recording the typed input of the users keyboard.
This is done using the [KeyTypedInput.java](src/com/g4mesoft/input/key/KeyTypedInput.java)
class. Typed input can be understood as all the keys on the keyboard, which can be translated
into a unicode character. For example this implementation of key-listening is used by the
*TextFieldComposition* in order to record what the user types in the text field. It is
important to note that ASCII code characters are also included in this implementation. This
means that characters such as DELETE and BACKSPACE are also part of the typed keys. These
keys will have to be handled properly as there are no graphical characters associated with
these keys. For more information look at the source code documentation. A typed key can be
added to the *KeyInputListener* using the *addTypedKey(KeyTypedInput)* function. The following
code can be used to record the typed input over several ticks.

```java
@Override
public void init() {
	...

	// Initialize our typed key input.
	typedKey = new KeyTypedInput();

	KeyInputListener.getInstance().addTypedKey(typedKey);
}

...

@Override
public void tick() {
	...

	if (typedKey.hasTypedCharacters()) {
		char[] buffer = typedKey.flushBuffer();
		// Handle buffer of chars
		handleTypedInput(buffer);
	}

	// Make sure we record the
	// user input in the next tick.
	typedKey.recordNextUpdate();
}
```

It is important to note that the above code is very close to the engine core. It is not
recommended using the code unless you know what you're doing. It can quickly become a
mess if the typed characters are not handled correctly as there can be edge cases where
unexpected control-characters could lead to unexpected bugs.

##### Disabling key input
As mentioned earlier, key input is enabled by default. If one wishes to disable the
*KeyInputListener* they should call the *disableKeyInput()* function in the *Application*
class. For example in the *init()* function as follows:

```java
@Override
public void init() {
	super.init();

	...

	disableKeyInput();
}
```

If one wishes to disable a single key input they should instead of the abovewritten code
simply remove the key from the *KeyInputListener*. This can be done during runtime.
