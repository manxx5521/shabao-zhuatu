package com.xiaoshabao.zhuatu.service.impl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.Tag;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.tags.Bullet;
import org.htmlparser.tags.Div;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.tags.Span;
import org.htmlparser.util.NodeList;
import org.htmlparser.visitors.NodeVisitor;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xiaoshabao.zhuatu.TuInfo;
import com.xiaoshabao.zhuatu.ZhuatuConfig;
import com.xiaoshabao.zhuatu.core.ZhuatuFactory;
import com.xiaoshabao.zhuatu.service.ZhuatuService;
import com.xiaoshabao.zhuatu.service.ZhuatuWaitService;

public class ZhuatuYouku {

	private final static Logger logger = LoggerFactory
			.getLogger(ZhuatuYouku.class);
	
	private static DateFormat ddFormat = new SimpleDateFormat("yyyy-MM-dd");

	private String defaultCharset = "UTF-8";
	
	private List<Project> projectList=new LinkedList<ZhuatuYouku.Project>();
	protected ZhuatuYoukuReader reader;
	@Test
	public void test(){
		projectList.add(new Project("小君广场舞", "http://i.youku.com/u/UMzA4MTc5NTIzMg==?spm=a2h0z.8244218.2371631.2",
				"D:\\soft\\FLV Downloader\\整理\\小君广场舞\\(专辑)小君广场舞的自频道-优酷视频", ""));
		
		reader=new ZhuatuYoukuReader("E:\\test\\test\\youkulog");
		for(Project project :projectList){
			reader.load(project.getTitle());
			this.start(project);
		}
	}
	
	
	
	
	public void start(Project project) {
		
		List<ZhuatuService> zhuatuServices = new ArrayList<ZhuatuService>();
		// 第一层解析分项的信息，找打具体的项目
		zhuatuServices.add(new ZhuatuWaitService() {
			@Override
			public List<TuInfo> parser(String html, TuInfo pageInfo, ZhuatuConfig config) {
				final List<TuInfo> result = new LinkedList<TuInfo>();
				try {
					Parser parser = Parser.createParser(html, defaultCharset);
					NodeList list = parser.parse(new HasAttributeFilter("class",
							"v-meta"));

					for (Node node : list.toNodeArray()) {
						if (node instanceof Div) {
							Map<String,String> rs=new HashMap<String, String>();
							
							node.accept(new NodeVisitor() {
								@Override
								public void visitTag(Tag tag) {
									if (tag instanceof LinkTag) {
										LinkTag link = (LinkTag) tag;
										rs.put("title", link.getAttribute("title")) ;
									}
									if (tag instanceof Span) {
										Span span = (Span) tag;
										if("v-publishtime".equals(span.getAttribute("class"))){
											rs.put("date", span.getStringText()) ;
										}
									}
								}
							});
							reader.put(rs.get("title"), parserDate(rs.get("date")));
						}

					}
				} catch (Exception e) {
					logger.error("解析出错{}", pageInfo.getUrl(), e);
				}
				return result;
			}

			@Override
			public String  nextPage(String html, ZhuatuConfig config) {
				try {
					Parser parser = Parser.createParser(html, defaultCharset);
					NodeList nexts = parser.parse(new HasAttributeFilter(
							"class", "next"));
					for (Node node : nexts.toNodeArray()) {
						if (node instanceof Bullet) {
							Bullet li = (Bullet) node;
							Node [] links=li.getChildrenAsNodeArray();
							if(links.length>0&&links[0] instanceof LinkTag){
								LinkTag link = (LinkTag)links[0];
								String href = link.getLink();
								return href;
							}
						}

					}
				} catch (Exception e) {
					logger.error("下一页 解析出错{}", e);
				}
				return null;
			}

		});
		
		// 装载抓图任务
		ZhuatuFactory.createDownloadZhuatu().start(project.getUrl(), zhuatuServices,
						null, defaultCharset);
//		BaseMZhuatu zhuatu = new MZhuatuToHeavy();
//		zhuatu.start(project.getUrl(),null,
//				defaultCharset, zhuatuServices);
	}
	
	public String parserDate(String str){
		String rs=null;
		Calendar c = Calendar.getInstance(); 
		c.setTime(new Date()); 
		int day=c.get(Calendar.DATE); 
		int year=c.get(Calendar.YEAR);
		try {
			if(str.startsWith("昨天")){
				c.set(Calendar.DATE,day-1); 
			}else if(str.startsWith("前天")){
				c.set(Calendar.DATE,day-2);
			}else if(str.startsWith("3天前")){
				c.set(Calendar.DATE,day-3);
			}else if(str.startsWith("4天前")){
				c.set(Calendar.DATE,day-4);
			}else if(str.startsWith("5天前")){
				c.set(Calendar.DATE,day-5);
			}else if(str.startsWith("6天前")){
				c.set(Calendar.DATE,day-6);
			}else if(str.startsWith("7天前")){
				c.set(Calendar.DATE,day-7);
			}else if(str.startsWith("8天前")){
				c.set(Calendar.DATE,day-8);
			}else if(str.startsWith("9天前")){
				c.set(Calendar.DATE,day-9);
			}else{
				if(str.matches("\\d{4}-\\d{2}-\\d{2}")){
					c.setTime(new SimpleDateFormat("yyyy-MM-dd").parse(str));
				}else{
					c.setTime(new SimpleDateFormat("MM-dd kk:mm:ss").parse(str));
					c.set(Calendar.YEAR, year);
				}
			}
			rs=ddFormat.format(c.getTime());
		} catch (Exception e) {
			logger.error("日期解析失败{}",str,e);
		}
		return rs;
	}

	
	class Project {
		private String title;
		private String url;
		private String downloadPath;
		private String logpath;
		public Project(String title, String url, String downloadPath,
				String logpath) {
			super();
			this.title = title;
			this.url = url;
			this.downloadPath = downloadPath;
			this.logpath = logpath;
		}
		public String getTitle() {
			return title;
		}
		public void setTitle(String title) {
			this.title = title;
		}
		public String getUrl() {
			return url;
		}
		public void setUrl(String url) {
			this.url = url;
		}
		public String getDownloadPath() {
			return downloadPath;
		}
		public void setDownloadPath(String downloadPath) {
			this.downloadPath = downloadPath;
		}
		public String getLogpath() {
			return logpath;
		}
		public void setLogpath(String logpath) {
			this.logpath = logpath;
		}
	}
	
	

}
