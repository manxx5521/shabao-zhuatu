package com.xiaoshabao.zhuatu.http;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


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
	
	@Override
	public boolean download(String url, String pathName) throws IOException {
		/*try {*/
		Request request = new Request.Builder().url(url).build();
		Response response = getClient().newBuilder()
				.readTimeout(DOWNLOAD_READ_TIME_OUT, TimeUnit.SECONDS) //读取超时
				.build().newCall(request).execute();
		if (response.isSuccessful()) {
			FileUtils.writeByteArrayToFile(new File(pathName), response.body()
					.bytes());
			return true;
		} else {
			if (response.code() == 404) {
				log.error("url访问失败,404：url->{}", url);
				FileUtils.copyInputStreamToFile(BaseOkHttp.class.getResourceAsStream("/images/404.png"),new File(pathName));
			} else {
				log.error("url访问失败返回识别码 " + response + "\n\r url->" + url);
			}
		}
		/*}catch(SocketTimeoutException e){
			throw e;
		}catch (IOException e) {
			log.error("url访问失败 ",e);
		}*/
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
