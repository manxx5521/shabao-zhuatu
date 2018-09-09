package com.xiaoshabao.zhuatu.core;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xiaoshabao.zhuatu.TuInfo;
import com.xiaoshabao.zhuatu.ZhuatuAble;
import com.xiaoshabao.zhuatu.ZhuatuConfig;
import com.xiaoshabao.zhuatu.ZhuatuUtil;
import com.xiaoshabao.zhuatu.http.ZhuatuHttpManager;
import com.xiaoshabao.zhuatu.service.ZhuatuService;

/**
 * 抓图抽象类
 */
public abstract class AbstractZhuatuImpl implements ZhuatuAble {

	protected Logger log = LoggerFactory.getLogger(getClass());

	protected List<ZhuatuService> zhuatuServices;
	protected ZhuatuConfig config;

	@Override
	final public void start(String url, List<ZhuatuService> zhuatuServices) {
		this.init(url, zhuatuServices, new ZhuatuConfig());
	}

	@Override
	final public void start(String url, List<ZhuatuService> zhuatuServices, String savePath) {
		ZhuatuConfig config = new ZhuatuConfig();
		config.setSavePath(savePath);
		this.init(url, zhuatuServices, config);
	}

	@Override
	final public void start(String url, List<ZhuatuService> zhuatuServices, String savePath, String charset) {
		ZhuatuConfig config = new ZhuatuConfig();
		config.setSavePath(savePath);
		config.setCharset(charset);
		this.init(url, zhuatuServices, config);
	}

	@Override
	public void start(String url, List<ZhuatuService> zhuatuServices, ZhuatuConfig config) {
		init(url, zhuatuServices, config);
	}
	
	/**
	 * 初始化方法
	 * @param url
	 * @param zhuatuServices
	 * @param config
	 */
	protected void init(String url, List<ZhuatuService> zhuatuServices, ZhuatuConfig config) {
		log.info("开始抓取：{}", url);
		try {
			if (StringUtils.isEmpty(url) || zhuatuServices == null || zhuatuServices.size() < 1) {
				log.error("错误的初始化信息，没有url或者为设置抓取服务实现");
				return;
			}
			this.zhuatuServices = zhuatuServices;
			this.config = config;

			// 预先加载服务
			for (ZhuatuService service : zhuatuServices) {
				initBeforSerivce(service);
			}
			
			String host=new  java.net.URL(url).getHost();
			config.setWebRoot(url.substring(0,url.lastIndexOf(host))+host+"/");

			TuInfo info = new TuInfo();
			info.setUrl(url);
			final CountDownLatch latch=new CountDownLatch(config.getThreadCount());
			for(int i=0;i<config.getThreadCount();i++) {
				CompletableFuture.runAsync(() -> {
					parserPage(info, 0, true);
					latch.countDown();
				});
			}
			latch.await();
		} catch (Exception e) {
			log.error("程序异常结束", e);
		}
	}

	/**
	 * 根据加载服务的不同类型进行初始化操作
	 * 
	 * @param service
	 */
	protected void initBeforSerivce(ZhuatuService service) {

	}

	/**
	 * 解析页面
	 * 
	 * @param pageInfo
	 * @param zhuatuService
	 * @param idx
	 *            当前层次
	 * @param newProject
	 *            是否是新项目（如果false表示是下一页解析）
	 */
	protected void parserPage(TuInfo pageInfo, int idx, boolean newProject) {
		ZhuatuService zhuatuService = this.zhuatuServices.get(idx);

		String html = null;
		if (config.isReqHtml()&&zhuatuService.isReqHtml()) {
			// 访问url
			html = ZhuatuHttpManager.getInstance().doHTTPAuto5(
					ZhuatuUtil.formatUrl(pageInfo.getUrl(),config.getWebRoot()), config);
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
		if(list!=null&&list.size() > 1){
			Iterator<TuInfo> iterator = list.iterator();
			// 链表用迭代器
			ma:
			while (iterator.hasNext()) {
				TuInfo tuInfo = iterator.next();
				
				//如果时不需要访问的域名前缀
				for(String start:this.config.getNoUrl()){
					if(tuInfo.getUrl().startsWith(start)){
						log.info("链接在noUrl中无需访问。url->{}",tuInfo.getUrl());
						continue ma;
					}
				}
				
				// 扩展操作
				if (!exeCurrPageProjet(zhuatuService, tuInfo)) {
					continue ma;
				}

				if (zhuatuServices.size() > idx + 1) {
					// 进行下一层任务
					parserPageNextIdx(tuInfo, zhuatuServices.get(idx + 1), idx + 1);
				}
				
				// 结束扩展
				endCurrPageProjet(zhuatuService, tuInfo);
			}
		}else{
			log.error("url解析内容 未能正常返回直接跳过,进行下一页  url->{}", pageInfo.getUrl());
		}

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
			parserPage(tuInfo, idx, false);// 下一页
		}

	}

	/**
	 * 进行下一层任务
	 */
	protected void parserPageNextIdx(TuInfo pageInfo, ZhuatuService zhuatuService, int idx) {
		parserPage(pageInfo, idx, true);
	}

	/**
	 * 对当前页解析出的项目操作
	 * 
	 * @param service
	 * @param tuInfo
	 *            解析出的项目（列表中的一个）
	 * @return 返回false代表跳过当前，不进行下层操作。否则查找下层任务
	 */
	protected boolean exeCurrPageProjet(ZhuatuService service, TuInfo tuInfo) {
		return true;
	}
	
	/**
	 * 对当前页解析以及下层内容解析完成后的扩展
	 * @param service
	 * @param tuInfo
	 *            解析出的项目（列表中的一个）
	 */
	protected void endCurrPageProjet(ZhuatuService service, TuInfo tuInfo) {
	}

	/**
	 * 关闭资源
	 */
	@Override
	public void colse() {

	}
}
