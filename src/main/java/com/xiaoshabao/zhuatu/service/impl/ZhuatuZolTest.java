package com.xiaoshabao.zhuatu.service.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;

import com.gargoylesoftware.htmlunit.html.HtmlImage;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.xiaoshabao.zhuatu.core.TuInfo;
import com.xiaoshabao.zhuatu.core.ZhuatuCenter;
import com.xiaoshabao.zhuatu.http.RetryFactory;
import com.xiaoshabao.zhuatu.http.ZhuatuHttpUnitManager;

/**
 * 中关村在线抓取（使用jsoup，htmlunit）
 */
public class ZhuatuZolTest {

	// private String indexUrl = "http://bbs.zol.com.cn/dcbbs/d14_pic.html#c";
	private String indexUrl = "http://bbs.zol.com.cn/dcbbs/d16_pic.html#c";

	@Test
	public void test() {
		new ZhuatuCenter().createDownloadConfig().setUrl(indexUrl)
		.setCharset("gb2312").setSavePath("E:\\test\\shabao-m\\resources\\plugins\\mm\\zol")
		.createService().waitProject(true)
		.parser((html,pageInfo,config)->{
			List<TuInfo> result = new LinkedList<TuInfo>();
			Document doc = Jsoup.parse(html);
			Elements divs = doc.select("div.pic-infor");
			for (Element div : divs) {
				Elements links = div.select("a.listbook");
				if (links.size() > 0) {
					Element link = links.get(0);
					String href = link.attr("href");
					String title = link.text();
					TuInfo info = new TuInfo();
					info.setUrl(config.getWebRoot() + href);
					info.setTitle(title);
					result.add(info);
				}
			}
			return result;
		}).next((html,config)->{
			Document doc = Jsoup.parse(html);
			Elements links = doc.select("div.page > a.next");
			if (links.size() > 0) {
				Element link = links.get(0);
				String href = link.attr("href");
				return config.getWebRoot() + href;
			}
			return null;
		}).createService()
		.parser((html,pageInfo,config)->{
			List<TuInfo> result = new ArrayList<TuInfo>();

			Document doc = Jsoup.parse(html);
			Elements links = doc.select("ul#change > li");

			Set<String> sets = new HashSet<String>();
			HtmlPage page = ZhuatuHttpUnitManager.getInstance().getPage(pageInfo.getUrl());
			for (int i = 0; i < links.size(); i++) {
				// 查看链接是否生成
				RetryFactory<HtmlPage, TuInfo> retry = new RetryFactory<HtmlPage, TuInfo>(page, "抓取ajax返回图片");
				retry.setSleepTime(500);
				retry.setCount(10);
				TuInfo info = retry.execute(page1 -> {
					HtmlImage img = (HtmlImage) page.getElementById("bigPicHome");
					String href = img.getSrcAttribute();
					int size=sets.size();
					sets.add(href);
					if (size==sets.size()) {
						// 跳出重试
						return null;
					}
					String title = pageInfo.getTitle();
					TuInfo info1 = new TuInfo();
					info1.setUrl(href);
					info1.setTitle(title);
					return info1;
				});
				if(info!=null) {
					result.add(info);
				}
				// 不是最后一次点击下一页
				if (i < links.size() - 1) {
					page.getElementById("nextBtn").click();
				}
			}
//			return links.size()==result.size()?result:null;
			return result;
		}).start();

		
	}

}
