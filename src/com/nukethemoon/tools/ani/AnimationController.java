package com.nukethemoon.tools.ani;

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
	 * The global time factor used for all animations.
	 */
	private static float globalAnimationTimeFactor = 1.0f;

	/**
	 * The animations to control.
	 */
	private final List<AbstractAnimation> animations;

	/**
	 * A cache of animations to remove.
	 */
	private List<AbstractAnimation> tmpAnimationsToFinish;


	private List<AbstractAnimation> tmpAnimationsToAdd;

	private List<AbstractAnimation> tmpAnimationsToRemove;

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
		animations = new ArrayList<AbstractAnimation>();
		tmpAnimationsToFinish = new ArrayList<AbstractAnimation>();
		tmpAnimationsToAdd = new ArrayList<AbstractAnimation>();
		tmpAnimationsToRemove = new ArrayList<AbstractAnimation>();
	}

	/**
	 * Creates a new animation controller. Updates it self
	 * every pInterval milliseconds.
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
	 * @return This instance.
	 */
	public final AnimationController addAnimation(final AbstractAnimation pAnimation) {
		if (pAnimation != null) {
			tmpAnimationsToAdd.add(pAnimation.start());
		}
		return this;
	}

	/**
	 * Adds an animation with a start delay.
	 *
	 * @param pStartDelayMillis A delay to start the animation in milliseconds.
	 * @param pAnimation The animation to add.
	 * @return This instance.
	 */
	public final AnimationController addAnimation(final int pStartDelayMillis, final AbstractAnimation pAnimation) {
		new java.util.Timer().schedule(
				new java.util.TimerTask() {
					@Override
					public void run() {
						addAnimation(pAnimation);
					}
				},
				pStartDelayMillis
		);
		return this;
	}

	/**
	 * Adds multiple animations and a listener to fall if all are finished.
	 * @param pAnimations The animations.
	 * @param pAllAnimationFinishedListener The listener to call is all animations are finished.
	 * @return This instance.
	 */
	public final AnimationController addAnimations(final AbstractAnimation pAnimations[], final AnimationFinishedListener pAllAnimationFinishedListener) {
		if (pAnimations == null || pAnimations.length == 0) {
			return this;
		}
		AnimationsFinishedCollector finishedCollector = null;
		if (pAllAnimationFinishedListener != null) {
			finishedCollector = new AnimationsFinishedCollector(pAnimations.length,
					pAllAnimationFinishedListener);
		}
		for (AbstractAnimation animation : pAnimations) {
			if (finishedCollector != null) {
				animation.addFinishedListener(finishedCollector);
			}
			this.addAnimation(animation);
		}
		return this;
	}

	/**
	 * Adds multiple animations.
	 * @param pAnimations The animations.
	 * @return This instance.
	 */
	public final AnimationController addAnimations(final AbstractAnimation pAnimations[]) {
		for (AbstractAnimation animation : pAnimations) {
			this.addAnimation(animation);
		}
		return this;
	}

	/**
	 * Adds a sequence of animations that will be added one after another.
	 * @param pAnimations The animation sequence to add.
	 * @param pSequenceFinishedListener A listener that gets called if the sequence has ended.
	 * @return This instance.
	 */
	public final AnimationController addAnimationSequence(final AbstractAnimation[] pAnimations,
														  final AnimationFinishedListener pSequenceFinishedListener) {
		if (pAnimations != null && pAnimations.length > 0) {
			if (pAnimations.length == 1) {
				addAnimation(pAnimations[0]);
				return this;
			}
			for (int i = 0; i < pAnimations.length - 1; i++) {
				final AbstractAnimation animation = pAnimations[i];
				final int finalI = i;
				animation.addFinishedListener(new AnimationFinishedListener() {
					@Override
					public void onAnimationFinished(AbstractAnimation pAnimation) {
						addAnimation(pAnimations[finalI + 1]);
					}
				});
			}
			pAnimations[pAnimations.length - 1].addFinishedListener(pSequenceFinishedListener);
			addAnimation(pAnimations[0]);
		}
		return this;
	}

	/**
	 * Adds a sequence of animations that will be added one after another.
	 * @param pAnimations The animation sequence to add.
	 * @return This instance.
	 */
	public final AnimationController addAnimationSequence(final AbstractAnimation[] pAnimations) {
		addAnimationSequence(pAnimations, null);
		return this;
	}

	/**
	 * Calls the animation update method or removes the animation if it is done.
	 * It also calls the "AnimationFinishListener" of the animation if it is done.
	 *
	 * @return False if no animation has been handled.
	 */

	public final boolean updateAnimations() {
		boolean didHandleAnimation = false;

		// to avoid concurrent modification
		if (tmpAnimationsToRemove.size() > 0) {
			for (AbstractAnimation animation: tmpAnimationsToRemove) {
				animations.remove(animation);
			}
			tmpAnimationsToRemove.clear();
		}

		// to avoid concurrent modification
		if (tmpAnimationsToAdd.size() > 0) {
			for (AbstractAnimation animation: tmpAnimationsToAdd) {
				animations.add(animation);
			}
			tmpAnimationsToAdd.clear();
		}

		for (AbstractAnimation animation : animations) {
			if (animation.isFinished() && animation.getRemainingLoopCount() == 0) {
				tmpAnimationsToFinish.add(animation);
			} else {
				animation.update();
				didHandleAnimation = true;
			}
		}

		// to avoid concurrent modification
		for (AbstractAnimation animation : tmpAnimationsToFinish) {
			animations.remove(animation);
			animation.callAnimationFinishedListeners();
			if (animations.size() == 0 && allAnimationsFinishedListener != null) {
				allAnimationsFinishedListener.onAnimationFinished(null);
			}
		}

		tmpAnimationsToFinish.clear();
		return didHandleAnimation;
	}

	/**
	 * Stops the assigned animation.
	 * @param pAnimation The animation to stop.
	 * @return This instance.
	 */
	public AnimationController forceStopAnimation(AbstractAnimation pAnimation) {
		if (pAnimation != null) {
			pAnimation.onFinish();
			tmpAnimationsToRemove.add(pAnimation);
		}
		return this;
	}

	/**
	 * Sets a listener that will be called if all animations of this controller are finished.
	 * @param pListener The listener to call.
	 * @return This instance.
	 */
	public AnimationController setAllAnimationFinishedListener(AnimationFinishedListener pListener) {
		allAnimationsFinishedListener =  pListener;
		return this;
	}

	/**
	 * Stets a global factor to the animation speed.
	 * Value 1.0 is the standard value.
	 * The assigned value must be higher than 0.0.
	 * @param pTimeFactor The time factor to set for all animations.
	 */
	public static void setGlobalAnimationTimeFactor(float pTimeFactor) {
		if (pTimeFactor > 0.0f) {
			globalAnimationTimeFactor = pTimeFactor;
		}
	}

	/**
	 * Gets the global time factor used for all animations.
	 * @return The global time factor.
	 */
	public static float getGlobalAnimationTimeFactor() {
		return globalAnimationTimeFactor;
	}


}
