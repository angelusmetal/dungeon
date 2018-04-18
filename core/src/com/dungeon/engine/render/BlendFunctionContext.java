package com.dungeon.engine.render;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.function.Consumer;

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

	public void run(SpriteBatch batch, Consumer<SpriteBatch> consumer) {
		set(batch);
		consumer.accept(batch);
		unset(batch);
	}
}
