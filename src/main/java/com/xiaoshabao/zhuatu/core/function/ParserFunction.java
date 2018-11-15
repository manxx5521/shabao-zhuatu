package com.xiaoshabao.zhuatu.core.function;

import java.util.List;

import com.xiaoshabao.zhuatu.TuInfo;
import com.xiaoshabao.zhuatu.ZhuatuConfig;

@FunctionalInterface
public interface ParserFunction {
	
	/**
	 * 主要解析内容
	 * @param html
	 * @param pageInfo
	 * @param config 
	 * @return 大小返回0或者null时跳过，直接进行下一页
	 */
	List<TuInfo> parser(String html,TuInfo pageInfo,ZhuatuConfig config) throws Exception;

}
