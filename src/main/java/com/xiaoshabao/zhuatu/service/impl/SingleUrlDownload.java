package com.xiaoshabao.zhuatu.service.impl;

import org.junit.Test;

import com.xiaoshabao.zhuatu.http.OkHttpManager;

public class SingleUrlDownload {
	
	@Test
	public void test(){
		String url="http://s6tu.com/images/2018/08/11/2017-08-03-220242.md.jpg";
		
		String fileName = url.substring(url.lastIndexOf("/") + 1, url.length());
		try {
			OkHttpManager.getInstance().download(url, "E:\\test\\shabao-m\\resources\\plugins\\mm\\"+fileName);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
