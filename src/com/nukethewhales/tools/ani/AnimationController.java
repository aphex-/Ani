package com.nukethewhales.tools.ani;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * A controller to add and update animations.
  *
 * @author lucahofmann@gmx.net
 */
public class AnimationController {

	/**
	 * The animations to control.
	 */
	private List<AbstractAnimation> animations = new ArrayList<AbstractAnimation>();;

	/**
	 * A cache of animations to remove.
	 */
	private List<AbstractAnimation> cacheAnimationsToRemove = new ArrayList<AbstractAnimation>();

	/**
	 * A time to schedule animation updates or null.
	 */
	private Timer timer = null;

	/**
	 * Creates a new animation controller. Does not update
	 * itself. Use this if you want to update animations
	 * on your own (e.g. every render frame)
	 *
	 */
	public AnimationController() { }

	/**
	 * Creates a new animation controller. Updates it self
	 * for ever pInterval in milliseconds.
	 * @param pInterval The update interval in milliseconds.
	 */
	public AnimationController(int pInterval) {
		timer = new Timer();
		TimerTask timerTask = new TimerTask() {
			@Override
			public void run() {
				updateAnimations();
			}
		};
		timer.schedule(timerTask, pInterval, pInterval);
	}

	/**
	 * Adds an animation.
	 *
	 * @param pAnimation The animation to add.
	 */
	public final void addAnimation(final AbstractAnimation pAnimation) {
		animations.add(pAnimation.start());
	}

	/**
	 * Adds an animation with a start delay.
	 *
	 * @param pStartDelayMillis A delay to start the animation in milliseconds.
	 * @param pAnimation The animation to add.
	 */
	public final void addAnimation(final int pStartDelayMillis, final AbstractAnimation pAnimation) {
		new java.util.Timer().schedule(
				new java.util.TimerTask() {
					@Override
					public void run() {
						addAnimation(pAnimation);
					}
				},
				pStartDelayMillis
		);
	}

	/**
	 * Calls the animation update method or removes the animation if it is done.
	 * It also calls the "AnimationFinishListener" of the animation if it is done.
	 *
	 * @return False if no animation has been handled.
	 */

	public final boolean updateAnimations() {
		boolean didHandleAnyAnimation = false;

		// handle current animations
		for (AbstractAnimation animation : animations) {
			if (animation.isFinished()) {
				cacheAnimationsToRemove.add(animation);
			} else {
				if (animation.isStarted()) {
					animation.update();
					didHandleAnyAnimation = true;
				}
			}
		}

		// remove finished animations.
		for (AbstractAnimation animation : cacheAnimationsToRemove) {
			animation.callAnimationFinishedListener();
			animations.remove(animation);
		}
		cacheAnimationsToRemove.clear();

		return didHandleAnyAnimation;
	}
}
