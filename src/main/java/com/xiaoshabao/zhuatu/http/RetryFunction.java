package com.xiaoshabao.zhuatu.http;

/**
 * 重试函数
 * @param <T>
 * @param <R>
 */
public interface RetryFunction<T, R> {
	/**
	 * 具体操作
	 */
	R apply(T t) throws Exception;

}
