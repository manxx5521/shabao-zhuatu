package com.xiaoshabao.zhuatu.service.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.tags.Span;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xiaoshabao.zhuatu.TuInfo;
import com.xiaoshabao.zhuatu.RequestMethod;
import com.xiaoshabao.zhuatu.ZhuatuConfig;
import com.xiaoshabao.zhuatu.core.ZhuatuFactory;
import com.xiaoshabao.zhuatu.service.ZhuatuDownloadService;
import com.xiaoshabao.zhuatu.service.ZhuatuService;
import com.xiaoshabao.zhuatu.service.ZhuatuWaitService;

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

		// 第一任务 界面解析任务
		List<ZhuatuService> zhuatuServices1 = new ArrayList<ZhuatuService>();
		// 第一层解析分项的信息，找打具体的项目
		zhuatuServices1.add(new ZhuatuService() {

			@Override
			public List<TuInfo> parser(String html, TuInfo pageInfo, ZhuatuConfig config) throws ParserException {
				Parser parser = Parser.createParser(html, config.getCharset());

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
				return null;
			}

			@Override
			public String nextPage(String html, ZhuatuConfig config) {
				return null;
			}
		});
		ZhuatuFactory.start(indexUrl, zhuatuServices1);

		// 第二个任务查找具体内容
		List<ZhuatuService> zhuatuServices = new ArrayList<ZhuatuService>();
		// 第一层解析分项的信息，找打具体的项目
		zhuatuServices.add(new ZhuatuWaitService() {

			@Override
			public List<TuInfo> parser(String html, TuInfo pageInfo, ZhuatuConfig config) {
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
			}

			@Override
			public String nextPage(String html, ZhuatuConfig config) {
				return getNextUrl();
			}
		});

		// 第二层解析具体照片
		zhuatuServices.add(new ZhuatuDownloadService() {

			@Override
			public List<TuInfo> parser(String html, TuInfo pageInfo, ZhuatuConfig config) throws IOException {
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
			}

			@Override
			public String nextPage(String html, ZhuatuConfig config) {
				return null;
			}
		});
		ZhuatuConfig config = new ZhuatuConfig();
		config.setMethod(RequestMethod.POST);
		config.setSavePath("E:\\test\\test\\fengniao");
		config.setDownlaodUrlParser(url->{
			return url.substring(0, url.indexOf("?"));
		});
		ZhuatuFactory.start(getNextUrl(), zhuatuServices, config);
	}

	private String getNextUrl() {
		return this.nextUrl + "?" + "class_id=" + this.postid + "&lastid=" + this.lastid;
	}

}
