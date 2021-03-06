package com.xiaoshabao.zhuatu.core.function;

import java.util.List;

import com.xiaoshabao.zhuatu.core.TuInfo;
import com.xiaoshabao.zhuatu.core.config.ZhuatuConfig;

/**
 * 解析url
 */
@FunctionalInterface
public interface ParserUrlFunction {
	
	/**
	 * 不做任何处理，直接传递url到函数
	 * @param html
	 * @param pageInfo
	 * @param config 
	 * @return 大小返回0或者null时跳过，直接进行下一页
	 */
	void parser(String url,TuInfo pageInfo,ZhuatuConfig config,List<TuInfo> result) throws Exception;

}
