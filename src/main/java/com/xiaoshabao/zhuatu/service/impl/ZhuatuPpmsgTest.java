package com.xiaoshabao.zhuatu.service.impl;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.Tag;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.tags.BulletList;
import org.htmlparser.tags.ImageTag;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;
import org.htmlparser.visitors.NodeVisitor;
import org.junit.Test;

import com.xiaoshabao.zhuatu.TuInfo;
import com.xiaoshabao.zhuatu.ZhuatuConfig;
import com.xiaoshabao.zhuatu.ZhuatuUtil;
import com.xiaoshabao.zhuatu.core.ZhuatuFactory;
import com.xiaoshabao.zhuatu.service.ZhuatuDownloadService;
import com.xiaoshabao.zhuatu.service.ZhuatuService;
import com.xiaoshabao.zhuatu.service.ZhuatuWaitService;

public class ZhuatuPpmsgTest {

	protected String urlRoot = "http://www.ppmsg.net/jiepaimeinv/";
	
	private String pohtoRoot=null;

	@Test
	public void test() {
		List<ZhuatuService> zhuatuServices = new ArrayList<ZhuatuService>();
		// 第一层解析分项的信息，找打具体的项目
		zhuatuServices.add(new ZhuatuWaitService()  {
			@Override
			public List<TuInfo> parser(String html, TuInfo pageInfo, ZhuatuConfig config) throws Exception {
				List<TuInfo> result = new ArrayList<TuInfo>();
				Parser parser = Parser.createParser(html, config.getCharset());
				NodeList list = parser.parse(new HasAttributeFilter("class", "lm"));
				Node body = list.elementAt(0);
				body.accept(new NodeVisitor() {
					@Override
					public void visitTag(Tag tag) {
						if (tag instanceof LinkTag) {
							LinkTag link = (LinkTag) tag;
							String href = link.getLink();
							String title = ZhuatuUtil.formatTitleName(link.getLinkText());
							
							Object parent=link.getParent();
							if(parent instanceof BulletList){
								BulletList bulletList=(BulletList) parent;
								String classStr=bulletList.getAttribute("class");
								if("page".equals(classStr)){
									return;
								}
							}
							
							TuInfo info = new TuInfo();
							info.setUrl(urlRoot + href);
							info.setTitle(title);
							result.add(info);
						}
					}
				});
				return result;
			}

			@Override
			public String nextPage(String html, ZhuatuConfig config) throws ParserException {
				String nextUrl = null;
				Parser parser = Parser.createParser(html, config.getCharset());
				NodeList nextUl = parser.parse(new HasAttributeFilter("class", "page"));
				NodeList nexts=nextUl.toNodeArray()[0].getChildren();
				for (Node node : nexts.toNodeArray()) {
					if (node instanceof LinkTag) {
						LinkTag link = (LinkTag) node;
						if("下一页".equals(link.getLinkText())){
							nextUrl = urlRoot+"/"+link.getLink();
							return nextUrl;
						}
					}

				}
				return nextUrl;
			}


		});

		// 第二层解析具体照片
		zhuatuServices.add(new ZhuatuDownloadService() {
			@Override
			public List<TuInfo> parser(String html, TuInfo pageInfo, ZhuatuConfig config) throws Exception {
				List<TuInfo> result = new LinkedList<TuInfo>();
				Parser parser = Parser.createParser(html, config.getCharset());
				NodeList imgagetList = parser.parse(new HasAttributeFilter("id", "imagelist"));
				NodeList imgs=imgagetList.toNodeArray()[0].getChildren();
				for (Node node : imgs.toNodeArray()) {
					if (node instanceof ImageTag) {
						ImageTag img = (ImageTag) node;
						String src = ZhuatuUtil.formatUrl(img.getAttribute("src"));
						
						String alt = ZhuatuUtil.formatTitleName(img.getAttribute("alt"));
						TuInfo info = new TuInfo();
						info.setUrl(src);
						info.setTitle(alt);
						result.add(info);
					}
				}
				pohtoRoot=pageInfo.getUrl().substring(0, pageInfo.getUrl().lastIndexOf("/"));
				return result;
			}

			@Override
			public String nextPage(String html, ZhuatuConfig config) throws Exception {
				TuInfo info = new TuInfo();
				Parser parser = Parser.createParser(html, config.getCharset());
				NodeList list = parser.parse(new HasAttributeFilter("class", "image"));
				Node body = list.elementAt(0);
				body.accept(new NodeVisitor() {
					@Override
					public void visitTag(Tag tag) {
						if (tag instanceof LinkTag) {
							LinkTag link = (LinkTag) tag;
							if("下一页".equals(link.getLinkText())){
								String nextUrl = link.getLink();
								info.setUrl(pohtoRoot+"/"+nextUrl);
							}
						}
					}
				});
				return info.getUrl();
			}

		});

		// 装载抓图任务
		ZhuatuConfig config=new ZhuatuConfig();
		config.setCharset("gb2312");
		config.setSavePath("E:\\test\\shabao-m\\resources\\plugins\\mm\\ppmsgjp");
//		config.setReqHtml(false);
		ZhuatuFactory.createDownloadZhuatu().start(
				urlRoot, zhuatuServices,config);
	}

}
