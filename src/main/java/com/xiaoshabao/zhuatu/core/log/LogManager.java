package com.xiaoshabao.zhuatu.core.log;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xiaoshabao.zhuatu.core.pool.ZhuatuDownloadPool;

/**
 * 下载线程日志管理
 */
public class LogManager {
	
	private final static Logger log = LoggerFactory.getLogger(LogManager.class);

	private volatile static LogManager instance = null;
	
	private final static int TASK_TIME=5;
	private AtomicInteger time=new AtomicInteger(TASK_TIME);

	private LogManager() {
		// 启动一个日志定时线程输出 线程池状态
		CompletableFuture.runAsync(() -> {
			while (true) {
				try {
					Thread.sleep(1000);
					if (time.get() < 1) {
						log.info(getInfoAndRefresh());
					} else {
						time.getAndDecrement();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		});
	}

	public static LogManager getInstance() {
		if (instance == null) {
			synchronized (LogManager.class) {
				if (instance == null) {
					instance = new LogManager();
				}
			}
		}
		return instance;
	}
	
	/**
	 * 获得日志信息，并刷新打印时间
	 * @return
	 */
	public String getInfoAndRefresh(){
		time.set(TASK_TIME);
		StringBuilder sb=new StringBuilder();
		sb.append("{活跃下载：").append(ZhuatuDownloadPool.getInstance().getActiveCount()).append("}");
		return sb.toString(); 
	}
	
}
