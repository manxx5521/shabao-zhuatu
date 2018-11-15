package com.xiaoshabao.zhuatu.core;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xiaoshabao.zhuatu.TuInfo;



/**
 * 抓图工具
 * <p>对URL去重</p>
 */
public class ZhuatuToHeavy extends Decorator{

	private final static Logger log = LoggerFactory
			.getLogger(ZhuatuToHeavy.class);
	
	
	private Map<Integer,List<String>> pageMap=new HashMap<Integer,List<String>>();
	
	public ZhuatuToHeavy(ZhuatuParser parser) {
		super(parser);
	}

	@Override
	public boolean beforPageProjet(Service service, TuInfo info,int index) {
		if(!super.beforPageProjet(service, info,index)){
			return false;
		}
		
		List<String> pages=pageMap.get(index);
		String url=info.getUrl();
		if(pages==null){
			pages=new LinkedList<String>();
			pages.add(url);
			pageMap.put(index, pages);
		}else if(!pages.contains(url)){
			pages.add(url);
		}else{
			log.info("{}已经解析过了",info.getTitle()==null?"":info.getTitle());
			return false;
		}
		return true;
		
	}
	
	



	@Override
	public void beforNextService(TuInfo info, Service nextService,int nextIndex) {
		super.beforNextService(info, nextService, nextIndex);
		pageMap.put(nextIndex, new LinkedList<String>());
	}

}
