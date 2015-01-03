package com.nukethemoon.tools.ani;

import java.util.ArrayList;
import java.util.List;

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

	private int loop = 1;

	private List<AnimationFinishedListener> animationFinishedListeners;

	private long timeStarted;
	private int durationMillis;
	private boolean isLastRenderCallDone = false;
	private boolean started = false;

	public AbstractAnimation(int pDurationMillis) {
		this(pDurationMillis, null);
	}

	public AbstractAnimation(int pDurationMillis, AnimationFinishedListener pAnimationFinishedListener) {
		this.durationMillis = pDurationMillis;
		addFinishedListener(pAnimationFinishedListener);
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

	public AbstractAnimation start() {
		this.timeStarted = System.currentTimeMillis();
		started = true;
		isLastRenderCallDone = false;
		this.onStart();
		return this;
	}


	private float computeProgress() {
		return ((float)(System.currentTimeMillis() - this.timeStarted) / (float)this.durationMillis) * AnimationController.getGlobalAnimationTimeFactor();
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
	public void callAnimationFinishedListener() {
		onFinish();
		if (this.animationFinishedListeners != null) {
			for (AnimationFinishedListener listener : animationFinishedListeners) {
				listener.onAnimationFinished();
			}
		}
	}

	public static float computeIntervalProgress(float pProgress, float pStart, float pEnd) {
		if (pProgress < pStart) return -1.0f;
		if (pProgress > pEnd) return  -1.0f;
		return (pProgress - pStart) / Math.abs(pEnd - pStart);
	}

	public float update() {
		float progress = handleProgress();
		onProgress(progress);
		return progress;
	}

	public void addFinishedListener(AnimationFinishedListener pAnimationFinishedListener) {
		if (pAnimationFinishedListener != null) {
			if (animationFinishedListeners == null) {
				animationFinishedListeners = new ArrayList<AnimationFinishedListener>();
			}
			animationFinishedListeners.add(pAnimationFinishedListener);
		}
	}

	public abstract void onProgress(float pProgress);

	public abstract void onFinish();

	public abstract void onStart();

	public boolean isStarted() {
		return started;
	}

	public int getLoop() {
		return loop;
	}

	public void setLoop(int loop) {
		this.loop = loop;
	}


}
