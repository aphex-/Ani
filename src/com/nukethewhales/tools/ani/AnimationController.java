package com.nukethewhales.tools.ani;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * A controller to add and update animations.
 *
 * @author lucahofmann@gmx.net
 */
public class AnimationController {

	private static float GLOBAL_ANIMATION_TIME_FACTOR = 1.0f;

	/**
	 * The animations to control.
	 */
	private final List<AbstractAnimation> animations;

	/**
	 * A cache of animations to remove.
	 */
	private List<AbstractAnimation> cacheAnimationsToRemove;

	private AnimationFinishedListener allAnimationsFinishedListener;

	/**
	 * A time to schedule animation updates or null.
	 */
	private Timer timer = null;

	/**
	 * Creates a new animation controller. Use this if you want to update animations
	 * on your own (e.g. every render frame)
	 *
	 */
	public AnimationController() {
		animations = Collections.synchronizedList(new ArrayList<AbstractAnimation>());
		cacheAnimationsToRemove = new ArrayList<AbstractAnimation>();
	}

	/**
	 * Creates a new animation controller. Updates it self
	 * for ever pInterval in milliseconds.
	 * @param pInterval The update interval in milliseconds.
	 */
	public AnimationController(int pInterval) {
		this();
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
		boolean didHandleAnimation = false;
		for (AbstractAnimation animation : animations) {
			if (animation.isFinished() && animation.getLoop() != -1) {
				cacheAnimationsToRemove.add(animation);
			} else {

				if (animation.isFinished() && animation.getLoop() == -1) {
					animation.start();
				}

				if (animation.isStarted()) {
					animation.update();
					didHandleAnimation = true;
				}
			}
		}
		for (AbstractAnimation animation : cacheAnimationsToRemove) {
			animation.callAnimationFinishedListener();
			animations.remove(animation);

			if (animations.size() == 0 && allAnimationsFinishedListener != null) {
				allAnimationsFinishedListener.onAnimationFinished();
			}
		}
		cacheAnimationsToRemove.clear();
		return didHandleAnimation;
	}

	/**
	 * Finishes the assigned animation.
	 * @param pAnimation The animatipon to finish.
	 */
	public void finishAnimation(AbstractAnimation pAnimation) {
		if (pAnimation != null) {
			pAnimation.onFinish();
			animations.remove(pAnimation);
		}
	}

	/**
	 * Sets a listener that will be called if all animations of this controller are finished.
	 * @param pListener The listener to call.
	 */
	public void setAllAnimationFinishedListener(AnimationFinishedListener pListener) {
		allAnimationsFinishedListener =  pListener;
	}

	/**
	 * Stets a global factor to the animation speed.
	 * Value 1.0 is the standard value.
	 * The assigned value must be higher than 0.0.
	 * @param pTimeFactor The time factor to set for all animations.
	 */
	public static void setGlobalAnimationTimeFactor(float pTimeFactor) {
		if (pTimeFactor > 0.0f) {
			GLOBAL_ANIMATION_TIME_FACTOR = pTimeFactor;
		}
	}

	/**
	 * Gets the global time factor used for all animations.
	 * @return The global time factor.
	 */
	public static float getGlobalAnimationTimeFactor() {
		return GLOBAL_ANIMATION_TIME_FACTOR;
	}
}
