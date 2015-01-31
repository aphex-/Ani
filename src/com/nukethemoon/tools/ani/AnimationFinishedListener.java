package com.nukethemoon.tools.ani;

/**
 * A listener that can be used to listen to an animation to finish.
 *
* @author NukeTheMoon
*/
public interface AnimationFinishedListener {
	/**
	 * Callable on animation finished.
	 */
	void onAnimationFinished(AbstractAnimation pAnimation);
}
