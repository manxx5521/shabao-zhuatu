package com.xiaoshabao.zhuatu.core;

import com.xiaoshabao.zhuatu.ZhuatuAble;

/**
 * 抓图工厂
 */
public class ZhuatuFactory {
	
	/**
	 * 简单抓图任务
	 * @return
	 */
	public static ZhuatuAble createBaseZhuatu() {
		return new SimpleZhuatuImpl();
	}
	
	/**
	 * 排重抓图任务
	 * <p>对统一链接过滤，不再抓取</p>
	 * @return
	 */
	public static ZhuatuAble createHeavyZhuatu() {
		return new ZhuatuToHeavy();
	}
	/**
	 * 下载抓图任务
	 * <p>对统一链接过滤，不再抓取</p>
	 * @return
	 */
	public static ZhuatuAble createDownloadZhuatu() {
		return new DownloadZhuatuImpl();
	}
	

}
