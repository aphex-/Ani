package com.nukethemoon.tools.ani;

import com.nukethemoon.tools.ani.AbstractAnimation;
import com.nukethemoon.tools.ani.AnimationController;
import com.nukethemoon.tools.ani.AnimationFinishedListener;

import java.lang.Override;

/**
 * @author luca.hofmann
 */
public class ConsoleExample {

	/**
	 * An animation that inherits form AbstractAnimation and animates "Hello World" to the console.
	 */
	public static class HelloWorldAnimation extends AbstractAnimation{

		private String textToPrint;

		/**
		 * The Hello World animation.
		 */
		public HelloWorldAnimation(AnimationFinishedListener pAnimationFinishedListener) {
			// set the duration of the animation to its parent
			super(1000, pAnimationFinishedListener);
			textToPrint = "Hello World!!!";
			setLoopLength(1);
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

		@Override
		public void onLoopStart(int pLoopIndex) {
			// implementation not needed in this example
		}
	}

	public static void main(String[ ] args) {

		AnimationController animationController = new AnimationController(10);

		HelloWorldAnimation myAnimation = new HelloWorldAnimation(new AnimationFinishedListener() {
			@Override
			public void onAnimationFinished(AbstractAnimation pAnimation) {
				System.out.println("onAnimationFinished()");
			}
		});

		// add the animation
		animationController.addAnimation(myAnimation);
	}



}
