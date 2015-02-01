package com.nukethemoon.ani.tools.test.animation;


import com.nukethemoon.tools.ani.BaseAnimation;
import com.nukethemoon.tools.ani.AnimationFinishedListener;

public class TestAnimation extends BaseAnimation {

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
