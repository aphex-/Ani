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

	/**
	 * The animations to control.
	 */
	private List<AbstractAnimation> animations;

	/**
	 * A cache of animations to remove.
	 */
	private List<AbstractAnimation> cacheAnimationsToRemove;

	private AnimationFinishedListener allAnimationsFinishedListener;

	/**
	 * A time to schedule animation updates or null.
	 */
	private Timer timer = null;
	private float timeFactor = 1.0f;

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
	 * Sets the time factor for all animations controlled by this.
	 * 1.0 is standard time.
	 * @param pTimeFactor
	 */
	public void setTimeFactor(float pTimeFactor) {

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

	public void setAllAnimationFinishedListener(AnimationFinishedListener pListener) {
		allAnimationsFinishedListener =  pListener;
	}
}
