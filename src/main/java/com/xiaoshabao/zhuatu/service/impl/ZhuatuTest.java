package com.xiaoshabao.zhuatu.service.impl;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;

import com.xiaoshabao.zhuatu.core.TuInfo;
import com.xiaoshabao.zhuatu.core.ZhuatuCenter;

/**
 * 模版
 */
public class ZhuatuTest {
	
	private String url = "http://bbs.fengniao.com/forum/forum_101.html";
	
	@Test
	public void test() {
		new ZhuatuCenter().createDownloadConfig().setUrl(url)
		.setSavePath("E:\\test\\shabao-m\\resources\\plugins\\mm\\fengniao")
		.createService().waitProject(true)//解析出项目
		.parserResultFunction((html,pageInfo,config,result)->{
			Document doc = Jsoup.parse(html);
			Elements as = doc.select("div.bbsListAll > ul.txtList > li > h3 > a");
			for (Element a : as) {
					String href = a.attr("href");
					String title = a.text();
					result.add(new TuInfo(href,title));
			}
		}).next((html,config)->{
			Document doc = Jsoup.parse(html);
			Elements as = doc.select("div.page > a.btn3");
			for (Element a : as) {
					String href = a.attr("href");
					String title = a.text();
					if("下一页".equals(title)) {
						return href;
					}
			}
			return null;
		}).createService().downloadUrl(true)//解析出要下载的链接
		.parserResultFunction((html,pageInfo,config,result)->{
			//自定义解析
		}).start();
	}

}
