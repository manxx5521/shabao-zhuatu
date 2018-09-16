package com.xiaoshabao.zhuatu.exception;

/**
 * 抓图异常
 */
public class ZhuatuException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ZhuatuException(String mess) {
		super(mess);
	}

	public ZhuatuException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public ZhuatuException(Throwable cause) {
		super(cause);
	}
}
