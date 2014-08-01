import com.nukethewhales.tools.ani.AbstractAnimation;
import com.nukethewhales.tools.ani.AnimationController;
import com.nukethewhales.tools.ani.AnimationFinishedListener;

/**
 * @author luca.hofmann
 */
public class AniHelloWorld {

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

	/**
	 * Main entry point.
	 */
	public static void main(String[ ] args) {

		// Create a animation controller with an update interval of 300 milliseconds.
		AnimationController animationController = new AnimationController(300);

		// create an instance of our animation.
		HelloWorldAnimation myAnimation = new HelloWorldAnimation(new AnimationFinishedListener() {
			@Override
			public void onAnimationFinished() {
				// Do something after the animation.
				System.out.println("onAnimationFinished()");
			}
		});

		// add the animation
		animationController.addAnimation(myAnimation);
	}



}
