package com.xiaoshabao.zhuatu.http;

import java.net.MalformedURLException;
import java.net.Socket;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

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
	private static ExecutorService slowPool=Executors.newFixedThreadPool(10);

	private Map<String,Wang> wang=new ConcurrentHashMap<String, Wang>();
	
	private static Map<String,List<String>> conUrl=new HashMap<String,List<String>>();
	
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
	/**如果连续5无法正常连接尝试代理*/
	private int tryProxyCount=5;
	
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
		
		String webRoot=null;
		try {
			String host = new java.net.URL(url).getHost();
			webRoot=url.substring(0, url.lastIndexOf(host)) + host + "/";
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		Wang wang=getWang(webRoot, 1);
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
		boolean proxyFlag=false;
		if (wang.type==1&&wang.failLocalCount < maxLocalFail) {
			try {
				success = doUrl(url, fileNamePath, config.getDwonloadType());
			} catch (ConnectException e) {
				proxyFlag=startProxy;
			}
			if (!success) {
				wang.failLocalCount++;// 本地错误+1
			}
		}
		
		//判断是否使用代理模式
		if(proxyFlag) {
			
			synchronized (conUrl) {
				if(wang.type!=2){
					List<String> noUrl=conUrl.get(wang.webRoot);
					if(noUrl==null) {
						noUrl=new ArrayList<String>(tryProxyCount);
						conUrl.put(wang.webRoot, noUrl);
					}
					noUrl.add(url);
					
					if(noUrl.size()>tryProxyCount-1) {
						wang.type=2;
						log.info("切换代理尝试：{}",wang.webRoot);
						for(String u:noUrl) {
							if (!doProxyUrl(u, fileNamePath)) {
								wang.failProxyCount++;
							}
						}
						conUrl.remove(wang.webRoot);
					}
					return;
				}
			}
		}

		
		//执行代理模式
		if (startProxy &&wang.type==2&& !success && wang.failProxyCount < maxProxyFail) {
			try {
				success = doProxyUrl(url, fileNamePath);
			} catch (ConnectException e) {
				log.error("此项目无法访问{}",url);
				wang.type=0;
			}
			if (!success) {
				wang.failProxyCount++;
			}
		}
		
		if(Duration.between(now, LocalDateTime.now()).getSeconds()>slowTime) {
			wang.slowCount++;
		}else {
			wang.fastCount++;
		}
		if (!success) {
			log.error("文件下载失败：{}", url);
		}
		
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
					} else if (!StringUtils.isEmpty(config.getProxyIp())) {
						ip = config.getProxyIp();
						port = config.getProxyPort();
					}
					if (ip != null) {
						try {
							Socket socket = new Socket(ip, port); // 建立一个Socket连接
							socket.close();
							startProxy=true;
						} catch (Exception e) {
							startProxy=false;
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
			dto.webRoot=webRoot;
			wang.put(webRoot, dto);
		}
		return dto;
	}
	
	public static Integer getSlowCount(){
		if(instance!=null){
			return ((ThreadPoolExecutor)slowPool).getActiveCount();
		}
		return null;
	}
	
	public static void waitDownload(){
		if(instance!=null){
			ThreadPoolExecutor pool=(ThreadPoolExecutor)slowPool;
			pool.shutdown();
			
			while(true) {
				//关闭后所有任务都已完成,则返回true
				if(pool.isTerminated()) {
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
	
	class Wang {
		String webRoot;
		/**0-不访问，1=本地访问，2-代理访问*/
		int type=1;
		/**尝试本地url失败个数**/
		int failLocalCount=0;
		/**尝试代理url失败个数**/
		int failProxyCount=0;
		/**下载缓慢的次数**/
		int slowCount=0;
		/**下载快的次数**/
		int fastCount=0;
	}


}
