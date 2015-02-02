# Ani

### A simple animation library for every purpose.

If you want to write an animmation you usually need something like an **update** method where you apply a **progress** to the stuff you want to animate. In most cases you also want to get noticed if the animation is **finished**. Beside that it's a good practice to separate such logic from your productive code. **Ani** does that for you and helps focusing on the animation logic itself. Just write your own animation class and extend <a href="https://github.com/aphex-/Ani/blob/master/src/com/nukethemoon/tools/ani/BaseAnimation.java">BaseAnimation</a>.

**Ani** is a simple tool that does not depend on other libraries. You can use it in combination with technologies like <a href="http://libgdx.badlogicgames.com/">libGDX</a>, **Swing** or the **Android SDK** etc.

You can find the **jar** at <a href="https://github.com/aphex-/Ani/tree/master/build/jar">build/jar/</a><br>
Also take a look at the <a href="https://github.com/aphex-/Ani/tree/master/examples">examples</a>.

### How to write a custom animation?
This example shows a custom animation that extends <a href="https://github.com/aphex-/Ani/blob/master/src/com/nukethemoon/tools/ani/BaseAnimation.java">BaseAnimation</a>. It simply fades in a Graphic object that has a method to *setAlpha*. (Note that the Graphic object is nothing that comes from **Ani**)
```java
/**
 * An animation to fade in a Graphic.
 */
public static class SimpleFadeAnimation extends BaseAnimation {

	private Graphic graphic;

	public SimpleFadeAnimation(Graphic pGrapic) {
		super(5000); // sets the duration of 5 sec
		graphic = pGraphic;
	}

	// called constantly while animating
	@Override
	public void onProgress(float pProgress) {
		graphic.setAlpha(pProgress) // progress from 0.0 to 1.0
	}
}
```

The **onProgress** method will be called constantly until the animation is finished. The parameter value starts with 0.0 and ends with 1.0. You can also override lifecycle methods like *onStart* and *onFinish*.
 

## How to start an animation?
To run your animation simply create an instance of it, create an instnace of <a href="https://github.com/aphex-/Ani/blob/master/src/com/nukethemoon/tools/ani/Ani.java">Ani</a> and add the animation to it.
```java
// Create an animation controller with an update interval of 30 milliseconds.
Ani ani = new Ani(30);

// Create an instance of our animation.
SimpleFadeAnimation myAnimation = new SimpleFadeAnimation(myGraphic);

// Add the animation.
ani.add(myAnimation);
```
The animation controller <a href="https://github.com/aphex-/Ani/blob/master/src/com/nukethemoon/tools/ani/Ani.java">Ani</a> is reusable and can handle multiple animations at once. In the example we use an update rate of 30 milliseconds as constructor parameter. This means that the controller updates it self. If no update rate is set you have to update on your own (e.g. every render loop) by calling *ani.update()*. 

## How to get noticed if the animation has finished?
To implement logic that should be executed after the animation you can simply use the <a href="https://github.com/aphex-/Ani/blob/master/src/com/nukethemoon/tools/ani/AnimationFinishedListener.java">AnimationFinishedListener</a>.
```
SimpleFadeAnimation myAnimation = new SimpleFadeAnimation(myGraphic);

myAnimation.addFinishedListener(new AnimationFinishedListener() {
		@Override
		public void onAnimationFinished(AbstractAnimation pAnimation) {
			// add your code here
		}
	});
```

### The animation lifecycle.

The folowing table shows the function calls of a running animation with 3 loops. The count of the 'onProgress' calls is depending on the update rate of the animation controller <a href="https://github.com/aphex-/Ani/blob/master/src/com/nukethemoon/tools/ani/Ani.java">Ani</a> and the duration of the animation.

function call | parameter value
-------------- | -------
onStart |
 | 
onProgress | 0.0
onProgress | 0.36666667
onProgress | 0.76666665
onProgress | 1.0
 |
onLoopStart | 1
 | 
onProgress | 0.0
onProgress | 0.4
onProgress | 0.73333335
onProgress | 1.0
 | 
onLoopStart | 2
 | 
onProgress | 0.0
onProgress | 0.36666667
onProgress | 0.76666665
onProgress | 1.0
 | 
onLoopStart | 3
 | 
onProgress | 0.0
onProgress | 0.4
onProgress | 0.76666665
onProgress | 1.0
 | 
onFinish |

### Licence
```
The MIT License (MIT)

Copyright (c) 2014 Luca Hofmann

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```
