package com.xiaoshabao.zhuatu.core;

import java.util.List;

import com.xiaoshabao.zhuatu.ZhuatuConfig;
import com.xiaoshabao.zhuatu.ZhuatuUtil;
import com.xiaoshabao.zhuatu.service.ZhuatuService;

/**
 * 抓图工厂
 */
public class ZhuatuFactory {
	
	final public static void start(String url, List<ZhuatuService> zhuatuServices) {
		start(url, zhuatuServices, new ZhuatuConfig());
	}

	final public static void start(String url, List<ZhuatuService> zhuatuServices, String savePath) {
		ZhuatuConfig config = new ZhuatuConfig();
		config.setSavePath(savePath);
		start(url, zhuatuServices, config);
	}

	final public static void start(String url, List<ZhuatuService> zhuatuServices, String savePath, String charset) {
		ZhuatuConfig config = new ZhuatuConfig();
		config.setSavePath(savePath);
		config.setCharset(charset);
		start(url, zhuatuServices, config);
	}

	public static void start(String url, List<ZhuatuService> zhuatuServices, ZhuatuConfig config) {
		config.setUrl(ZhuatuUtil.formatUrl(url));
		ZhuatuCenter center=new ZhuatuCenter(config,zhuatuServices);
		center.run();
	}
}
