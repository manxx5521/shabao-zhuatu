package com.xiaoshabao.zhuatu;

/**
 * 重试函数
 * @param <T>
 * @param <R>
 */
public interface RetryFunction<T, R> {
	
	R apply(T t) throws Exception;

}
