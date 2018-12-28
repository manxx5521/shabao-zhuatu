package com.xiaoshabao.zhuatu.service.impl;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xiaoshabao.zhuatu.core.TuInfo;
import com.xiaoshabao.zhuatu.core.ZhuatuCenter;
import com.xiaoshabao.zhuatu.http.HttpAble.Method;

public class ZhuatuFengniaoTest {
	//人像摄影
	private String indexUrl = "http://bbs.fengniao.com/forum/forum_101.html";
	
	@Test
	public void test() {
		new ZhuatuCenter().createDownloadConfig()
		.setUrl(indexUrl)
		.setMethod(Method.POST)
		.setSavePath("E:\\test\\shabao-m\\resources\\plugins\\mm\\fengniao")
//		.autoDownload().testDownloadProxy("127.0.0.1", 1080)
		.setDownlaodUrlParser(url->{
			return url.substring(0, url.indexOf("?"));
		}).createService().waitProject(true)//解析出具体项目
		.parserResultFunction((html,pageInfo,config,list)->{
			Document doc = Jsoup.parse(html);
			Elements as = doc.select("div.bbsListAll > ul.txtList > li > h3 > a");
			for (Element a : as) {
					String href = a.attr("href");
					String title = a.text();
					list.add(new TuInfo(href,title));
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
		}).createService()//解析出详情中的第一张图
		.parserResultFunction((html,pageInfo,config,list)->{
			Document doc = Jsoup.parse(html);
			Elements as = doc.select("div.postMain > div.postList div.aMain > div.cont > div.img > a");
			if(as!=null&&as.size()>0) {
				String href = as.get(0).attr("href");
				list.add(new TuInfo(href,pageInfo.getTitle()));
			}
		}).createService().downloadUrl(true)//打开相册获取大图链接
		.parser((html,pageInfo,config)->{
			StringReader stringReader = new StringReader(html);

			String upStrFlag = "var picList =".trim();
			String strLine = null;
			String jsonStr = null;
			try (BufferedReader bufferedReader = new BufferedReader(stringReader)) {
				while ((strLine = bufferedReader.readLine()) != null) {
					if (strLine != null && strLine.trim().contains(upStrFlag)) {
						jsonStr = strLine.substring(strLine.indexOf("=") + 1, strLine.lastIndexOf(";"));
						break;
					}
				}
			}

			JSONArray array = JSONArray.parseArray(jsonStr.toString());
			List<TuInfo> result = new ArrayList<TuInfo>(array.size());
			for (int i = 0, len = array.size(); i < len; i++) {
				JSONObject info = array.getJSONObject(i);
				String title = pageInfo.getTitle();
				String url = info.getString("bigPic");
				result.add(new TuInfo(url, title));
			}
			return result;
		}).start();
	}

}
