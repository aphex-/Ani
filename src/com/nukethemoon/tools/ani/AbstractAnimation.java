package com.nukethemoon.tools.ani;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * This class helps to create animations by providing start, duration,
 * current progress, and a 'onFinish listener'.
 *
 * Inherit from this class and implement the methods to get the
 * current progress of the animation.
 *
 * @author lucahofmann@gmx.net
 */
public abstract class AbstractAnimation {

	private List<AnimationFinishedListener> finishedListenersList;
	private AnimationFinishedListener finishedListener; // to avoid instance creation if only one listener is used.

	private long timeStarted;
	private int durationMillis;
	private boolean isLastRenderCallDone = false;
	private boolean started = false;
	private int loopCount = 0;

	/**
	 * Creates a new instance.
	 * @param pDurationMillis The duration of the animation in milliseconds.
	 */
	public AbstractAnimation(int pDurationMillis) {
		this(pDurationMillis, null);
	}

	/**
	 * Creates a new instance.
	 * @param pDurationMillis The duration of the animation in milliseconds.
	 * @param pAnimationFinishedListener The finished listener.
	 */
	public AbstractAnimation(int pDurationMillis, AnimationFinishedListener pAnimationFinishedListener) {
		this.durationMillis = pDurationMillis;
		addFinishedListener(pAnimationFinishedListener);
	}

	/**
	 * Implement this method to apply your animation progress.
	 * @param pProgress The current progress between 0.0 and 1.0.
	 */
	public abstract void onProgress(float pProgress);

	/**
	 * Implement this method to apply the finish of the animation.
	 */
	public abstract void onFinish();

	/**
	 * Implement this method to apply the start of the animation.
	 */
	public abstract void onStart();

	/**
	 * Computes the progress of the animation at the current time.
	 * @return The progress between 0.0 and 1.0
	 */
	private float computeProgress() {
		return ((float)(System.currentTimeMillis() - this.timeStarted) / (float)this.durationMillis) * AnimationController.getGlobalAnimationTimeFactor();
	}

	/**
	 * Computes the progress of the progress within the limit os start and end.
	 *
	 * @param pProgress The progress.
	 * @param pStart The start limit.
	 * @param pEnd The end limit.
	 * @return the progress or -1.0
	 */
	public static float computeIntervalProgress(float pProgress, float pStart, float pEnd) {
		if (pProgress < pStart) return -1.0f;
		if (pProgress > pEnd) return  -1.0f;
		return (pProgress - pStart) / Math.abs(pEnd - pStart);
	}

	/**
	 * Computes the current animation progress (0.0 - 1.0) and returns it.
	 * Sets the "isLastRenderCallDone" field to true if a 1.0 progress was computed.
	 * @return The current animation progress (0.0 - 1.0)
	 */
	protected float handleProgress() {
		float progress = computeProgress();
		if (progress > 1f) {
			progress = 1f;
			if (!this.isLastRenderCallDone) {
				this.isLastRenderCallDone = true;
			}
		}
		return progress;
	}

	/**
	 * Starts the animation.
	 * @return This animation.
	 */
	public AbstractAnimation start() {
		this.timeStarted = System.currentTimeMillis();
		started = true;
		isLastRenderCallDone = false;
		this.onStart();
		return this;
	}

	/**
	 * Stops looping the animation. (After the current run through)
	 * @return This animation.
	 */
	public AbstractAnimation stopLoop() {
		return setLoopCount(0);
	}

	/**
	 * Returns true if the animation is done.
	 * It is only done if the last render call (progress = 1.0) is done.
	 * @return true if the animation is done.
	 */
	public boolean isFinished() {
		return this.isLastRenderCallDone;
	}

	/**
	 * Calls the internal AnimationFinishedListener if it is not null.
	 */
	public void callAnimationFinishedListeners() {
		onFinish();
		if (finishedListenersList != null) {
			for (AnimationFinishedListener listener : finishedListenersList) {
				listener.onAnimationFinished();
			}
		} else {
			if (finishedListener != null) {
				finishedListener.onAnimationFinished();
			}
		}
	}

	/**
	 * Updates the progress. (Usually called by a animation controller)
	 * @return The current progress.
	 */
	public float update() {
		float progress = handleProgress();
		onProgress(progress);
		return progress;
	}

	/**
	 * Adds an animation finished listener. Only creates new list instance if there is more than one listener.
	 * @param pAnimationFinishedListener A listener that gets called if the animation is finished.
	 * @return This animation.
	 */
	public AbstractAnimation addFinishedListener(AnimationFinishedListener pAnimationFinishedListener) {
		if (pAnimationFinishedListener != null) {
			if (finishedListener == null) {
				finishedListener = pAnimationFinishedListener;
			} else {
				if (finishedListenersList == null) {
					finishedListenersList = new ArrayList<AnimationFinishedListener>();
					finishedListenersList.add(finishedListener);
				}
				finishedListenersList.add(pAnimationFinishedListener);
			}
		}
		return this;
	}

	/**
	 * Return true if the animation is started.
	 * @return true if the animation is started.
	 */
	public boolean hasStarted() {
		return started;
	}

	/**
	 * Gets the loopCount count.
	 * @return the loopCount count.
	 */
	public int getLoopCount() {
		return loopCount;
	}

	/**
	 * Sets the loopCount count.
	 * @param pLoopCount The count to loopCount.
	 * @return This animation.
	 */
	public AbstractAnimation setLoopCount(int pLoopCount) {
		this.loopCount = pLoopCount;
		return this;
	}

	/**
	 * Sets an infinite loop for this animation.
	 * @return This animation.
	 */
	public AbstractAnimation setInfiniteLoop() {
		return setLoopCount(-1);
	}

}
