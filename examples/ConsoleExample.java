import com.nukethemoon.tools.ani.BaseAnimation;
import com.nukethemoon.tools.ani.Ani;
import com.nukethemoon.tools.ani.AnimationFinishedListener;

import java.lang.Override;

/**
 * Example to print on the console.
 */
public class ConsoleExample {

	/**
	 * Animates "Hello World" to the console.
	 */
	public static class HelloWorldAnimation extends BaseAnimation {

		private String textToPrint;

		public HelloWorldAnimation(AnimationFinishedListener pAnimationFinishedListener) {
			// set the duration of the animation to its parent
			super(1000, pAnimationFinishedListener);
			textToPrint = "Hello World!!!";
			setLoopLength(1);
		}

		@Override
		public void onProgress(float pProgress) {
			// print the text using the progress as length.
			System.out.println(textToPrint.substring(0, (int) (pProgress * textToPrint.length())));
		}
	}

	public static void main(String[ ] args) {
		Ani ani = new Ani(10);
		HelloWorldAnimation myAnimation = new HelloWorldAnimation(new AnimationFinishedListener() {
			@Override
			public void onAnimationFinished(BaseAnimation pAnimation) {
				System.out.println("onAnimationFinished()");
			}
		});
		// add the animation
		ani.add(myAnimation);
	}
}
