package com.xiaoshabao.zhuatu.http;

import okhttp3.OkHttpClient;


public class OkHttpManager extends BaseOkHttp{
	
	private volatile static OkHttpManager instance = null;
	
	private OkHttpManager() {
		client=new OkHttpClient();
	}

	public static OkHttpManager getInstance() {
		if (instance == null) {
			synchronized (ZhuatuHttpManager.class) {
				if (instance == null) {
					instance = new OkHttpManager();
				}
			}
		}
		return instance;
	}
	
}
