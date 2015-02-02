package com.nukethemoon.ani.tools.test;

import com.nukethemoon.ani.tools.test.animation.TestAnimation;
import com.nukethemoon.ani.tools.test.animation.TestAnimationLoops;
import com.nukethemoon.tools.ani.BaseAnimation;
import com.nukethemoon.tools.ani.Ani;
import com.nukethemoon.tools.ani.AnimationFinishedListener;
import org.junit.Test;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class TestGeneralAnimation {

	/**
	 * Tests the duration of an animation with a little tolerance.
	 * Also tests some lifecycle logic.
	 * @throws InterruptedException
	 */
	@Test
	public void testAnimationDuration() throws InterruptedException {
		final CountDownLatch latch = new CountDownLatch(1);
		int animationDuration = 10;


		// you may need to increase the tolerance depending on your CPU performance
		int differenceTolerance = 5;

		final Ani controller = new Ani(1);

		// take the start time
		final List<Long> times = new ArrayList<Long>();
		times.add(System.currentTimeMillis());

		TestAnimation testAnimation = new TestAnimation(animationDuration, new AnimationFinishedListener() {
			@Override
			public void onAnimationFinished(BaseAnimation pAnimation) {

				// test if the animation is finished.
				assertEquals("The animation isFinished method did return false " +
						"in the animation finished listener.", true, pAnimation.isFinished());

				// take the ending time
				times.add(System.currentTimeMillis());
				latch.countDown();
			}
		});

		assertEquals("The animation state is 'finished' before it was started.",
				false, testAnimation.isFinished());


		// start the animation
		controller.add(testAnimation);

		// wait for the animation to finish
		latch.await(100, TimeUnit.MILLISECONDS);
		assertEquals("Animation did not finish in the expected time.", 0, latch.getCount());


		// test if the animation listener was called.
		assertEquals("The animation finished listener was not called within the " +
				"expected time.", 2, times.size());

		// test of the animation duration is valid.
		long diff = (times.get(1) - times.get(0)) - animationDuration;
		assertEquals("The animation ended " + diff + " ms later as expected. The " +
				"tolerance is " + differenceTolerance  +"ms.", true, diff < 5 && diff >= 0);
	}


	/**
	 * Tests the progress of an animation with loops.
	 * Also tests some lifecycle logic.
	 * @throws InterruptedException
	 */
	@Test
	public void testLoops() throws InterruptedException {
		Ani ani = new Ani(2);
		final CountDownLatch latch = new CountDownLatch(1);

		int loopCountToTest = 3;

		final List<Float> animationProgressValues = new ArrayList<Float>();
		final List<Integer> animationLoopIndexList = new ArrayList<Integer>();

		AnimationFinishedListener listener = new AnimationFinishedListener() {
			@Override
			public void onAnimationFinished(BaseAnimation pAnimation) {

				assertEquals("Animation is still in state 'looping' on finished.",
						false, pAnimation.isLooping());

				assertEquals("There are still remaining loops on finished.",
						0, pAnimation.getRemainingLoopCount());

				latch.countDown();
			}
		};

		final TestAnimationLoops testAnimation = new TestAnimationLoops(
				50, animationProgressValues, animationLoopIndexList, listener);

		testAnimation.setLoopLength(loopCountToTest);
		ani.add(testAnimation);

		assertEquals("Animation is not looping after adding to the controller.",
				true, testAnimation.isLooping());

		latch.await(250, TimeUnit.MILLISECONDS);
		assertEquals("Animation did not finish in the expected time.", 0, latch.getCount());

		// count 1.0 values
		int fullProgressCount = Collections.frequency(animationProgressValues, 1.0f);
		int noProgressCount = Collections.frequency(animationProgressValues, 0.0f);

		assertEquals(true, fullProgressCount >= loopCountToTest + 1);
		assertEquals(true, noProgressCount >= loopCountToTest);

		// test the call of onLoopStart
		assertEquals("Expected to call onLoopStart " + loopCountToTest + " times.",
				loopCountToTest, animationLoopIndexList.size());

		// test the parameter of onLoopStart
		for (int i = 1; i < animationLoopIndexList.size(); i++) {
			assertEquals("The call of onLoopStart is not valid.", i, (int) animationLoopIndexList.get(i - 1));
		}
	}

	/**
	 * Tests the usage of pause and resume.
	 */
	@Test
	public void testPauseAndResume() throws InterruptedException {

		int animationDurationToTest = 1000;
		int pauseTimeToTest = 200;

		// you may need to increase the tolerance depending on your CPU performance
		int animationDurationTolerance = 20;
		float progressDifferenceTolerance = 0.008f;

		Ani controller = new Ani(3);
		Timer timer = new Timer();

		final CountDownLatch animationLatch = new CountDownLatch(1);
		final List<Long> times = new ArrayList<Long>();

		times.add(System.currentTimeMillis());

		TestAnimation animation = new TestAnimation(animationDurationToTest,
				new AnimationFinishedListener() {
			@Override
			public void onAnimationFinished(BaseAnimation pAnimation) {
				times.add(System.currentTimeMillis());
				animationLatch.countDown();
			}
		});

		controller.add(animation);
		wait(pauseTimeToTest, timer); // animation is running

		assertProgress(animation, pauseTimeToTest,
				animationDurationToTest, progressDifferenceTolerance);

		animation.pause();

		assertEquals("Animation is not paused.", true, animation.isPaused());
		wait(pauseTimeToTest, timer); // animation is paused


		assertProgress(animation, pauseTimeToTest,
				animationDurationToTest, progressDifferenceTolerance);

		animation.resume();
		assertEquals("Animation is paused.", false, animation.isPaused());

		assertProgress(animation, pauseTimeToTest,
				animationDurationToTest, progressDifferenceTolerance);
		assertEquals("Animation is finished.", false, animation.isFinished());

		animationLatch.await(animationDurationToTest * 2, TimeUnit.MILLISECONDS);
		assertEquals("Animation did not finish in the expected time.", 0, animationLatch.getCount());

		long animationTimeMeasured = times.get(1) - times.get(0);
		long animationTimeExpected = animationDurationToTest + pauseTimeToTest;
		long animationTimeDifference = animationTimeMeasured - animationTimeExpected;

		assertEquals("Animation duration was " + animationTimeMeasured + "ms " +
				"but expected was " + animationTimeExpected + "ms by a tolerance of " +
				animationDurationTolerance,
				true,
				animationTimeDifference < animationDurationTolerance);

		assertEquals("Animation is not finished. ", true, animation.isFinished());

	}

	private void assertProgress(BaseAnimation pAnimation, float pauseTime,
								float pAnimationDuration, float pTolerance) {
		float currentProgress = pAnimation.update();
		float expectedProgress = (pauseTime / pAnimationDuration);
		float progressDifference = Math.abs(currentProgress - expectedProgress);
		assertEquals("The expected progress is " + expectedProgress + " but the current" +
						"progress is " + currentProgress + " by a tolerance of " + pTolerance,
				true,
				progressDifference < pTolerance
		);
	}

	public void wait(int pMillis, Timer pTimer) throws InterruptedException {
		final CountDownLatch pauseLatch = new CountDownLatch(1);
		TimerTask timerTask = new TimerTask() {
			@Override
			public void run() {
				pauseLatch.countDown();
			}
		};
		pTimer.schedule(timerTask, pMillis);
		pauseLatch.await(pMillis * 2, TimeUnit.MILLISECONDS);
	}
}