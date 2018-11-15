package com.xiaoshabao.zhuatu.service.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.htmlparser.Parser;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.util.NodeList;
import org.junit.Test;

import com.xiaoshabao.zhuatu.TuInfo;
import com.xiaoshabao.zhuatu.ZhuatuUtil;
import com.xiaoshabao.zhuatu.core.ZhuatuCenter;

public class ZhuatuBX3Test2 {

	@Test
	public void test() {
		new ZhuatuCenter().createConfig()
		.setUrl("http://www.bxwx3.org/txt/177991/")
		.setCharset("gb2312")
		.setSavePath("E:\\test\\shabao-m\\resources\\plugins\\mm\\bx3")
		.createService()
		.parser((html, pageInfo, config)->{
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
		}).start();
		
	}


}
