package com.nukethemoon.ani.tools.test.animation;

import com.nukethemoon.tools.ani.BaseAnimation;
import com.nukethemoon.tools.ani.AnimationFinishedListener;

import java.util.List;

/**
 * NukeTheMoon
 */
public class TestAnimationLifecycle extends BaseAnimation {

	private List<String> lifecycleProtocol;

	public TestAnimationLifecycle(List<String> pLifecycleProtocol, AnimationFinishedListener pAnimationFinishedListener) {
		super(30, pAnimationFinishedListener);
		lifecycleProtocol = pLifecycleProtocol;
	}

	@Override
	protected void onProgress(float pProgress) {
		lifecycleProtocol.add("onProgress " + pProgress);
	}

	@Override
	protected void onFinish() {
		lifecycleProtocol.add("onFinish");
	}

	@Override
	protected void onStart() {
		lifecycleProtocol.add("onStart");
	}

	@Override
	protected void onLoopStart(int pLoopIndex) {
		lifecycleProtocol.add("onLoopStart " + Integer.toString(pLoopIndex));
	}
}
