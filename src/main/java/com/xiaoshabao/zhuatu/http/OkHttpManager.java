package com.xiaoshabao.zhuatu.http;

import java.io.File;
import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xiaoshabao.zhuatu.RetryFactory;


public class OkHttpManager {
	protected Logger log = LoggerFactory.getLogger(getClass());
	private volatile static OkHttpManager instance = null;
	
	OkHttpClient client = null;
	
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
	
	public void download5(String url, String pathName) {
		new RetryFactory<String, Boolean>(url, "下载文件").execute(t -> {
			this.download(url, pathName);
			log.info("下载文件成功 url->{}", url);
			return Boolean.TRUE;
		});
	}
	
	private void download(String url, String pathName) throws Exception {
		Request request = new Request.Builder().url(url).build();
		Response response = client.newCall(request).execute();
	    if (response.isSuccessful()) {
	    	FileUtils.writeByteArrayToFile(new File(pathName), response.body().bytes());
	    } else {
	    	throw new IOException("url访问失败返回识别码 " + response+"\n\r url->"+url);
	    }
	}
	

}
