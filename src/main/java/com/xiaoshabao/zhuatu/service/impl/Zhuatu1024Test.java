package com.xiaoshabao.zhuatu.service.impl;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.tags.InputTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;

import com.xiaoshabao.zhuatu.TuInfo;
import com.xiaoshabao.zhuatu.ZhuatuConfig;
import com.xiaoshabao.zhuatu.ZhuatuUtil;
import com.xiaoshabao.zhuatu.core.ZhuatuFactory;
import com.xiaoshabao.zhuatu.service.ZhuatuDownloadService;
import com.xiaoshabao.zhuatu.service.ZhuatuService;
import com.xiaoshabao.zhuatu.service.ZhuatuWaitService;

public class Zhuatu1024Test {

	protected String url = "http://cl.mf8q.pw/thread0806.php?fid=16";
	
	protected String urlRoot = "http://cl.mf8q.pw/";
	
	@Test
	public void test() {
		List<ZhuatuService> zhuatuServices = new ArrayList<ZhuatuService>();
		// 第一层解析分项的信息，找打具体的项目
		zhuatuServices.add(new ZhuatuWaitService()  {
			@Override
			public List<TuInfo> parser(String html, TuInfo pageInfo, ZhuatuConfig config) throws Exception {
				List<TuInfo> result = new ArrayList<TuInfo>();
				Document doc = Jsoup.parse(html);
				Elements divs = doc.select("td.tal > h3 > a");
				for (Element div : divs) {
					String href = div.attr("href");
					Elements fonts = div.select("font");
					String title=null;
					if(fonts.size()>0){
						Element font=fonts.get(0);
						if("blue".equals(font.attr("color"))||"red".equals(font.attr("color"))){
							continue;
						}
						title=font.text();
					}else{
						title=div.text();
					}
					
					TuInfo info = new TuInfo();
					info.setUrl(ZhuatuUtil.formatUrl(urlRoot + href));
					info.setTitle(ZhuatuUtil.formatTitleName(title));
					result.add(info);
				}
				result.forEach(tu->{
					System.out.println(tu.getTitle());
				});
				return result;
			}

			@Override
			public String nextPage(String html, ZhuatuConfig config) throws ParserException {
				Document doc = Jsoup.parse(html);
				Elements divs = doc.select("div.pages > a");
				for (Element a : divs) {
					if("下一頁".equals(a.text())){
						return urlRoot+a.attr("href");
					}
				}
				return null;
			}
		});

		// 第二层解析具体照片
		zhuatuServices.add(new ZhuatuDownloadService() {
			@Override
			public List<TuInfo> parser(String html, TuInfo pageInfo, ZhuatuConfig config) throws Exception {
				List<TuInfo> result = new LinkedList<TuInfo>();
				Parser parser = Parser.createParser(html, config.getCharset());
				NodeList imgagetList = parser.parse(new HasAttributeFilter("type", "image"));
				for (Node node : imgagetList.toNodeArray()) {
					if (node instanceof InputTag) {
						InputTag input = (InputTag) node;
						String src = ZhuatuUtil.formatUrl(input.getAttribute("src"));
						TuInfo info = new TuInfo();
						info.setUrl(src);
						info.setTitle(pageInfo.getTitle());
						result.add(info);
					}
				}
				return result;
			}

			@Override
			public String nextPage(String html, ZhuatuConfig config) throws Exception {
				return null;
			}

		});

		// 装载抓图任务
		ZhuatuConfig config=new ZhuatuConfig();
		config.setCharset("gbk");
		config.setSavePath("E:\\test\\shabao-m\\resources\\plugins\\mm\\1024");
		
		config.addNoUrl("http://www.s7tu.com");
		config.addNoUrl("https://s25.postimg.org");//访问慢
		
		//优先下载
		config.addFirstProject("兔妈妈","贱宝","海南小骚","玲玲的鸡巴","太乙归来","美腿娇妻");
		config.addFirstProject("球王酥酥原创","小炮哥","蜜丝原创","啪照工作室","吾家骚妻","嫩穴媳妇","球王酥酥","软萌小仙女","娇妻美美","甜甜","旧叙系列","花颜");
		config.addFirstProject("上官大人","樱玉花子","花花","济南活动","璐璐","辣妈辣嘛","老司机集结号","西安的太阳","海南小骚逼","美美");
		config.addFirstProject("模特第","骚婷婷","闲愁出品","一纳疯骚");
		config.addFirstProject("露出","SM","公园","野外");
		
		ZhuatuFactory.createDownloadZhuatu().start(
				url, zhuatuServices,config);
	}
}
