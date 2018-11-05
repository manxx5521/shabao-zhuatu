package com.xiaoshabao.zhuatu.core;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xiaoshabao.zhuatu.TuInfo;
import com.xiaoshabao.zhuatu.ZhuatuConfig;
import com.xiaoshabao.zhuatu.ZhuatuUtil;
import com.xiaoshabao.zhuatu.exception.ZhuatuException;
import com.xiaoshabao.zhuatu.http.HttpAble;
import com.xiaoshabao.zhuatu.http.ProxyOkHttp;
import com.xiaoshabao.zhuatu.http.ZhuatuHttpManager;
import com.xiaoshabao.zhuatu.service.ZhuatuService;
import com.xiaoshabao.zhuatu.service.able.HeavyAble;
import com.xiaoshabao.zhuatu.service.able.ZhuatuDownloadAble;

public class ZhuatuCenter{
	private final static Logger log = LoggerFactory
			.getLogger(ZhuatuCenter.class);

	private ZhuatuParser parser;
	private List<ZhuatuService> serviceList;
	private ZhuatuConfig config;
	
	public ZhuatuCenter(ZhuatuConfig config,
			List<ZhuatuService> serviceList) {
		this.serviceList = serviceList;
		this.config = config;
	}
	public ZhuatuCenter(ZhuatuParser parser, ZhuatuConfig config,
			List<ZhuatuService> serviceList) {
		this.parser = parser;
		this.serviceList = serviceList;
		this.config = config;
	}

	public void run() {
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
					parserPage(this.serviceList.get(0),info, 0, true);
					latch.countDown();
				});
			}
			latch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void init() {
		try {
			String url = config.getUrl();
			if (StringUtils.isEmpty(url) || serviceList == null
					|| serviceList.size() < 1) {
				throw new ZhuatuException("错误的初始化信息，没有url或者为设置抓取服务实现");
			}
			
			if(parser==null){
				parser=new BaseZhuatuImpl();
				boolean heavy=false,download=false;
				for(ZhuatuService serivce:serviceList){
					if(serivce instanceof HeavyAble&&heavy!=true){
						parser=new ZhuatuToHeavy(parser);
						heavy=true;
					}
					if(serivce instanceof ZhuatuDownloadAble&&download!=true){
						parser=new DownloadZhuatuImpl(parser);
						download=true;
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
	public void parserPage(ZhuatuService zhuatuService, TuInfo pageInfo,
			int idx, boolean newProject) {
		if(!parser.beforPageProjet(zhuatuService, pageInfo,idx)){
			return;
		}
		
		//解析当前层
		String html = null;
		if (config.isReqHtml()&&zhuatuService.isReqHtml()) {
			HttpAble httAble=null;
			if(StringUtils.isEmpty(config.getProxyIp())){
				// 访问url
				httAble = ZhuatuHttpManager.getInstance();
			}else{
				httAble=ProxyOkHttp.getInstance(config.getProxyIp(), config.getProxyPort());
			}
			html=httAble.doUrl(pageInfo.getUrl(), config.getMethod(), config.getCharset(), 3);
			// 访问失败跳出
			if (html == null) {
				return;
			}
			
		}

		List<TuInfo> list = null;
		try {
			list = zhuatuService.parser(html, pageInfo, config);
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
				
				//如果时不需要访问的域名前缀
				for(String start:config.getNoUrl()){
					if(tuInfo.getUrl().startsWith(start)){
						log.info("链接在noUrl中无需访问。url->{}",tuInfo.getUrl());
						continue ma;
					}
				}
				
				// 扩展操作
				if (!parser.doReturnProject(zhuatuService, tuInfo)) {
					continue ma;
				}

				if (serviceList.size() > idx + 1) {
					// 进行下一层任务
					parser.beforNextService(tuInfo, serviceList.get(idx + 1), idx + 1);
					parserPage(serviceList.get(idx + 1),tuInfo,  idx + 1, true);
				}
				
				// 结束扩展
				parser.afterPageProjet(zhuatuService, tuInfo);
			}
		}else{
			log.error("url解析内容 未能正常返回直接跳过,进行下一页  url->{}", pageInfo.getUrl());
		}

		//解析当前层下一页
		String nextUrl = null;
		try {
			nextUrl = zhuatuService.nextPage(html, config);
		} catch (Exception e) {
			log.warn("url解析下一页时错误。  url->{}", pageInfo.getUrl());
		}
		if (StringUtils.isNotEmpty(nextUrl)) {
			TuInfo tuInfo = new TuInfo();
			tuInfo.setUrl(nextUrl);
			tuInfo.setTitle(pageInfo.getTitle());
			parserPage(zhuatuService,tuInfo, idx, false);// 下一页
		}
		
	}

}
