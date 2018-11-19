# G4mEngine

A lightweight general purpose 2D game engine for practice and simple game
implementations in Java. Using no external libraries.

## Installation

There are several ways you can add G4mEngine to your project. But before we get
to any of that there are several things you need to make sure of. I will be going
through the simplest way to add Java to your project.

### Before installation
Before you install the engine you should make sure that you have the following
software installed on your computer. G4mEngine is a lightweight java project and
does not need any third party (except for Java JRE) software to work, however it
is still advised that the following is to be installed:

* Java JDK and JRE version 1.8 or above should be
  [installed](https://www.oracle.com/technetwork/java/javase/downloads/index.html).
* Eclipse IDE or any other integrated development environment for Java should
  be [installed](https://www.eclipse.org/downloads/).

It is possible to use the project without the use of any IDE, but as there are
resource files and many class files, it is advised to use an IDE. The project is
originally written utilizing features in the Eclipse IDE, but other IDE's such as
IntelliJ may also work if the project is imported as an Eclipse project.

### Download project
G4mEngine is a very lightweight implementation, and is therefore very easy to add
to any new or ongoing java project without adding too much complexity. Firstly you'll
need to download the project on [GitHub](https://github.com/G4me4u/g4mengine). The
entire master zip should be downloaded. Alternatively, you can fork the project and
pull it to your computer using git. Just make sure you have the entire repository on
your computer. If you downloaded the Zip, you should un-zip the project into a new
folder.

### Import and compile
Once you've downloaded the project you have to compile the Java files. Because the
engine is so lightweight, it is not pre-compiled like many other projects, but that
shouldn't be an issue. If you're using Eclipse all you have to do is import an existing
project. This can be done by selecting *File->Import...->General->Existing Projects
into Workspace*, browsing the root directory of the newly created folder with the
project files and clicking *Finish*. The Eclipse project wizard will automatically set
up the project with the necessary resources. Any other IDE should be nearly the same.
In IntelliJ you should select the *Import Eclipse project* option. And that's it!
You can now compile and use the project as a dependency to any of your own Java
projects!

If you want to use G4mEngine as a library jar file (and not a project dependency) you
can do so by compiling the G4mEngine source files into a single jar file. This is done
in the Eclipse IDE by selecting the *File->Export...->Java->JAR file* option.

**NOTE:** *If the project is not imported as an Eclipse project you should make sure
that the /res folder is selected as a resource folder, and the contents are added to the
root of the jar output file.*

## Usage
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

The first thing we'll need to do is create a new class, which extends the Application
class in the G4mEngine. The Application class can be seen as the main class of the
program. It is here where the main loop will be managed and functions like *tick* and
*render* will be called. It should be noted, that the Application class is abstract and
has to be sub-classed by your own implementation.

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

	public void init() {
		super.init();

		// Put some initialization code here.
	}

	public void tick() {
		// This should be in charge of all
		// movement and updates made to the
		// program elements. Tick is called
		// with a set interval (default 20
		// ticks per second) which can be
		// changed using #setTps(float).
	}

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

As mentioned earlier we want to make a *Hello world* program, which draws a pink square
onto a white background. This can be done using the *render(IRenderer2D, float)*
function. The following code snippet will draw a 100x100 square onto the center of the
canvas.

```java
	public void render(IRenderer2D renderer, float dt) {
		// Clear viewport to white.
		renderer.setColor(Color.WHITE);
		renderer.clear();

		// Get dimensions of viewport.
		int w = renderer.getWidth();
		int h = renderer.getHeight();

		renderer.setColor(Color.PINK);

		// Fill 100x100 square at the
		// center of the viewport.
		renderer.fillRect((w - 100) / 2, (h - 100) / 2, 100, 100);
	}
```

**NOTE:** *For the abovewritten code to work, you will have to import
java.awt.Color at the top of the class.*

```java
import java.awt.Color;
```

And that's how simple it is to get a program to run! But it's not the end of our little
example. There's one small, but important, step that we've skipped. Where did we ever set
the size and title of the display? Well - we didn't. The Application didn't find any
configuration for the display, and it therefore chose the default values. Hence you'll see
"My Title" as the title of the 400x400 window. These are all default values. You can change
them by creating a *display.txt* file inside of a folder called *config* or simply by
supplying the *Application* constructor with a path to the display configuration. A display
config file could look as follows:

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

There are also other options such as *minimumWidth* and *minimumHeight*. If a value is not
specified in the *config/display.txt* file then the default value for that property will be
used. An example of all the parameters, what they do and what their default values are can
be found in the [config/display-default.txt](res/config/display-default.txt) file. It should
be noted that the display config file has to be a resource which gets added to the jar
itself.
