package com.nukethemoon.ani.tools.test.animation;

import com.nukethemoon.tools.ani.BaseAnimation;
import com.nukethemoon.tools.ani.AnimationFinishedListener;

import java.util.List;

/**
 * NukeTheMoon
 */
public class TestAnimationLoops extends BaseAnimation {

	private List<Float> progressList;
	private List<Integer> loopIndexList;

	public TestAnimationLoops(int pDuration, List<Float> pProgressList,
							  List<Integer> pLoopIndexList,
							  AnimationFinishedListener pFinishListener) {
		super(pDuration, pFinishListener);
		progressList = pProgressList;
		loopIndexList = pLoopIndexList;
	}

	@Override
	protected void onProgress(float pProgress) {
		progressList.add(pProgress);
	}

	@Override
	protected void onFinish() {
	}

	@Override
	protected void onStart() {
	}

	@Override
	protected void onLoopStart(int pLoopIndex) {
		loopIndexList.add(pLoopIndex);
	}
}
