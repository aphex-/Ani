package com.nukethemoon.ani.tools.test;

import com.nukethemoon.ani.tools.test.animation.TestAnimationLifecycle;
import com.nukethemoon.tools.ani.BaseAnimation;
import com.nukethemoon.tools.ani.Ani;
import com.nukethemoon.tools.ani.AnimationFinishedListener;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

/**
 * NukeTheMoon
 */
public class TestLifecycle {

	@Test
	public void testLifecycle() throws InterruptedException {
		Ani controller = new Ani(10);

		// a latch to wait for animations in the test
		final CountDownLatch latch = new CountDownLatch(1);

		List<String> lifecycleProtocol = new ArrayList<String>();

		// create the animation
		TestAnimationLifecycle animation = new TestAnimationLifecycle(lifecycleProtocol,
				new AnimationFinishedListener() {
			@Override
			public void onAnimationFinished(BaseAnimation pAnimation) {
				latch.countDown();
			}
		});

		animation.setLoopLength(3);

		controller.add(animation);

		// wait for the animation to finish in the test
		latch.await(200, TimeUnit.MILLISECONDS);
		assertEquals("Animation did not finish in the expected time.", 0, latch.getCount());

		assertEquals("The protocol is empty", true, lifecycleProtocol.size() > 0);

		int onFinishCallCount = Collections.frequency(lifecycleProtocol, "onFinish");
		int onStartCallCount = Collections.frequency(lifecycleProtocol, "onStart");

		assertEquals("The method onFinish is called " + onFinishCallCount + " times.",
				1, onFinishCallCount);

		assertEquals("The method onStart is called " + onStartCallCount + " times.",
				1, onStartCallCount);

		/*for (String line : lifecycleProtocol) {
			System.out.println(line);
		}*/
	}
}
