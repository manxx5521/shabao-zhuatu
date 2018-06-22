package com.xiaoshabao.zhuatu;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

import com.xiaoshabao.zhuatu.http.HttpType;

public class ZhuatuConfig {

	private String charset = "UTF-8";
	/**
	 * 设置保存路径
	 */
	private String savePath;
	
	/**扩展保存目录，只做项目查询，不保存数据*/
	private Set<String> extSavePath=new HashSet<String>();

	private RequestMethod method = RequestMethod.GET;
	/**
	 * 是否直接请求url，返回内容到html变量默认true（对于服务所有请求）
	 * @param reqHtml
	 */
	private boolean reqHtml=true;

	/** 下载链接解析函数 */
	private Function<String, String> downlaodUrlParser;
	
	/**
	 * 不下载的url前缀
	 */
	private Set<String> noUrl=new HashSet<String>();
	/**
	 * 优先下载的项目
	 */
	private Set<String> firstProject=new HashSet<String>();
	
	/**
	 * 下载方式
	 */
	private HttpType dwonloadType=HttpType.OKHTTP;
	/**
	 * 获得基本url 比如：http://tu.fengniao.com
	 */
	private String webRoot;
	
	/**不下载文件的名称*/
	private Set<String> noDownloadName=new HashSet<String>();
	
	/**是否保存项目链接地址*/
	private boolean saveLink=false;

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
	/**
	 * 是否直接请求url，返回内容到html变量默认true
	 * @param reqHtml
	 */
	public void setReqHtml(boolean reqHtml) {
		this.reqHtml = reqHtml;
	}



	public String getCharset() {
		return charset;
	}


	public String getSavePath() {
		return savePath;
	}
	

	public Set<String> getExtSavePath() {
		return extSavePath;
	}


	public RequestMethod getMethod() {
		return method;
	}


	public Function<String, String> getDownlaodUrlParser() {
		return downlaodUrlParser;
	}

	/**
	 * 是否直接请求url，返回内容到html变量默认true
	 */
	public boolean isReqHtml() {
		return reqHtml;
	}


	/**
	 * 获得不需要下载的url前缀比如 http://www.baidu.com/
	 * @return
	 */
	public Set<String> getNoUrl() {
		return noUrl;
	}
	/**
	 * 添加不需要下载的url前缀比如 http://www.baidu.com/
	 * @return
	 */
	public ZhuatuConfig addNoUrl(String url){
		noUrl.add(url);
		return this;
	}
	/**
	 * 添加不需要下载的url前缀比如 http://www.baidu.com/
	 * @return
	 */
	public ZhuatuConfig addNoUrl(String...url){
		for(String u:url){
			noUrl.add(u);
		}
		return this;
	}

	/**
	 * 优先下载的项目
	 * @return
	 */
	public Set<String> getFirstProject() {
		return firstProject;
	}
	/**
	 * 优先下载的项目
	 */
	public ZhuatuConfig addFirstProject(String title){
		firstProject.add(title);
		return this;
	}
	/**
	 * 优先下载的项目
	 */
	public ZhuatuConfig addFirstProject(String...titles){
		for(String title:titles){
			firstProject.add(title);
		}
		return this;
	}
	
	/**
	 * 添加不需要下载的文件名称
	 * @param names
	 * @return
	 */
	public ZhuatuConfig addNoDownloadName(String...names){
		for(String name:names){
			noDownloadName.add(name);
		}
		return this;
	}

	/**
	 * 下载方式
	 * @return
	 */
	public HttpType getDwonloadType() {
		return dwonloadType;
	}

	/**
	 * 下载方式
	 * @param dwonloadType
	 */
	public void setDwonloadType(HttpType dwonloadType) {
		this.dwonloadType = dwonloadType;
	}


	/**
	 * 获得基本url 比如：http://tu.fengniao.com
	 * @return
	 */
	public String getWebRoot() {
		return webRoot;
	}
	/**
	 * 获得基本url 比如：http://tu.fengniao.com/
	 * @return
	 */
	public String getWebRootAll() {
		return webRoot+"/";
	}


	public void setWebRoot(String webRoot) {
		this.webRoot = webRoot;
	}
	
	/**扩展保存目录，只做项目查询，不保存数据*/
	public ZhuatuConfig addExtSavePath(String path) {
		this.extSavePath.add(path);
		return this;
	}


	public Set<String> getNoDownloadName() {
		return noDownloadName;
	}


	public boolean isSaveLink() {
		return saveLink;
	}


	public void setSaveLink(boolean saveLink) {
		this.saveLink = saveLink;
	}
	
}
