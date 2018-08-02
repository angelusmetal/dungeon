package com.dungeon.engine.resource;

public class LoadingException extends RuntimeException {

	public LoadingException(String msg, String filename) {
		super(getMessage(msg, filename));
	}

	public LoadingException(Throwable e, String filename) {
		super(getMessage("Error", filename), e);
	}

	public LoadingException(String msg, String filename, Throwable e) {
		super(getMessage(msg, filename), e);
	}

	private static String getMessage(String msg, String filename) {
		return msg + " while loading file " + filename;
	}
}
