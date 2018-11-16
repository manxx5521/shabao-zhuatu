package com.xiaoshabao.zhuatu.core.function;

import java.util.List;

import com.xiaoshabao.zhuatu.core.TuInfo;
import com.xiaoshabao.zhuatu.core.config.ZhuatuConfig;

@FunctionalInterface
public interface ParserResultFunction {
	
	/**
	 * 主要解析内容
	 * @param html
	 * @param pageInfo
	 * @param config 
	 * @param result 进行下一层解析的url信息
	 * @return 大小返回0或者null时跳过，直接进行下一页
	 */
	void parser(String html,TuInfo pageInfo,ZhuatuConfig config,List<TuInfo> result) throws Exception;

}
