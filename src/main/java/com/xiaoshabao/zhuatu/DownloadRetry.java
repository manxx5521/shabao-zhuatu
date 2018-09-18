package com.xiaoshabao.zhuatu;

import java.net.SocketException;
import java.net.SocketTimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xiaoshabao.zhuatu.exception.ConnectException;

/**
 * 重试操作，如果成功就跳出
 * 
 * @param <R>
 *            返回值类型
 */
public class DownloadRetry<T, R> {

	protected Logger log = LoggerFactory.getLogger(getClass());

	private int count = 5;
	
	private int sleepTime=1000*3;

	/**
	 * 描述信息
	 */
	private String detailMsg;

	private T t;

	public DownloadRetry(T t, String detailMsg) {
		this.t = t;
		this.detailMsg = detailMsg;
	}

	public DownloadRetry(T t, String detailMsg, int count) {
		this.t = t;
		this.detailMsg = detailMsg;
		this.count = count;
	}

	/**
	 * 执行
	 * <p>
	 * 函数内无需捕获异常，直接向上抛，在重试工厂中统一捕获
	 * </p>
	 * 
	 * @param function
	 * @return 错误返回null
	 */
	public R execute(RetryFunction<T, R> function) {
		R result = null;
		int i = 1;
		
		// 记录最后一次失败异常
		Exception laste = null;
		do {
			if (i > count) {
				if(laste!=null) {
					log.error("{}执行失败,出现异常。", this.detailMsg, laste);
					if(laste instanceof SocketException||laste instanceof SocketTimeoutException){
						throw new ConnectException(laste);
					}
					
				}else {
					log.error("{}执行失败,原因：函数中返回结果为null", this.detailMsg);
				}
				return null;
			}
			try {
				result = function.apply(t);
				if (result instanceof Boolean && (Boolean) result) {
					return result;
				} else if (result != null) {
					return result;
				}
			} catch (Exception e) {
				log.warn("{} 执行失败，进行重试。开始重试第{}次", this.detailMsg, i);
				if(e instanceof SocketException){
					log.warn("错误原因：SocketException->Connection reset。");
				}else if(e instanceof SocketTimeoutException){
					log.warn("错误原因：SocketTimeoutException->Read timed out。");
				}
				
				//记录错误信息
				laste = e;
				if (e instanceof SocketException|| e instanceof SocketTimeoutException) {
					if (2 < count) {
						count = 2;
					}
				}
			}
			try {
				Thread.sleep(sleepTime);
			} catch (InterruptedException e1) {
			}
			i++;
		} while (true);
	}

	public DownloadRetry<T, R> setDetailMsg(String detailMsg) {
		this.detailMsg = detailMsg;
		return this;
	}

	public DownloadRetry<T, R> setCount(int count) {
		this.count = count;
		return this;
	}

	public DownloadRetry<T, R> setSleepTime(int sleepTime) {
		this.sleepTime = sleepTime;
		return this;
	}
	
}
