package com.xiaoshabao.zhuatu.service.impl;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.tags.Span;
import org.htmlparser.util.NodeList;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xiaoshabao.zhuatu.core.TuInfo;
import com.xiaoshabao.zhuatu.core.ZhuatuCenter;
import com.xiaoshabao.zhuatu.http.HttpAble.Method;

public class ZhuatuFengniaoTest {
	private final static Logger log = LoggerFactory.getLogger(ZhuatuFengniaoTest.class);	
	//专辑精选
//	private String indexUrl = "http://tu.fengniao.com/album/";
//	private String nextUrl = "http://tu.fengniao.com/data/loadAlbum.php";
	
	//美女
	private String indexUrl = "http://tu.fengniao.com/13/";
	private String nextUrl = "http://tu.fengniao.com/data/loadHot.php";

	private String lastid;
	private String postid;

	@Test
	public void test() {
		
		new ZhuatuCenter().createDownloadConfig()
		.setUrl(indexUrl)
		.setMethod(Method.POST)
		.setSavePath("E:\\test\\test\\fengniao")
		.setDownlaodUrlParser(url->{
			return url.substring(0, url.indexOf("?"));
		}).createService()//先解析出真实要访问的url
		.parser((html,pageInfo,config)->{
			Parser parser = Parser.createParser(html, config.getCharsetString());

			NodeList list = parser.parse(new TagNameFilter("span"));
			for (Node node : list.toNodeArray()) {
				if (node instanceof Span) {
					Span span = (Span) node;
					String id = span.getAttribute("id");
					if (id != null && "lastid".equals(id)) {
						lastid = span.getStringText();
					}
					if (id != null && "postid".equals(id)) {
						postid = span.getStringText();
					}
				}
			}
			log.info("找到想要的url，忽略下边的报错，进行下一任务。");
			// 找到想要的直接跳出，完成本任务
			List<TuInfo> result=new ArrayList<TuInfo>();
			result.add(new TuInfo(getNextUrl(), "解析首次访问"));
			return result;
		}).createService().waitProject(true)//解析出具体项目
		.parser((html,pageInfo,config)->{
			JSONObject json = JSONObject.parseObject(html);
			JSONArray array = json.getJSONArray("data");
			List<TuInfo> result = new ArrayList<TuInfo>(array.size());
			for (int i = 0, len = array.size(); i < len; i++) {
				JSONObject info = array.getJSONObject(i);
				String title = info.getString("album_name");
				String url = info.getString("photo_url");
				result.add(new TuInfo(url, title));
				if (i == len - 1) {
					lastid = info.getString("dateline");
				}
			}
			return result;
		}).next((html,config)->{
			return getNextUrl();
		}).createService()//解析出照片
		.parser((html,pageInfo,config)->{
			StringReader stringReader = new StringReader(html);

			String upStrFlag = "var picList = ".trim();
			String strLine = null;
			String jsonStr = null;
			try (BufferedReader bufferedReader = new BufferedReader(stringReader)) {
				while ((strLine = bufferedReader.readLine()) != null) {
					if (strLine != null && strLine.trim().contains(upStrFlag)) {
						jsonStr = strLine.substring(strLine.indexOf("'") + 1, strLine.lastIndexOf("'"));
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
				log.info("获得下载链接 {}", url);
				result.add(new TuInfo(url, title));
			}
			return result;
		}).start();
	}

	private String getNextUrl() {
		return this.nextUrl + "?" + "class_id=" + this.postid + "&lastid=" + this.lastid;
	}

}
