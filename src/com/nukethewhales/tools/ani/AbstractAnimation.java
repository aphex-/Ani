package com.nukethewhales.tools.ani;

/**
 *
 * This class helps to create animations by providing start, duration,
 * current progress, finish and a on finish listener.
 *
 * Inherit from this class and implement the methods to get the
 * current progress of the animation.
 *
 * @author lucahofmann@gmx.net
 */
public abstract class AbstractAnimation {

	/**
	 * A time factor to scale all animations. (useful for debug reasons)
	 */
	private static float animationTimeFactor = 1f;

	/**
	 * The listener to be call on finish or null.
	 */
	private AnimationFinishedListener animationFinishedListener;

	/**
	 * The time the animation started.
	 */
	private long timeStarted = -1;

	/**
	 * The duration of the animation in milliseconds.
	 */
	private int durationMillis;

	/**
	 * True if the last update call of this animation was done.
	 */
	private boolean isLastUpdateDone = false;

	/**
	 * Creates a new instance.
	 *
	 * @param pDurationMillis The duration for the animation in milli seconds.
	 * @param pAnimationFinishedListener The listener to call on finish or null.
	 */
	public AbstractAnimation(final int pDurationMillis, final AnimationFinishedListener pAnimationFinishedListener) {
		this.animationFinishedListener = pAnimationFinishedListener;
		this.durationMillis = pDurationMillis;
	}

	/**
	 * Creates a new instance.
	 *
	 * @param pDurationMillis The duration for the animation in milli seconds.
	 */
	public AbstractAnimation(final int pDurationMillis) {
		this(pDurationMillis, null);
	}

	/**
	 * Starts the animation.
	 *
	 * @return This instance.
	 */
	public final AbstractAnimation start() {
		this.timeStarted = System.currentTimeMillis();
		this.onStart();
		return this;
	}

	/**
	 * Call this method every time you want to update the animation (e.g. every render frame)
	 *
	 * @return The current animation progress (0.0 to 1.0) or -1 if it is not isStarted.
	 */
	public final float update() {
		if (this.isStarted()) {
			float progress = handleProgress();
			this.onProgress(progress);
			return progress;
		}
		return -1f;
	}

	/**
	 * Computes the current animation progress (0.0 to 1.0) and returns it.
	 * Sets the "isLastUpdateDone" field to true if a 1.0 progress was computed.
	 *
	 * @return The current animation progress (0.0 - 1.0)
	 */
	private float handleProgress() {
		float progress = computeProgress();
		if (progress > 1f) {
			progress = 1f;
			if (!this.isLastUpdateDone) {
				this.isLastUpdateDone = true;
			}
		}
		return progress;
	}

	/**
	 * Computes the progress of the animation based on the start time, the duration and the animationTimeFactor.
	 *
	 * @return The current animation progress.
	 */
	private float computeProgress() {
		if (this.durationMillis == 0) {
			return 1.0f;
		}
		return ((float) (System.currentTimeMillis() - this.timeStarted) / (float) this.durationMillis) * animationTimeFactor;
	}

	/**
	 * Returns true if the animation is finished.
	 * This depends on the last update.
	 *
	 * @return true if the animation is done.
	 */
	public final boolean isFinished() {
		return this.isLastUpdateDone;
	}

	/**
	 * Returns true if the animation is started.
	 *
	 * @return true if the animation is started.
	 */
	public final boolean isStarted() {
		return this.timeStarted > -1;
	}

	/**
	 * Calls the internal AnimationFinishedListener if it is not null.
	 */
	public final void callAnimationFinishedListener() {
		this.onFinish();
		if (this.animationFinishedListener != null) {
			this.animationFinishedListener.onAnimationFinished();
		}
	}

	/**
	 * Computes the progress of an interval. It the assigned progress
	 * is not between start and end it returns -1:
	 * @param pProgress A progress (0.0 to 1.0).
	 * @param pIntervalStart The start of the interval (0.0 to 1.0)
	 * @param pIntervalEnd The end of the interval (0.0 to 1.0)
	 * @return The progress in the interval.
	 */
	public static float computeIntervalProgress(final float pProgress, final float pIntervalStart, final float pIntervalEnd) {
		if (pProgress < pIntervalStart) {
			return -1.0f;
		}
		if (pProgress > pIntervalEnd) {
			return  -1.0f;
		}
		return (pProgress - pIntervalStart) / Math.abs(pIntervalEnd - pIntervalStart);
	}

	/**
	 * Implement this method to apply the progress to your animation.
	 *
	 * @param pProgress The progress of the animation.
	 */
	public abstract void onProgress(float pProgress);

	/**
	 * Implement this method to run logic before finishing the animation (before the AnimationFinishedListener).
	 */
	public abstract void onFinish();

	/**
	 * Implement this method to run logic before starting the animation.
	 */
	public abstract void onStart();


}
