package com.xiaoshabao.zhuatu.http;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xiaoshabao.zhuatu.DownloadRetry;


public class BaseOkHttp {
	
	protected Logger log = LoggerFactory.getLogger(getClass());
	
	protected OkHttpClient client = null;
	
	/**
	 * 下载文件文件到指定目录（尝试N次）
	 * @param url
	 * @param pathName
	 * @param count 尝试次数
	 */
	public void download(String url, String pathName,int count) {
		new DownloadRetry<String, Boolean>(url, "下载文件"+url)
			.setCount(2)
			.execute(t -> {
			this.download(url, pathName);
			log.info("下载文件成功 url->{}", url);
			return Boolean.TRUE;
		});
	}
	
	public void download(String url, String pathName) throws Exception {
		Request request = new Request.Builder().url(url).build();
		Response response = getClient().newCall(request).execute();
	    if (response.isSuccessful()) {
	    	FileUtils.writeByteArrayToFile(new File(pathName), response.body().bytes());
	    } else {
	    	if(response.code()==404){
	    		log.info("url访问失败,404：url->{}",url);
	    		FileUtils.copyInputStreamToFile(BaseOkHttp.class.getResourceAsStream("/images/404.png"), new File(pathName));
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
			Response response = getClient().newCall(request).execute();
		    if (response.isSuccessful()||response.code()==404) {
		    	return true;
		    }
		} catch (Exception e) {
		}
		log.debug("url无法访问成功，url->{}",url);
		return false;
		
	}
	/**
	 * 尝试访问url查看是否可以访问成功
	 * @param url
	 * @return true成功访问，false访问失败
	 */
	public String doGet(String url) {
		return doGet(url, Charset.defaultCharset());
	}
	/**
	 * 尝试访问url查看是否可以访问成功
	 * @param url
	 * @return true成功访问，false访问失败
	 */
	public String doGet(String url,Charset charset ) {
		try {
			Request request = new Request.Builder().url(url).build();
			Response response = getClient().newCall(request).execute();
		    if (response.isSuccessful()||response.code()==404) {
		    	return new String(response.body().bytes(),charset);
		    }
		} catch (Exception e) {
			e.printStackTrace();
		}
		log.debug("url无法访问成功，url->{}",url);
		return null;
	}
	
	protected OkHttpClient getClient() {
		return client;
	}
	

}
