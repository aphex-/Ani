# Ani

A simple animation tool for Java applications.

This library contains an AbstractAnimation to inherit from. It helps to create animations by providing start, duration, progress and finish functionality. The AnimationController helps to control multiple animations. It can update itself by an interval or by your program (e.g. every render frame).


#### How to use the controller
```java
// Create a animation controller with an update interval of 300 milliseconds.
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
