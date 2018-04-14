package com.xiaoshabao.zhuatu.service.impl;

import org.junit.Test;

import com.xiaoshabao.zhuatu.http.OkHttpManager;

public class SingleUrlDownload {
	
	@Test
	public void test(){
		String url="http://120.52.72.23/c.pic303.com/images/2017/12/05/DSC_2021672a9a56848ff21c.jpg";
		
		String fileName = url.substring(url.lastIndexOf("/") + 1, url.length());
		OkHttpManager.getInstance().download5(url, "E:\\test\\shabao-m\\resources\\plugins\\mm\\"+fileName);
	}

}
