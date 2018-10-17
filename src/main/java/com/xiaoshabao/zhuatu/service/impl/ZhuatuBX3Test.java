package com.xiaoshabao.zhuatu.service.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.Tag;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;
import org.htmlparser.visitors.NodeVisitor;
import org.junit.Test;

import com.xiaoshabao.zhuatu.TuInfo;
import com.xiaoshabao.zhuatu.ZhuatuConfig;
import com.xiaoshabao.zhuatu.ZhuatuUtil;
import com.xiaoshabao.zhuatu.core.ZhuatuFactory;
import com.xiaoshabao.zhuatu.service.ZhuatuService;

public class ZhuatuBX3Test {

	private String indexUrl = "http://www.bxwx3.org/txt/177991/";

	@Test
	public void test() {

		List<ZhuatuService> zhuatuServices = new ArrayList<ZhuatuService>();
		// 第一层解析分项的信息，找打具体的章节
		zhuatuServices.add(new ZhuatuService() {

			@Override
			public List<TuInfo> parser(String html, TuInfo pageInfo, ZhuatuConfig config) throws ParserException {
				List<TuInfo> result = new LinkedList<TuInfo>();
				Parser parser = Parser.createParser(html, config.getCharsetString());
				NodeList list = parser.parse(new HasAttributeFilter("id", "list"));
				Node body = list.elementAt(0);
				body.accept(new NodeVisitor() {
					@Override
					public void visitTag(Tag tag) {
						if (tag instanceof LinkTag) {
							LinkTag link = (LinkTag) tag;
							String href = ZhuatuUtil.formatUrl(link.getLink(), config.getWebRoot());
							String title = ZhuatuUtil.formatTitleName(link.getLinkText());

							TuInfo info = new TuInfo();
							info.setUrl(href);
							info.setTitle(title);
							result.add(info);
						}
					}
				});
				return result;
			}

			@Override
			public String nextPage(String html, ZhuatuConfig config) {
				return null;
			}
		});
		// 第二层解析文章内容
		zhuatuServices.add(new ZhuatuService() {

			@Override
			public List<TuInfo> parser(String html, TuInfo pageInfo, ZhuatuConfig config) throws ParserException {
				List<TuInfo> result = new LinkedList<TuInfo>();
				Parser parser = Parser.createParser(html, config.getCharsetString());
				NodeList list = parser.parse(new HasAttributeFilter("id", "zjneirong"));
				String content = list.elementAt(0).getChildren().toHtml();
				try {
					File file=new File(config.getSavePath()+File.separator+pageInfo.getTitle()+".txt");
					if(!file.exists()) {
						if(!file.getParentFile().exists()) {
							file.getParentFile().mkdirs();
						}
						file.createNewFile();
					}
					IOUtils.write(ZhuatuUtil.formatContentToTxt(content), new FileOutputStream(file),config.getCharset());
					return result;
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				return null;
			}

			@Override
			public String nextPage(String html, ZhuatuConfig config) {
				return null;
			}
		});
		ZhuatuFactory.start(indexUrl, zhuatuServices, "E:\\test\\shabao-m\\resources\\plugins\\mm\\bx3", "gb2312");

	}


}
