package com.gameduell.jira.exception;

public class NotLoggedException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public NotLoggedException() {
		super();
	}

	public NotLoggedException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public NotLoggedException(String message, Throwable cause) {
		super(message, cause);
	}

	public NotLoggedException(String message) {
		super(message);
	}

	public NotLoggedException(Throwable cause) {
		super(cause);
	}
}
