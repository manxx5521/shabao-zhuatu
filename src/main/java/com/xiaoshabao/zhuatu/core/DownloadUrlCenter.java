package com.xiaoshabao.zhuatu.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.StringUtils;

/**
 * 根据url下载
 */
public class DownloadUrlCenter extends ZhuatuCenter {

	private List<TuInfo> list = Collections.synchronizedList(new ArrayList<TuInfo>());

	AtomicInteger index = new AtomicInteger(0);

	public DownloadUrlCenter addDownloadUrl(String url) {
		addDownloadUrl(null,url);
		return this;
	}

	public DownloadUrlCenter addDownloadUrl(String title,String url) {
		if(list.stream().filter(t->t.getUrl().equals(url))
				.count()==0){
			list.add(new TuInfo(url,null));
		}
		return this;
	}
	
	

	@Override
	public void start() {
		this.config.setUrl(list.get(0).getUrl());
		
		this.createService().downloadUrl(true)
				.parserUrlFunction((url, pageInfo, config, result) -> {
					result.add(list.get(index.getAndAdd(1)));
				}).nextNoRequestFunction((html, config) -> {
					if (index.get() < list.size()) {
						String next = list.get(index.get()).getUrl();
						if (StringUtils.isNotEmpty(next)) {
							return next;
						}
					}
					return null;
				});
		super.start();
	}

}
