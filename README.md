# Ani

currently under development

### A simple animation library for Java applications.

If you want to write an animmation you usually need something like an **update** method where you apply a **progress** to the stuff you want to animate. In most cases you also want to get noticed if the animation is **finished**. It's also a good practice to separate such logic from your productive code.

**Ani** tries to follow the philosophy to 'do just a small thing but do this right'. It helps you to focus on the animation logic itself. Just write your own animation class and inerhit from *AbstractAnimation*. You need to override the methods *onStart*, *onProgress* and *onFinish* to implement the animation.

### How to write a custom animation?
This example shows a custom animation that inherits from *AbstractAnimation*. It simply fades in a Graphic that has a method **setAlpha**.
```java
/**
 * An animation to fade in a Graphic.
 */
public static class SimpleFadeAnimation extends AbstractAnimation{

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

	// called once at the end
	@Override
	public void onFinish() {
		graphic.setAlpha(1.0f); 
	}

	// called once at start
	@Override
	public void onStart() {
		graphic.setAlpha(0.0f); 
	}
}
```
This example shows the lifecycle of the animation and the fade in logic. Note that the Graphic object is nothing that comes from **Ani**.


## How to start an animation?
To run your animation simply create an instance of your animation, create an **AnimationController** and add the animation to the controller.
```java
// Create an animation controller with an update interval of 30 milliseconds.
AnimationController animationController = new AnimationController(30);

// Create an instance of our animation.
SimpleFadeAnimation myAnimation = new SimpleFadeAnimation(myGraphic);

// Add the animation.
animationController.addAnimation(myAnimation);
```
The animation controller is reusable and must not be created for every animation. It can handle multiple animations at once.

## How to get noticed if the animation has finished?
To implement logic that should be executed after the animation you can use the simple **AnimationFinishedListener**.


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
