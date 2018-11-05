package com.xiaoshabao.zhuatu.core;

import java.util.List;

import com.xiaoshabao.zhuatu.TuInfo;
import com.xiaoshabao.zhuatu.ZhuatuConfig;
import com.xiaoshabao.zhuatu.service.ZhuatuService;

public interface ZhuatuParser {
	
	/**
	 * 初始化
	 */
	void init(List<ZhuatuService> serviceList,ZhuatuConfig config);
	/**
	 * 解析页面之前扩展
	 * @param service
	 * @param info
	 *            解析出的项目（列表中的一个）
	 * @return 是否继续执行，继续true
	 */
	boolean beforPageProjet(ZhuatuService service,TuInfo info,int index);
	/**
	 * 对当前页解析以及下层内容解析完成后的扩展
	 * @param service
	 * @param info
	 *            解析出的项目（列表中的一个）
	 */
	void afterPageProjet(ZhuatuService service,TuInfo info);
	/**
	 * 对当前页解析出的项目操作
	 * 
	 * @param service
	 * @param tuInfo
	 *            解析出的项目（列表中的一个）
	 * @return 返回false代表跳过当前，不进行下层操作。否则查找下层任务
	 */
	boolean doReturnProject(ZhuatuService service, TuInfo info);
	
	/**
	 * 进行下一层任务之前
	 * @param info
	 * @param nextService 下一层任务的service
	 * @param nextIndex 下一层次的深度
	 */
	void beforNextService(TuInfo info, ZhuatuService nextService, int nextIndex);
	
	/**
	 * 项目启动之后执行
	 */
	default void afterRuning(){
		
	};
	
}
