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
		
		config.addNoUrl("https://s19.postimg.org");
		config.addNoUrl("https://s20.postimg.org");//https://s20.postimg.org/s7cdqfyel/image.jpg
		config.addNoUrl("http://s7tu.com/","http://www.s7tu.com");//http://s7tu.com/images/2018/01/18/DSC021425cfc7.jpg
		config.addNoUrl("https://xxx.freeimage.us");//https://xxx.freeimage.us/image.php?id=FECF_5A5812CE&jpg
//		config.addNoUrl("http://120.52.72.23");//http://120.52.72.23/c.pic303.com/images/2017/12/05/DSC_2021672a9a56848ff21c.jpg
		config.addNoUrl("https://s18.postimg.org");//https://s18.postimg.org/gkemehp61/IMG_5813.jpg
		config.addNoUrl("https://s8.postimg.org");//https://s8.postimg.org/vz5r1e3d1/IMG_5708.jpg
		config.addNoUrl("https://s26.postimg.org");//https://s26.postimg.org/vy8gxl421/IMG_1486.jpg
		config.addNoUrl("https://s1.areyoucereal.com/");//https://s1.areyoucereal.com/xedoR.png
		config.addNoUrl("http://ipoock.com");//http://ipoock.com/img/g1/20160904124106xp354.jpeg
		config.addNoUrl("https://66.media.tumblr.com/");//https://66.media.tumblr.com/91f270ab0ae9f8f6d7a5f693b8a0beb6/tumblr_ocpvsdWPDa1u1izgro10_1280.jpg
		config.addNoUrl("https://65.media.tumblr.com/");
		config.addNoUrl("https://67.media.tumblr.com/");
		config.addNoUrl("http://www.99kuma.com");//http://www.99kuma.com/1024AAsadfs34qw123qre/001/06.jpg
		
		config.addNoUrl("https://s25.postimg.org");//访问慢
		config.addNoUrl("http://www.sxeimg.com");//访问慢
		
		//优先下载
		/*
		config.addFirstProject("兔妈妈","贱宝","海南小骚","玲玲的鸡巴","太乙归来","美腿娇妻");
		config.addFirstProject("球王酥酥原创","小炮哥","蜜丝原创","啪照工作室","吾家骚妻","嫩穴媳妇","球王酥酥","软萌小仙女","娇妻美美","甜甜","旧叙系列","花颜");
		config.addFirstProject("上官大人","樱玉花子","花花","济南活动","璐璐","辣妈辣嘛","老司机集结号","西安的太阳","海南小骚逼","美美");
		config.addFirstProject("模特第","骚婷婷","闲愁出品","一纳疯骚","月儿吖吖","Tumblr","快乐18出品","浪子原创","骚妻养成计划","美腿娇妻","感恩草榴","单纯小婷婷");
		config.addFirstProject("疯骚贱客","魅蓝师傅","母畜多多","抽象孙先生","约神猎物","森系女神","小母狗","菀晴","南阳凡哥","包子铺","真空少女","楚榴香","12316757","缘分","萌娃M","骚妻情趣睡衣诱惑","人造白虎少妇情人");
		config.addFirstProject("露出","SM","公园","野外");
		*/
		ZhuatuFactory.createDownloadZhuatu().start(
				url, zhuatuServices,config);
	}
}
