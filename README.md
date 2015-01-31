# Ani

currently under development

### A simple animation library for Java applications.

If you want to write an animmation you usually need something like an **update** method where you apply a **progress** to the stuff you want to animate. In most cases you also want to get noticed if the animation is finished. It's also a good practice to separate such logic from your productive code.

**Ani** helps you to focus on the animation logic itself. Just write your own animation class and inerhit from *AbstractAnimation*. You need to override the methods *onStart*, *onProgress* and *onFinish* to implement the animation.

### How to write a custom animation.
This example shows a custom animation that inherits from *AbstractAnimation*. It writes a text to the console animated char by char.
```java
/**
 * An animation that inherits form AbstractAnimation and animates "Hello World" to the console.
 */
public static class HelloWorldAnimation extends AbstractAnimation{

	private String textToPrint;

	public HelloWorldAnimation() {
		super(5000); // sets the duration of 5 sec
	}

	/**
	 * Implement what should happen while animating.
	 * @param pProgress The progress between 0.0 and 1.0.
	 */
	@Override
	public void onProgress(float pProgress) {
		// print the text using the progress as length.
		System.out.println(textToPrint.substring(0, (int) (pProgress * textToPrint.length())));
	}

	@Override
	public void onFinish() {
		textToPrint = null;
		System.out.println("onFinish()");
	}

	@Override
	public void onStart() {
		textToPrint = "Hello World!!!";
		System.out.println("onStart()");
	}
}
```

## How to use the controller
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



### The animation lifecycle.

The folowing table shows the function calls of a running animation with 3 loops. The count of the 'onProgress' calls is depending on the update rate of the AnimationController and the duration of the animation.

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
