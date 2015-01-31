# Ani

A simple animation library for Java applications.

This library contains an AbstractAnimation to inherit from. It helps to create animations by providing start, duration, progress and finish functionality. The AnimationController helps to control multiple animations. It can update itself by an interval or by your program (e.g. every render frame).


#### How to use the controller
```java
// Create an animation controller with an update interval of 300 milliseconds.
AnimationController animationController = new AnimationController(300);

// Create an instance of our animation.
HelloWorldAnimation myAnimation = new HelloWorldAnimation(new AnimationFinishedListener() {
	@Override
	public void onAnimationFinished() {
		// Do something after the animation.
		System.out.println("onAnimationFinished()");
	}
});

// Add the animation.
animationController.addAnimation(myAnimation);
```

#### How to write a custom animation.
```java
/**
 * An animation that inherits form AbstractAnimation and animates "Hello World" to the console.
 */
public static class HelloWorldAnimation extends AbstractAnimation{

	// the text we want to animate
	private String textToPrint;

	/**
	 * The Hello World animation.
	 */
	public HelloWorldAnimation(AnimationFinishedListener pAnimationFinishedListener) {
		// set the duration of the animation to its parent
		super(5000, pAnimationFinishedListener);
		textToPrint = "Hello World!!!";
	}

	/**
	 * Implement what should happen while animating.
	 * @param pProgress The progress of the animation.
	 */
	@Override
	public void onProgress(float pProgress) {
		// print the text using the progress as length.
		System.out.println(textToPrint.substring(0, (int) (pProgress * textToPrint.length())));
	}

	@Override
	public void onFinish() {
		// animation is finished.
		textToPrint = null;
		System.out.println("onFinish()");
	}

	@Override
	public void onStart() {
		// animation starts.
		textToPrint += "!!";
		System.out.println("onStart()");
	}
}
```

### The animation lifecycle.

The folowing table shows the function calls of a running animation with 3 loops. The count of the 'onProgress' calls is depending on the update rate of the AnimationController and the duration of the animation.

function call | parameter value
-------------- | -------
onStart |
onProgress | 0.0
onProgress | 0.36666667
onProgress | 0.76666665
onProgress | 1.0
onLoopStart | 1
onProgress | 0.0
onProgress | 0.4
onProgress | 0.73333335
onProgress | 1.0
onLoopStart | 2
onProgress | 0.0
onProgress | 0.36666667
onProgress | 0.76666665
onProgress | 1.0
onLoopStart | 3
onProgress | 0.0
onProgress | 0.4
onProgress | 0.76666665
onProgress | 1.0
onFinish |
