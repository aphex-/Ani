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
	private long timeElapsedOnPause = -1;

	private boolean lastUpdateCallDone = false;
	private boolean started = false;

	private int loopLength = 0;
	private int loopCount;


	/**
	 * Creates a new instance.
	 *
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
	protected abstract void onProgress(float pProgress);

	/**
	 * Implement this method to apply the finish of the animation.
	 */
	protected abstract void onFinish();

	/**
	 * Implement this method to apply the start of the animation.
	 */
	protected abstract void onStart();

	/**
	 * Use this method to implement logic if a loop starts.
	 * @param pLoopIndex The index of the current loop.
	 */
	protected abstract void onLoopStart(int pLoopIndex);

	/**
	 * Computes the progress of the animation at the current time.
	 *
	 * @return The progress between 0.0 and 1.0
	 */
	protected float computeProgress() {
		return ((float)(System.currentTimeMillis() - this.timeStarted)
				/ (float)this.durationMillis) * AnimationController.getGlobalAnimationTimeFactor();
	}

	/**
	 * Computes the progress of the progress within the limit os start and end.
	 *
	 * @param pProgress The progress.
	 * @param pStart The start limit.
	 * @param pEnd The end limit.
	 *
	 * @return the progress or -1.0
	 */
	public static float computeIntervalProgress(float pProgress, float pStart, float pEnd) {
		if (pProgress < pStart) return -1.0f;
		if (pProgress > pEnd) return  -1.0f;
		return (pProgress - pStart) / Math.abs(pEnd - pStart);
	}

	/**
	 * Computes the current animation progress (0.0 - 1.0) and returns it.
	 * Sets the "lastUpdateCallDone" field to true if a 1.0 progress was computed.
	 *
	 * @return The current animation progress (0.0 - 1.0)
	 */
	protected float handleProgress() {
		float progress = computeProgress();
		if (progress > 1f) {
			progress = 1f; // to guarantee progress 1.0 on end.
			if (!this.lastUpdateCallDone) {
				this.lastUpdateCallDone = true;
			}
		}
		return progress;
	}

	/**
	 * Starts the animation.
	 *
	 * @return This animation.
	 */
	public AbstractAnimation start() {
		if (!hasStarted()) {
			reset();
			loopCount = 0;
			this.onStart();
			this.onProgress(0.0f); // to guarantee progress 0.0 on start.
			started = true;
		}
		return this;
	}

	private void reset() {
		this.timeStarted = System.currentTimeMillis();
		lastUpdateCallDone = false;
	}

	/**
	 * Stops looping the animation. (After the current run through)
	 *
	 * @return This animation.
	 */
	public AbstractAnimation stopLoop() {
		return setLoopLength(0);
	}

	/**
	 * Returns true if the animation is done.
	 * It is only done if the last render call (progress = 1.0) is done.
	 *
	 * @return true if the animation is done.
	 */
	public boolean isFinished() {
		return this.lastUpdateCallDone && !isLooping();
	}

	/**
	 * Calls the internal AnimationFinishedListener if it is not null.
	 * Does not finish the animation.
	 */
	public void callAnimationFinishedListeners() {
		if (finishedListenersList != null) {
			for (AnimationFinishedListener listener : finishedListenersList) {
				listener.onAnimationFinished(this);
			}
		} else {
			if (finishedListener != null) {
				finishedListener.onAnimationFinished(this);
			}
		}
	}

	/**
	 * Updates the progress. (Usually called by a animation controller)
	 *
	 * @return The current progress.
	 */
	public float update() {
		float progress = handleProgress();
		if (hasStarted() && !isPaused()) {
			onProgress(progress);
			if (lastUpdateCallDone) {
				if (isLooping()) {
					// progress ended and new loop
					reset();
					loopCount++;
					onLoopStart(loopCount);
					return update();
				} else {
					// progress ended and not lopping
					onFinish();
					loopCount = 0;
					started = false;
					return progress;
				}
			}
		}
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
	 * Gets the count of remaining loops.
	 * @return the count of remaining loops.
	 */
	public int getRemainingLoopCount() {
		return Math.max(loopLength - loopCount, - 1);
	}

	/**
	 * Sets the loopLength count.
	 * @param pLoopLength The count to loopLength.
	 * @return This animation.
	 */
	public AbstractAnimation setLoopLength(int pLoopLength) {
		this.loopLength = pLoopLength;
		return this;
	}

	/**
	 * Sets an infinite loop for this animation.
	 * @return This animation.
	 */
	public AbstractAnimation loopInfinite() {
		return setLoopLength(-1);
	}


	public boolean isLooping() {
		return getRemainingLoopCount() > 0 || getRemainingLoopCount() == -1;
	}

	/**
	 * Pause the animation.
	 * @return This animation.
	 */
	public AbstractAnimation pause() {
		if (hasStarted()) {
			timeElapsedOnPause = (System.currentTimeMillis() - timeStarted);
			if (timeElapsedOnPause < 0) {
				timeElapsedOnPause = 0;
			}
		}
		return this;
	}

	/**
	 * Resumes the animation if it is paused.
	 * @return This animation.
	 */
	public AbstractAnimation resume() {
		if (hasStarted() && isPaused()) {
			timeStarted = (System.currentTimeMillis() - timeElapsedOnPause);
			timeElapsedOnPause = - 1;
		}
		return this;
	}

	/**
	 * Returns true if this animation is paused.
	 * @return true if this animation is paused.
	 */
	public boolean isPaused() {
		return timeElapsedOnPause >= 0;
	}
}
