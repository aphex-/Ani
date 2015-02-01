package com.nukethemoon.tools.ani;

/**
 * Can be used to wait for multiple animations to finish.
 * Collects onAnimationFinished calls and calls the assigned listener.
 *
 * @author lucahofmann@gmx.net
 */
public class AnimationsFinishedCollector implements AnimationFinishedListener {

	/**
	 * The cont of listener calls to collect.
	 */
	private int collectCount;

	/**
	 * The current count of collected listener calls.
	 */
	private int collectedCalls;

	/**
	 * The listener to call if all calls are collected.
	 */
	private AnimationFinishedListener listener;

	/**
	 * Creates a new instance.
	 *
	 * @param pCollectCount Count of calls to wait for.
	 * @param pListener Listener to call if all calls have been done.
	 */
	public AnimationsFinishedCollector(final int pCollectCount,
		final AnimationFinishedListener pListener) {

		collectCount = pCollectCount;
		listener = pListener;
	}

	/**
	 * Collects onAnimationFinished calls.
	 */
	@Override
	public final void onAnimationFinished(BaseAnimation pAnimation) {
		collectedCalls++;
		if (collectCount == collectedCalls) {
			listener.onAnimationFinished(pAnimation);
		}
	}
}
