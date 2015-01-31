package com.nukethemoon.ani.tools.test.animation;


import com.nukethemoon.tools.ani.AbstractAnimation;
import com.nukethemoon.tools.ani.AnimationFinishedListener;

public class TestAnimation extends AbstractAnimation {

	public TestAnimation(int pDuration, AnimationFinishedListener pListener) {
		super(pDuration, pListener);
	}

	@Override
	protected void onProgress(float pProgress) { }

	@Override
	protected void onFinish() { }

	@Override
	protected void onStart() { }

	@Override
	protected void onLoopStart(int pLoopIndex) {

	}
}
