package com.dungeon.engine.render;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class BlendFunctionContext implements DrawContext {

	private final int srcFunc;
	private final int dstFunc;
	private int oldSrcFunc;
	private int oldDstFunc;

	public BlendFunctionContext(int srcFunc, int dstFunc) {
		this.srcFunc = srcFunc;
		this.dstFunc = dstFunc;
	}

	public void set(SpriteBatch batch) {
		oldSrcFunc = batch.getBlendSrcFunc();
		oldDstFunc = batch.getBlendDstFunc();
		batch.setBlendFunction(srcFunc, dstFunc);
	}

	public void unset(SpriteBatch batch) {
		batch.setBlendFunction(oldSrcFunc, oldDstFunc);
	}

}
