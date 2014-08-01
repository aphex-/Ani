package com.nukethewhales.tools.ani;

import java.util.ArrayList;
import java.util.List;

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

	/**
	 * Creates a new animation controller.
	 */
	public AnimationController() {
		this.animations = new ArrayList<AbstractAnimation>();
		this.cacheAnimationsToRemove = new ArrayList<AbstractAnimation>();
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
