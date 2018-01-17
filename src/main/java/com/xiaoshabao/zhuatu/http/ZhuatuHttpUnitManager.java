package com.xiaoshabao.zhuatu.http;

import java.io.IOException;
import java.net.MalformedURLException;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class ZhuatuHttpUnitManager {
	private static ZhuatuHttpUnitManager instance = new ZhuatuHttpUnitManager();
	private WebClient webClient = null;

	private ZhuatuHttpUnitManager() {
		webClient = new WebClient();
		// 启用JS解释器，默认为true
		webClient.getOptions().setJavaScriptEnabled(true);
		// 禁用css支持
		webClient.getOptions().setCssEnabled(false);
		// 设置Ajax异步处理控制器即启用Ajax支持
		webClient.setAjaxController(new NicelyResynchronizingAjaxController());
		// 当出现Http error时，程序不抛异常继续执行
		webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
		// js运行错误时，是否抛出异常。 防止js语法错误抛出异常
		webClient.getOptions().setThrowExceptionOnScriptError(false);
	}

	public static ZhuatuHttpUnitManager getInstance() {
		return instance;
	}

	/**
	 * 获得网页内容
	 * @param url
	 * @return
	 * @throws IOException
	 * @throws MalformedURLException
	 * @throws FailingHttpStatusCodeException
	 */
	public HtmlPage getPage(String url) throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		return webClient.getPage(url);
	}
}
