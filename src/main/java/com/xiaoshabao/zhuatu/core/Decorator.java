package com.xiaoshabao.zhuatu.core;

import java.util.List;

import com.xiaoshabao.zhuatu.TuInfo;
import com.xiaoshabao.zhuatu.ZhuatuConfig;
import com.xiaoshabao.zhuatu.service.ZhuatuService;

public class Decorator implements ZhuatuParser {

	protected ZhuatuParser parser;
	
	public Decorator(ZhuatuParser parser){
		this.parser=parser;
	}

	public ZhuatuParser getParser() {
		return parser;
	}

	@Override
	public void init(List<ZhuatuService> serviceList,ZhuatuConfig config) {
		parser.init(serviceList,config);
	}

	@Override
	public void beforPageProjet(ZhuatuService service, TuInfo info,int index) {
		parser.beforPageProjet(service, info,index);
	}

	@Override
	public void afterPageProjet(ZhuatuService service, TuInfo info) {
		parser.afterPageProjet(service, info);
	}

	@Override
	public boolean doReturnProject(ZhuatuService service, TuInfo info) {
		return parser.doReturnProject(service, info);
	}

	@Override
	public void beforNextService(TuInfo info, ZhuatuService nextService,
			int nextIndex) {
		parser.beforNextService(info, nextService, nextIndex);
	}

}
