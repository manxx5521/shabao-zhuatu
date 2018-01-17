package com.xiaoshabao.zhuatu;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ZhuatuDownloadPool extends ThreadPoolExecutor {

	private volatile static ZhuatuDownloadPool instance = null;

	private ZhuatuDownloadPool() {
		super(10, 10, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
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
	 * 等待活跃的进程数小于5
	 */
	public void waitActiveThread() {
		waitActiveThread(5);
	}

	/**
	 * 等待活跃的进程数小于 size
	 */
	public void waitActiveThread(int size) {
		while (true) {
			if (this.getActiveCount() < 5) {
				return;
			} else {
				try {
					Thread.sleep(1000 * 2);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

		}
	}
}
