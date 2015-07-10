package com.nukethemoon.tools.ani;

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

	private boolean enabled = true;

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
		if (!enabled) {
			return this;
		}
		if (pAnimation != null) {
			for (int i = 0; i < animations.length; i++) {
				if (animations[i] == null) {
					if (pAnimation.getTimeStartPlaned() == -1 && !pAnimation.hasStarted()) {
						pAnimation.start();
					}
					animations[i] = pAnimation;
					return this;
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
		if (!enabled) {
			return this;
		}
		if (pStartDelayMillis > 0) {
			pAnimation.setTimeStartPlaned(System.currentTimeMillis() + pStartDelayMillis);
		}
		add(pAnimation);
		return this;
	}

	/**
	 * Adds multiple animations and a listener to fall if all are finished.
	 * @param pAnimations The animations.
	 * @param pAllAnimationFinishedListener The listener to call is all animations are finished.
	 * @return This instance.
	 */
	public final Ani add(final BaseAnimation pAnimations[], final AnimationFinishedListener pAllAnimationFinishedListener) {
		if (!enabled) {
			return this;
		}
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
		if (!enabled) {
			return this;
		}
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
		if (!enabled) {
			return this;
		}

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
		if (!enabled) {
			return false;
		}

		boolean didHandleAnimation = false;
		for (int i = 0; i < animations.length; i++) {
			BaseAnimation animation = animations[i];
			if (animation != null) {

				// for delayed animations.
				if (!animation.hasStarted() && animation.getTimeStartPlaned() != -1
						&& animation.getTimeStartPlaned() <= System.currentTimeMillis()) {
					animation.start();
					animation.setTimeStartPlaned(-1);
				}

				if (animation.isFinished()) {
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
	 * Stops the animation the hard way.
	 * Does not call animation.onFinish nor its finish listeners.
	 * @param pAnimation The animation to cancel.
	 * @return True if the animation was found.
	 */
	public boolean cancel(BaseAnimation pAnimation) {
		int indexOf = getIndexOf(pAnimation);
		if (indexOf > -1) {
			animations[indexOf] = null;
			return true;
		}
		return false;
	}

	/**
	 * Gracefully stops the animation and calls its listeners.
	 * @param pAnimation The animation to stop.
	 * @return This instance.
	 */
	public Ani stop(BaseAnimation pAnimation) {
		if (pAnimation != null) {
			pAnimation.onFinish();
			pAnimation.callAnimationFinishedListeners();
			cancel(pAnimation);
		}
		return this;
	}

	/**
	 * Cancels all animations without calling animation.onFinish or its listeners.
	 * @return This instance.
	 */
	public Ani resetHard() {
		for (BaseAnimation animation : animations) {
			if (animation != null) {
				cancel(animation);
			}
		}
		return this;
	}

	/**
	 * Stops all animations and call animation.inFinish and the listeners.
	 * Disables this instance while stopping. Enables it afterwards.
	 * @return This instance.
	 */
	public Ani resetGraceful() {
		setEnabled(false);
		for (BaseAnimation animation : animations) {
			if (animation != null) {
				stop(animation);
			}
		}
		setEnabled(true);
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


	/**
	 * Enables and disables the controller.
	 * @param pEnabled The state.
	 * @return This instance.
	 */
	public Ani setEnabled(boolean pEnabled) {
		enabled = pEnabled;
		return this;
	}

	/**
	 * Returns true if this instance is enabled.
	 * @return true if this instance is enabled.
	 */
	public boolean isEnabled() {
		return enabled;
	}
}
