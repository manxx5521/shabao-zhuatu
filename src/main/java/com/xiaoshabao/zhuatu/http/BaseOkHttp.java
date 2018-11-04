package com.xiaoshabao.zhuatu.http;

import java.io.File;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.nio.charset.Charset;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class BaseOkHttp implements HttpAble{
	
	protected Logger log = LoggerFactory.getLogger(getClass());
	
	protected OkHttpClient client = null;
	
	@Override
	public String doGet(String url, Charset charset) throws IOException {
			Request request = new Request.Builder().url(url).build();
			Response response = getClient().newCall(request).execute();
		    if (response.isSuccessful()||response.code()==404) {
		    	return new String(response.body().bytes(),charset);
		    }
		log.debug("url无法访问成功，url->{}",url);
		return null;
	}
	@Override
	public String doPost(String url, Charset charset) throws IOException {
		return doGet(url,charset);
	}
	public boolean download(String url, String pathName) throws SocketTimeoutException {
		try {
			Request request = new Request.Builder().url(url).build();
			Response response = getClient().newCall(request).execute();
		    if (response.isSuccessful()) {
		    	FileUtils.writeByteArrayToFile(new File(pathName), response.body().bytes());
		    	return true;
		    } else {
		    	if(response.code()==404){
		    		log.error("url访问失败,404：url->{}",url);
		    		FileUtils.copyInputStreamToFile(BaseOkHttp.class.getResourceAsStream("/images/404.png"), new File(pathName));
		    	}else{
		    		log.error("url访问失败返回识别码 " + response+"\n\r url->"+url);
		    	}
		    }
		}catch(SocketTimeoutException e){
			throw e;
		}catch (IOException e) {
			log.error("url访问失败 ",e);
		}
		return false;
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
	
	protected OkHttpClient getClient() {
		return client;
	}

	

	
	

}
