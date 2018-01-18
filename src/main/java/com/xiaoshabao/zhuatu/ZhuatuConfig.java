package com.xiaoshabao.zhuatu;

import java.util.function.Function;

public class ZhuatuConfig {

	private String charset = "UTF-8";
	/**
	 * 设置保存路径
	 */
	private String savePath;

	private RequestMethod method = RequestMethod.GET;

	/** 下载链接解析函数 */
	private Function<String, String> downlaodUrlParser;
	

	/**
	 * 下载链接解析函数
	 * <p>
	 * 对获得的下载链接进行重新解析，比如：<br>
	 * 下载url只取?之前部分
	 * <pre>
	 * config.setDownlaodUrlParser(url -> {
	 * 	return url.substring(0, url.indexOf("?"));
	 * });
	 * </pre>
	 * </p>
	 */
	public void setDownlaodUrlParser(Function<String, String> downlaodUrlParser) {
		this.downlaodUrlParser = downlaodUrlParser;
	}


	/**
	 * 设置编码格式
	 * @param charset UTF-8
	 */
	public void setCharset(String charset) {
		this.charset = charset;
	}

	/**
	 * 设置保存路径
	 * @param savePath
	 *            E:\\test\\test
	 */
	public void setSavePath(String savePath) {
		this.savePath = savePath;
	}


	/**
	 * 访问类型post或者get
	 * @param method
	 */
	public void setMethod(RequestMethod method) {
		this.method = method;
	}


	public String getCharset() {
		return charset;
	}


	public String getSavePath() {
		return savePath;
	}


	public RequestMethod getMethod() {
		return method;
	}


	public Function<String, String> getDownlaodUrlParser() {
		return downlaodUrlParser;
	}
	
}
