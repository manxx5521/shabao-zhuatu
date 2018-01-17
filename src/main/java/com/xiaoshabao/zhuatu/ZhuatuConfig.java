package com.xiaoshabao.zhuatu;

import java.util.function.Function;


public class ZhuatuConfig {

	private String charset = "UTF-8";

	private String savePath;

	private RequestMethod method = RequestMethod.GET;

	/** 下载链接解析函数 */
	private Function<String, String> downlaodUrlParser;

	public String getCharset() {
		return charset;
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}

	public String getSavePath() {
		return savePath;
	}

	/**
	 * 设置保存路径
	 * 
	 * @param savePath
	 *            E:\\test\\test
	 */
	public void setSavePath(String savePath) {
		this.savePath = savePath;
	}

	public RequestMethod getMethod() {
		return method;
	}

	public void setMethod(RequestMethod method) {
		this.method = method;
	}

	public Function<String, String> getDownlaodUrlParser() {
		return downlaodUrlParser;
	}

	/**
	 * 下载链接解析函数
	 * <p>
	 * 使用方式(下载url只取?之前部分)
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

}
