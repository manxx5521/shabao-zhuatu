package com.xiaoshabao.zhuatu.http;

import java.io.File;
import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;

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
	
	/**
	 * 下载文件文件到指定目录（尝试5次）
	 * @param url
	 * @param pathName
	 */
	public void download5(String url, String pathName) {
		new RetryFactory<String, Boolean>(url, "下载文件"+url)
			.addExceptionCount(SocketException.class, 2)
			.addExceptionCount(SocketTimeoutException.class, 3)
			.execute(t -> {
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
	    	if(response.code()==404){
	    		log.info("url访问失败,404：url->{}",url);
	    		FileUtils.copyInputStreamToFile(OkHttpManager.class.getResourceAsStream("/images/404.png"), new File(pathName));
	    	}else{
	    		throw new IOException("url访问失败返回识别码 " + response+"\n\r url->"+url);
	    	}
	    	
	    }
	}
	
	/**
	 * 尝试访问url查看是否可以访问成功
	 * @param url
	 * @return true成功访问，false访问失败
	 */
	public boolean testUrl(String url) {
		try {
			Request request = new Request.Builder().url(url).build();
			Response response = client.newCall(request).execute();
		    if (response.isSuccessful()||response.code()==404) {
		    	return true;
		    }
		} catch (Exception e) {
		}
		log.debug("url无法访问成功，url->{}",url);
		return false;
		
	}
	

}
