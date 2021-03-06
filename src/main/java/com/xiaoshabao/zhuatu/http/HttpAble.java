package com.xiaoshabao.zhuatu.http;

import java.io.IOException;
import java.nio.charset.Charset;

/**
 * 统一规划访问数据接口
 * 
 * <p>
 * 可以通过实现接口的类获得对应接口 。<br>
 * HttpAble http=OkHttpManager.getInstance();
 * </p>
 *
 */
public interface HttpAble {
	/**读取超时时间*/
	int DOWNLOAD_READ_TIME_OUT=1000 * 60 * 15;
	
	public enum Method {
		GET, HEAD, POST, PUT, PATCH, DELETE, OPTIONS, TRACE
	}
	String HTTP = "http";
	String HTTPS = "https";
	
	/**
	 * 访问url
	 * @param url
	 * @param count 尝试访问次数
	 * @return 正常访问时返回数据字符串，失败时返回 null
	 */
	default String doUrl(String url, Method method, Charset charset, int count) {
		return new RetryFactory<String, String>(url, "访问URL->"+url).setCount(count).execute(tempUrl -> {
			switch (method) {
			case POST:
				return doPost(tempUrl, charset);
			default:
				return doGet(tempUrl, charset);
			}
		});
	}
	
	/**
	 * 访问url
	 * @param url
	 * @return 正常访问时返回数据字符串，失败时返回 null
	 */
	default String doGetA(String url) {
		try {
			return doGet(url,Charset.defaultCharset());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * 访问url
	 * @param url
	 * @return 正常访问时返回数据字符串，失败时返回 null
	 */
	default String doGet(String url) throws IOException{
		return doGet(url,Charset.defaultCharset());
	}
	/**
	 * 访问url
	 * @param url
	 * @param charset 编码
	 * @return 正常访问时返回数据字符串，失败时返回 null
	 */
	default String doGetA(String url,Charset charset) {
		try {
			return doGet(url,charset);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
	};
	
	/**
	 * 访问url
	 * @param url
	 * @param charset 编码
	 * @exception IOException 异常
	 * @return 正常访问时返回数据字符串，失败时返回 null
	 */
	String doGet(String url,Charset charset) throws IOException;
	
	
	/**
	 * 访问url
	 * @param url
	 * @return 正常访问时返回数据字符串，失败时返回 null
	 */
	default String doPostA(String url) {
		try {
			return doPost(url,Charset.defaultCharset());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * 访问url
	 * @param url
	 * @return 正常访问时返回数据字符串，失败时返回 null
	 */
	default String doPost(String url) throws IOException{
		return doPost(url,Charset.defaultCharset());
	}
	/**
	 * 访问url
	 * @param url
	 * @param charset 编码
	 * @return 正常访问时返回数据字符串，失败时返回 null
	 */
	default String doPostA(String url,Charset charset) {
		try {
			return doGet(url,charset);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
	};
	
	/**
	 * 访问url
	 * @param url
	 * @param charset 编码
	 * @exception IOException 异常
	 * @return 正常访问时返回数据字符串，失败时返回 null
	 */
	String doPost(String url,Charset charset) throws IOException;
	
	/**
	 * 下载文件文件到指定目录（尝试N次）
	 * @param url
	 * @param pathName 保存文件名
	 * @param count 尝试次数
	 * @return 下载成功true
	 */
	default boolean download(String url, String pathName, int count) {
		return new DownloadRetry<String, Boolean>(url, "下载文件" + url).setCount(count).execute(t -> download(t, pathName));
	}
	
	/**
	 * 下载文件文件到指定目录（尝试N次）
	 * @param url
	 * @param pathName 保存文件名
	 * @param count 尝试次数
	 * @param tryProxy 尝试代理下载
	 * @return 下载成功true
	 */
	default boolean download(String url, String pathName, int count,boolean tryProxy) {
		Boolean success= new DownloadRetry<String, Boolean>(url, "下载文件" + url)
				.setCount(count)
				.setTryProxy(tryProxy).execute(t -> download(t, pathName));
		return success==null?false:success;
	}
	
	/**
	 * 下载文件文件到指定目录
	 * @param url
	 * @param pathName 保存文件名
	 * @return 下载成功true
	 */
	default boolean downloadA(String url, String pathName){
		try {
			return download(url,pathName);
		} catch (Exception e) {
			return false;
		}
	}
	
	/**
	 * 下载文件文件到指定目录
	 * @param url
	 * @param pathName 保存文件名
	 * @return 下载成功true
	 */
	boolean download(String url, String pathName) throws IOException;
	
	
	
}
