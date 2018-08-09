package com.dungeon.engine.resource;

public class LoadingException extends RuntimeException {

	public LoadingException(String msg) {
		super(msg);
	}

	public LoadingException(Throwable e) {
		super(e);
	}

	public LoadingException(String msg, Throwable e) {
		super(msg, e);
	}
}
