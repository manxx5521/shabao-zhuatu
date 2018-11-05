package com.xiaoshabao.zhuatu.core;

import java.util.List;

import com.xiaoshabao.zhuatu.TuInfo;
import com.xiaoshabao.zhuatu.ZhuatuConfig;
import com.xiaoshabao.zhuatu.service.ZhuatuService;

public class BaseZhuatuImpl implements ZhuatuParser {

	@Override
	public void init(List<ZhuatuService> serviceList, ZhuatuConfig config) {

	}

	@Override
	public boolean beforPageProjet(ZhuatuService service, TuInfo info, int index) {
		return true;
	}

	@Override
	public void afterPageProjet(ZhuatuService service, TuInfo info) {
	}

	@Override
	public boolean doReturnProject(ZhuatuService service, TuInfo info) {
		return true;
	}

	@Override
	public void beforNextService(TuInfo info, ZhuatuService nextService,
			int nextIndex) {

	}

}
