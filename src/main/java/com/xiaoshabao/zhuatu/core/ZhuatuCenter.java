package com.xiaoshabao.zhuatu.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xiaoshabao.zhuatu.core.config.DownloadConfig;
import com.xiaoshabao.zhuatu.core.config.ZhuatuConfig;
import com.xiaoshabao.zhuatu.exception.ZhuatuException;
import com.xiaoshabao.zhuatu.http.HttpAble;
import com.xiaoshabao.zhuatu.http.ProxyOkHttp;
import com.xiaoshabao.zhuatu.http.ZhuatuHttpManager;

public class ZhuatuCenter{
	private final static Logger log = LoggerFactory.getLogger(ZhuatuCenter.class);
	private List<Service> serviceList=new ArrayList<Service>();
	private ZhuatuConfig config;
	
	
	
	private ZhuatuParser parser;
	
	
	
	public ZhuatuCenter() {
		
	}
	
	/**
	 * 创建配置信息
	 * @return
	 */
	public ZhuatuConfig createConfig() {
		config=new ZhuatuConfig(this);
		return config;
	}
	
	/**
	 * 创建配置信息
	 * @return
	 */
	public DownloadConfig createDownloadConfig() {
		DownloadConfig dconfig=new DownloadConfig(this);
		this.config=dconfig;
		return dconfig;
	}
	
	/**
	 * 创建一层抓图服务
	 * @return
	 */
	public Service createService() {
		Service service=new Service(this);
		serviceList.add(service);
		return service;
	}
	
	public void start() {
		log.info("初始化配置....");
		init();
		log.info("初始化完成");
		
		try {
			TuInfo info = new TuInfo();
			info.setUrl(config.getUrl());
			final CountDownLatch latch = new CountDownLatch(
					config.getThreadCount());
			for (int i = 0; i < config.getThreadCount(); i++) {
				CompletableFuture.runAsync(() -> {
					parserPage(serviceList.get(0),info, 0, true);
					latch.countDown();
				});
			}
			latch.await();
			parser.afterRuning();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void init() {
		try {
			String url = config.getUrl();
			if (StringUtils.isEmpty(url) || serviceList.size() < 1) {
				throw new ZhuatuException("错误的初始化信息，没有url或者为设置抓取服务实现");
			}
			
			//预先加载策略
			if(parser==null){
				parser=new BaseZhuatuImpl();
				if(config.isHeavyURL()) {
					parser=new ZhuatuToHeavy(parser);
				}
				for(Service serivce:serviceList){
					if(serivce.isDownloadUrl()){
						parser=new DownloadZhuatuImpl(parser);
						break;
					}
				}
			}

			// 预先加载服务
			parser.init(serviceList,config);

			String host = new java.net.URL(url).getHost();
			config.setWebRoot(url.substring(0, url.lastIndexOf(host)) + host
					+ "/");

		} catch (Exception e) {
			if (e instanceof ZhuatuException) {
				log.error(e.getMessage());
			} else {
				log.error("程序异常结束");
			}
			throw new ZhuatuException(e);
		}
	}
	/**
	 * 解析当前页面
	 * @param pageInfo
	 * @param zhuatuService
	 * @param idx
	 *            当前层次
	 * @param newProject
	 *            是否是新项目（如果false表示是下一页解析）
	 */
	public void parserPage(Service service, TuInfo pageInfo,
			int idx, boolean newProject) {
		if(!parser.beforPageProjet(service, pageInfo,idx)){
			return;
		}
		
		List<TuInfo> list = null;
		String html = null;
		try {
			if (service.getParserUrlFunction() != null) {
				list = service.getParserUrlFunction().parser(pageInfo.getUrl(), pageInfo, config);
			}
			
			if (service.getParserFunction() != null) {
				html=this.getUrl(pageInfo.getUrl());
				// 访问失败跳出
				if (html == null) {
					return;
				}
				list = service.getParserFunction().parser(html, pageInfo, config);
			}
		
		} catch (Exception e) {
			log.error("解析错误", e);
		}
		
		//解析下一层
		if(list!=null&&list.size() > 0){
			Iterator<TuInfo> iterator = list.iterator();
			// 链表用迭代器
			ma:
			while (iterator.hasNext()) {
				TuInfo tuInfo = iterator.next();
				ZhuatuUtil.formatInfo(tuInfo,config.getWebRoot());
				
				// 扩展操作
				if (!parser.doReturnProject(service, tuInfo)) {
					continue ma;
				}

				if (serviceList.size() > idx + 1) {
					// 进行下一层任务
					parser.beforNextService(tuInfo, serviceList.get(idx + 1), idx + 1);
					parserPage(serviceList.get(idx + 1),tuInfo,  idx + 1, true);
				}
				
				// 结束扩展
				parser.afterPageProjet(service, tuInfo);
			}
		}else{
			log.error("url解析内容 未能正常返回直接跳过,进行下一页  url->{}", pageInfo.getUrl());
		}

		//解析当前层下一页
		String nextUrl = null;
		try {
			if(html==null) {
				html=this.getUrl(pageInfo.getUrl());
			}
			nextUrl = service.getNextFunction().nextPage(html, config);
		} catch (Exception e) {
			log.warn("url解析下一页时错误。  url->{}", pageInfo.getUrl());
		}
		if (StringUtils.isNotEmpty(nextUrl)) {
			TuInfo tuInfo = new TuInfo();
			tuInfo.setUrl(nextUrl);
			tuInfo.setTitle(pageInfo.getTitle());
			parserPage(service,tuInfo, idx, false);// 下一页
		}
		
	}
	
	private String getUrl(String url) {
		HttpAble httAble=null;
		if(StringUtils.isEmpty(config.getProxyIp())){
			// 访问url
			httAble = ZhuatuHttpManager.getInstance();
		}else{
			httAble=ProxyOkHttp.getInstance(config.getProxyIp(), config.getProxyPort());
		}
		return httAble.doUrl(url, config.getMethod(), config.getCharset(), 3);
	}
	
	
	

}
