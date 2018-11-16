package com.xiaoshabao.zhuatu.core.config;

import java.nio.charset.Charset;

import com.xiaoshabao.zhuatu.core.Service;
import com.xiaoshabao.zhuatu.core.ZhuatuCenter;
import com.xiaoshabao.zhuatu.http.HttpAble;

public class ZhuatuConfig {

	/** 初始url */
	private String url;

	private Charset charset = Charset.defaultCharset();

	private HttpAble.Method method = HttpAble.Method.GET;

	/**
	 * 获得基本url 比如：http://tu.fengniao.com
	 */
	private String webRoot;

	/** 是否保存项目链接地址 */
	private boolean saveLink = false;

	/**
	 * 通过几个线程抓取
	 */
	private int threadCount = 1;

	/**
	 * 代理设置
	 */
	private String proxyIp;

	/**
	 * 代理端口
	 */
	private int proxyPort;

	/*** 对执行的url自动排重 */
	private boolean heavyURL = true;

	private ZhuatuCenter center;

	public ZhuatuConfig(ZhuatuCenter center) {
		this.center = center;
	}

	public ZhuatuCenter getCenter() {
		return center;
	}

	/**
	 * 创建一层抓图
	 * 
	 * @return
	 */
	public Service createService() {
		return center.createService();
	}

	/**
	 * 设置编码格式
	 * 
	 * @param charset
	 *            UTF-8
	 */
	public ZhuatuConfig setCharset(String charset) {
		this.charset = Charset.forName(charset);
		return this;
	}

	/**
	 * 访问类型post或者get
	 * 
	 * @param method
	 */
	public ZhuatuConfig setMethod(HttpAble.Method method) {
		this.method = method;
		return this;
	}

	public Charset getCharset() {
		return charset;
	}

	public String getCharsetString() {
		return charset.name();
	}

	public HttpAble.Method getMethod() {
		return method;
	}

	/**
	 * 获得基本url 比如：http://tu.fengniao.com
	 * 
	 * @return
	 */
	public String getWebRoot() {
		return webRoot;
	}

	/**
	 * 获得基本url 比如：http://tu.fengniao.com/
	 * 
	 * @return
	 */
	public String getWebRootAll() {
		return webRoot + "/";
	}

	public void setWebRoot(String webRoot) {
		this.webRoot = webRoot;
	}

	/**
	 * 设置是进行对url排重（默认 true）
	 * 
	 * @param b
	 * @return
	 */
	public ZhuatuConfig heavyURL(boolean b) {
		this.heavyURL = b;
		return this;
	}

	public boolean isHeavyURL() {
		return heavyURL;
	}

	public boolean isSaveLink() {
		return saveLink;
	}

	public ZhuatuConfig setSaveLink(boolean saveLink) {
		this.saveLink = saveLink;
		return this;
	}

	public int getThreadCount() {
		return threadCount;
	}

	public ZhuatuConfig setThreadCount(int threadCount) {
		this.threadCount = threadCount;
		return this;
	}

	public String getUrl() {
		return url;
	}

	public ZhuatuConfig setUrl(String url) {
		this.url = url;
		return this;
	}

	public String getProxyIp() {
		return proxyIp;
	}

	public int getProxyPort() {
		return proxyPort;
	}

	public ZhuatuConfig setProxyConfig(String ip, Integer port) {
		this.proxyIp = ip;
		this.proxyPort = port;
		return this;
	}

}
