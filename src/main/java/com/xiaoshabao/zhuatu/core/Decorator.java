package com.xiaoshabao.zhuatu.core;

import java.util.List;

import com.xiaoshabao.zhuatu.core.config.ZhuatuConfig;

public class Decorator implements ZhuatuParser {

	protected ZhuatuParser parser;
	
	public Decorator(ZhuatuParser parser){
		this.parser=parser;
	}

	/*public ZhuatuParser getParser() {
		return parser;
	}*/

	@Override
	public void init(List<Service> serviceList,ZhuatuConfig config) {
		parser.init(serviceList,config);
	}

	@Override
	public boolean beforPageProjet(Service service, TuInfo info,int index) {
		return parser.beforPageProjet(service, info,index);
	}

	@Override
	public void afterPageProjet(Service service,TuInfo info) {
		parser.afterPageProjet(service, info);
	}

	@Override
	public boolean doReturnProject(Service service, TuInfo info) {
		return parser.doReturnProject(service, info);
	}

	@Override
	public void beforNextService(TuInfo info, Service nextService,
			int nextIndex) {
		parser.beforNextService(info, nextService, nextIndex);
	}

}
