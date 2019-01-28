package com.xiaoshabao.zhuatu.core.pool;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ZhuatuDownloadPool extends ThreadPoolExecutor {

	private volatile static ZhuatuDownloadPool instance = null;

	private ZhuatuDownloadPool() {
		super(10, 10, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(100),new BlockPolicy());
	}
	
	/**
	 * 线程池拒绝策略，当队列满了时调用put方法，堵塞队列
	 */
	public static class BlockPolicy implements RejectedExecutionHandler {

		public BlockPolicy() {
		}

		@Override
		public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
			try {
				e.getQueue().put( r );
			}
			catch (InterruptedException e1) {
				Thread.currentThread().interrupt();
			}
		}
	}

	/**
	 * 获得下载池
	 * 
	 * @return
	 */
	public static ZhuatuDownloadPool getInstance() {
		if (instance == null) {
			synchronized (ZhuatuDownloadPool.class) {
				if (instance == null) {
					instance = new ZhuatuDownloadPool();
				}
			}
		}
		return instance;
	}

	/**
	 * 初始化（防止多次使用）
	 */
	public static void init() {
		if (instance != null) {
			synchronized (ZhuatuDownloadPool.class) {
				if (instance != null && instance.isShutdown()) {
					instance = new ZhuatuDownloadPool();
				}
			}
		}
	}
	
	/**
	 * 等待线程池下载完成
	 */
	public static synchronized void waitDownload() {
		if(instance!=null) {
			instance.shutdown();
			
			while(true) {
				//关闭后所有任务都已完成,则返回true
				if(instance.isTerminated()) {
					break;
				}
				try {
					TimeUnit.SECONDS.sleep(5);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		
	}
}
