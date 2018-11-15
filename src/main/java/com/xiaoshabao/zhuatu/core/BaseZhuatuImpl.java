package com.xiaoshabao.zhuatu.core;

import java.util.List;

import com.xiaoshabao.zhuatu.TuInfo;
import com.xiaoshabao.zhuatu.ZhuatuConfig;

public class BaseZhuatuImpl implements ZhuatuParser {

	@Override
	public void init(List<Service> serviceList,ZhuatuConfig config) {

	}

	@Override
	public boolean beforPageProjet(Service service, TuInfo info, int index) {
		return true;
	}

	@Override
	public void afterPageProjet(Service service,TuInfo info) {
	}

	@Override
	public boolean doReturnProject(Service service, TuInfo info) {
		return true;
	}

	@Override
	public void beforNextService(TuInfo info, Service nextService,int nextIndex) {

	}

}
