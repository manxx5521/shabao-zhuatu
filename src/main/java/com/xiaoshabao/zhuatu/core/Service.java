package com.xiaoshabao.zhuatu.core;

import com.xiaoshabao.zhuatu.core.function.NextFunction;
import com.xiaoshabao.zhuatu.core.function.NextNoRequestFunction;
import com.xiaoshabao.zhuatu.core.function.ParserFunction;
import com.xiaoshabao.zhuatu.core.function.ParserResultFunction;
import com.xiaoshabao.zhuatu.core.function.ParserUrlFunction;

/**
 * 单次抓图服务
 */
public class Service {
	
	private ZhuatuCenter center;
	private ParserFunction parserFunction;
	private ParserResultFunction parserResultFunction;
	private ParserUrlFunction parserUrlFunction;
	private NextNoRequestFunction nextNoRequestFunction;
	private NextFunction nextFunction;
	
	/**是否是等待项目*/
	private boolean waitProject=false;
	
	/**当前解析完成后，返回的url是否进行下载*/
	private boolean downloadUrl=false;
	
	/**保存路径*/
	private String savePath;
	
	public Service(ZhuatuCenter center) {
		this.center=center;
	}
	
	public ZhuatuCenter getCenter(){
		return center;
	}
	
	/**
	 * 启动抓图
	 */
	public void start() {
		center.start();
	}
	/**
	 *创建一层新的抓图服务
	 * @return
	 */
	public Service createService() {
		return center.createService();
	}
	
	
	/**
	 * 解析当前页面函数
	 * @param parser
	 * @return
	 */
	public Service parser(ParserFunction parser) {
		this.parserFunction=parser;
		return this;
	}
	
	
	/**
	 * 解析下一页url函数
	 * @param next
	 * @return
	 */
	public Service next(NextFunction next) {
		this.nextFunction=next;
		return this;
	}
	
	/**
	 * 当前解析完成后，返回的url进行下载
	 * @param downloadUrl
	 * @return
	 */
	public Service downloadUrl() {
		this.downloadUrl = true;
		return this;
	}
	
	
	/**
	 * 当前解析完成后，返回的url是否进行下载(默认不下载)
	 * @param downloadUrl
	 * @return
	 */
	public Service downloadUrl(boolean downloadUrl) {
		this.downloadUrl = downloadUrl;
		return this;
	}
	
	/**
	 * 不做任何处理，直接传递url到函数
	 * @param parserUrlFunction
	 * @return
	 */
	public Service parserUrlFunction(ParserUrlFunction parserUrlFunction) {
		this.parserUrlFunction = parserUrlFunction;
		return this;
	}
	
	/**
	 * 解析界面，带结果
	 * @return
	 */
	public Service parserResultFunction(ParserResultFunction parserResultFunction) {
		this.parserResultFunction = parserResultFunction;
		return this;
	}

	/**
	 * 解析下一页，不会直接获取html内容
	 * @param nextNoRequestFunction
	 */
	public Service nextNoRequestFunction(NextNoRequestFunction nextNoRequestFunction) {
		this.nextNoRequestFunction = nextNoRequestFunction;
		return this;
	}
	
	/**
	 * 设置为需要等待的项目
	 * @param waitProject
	 */
	public Service waitProject() {
		this.waitProject = true;
		return this;
	}

	/**
	 * 是否需要等待(默认false)
	 * @param waitProject
	 */
	public Service waitProject(boolean waitProject) {
		this.waitProject = waitProject;
		return this;
	}
	
	/**
	 * 保存目录，根据service路径（只对当前servcie有效）
	 * @param savePath
	 */
	public void savePath(String savePath) {
		this.savePath = savePath;
	}
	
	public String getSavePath() {
		return savePath;
	}

	public ParserResultFunction getParserResultFunction() {
		return parserResultFunction;
	}
	

	public boolean isWaitProject() {
		return waitProject;
	}

	

	public ParserUrlFunction getParserUrlFunction() {
		return parserUrlFunction;
	}

	

	public boolean isDownloadUrl() {
		return downloadUrl;
	}

	

	public ParserFunction getParserFunction() {
		return parserFunction;
	}
	
	public NextNoRequestFunction getNextNoRequestFunction() {
		return nextNoRequestFunction;
	}


	public NextFunction getNextFunction() {
		return nextFunction;
	}

}
