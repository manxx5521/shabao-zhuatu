package com.xiaoshabao.zhuatu.service.impl;

import org.junit.Test;

import com.xiaoshabao.zhuatu.http.OkHttpManager;

public class SingleUrlDownload {
	
	@Test
	public void test(){
		String url="http://p1.qhimgs4.com/t01de8cf41f9fee867d.webp";
		
		String fileName = url.substring(url.lastIndexOf("/") + 1, url.length());
		OkHttpManager.getInstance().download5(url, "E:\\test\\shabao-m\\resources\\plugins\\mm\\"+fileName);
	}

}
