package com.xiaoshabao.zhuatu;

import java.util.List;

import com.xiaoshabao.zhuatu.service.ZhuatuService;
/**
 * 抓图功能接口
 */
public interface ZhuatuAble{
	
	/**
	 * 抓图入口方法
	 * @param url
	 * @param zhuatuServices 服务列表，层级操作
	 */
	void start(String url,List<ZhuatuService> zhuatuServices);
	/**
	 * 抓图入口方法
	 * @param url
	 * @param zhuatuServices 服务列表，层级操作
	 * @param savePath 保存文件夹
	 */
	void start(String url,List<ZhuatuService> zhuatuServices,String savePath);
	/**
	 * 抓图入口方法
	 * @param url
	 * @param zhuatuServices 服务列表，层级操作
	 * @param savePath 保存文件夹
	 * @param charset 编码格式
	 */
	void start(String url,List<ZhuatuService> zhuatuServices,String savePath,String charset);
	/**
	 * 抓图入口方法
	 * @param url
	 * @param zhuatuServices 服务列表，层级操作
	 * @param config 具体配置类，包含所有配置
	 */
	void start(String url,List<ZhuatuService> zhuatuServices,ZhuatuConfig config);
	
	/**
	 * 多个任务时需要关闭资源
	 */
	public void colse();

}
