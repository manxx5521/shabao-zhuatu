package com.xiaoshabao.zhuatu.service.impl;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.tags.HeadingTag;
import org.htmlparser.tags.ImageTag;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xiaoshabao.zhuatu.TuInfo;
import com.xiaoshabao.zhuatu.ZhuatuConfig;
import com.xiaoshabao.zhuatu.core.ZhuatuFactory;
import com.xiaoshabao.zhuatu.service.ZhuatuDownloadService;
import com.xiaoshabao.zhuatu.service.ZhuatuService;
import com.xiaoshabao.zhuatu.service.ZhuatuWaitService;

public class ZhuTuGmtt8 {

	private final static Logger logger = LoggerFactory.getLogger(ZhuTuGmtt8.class);

	protected String urlRoot = "http://www.gmtt8.com";

	@Test
	public void test() {
		List<ZhuatuService> zhuatuServices = new ArrayList<ZhuatuService>();
		// 第一层解析分项的信息，找打具体的项目
		zhuatuServices.add(new ZhuatuWaitService() {
			@Override
			public List<TuInfo> parser(String html, TuInfo pageInfo, ZhuatuConfig config) throws ParserException {
				final List<TuInfo> result = new LinkedList<TuInfo>();
				Parser parser = Parser.createParser(html, config.getCharset());
				NodeList list = parser.parse(new HasAttributeFilter("rel", "bookmark"));

				for (Node node : list.toNodeArray()) {
					if (node instanceof LinkTag && node.getParent() instanceof HeadingTag) {
						HeadingTag h2 = (HeadingTag) node.getParent();
						if ("entry-title".equals(h2.getAttribute("class"))) {
							LinkTag link = (LinkTag) node;
							String href = link.getLink();
							String title = link.childAt(0).getText();
							TuInfo info = new TuInfo();
							info.setUrl(href);
							info.setTitle(title);
							result.add(info);
						}

					}

				}
				return result;
			}

			@Override
			public String nextPage(String html, ZhuatuConfig config) throws ParserException {
				String nextUrl = null;
				Parser parser = Parser.createParser(html, config.getCharset());
				NodeList nexts = parser.parse(new HasAttributeFilter("class", "page-numbers"));
				for (Node node : nexts.toNodeArray()) {
					if (node instanceof LinkTag) {
						LinkTag link = (LinkTag) node;
						nextUrl = link.getLink();

					}

				}
				return nextUrl;
			}

		});

		// 第二层解析具体照片
		zhuatuServices.add(new ZhuatuDownloadService() {
			@Override
			public List<TuInfo> parser(String html, TuInfo pageInfo, ZhuatuConfig config) throws ParserException {
				List<TuInfo> result = new LinkedList<TuInfo>();
				Parser parser = Parser.createParser(html, config.getCharset());

				NodeList imgs = parser.parse(new HasAttributeFilter("class", "size-full"));

				// 换解析思路
				if (imgs == null || imgs.size() < 1) {
					imgs = Parser.createParser(html, config.getCharset()).parse(new TagNameFilter("img"));
				}

				for (Node node : imgs.toNodeArray()) {
					if (node instanceof ImageTag) {
						ImageTag img = (ImageTag) node;
						String src = img.getAttribute("src");
						String alt = img.getAttribute("alt");
						if (StringUtils.isNotEmpty(img.getAttribute("data-echo"))) {
							continue;
						}
						if (alt == null || src == null) {
							continue;
						}

						// 去除部分无用链接
						if (src.startsWith("http://33img.com")) {
							continue;
						}
						logger.info("取到下载链接：" + src);
						if (src.endsWith("/")) {
							throw new RuntimeException("获得的图片下载链接错误");
						}
						TuInfo info = new TuInfo();
						info.setUrl(src);
						info.setTitle(alt);
						result.add(info);
					}

				}
				return result;
			}

			@Override
			public String nextPage(String html, ZhuatuConfig config) {
				return null;
			}

		});

		// 装载抓图任务
		ZhuatuFactory.createDownloadZhuatu().start(
				"http://www.gmtt8.com/archives/category/%E5%9B%BD%E6%A8%A1%E5%A5%97%E5%9B%BE/", zhuatuServices,
				"E:\\test\\shabao-m\\resources\\plugins\\mm\\gmtt8");
	}

}
