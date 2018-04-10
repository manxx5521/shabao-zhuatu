package com.xiaoshabao.zhuatu.service.impl;

import org.junit.Test;

import com.xiaoshabao.zhuatu.http.OkHttpManager;

public class SingleUrlDownload {
	
	@Test
	public void test(){
		String url="https://www1.wi.to/2017/12/03/12013664c84fc7bdea2b4f7423877a71.jpg";
		
		String fileName = url.substring(url.lastIndexOf("/") + 1, url.length());
		OkHttpManager.getInstance().download5(url, "E:\\test\\shabao-m\\resources\\plugins\\mm\\"+fileName);
	}

}
