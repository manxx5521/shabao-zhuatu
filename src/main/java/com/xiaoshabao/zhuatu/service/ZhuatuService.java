package com.xiaoshabao.zhuatu.service;

import java.util.List;

import com.xiaoshabao.zhuatu.TuInfo;
import com.xiaoshabao.zhuatu.ZhuatuConfig;


/**
 * 抓图接口
 * <p>重要服务，所有的自定义接口要实现本接口，用来解析HTML页的内容</p>
 */
public interface ZhuatuService {
	
	/**
	 * 主要解析内容
	 * @param html
	 * @param pageInfo
	 * @param config 
	 * @return 大小返回0或者null时跳过，直接进行下一页
	 */
	List<TuInfo> parser(String html,TuInfo pageInfo,ZhuatuConfig config/*,List<String> projects,boolean newProject*/) throws Exception;
	
	/**
	 * 解析下一页的URL
	 */
	default String nextPage(String html,ZhuatuConfig config) throws Exception{
		return null;
	};
	
	/**
	 * 当前抓图 是否直接请求url，返回内容到html变量默认true
	 * <p>可以通过覆盖此方法修改变量值</p>
	 */
	default boolean isReqHtml() {
		return true;
	}

}
