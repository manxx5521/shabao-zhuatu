package com.xiaoshabao.zhuatu.service.impl;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Test;

import com.xiaoshabao.zhuatu.core.CoverMa;
import com.xiaoshabao.zhuatu.core.TuInfo;
import com.xiaoshabao.zhuatu.core.ZhuatuCenter;

public class 截取封面Test {
	//人像摄影
	private String indexUrl = "http://www.xiaoshabao.com/blog/view/65";
	
	@Test
	public void test() {
		String savePath="E:\\test\\shabao-m\\resources\\plugins\\mm\\抓取封面";
		
		CoverMa ma=new CoverMa();
		ma.endStr("这就导致线程2被唤醒")
		.saveStr("注意一点");
		
		new ZhuatuCenter().createDownloadConfig()
		.setUrl(indexUrl)
		.setSavePath(savePath)
		.createDownloadService()
		.parserResultFunction((html,pageInfo,config,list)->{
			Document doc = Jsoup.parse(html);
			ma.execute(doc, "div.markdown-body", savePath, pageInfo.getTitle()).forEach(url->{
				list.add(new TuInfo(url,pageInfo.getTitle()));
			});;
		}).start();
	}

}
