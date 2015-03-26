package com.nukethemoon.tools.ani;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Timer;
import java.util.TimerTask;

/**
 * A controller to add and update animations.
 *
 * @author lucahofmann@gmx.net
 */
public class Ani {

	/**
	 * The global time factor used for all animations.
	 */
	private static float globalAnimationTimeFactor = 1.0f;

	/**
	 * The animations to control.
	 */
	private final BaseAnimation[] animations;


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
	public Ani() {
		animations = new BaseAnimation[200];
	}

	/**
	 * Creates a new animation controller. Updates it self
	 * every pInterval milliseconds.
	 * @param pInterval The update interval in milliseconds.
	 */
	public Ani(int pInterval) {
		this();
		timer = new Timer();
		TimerTask timerTask = new TimerTask() {
			@Override
			public void run() {
				update();
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
	public final Ani add(final BaseAnimation pAnimation) {
		if (pAnimation != null) {
			for (int i = 0; i < animations.length; i++) {
				if (animations[i] == null) {
					animations[i] = pAnimation;
				}
			}
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
	public final Ani add(final int pStartDelayMillis, final BaseAnimation pAnimation) {
		new java.util.Timer().schedule(
				new java.util.TimerTask() {
					@Override
					public void run() {
						add(pAnimation);
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
	public final Ani add(final BaseAnimation pAnimations[], final AnimationFinishedListener pAllAnimationFinishedListener) {
		if (pAnimations == null || pAnimations.length == 0) {
			return this;
		}
		AnimationsFinishedCollector finishedCollector = null;
		if (pAllAnimationFinishedListener != null) {
			finishedCollector = new AnimationsFinishedCollector(pAnimations.length,
					pAllAnimationFinishedListener);
		}
		for (BaseAnimation animation : pAnimations) {
			if (finishedCollector != null) {
				animation.addFinishedListener(finishedCollector);
			}
			this.add(animation);
		}
		return this;
	}

	/**
	 * Adds multiple animations.
	 * @param pAnimations The animations.
	 * @return This instance.
	 */
	public final Ani add(final BaseAnimation pAnimations[]) {
		for (BaseAnimation animation : pAnimations) {
			this.add(animation);
		}
		return this;
	}

	/**
	 * Adds a sequence of animations that will be added one after another.
	 * @param pAnimations The animation sequence to add.
	 * @param pSequenceFinishedListener A listener that gets called if the sequence has ended.
	 * @return This instance.
	 */
	public final Ani addSequence(final BaseAnimation[] pAnimations,
								 final AnimationFinishedListener pSequenceFinishedListener) {
		if (pAnimations != null && pAnimations.length > 0) {
			if (pAnimations.length == 1) {
				add(pAnimations[0]);
				return this;
			}
			for (int i = 0; i < pAnimations.length - 1; i++) {
				final BaseAnimation animation = pAnimations[i];
				final int finalI = i;
				animation.addFinishedListener(new AnimationFinishedListener() {
					@Override
					public void onAnimationFinished(BaseAnimation pAnimation) {
						add(pAnimations[finalI + 1]);
					}
				});
			}
			pAnimations[pAnimations.length - 1].addFinishedListener(pSequenceFinishedListener);
			add(pAnimations[0]);
		}
		return this;
	}

	/**
	 * Adds a sequence of animations that will be added one after another.
	 * @param pAnimations The animation sequence to add.
	 * @return This instance.
	 */
	public final Ani addSequence(final BaseAnimation[] pAnimations) {
		addSequence(pAnimations, null);
		return this;
	}

	/**
	 * Calls the animation update method or removes the animation if it is done.
	 * It also calls the "AnimationFinishListener" of the animation if it is done.
	 *
	 * @return False if no animation has been handled.
	 */

	public final boolean update() {
		boolean didHandleAnimation = false;
		for (int i = 0; i < animations.length; i++) {
			BaseAnimation animation = animations[i];
			if (animation != null) {
				if (animation.isFinished() && animation.getRemainingLoopCount() == 0) {
					animations[i] = null;
					animation.callAnimationFinishedListeners();
					if (getAnimationCount() == 0 && allAnimationsFinishedListener != null) {
						allAnimationsFinishedListener.onAnimationFinished(null);
					}

				} else {
					animation.update();
					didHandleAnimation = true;
				}
			}
		}
		return didHandleAnimation;
	}

	/**
	 * Gets the count of all animations.
	 * @return The count.
	 */
	public int getAnimationCount() {
		int count = 0;
		for (BaseAnimation animation : animations) {
			if (animation != null) {
				count++;
			}
		}
		return count;
	}

	/**
	 * Gets the index of an animation or -1
	 * @param pAnimation The animation to get the index from.
	 * @return The index or -1
	 */
	private int getIndexOf(BaseAnimation pAnimation) {
		for (int i = 0; i < animations.length; i++) {
			if (animations[i] == pAnimation) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Removes the animation.
	 * @param pAnimation The animation to remove.
	 * @return True if the animation was found.
	 */
	private boolean remove(BaseAnimation pAnimation) {
		int indexOf = getIndexOf(pAnimation);
		if (indexOf > -1) {
			animations[indexOf] = null;
			return true;
		}
		return false;
	}

	/**
	 * Stops the assigned animation.
	 * @param pAnimation The animation to stop.
	 * @return This instance.
	 */
	public Ani forceStop(BaseAnimation pAnimation) {
		if (pAnimation != null) {
			pAnimation.onFinish();
			pAnimation.callAnimationFinishedListeners();
			remove(pAnimation);
		}
		return this;
	}

	/**
	 * Stops all animations.
	 * @return This instance.
	 */
	public Ani forceStop() {
		for (BaseAnimation animation : animations) {
			if (animation != null) {
				forceStop(animation);
			}
		}
		return this;
	}

	/**
	 * Sets a listener that will be called if all animations of this controller are finished.
	 * @param pListener The listener to call.
	 * @return This instance.
	 */
	public Ani setAllAnimationFinishedListener(AnimationFinishedListener pListener) {
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
	public static float getGlobalTimeFactor() {
		return globalAnimationTimeFactor;
	}


}
