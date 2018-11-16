package com.xiaoshabao.zhuatu.core.config;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

import com.xiaoshabao.zhuatu.core.ZhuatuCenter;
import com.xiaoshabao.zhuatu.http.HttpAble.Method;
import com.xiaoshabao.zhuatu.http.HttpType;
import com.xiaoshabao.zhuatu.http.OkHttpManager;

public class DownloadConfig extends ZhuatuConfig {

	/**
	 * 设置保存路径
	 */
	private String savePath;

	/** 扩展保存目录，只做项目查询，不保存数据 */
	private Set<String> extSavePath = new HashSet<String>();
	/** 下载链接解析函数 */
	private Function<String, String> downlaodUrlParser;

	/**
	 * 不下载的url前缀
	 */
	private Set<String> noUrl = new HashSet<String>();
	/**
	 * 优先下载的项目
	 */
	private Set<String> firstProject = new HashSet<String>();

	/**
	 * 下载方式
	 */
	private HttpType dwonloadType = HttpType.OKHTTP;

	/** 不下载文件的名称 */
	private Set<String> noDownloadName = new HashSet<String>();

	/**
	 * 需要重新检查的项目
	 */
	private Set<String> checkProjects = new HashSet<String>();

	/**
	 * 加载本地文件目录
	 */
	private boolean loadLocalFile = true;

	public DownloadConfig(ZhuatuCenter center) {
		super(center);
	}

	/**
	 * 下载链接解析函数
	 * <p>
	 * 对获得的下载链接进行重新解析，比如：<br>
	 * 下载url只取?之前部分
	 * 
	 * <pre>
	 * config.setDownlaodUrlParser(url -> {
	 * 	return url.substring(0, url.indexOf("?"));
	 * });
	 * </pre>
	 * </p>
	 */
	public DownloadConfig setDownlaodUrlParser(Function<String, String> downlaodUrlParser) {
		this.downlaodUrlParser = downlaodUrlParser;
		return this;
	}

	/**
	 * 设置保存路径
	 * 
	 * @param savePath
	 *            E:\\test\\test
	 */
	public DownloadConfig setSavePath(String savePath) {
		this.savePath = savePath;
		return this;
	}

	public String getSavePath() {
		return savePath;
	}

	public Set<String> getExtSavePath() {
		return extSavePath;
	}

	public Function<String, String> getDownlaodUrlParser() {
		return downlaodUrlParser;
	}

	/**
	 * 获得不需要下载的url前缀比如 http://www.baidu.com/
	 * 
	 * @return
	 */
	public Set<String> getNoUrl() {
		return noUrl;
	}

	/**
	 * 添加不需要下载的url前缀比如 http://www.baidu.com/
	 * 
	 * @return
	 */
	public DownloadConfig addNoUrl(String urlPrefix) {
		noUrl.add(urlPrefix);
		return this;
	}

	/**
	 * 测试添加，url是否可以下载
	 * 
	 * @param urlPrefix
	 *            url前缀比如 http://www.baidu.com/
	 * @param url
	 *            全路径url用来测试，是否可以返回内容
	 * @return
	 */
	public DownloadConfig testNoUrl(String urlPrefix, String url) {
		if (!OkHttpManager.getInstance().testUrl(url)) {
			noUrl.add(urlPrefix);
		}
		return this;
	}

	/**
	 * 添加不需要下载的url前缀比如 http://www.baidu.com/
	 * 
	 * @return
	 */
	public DownloadConfig addNoUrl(String... url) {
		for (String u : url) {
			noUrl.add(u);
		}
		return this;
	}

	/**
	 * 优先下载的项目
	 * 
	 * @return
	 */
	public Set<String> getFirstProject() {
		return firstProject;
	}

	public boolean isLoadLocalFile() {
		return loadLocalFile;
	}

	/**
	 * 是否加载本地目录（默认true）
	 * 
	 * @param loadLocalFile
	 * @return
	 */
	public DownloadConfig loadLocalFile(boolean loadLocalFile) {
		this.loadLocalFile = loadLocalFile;
		return this;
	}

	/**
	 * 优先下载的项目
	 */
	public DownloadConfig addFirstProject(String title) {
		firstProject.add(title);
		return this;
	}

	/**
	 * 优先下载的项目
	 */
	public DownloadConfig addFirstProject(String... titles) {
		for (String title : titles) {
			firstProject.add(title);
		}
		return this;
	}

	/**
	 * 添加不需要下载的文件名称
	 * 
	 * @param names
	 * @return
	 */
	public DownloadConfig addNoDownloadName(String... names) {
		for (String name : names) {
			noDownloadName.add(name);
		}
		return this;
	}

	/**
	 * 下载方式
	 * 
	 * @return
	 */
	public HttpType getDwonloadType() {
		return dwonloadType;
	}

	/**
	 * 下载方式
	 * 
	 * @param dwonloadType
	 */
	public void setDwonloadType(HttpType dwonloadType) {
		this.dwonloadType = dwonloadType;
	}

	/** 扩展保存目录，只做项目查询，不保存数据 */
	public DownloadConfig addExtSavePath(String path) {
		this.extSavePath.add(path);
		return this;
	}

	/**
	 * 添加需要重新检查下载的项目
	 * 
	 * @param name
	 * @return
	 */
	public DownloadConfig addCheckPoject(String... names) {
		for (String name : names) {
			checkProjects.add(name);
		}
		return this;
	}

	public Set<String> getCheckProjects() {
		return checkProjects;
	}

	public Set<String> getNoDownloadName() {
		return noDownloadName;
	}

	/******* 以下内容重构父级，方便设置属性 begin *********/

	@Override
	public DownloadConfig setCharset(String charset) {
		super.setCharset(charset);
		return this;
	}

	@Override
	public DownloadConfig setMethod(Method method) {
		super.setMethod(method);
		return this;
	}

	@Override
	public DownloadConfig heavyURL(boolean b) {
		super.heavyURL(b);
		return this;
	}

	@Override
	public DownloadConfig setSaveLink(boolean saveLink) {
		super.setSaveLink(saveLink);
		return this;
	}

	@Override
	public DownloadConfig setThreadCount(int threadCount) {
		super.setThreadCount(threadCount);
		return this;
	}

	@Override
	public DownloadConfig setUrl(String url) {
		super.setUrl(url);
		return this;
	}

	@Override
	public DownloadConfig setProxyConfig(String ip, Integer port) {
		super.setProxyConfig(ip, port);
		return this;
	}

	/******* 以上内容重构父级，方便设置属性 end *********/
}
