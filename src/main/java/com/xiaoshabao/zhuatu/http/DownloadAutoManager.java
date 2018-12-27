package com.xiaoshabao.zhuatu.http;

import java.net.Socket;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xiaoshabao.zhuatu.core.config.DownloadConfig;
import com.xiaoshabao.zhuatu.exception.ConnectException;

/**
 * HttpClient 实现的链接
 */
public class DownloadAutoManager{
	protected Logger log = LoggerFactory.getLogger(getClass());
	
	private volatile static DownloadAutoManager instance = null;
	
	/**重启一个有10个链接能力的慢下载池*/
	private Executor slowPool=Executors.newFixedThreadPool(10);

	private Map<String,Wang> wang=new ConcurrentHashMap<String, Wang>();
	
	private Boolean startProxy;
	private String ip;
	private int port;
	/**本地最大下载失败次数**/
	private int maxLocalFail=15;
	/**代理最大下载失败次数**/
	private int maxProxyFail=10;
	/**下载慢次数**/
	private int maxSlowCount=10;
	/**计算下载慢的时间秒数**/
	private int slowTime=30;
	
	private DownloadAutoManager() {
	}

	public static DownloadAutoManager getInstance() {
		if (instance == null) {
			synchronized (DownloadAutoManager.class) {
				if (instance == null) {
					instance = new DownloadAutoManager();
				}
			}
		}
		return instance;
	}
	
	public void download(String url, String fileNamePath,DownloadConfig config) {
		initProxy(config);
		
		String webRoot = config.getWebRoot();
		Wang wang = getWang(webRoot, 1);
		
		//如果是慢链接单独下载
		if(wang.slowCount>maxSlowCount&&wang.slowCount>wang.fastCount) {
			slowPool.execute(()->{
				this.download(url, fileNamePath, config,wang);
			});
			return;
		}
		
		this.download(url, fileNamePath, config,wang);
	}
	
	private void download(String url, String fileNamePath,DownloadConfig config,Wang wang) {
		LocalDateTime now = LocalDateTime.now();
		boolean success = false;
		if (wang.type==1&&wang.testLocalCount < maxLocalFail) {
			try {
				success = doUrl(url, fileNamePath, config.getDwonloadType());
			} catch (ConnectException e) {
				wang.type=2;
			}
			if (!success) {
				wang.testLocalCount++;// 本地错误+1
			} else {
				return;
			}
		}

		if (startProxy &&wang.type==2&& !success && wang.testProxyCount < maxProxyFail) {
			success = doProxyUrl(url, fileNamePath);
			if (!success) {
				wang.testProxyCount++;
			} else {
				return;
			}
		}
		
		if(Duration.between(now, LocalDateTime.now()).toMillis()>slowTime) {
			wang.slowCount++;
		}else {
			wang.fastCount++;
		}
		log.error("文件下载失败：{}", url);
	}
	
	private boolean doProxyUrl(String url,String fileNamePath) {
		HttpAble httpAble=ProxyOkHttp.getInstance(ip,port);
		return httpAble.download(url, fileNamePath, 5,true);
	}
	
	private boolean initProxy(DownloadConfig config){
		if(startProxy==null) {
			synchronized (DownloadAutoManager.class) {
				if(startProxy==null) {
					if (config.isTryProxy()) {
						ip = config.getTestProxyIp();
						port = config.getTestProxyPort();
					} else if (StringUtils.isEmpty(config.getProxyIp())) {
						ip = config.getProxyIp();
						port = config.getProxyPort();
					}
					if (ip != null) {
						try {
							Socket socket = new Socket(ip, port); // 建立一个Socket连接
							socket.close();
							startProxy=false;
						} catch (Exception e) {
							startProxy=true;
						}
					}else {
						startProxy=false;
					}
				}
			}
		}
		return startProxy;
	}
	
	private boolean doUrl(String url,String fileNamePath,HttpType type) {
		HttpAble httpAble;
		switch(type){
		case HTTPCLIENT:
			httpAble=HttpClientManager.getInstance();
			break;
		default:
			httpAble=OkHttpManager.getInstance();
			break;
		}
		return httpAble.download(url, fileNamePath, 5,true);
	}
	
	private Wang getWang(String webRoot,int type) {
		Wang dto=wang.get(webRoot);
		if(dto==null) {
			dto=new Wang();
			dto.type=type;
			wang.put(webRoot, dto);
		}
		return dto;
	}
	
	
	
	class Wang {
		/**0-不访问，1=本地访问，2-代理访问*/
		int type=1;
		/**尝试本地url失败个数**/
		int testLocalCount=0;
		/**尝试代理url失败个数**/
		int testProxyCount=0;
		/**下载缓慢的次数**/
		int slowCount=0;
		/**下载快的次数**/
		int fastCount=0;
	}


}
