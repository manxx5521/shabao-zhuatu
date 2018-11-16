package com.xiaoshabao.zhuatu.core.function;

import com.xiaoshabao.zhuatu.core.config.ZhuatuConfig;

/**
 * 解析下一页
 */
@FunctionalInterface
public interface NextFunction {
	
	/**
	 * 解析下一页的URL
	 */
	String nextPage(String html,ZhuatuConfig config) throws Exception;

}
