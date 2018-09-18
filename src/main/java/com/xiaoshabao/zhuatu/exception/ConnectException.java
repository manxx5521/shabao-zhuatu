package com.xiaoshabao.zhuatu.exception;

/**
 * 抓图异常
 */
public class ConnectException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ConnectException(String mess) {
		super(mess);
	}

	public ConnectException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public ConnectException(Throwable cause) {
		super(cause);
	}
}
