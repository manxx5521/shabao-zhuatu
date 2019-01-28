package com.xiaoshabao.zhuatu.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * 抓取封面码
 */
public class CoverMa {
	
	/**结束字符，只解析结束字符前的内容*/
	private List<String> endList;
	/**需要特别保存的字符位置，只解析结束字符前的内容*/
	private List<String> saveList;
	
	public CoverMa() {
	}
	
	/**
	 * 设置结束字符串
	 * @param endStr
	 * @return
	 */
	public CoverMa endStr(String ...endStr) {
		endList=Collections.unmodifiableList(new ArrayList<String>(Arrays.asList(endStr)));
		return this;
	}
	
	/**
	 * 设置需要保存的内容
	 * @param saveStr
	 * @return
	 */
	public CoverMa saveStr(String ...saveStr) {
		saveList=Collections.unmodifiableList(new ArrayList<String>(Arrays.asList(saveStr)));
		return this;
	}
	
	
	
	/**
	 * @param doc 抓取节点
	 * @param scope 抓取范围，获得哪个范围内的内容
	 * @return
	 */
	public List<String> execute(Document doc,String scope,String savePath,String title) {
		Optional<String> writePath=writerFile(savePath,title);
		List<String> imgs=new LinkedList<>();
		StringBuffer saveContent=new StringBuffer();
		Elements body = doc.select(scope);
		
		//递归解析
		parserElement(body,imgs,saveContent);
		
		
		if (writePath.isPresent()&&body.hasText()) {
			File file = new File(writePath.get());
			if(!file.getParentFile().exists()) {
				file.getParentFile().mkdirs();
			}
			try (PrintStream ps = new PrintStream(new FileOutputStream(file))) {
				if (saveContent.length()>0) {
					ps.println(saveContent);// 往文件里写入字符串
					ps.println("\n\n");
					ps.println("----------------以下是全部内容------------------------");
				}
				ps.println(body.text());
				ps.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
				
		}
		return imgs;
	}
	
	private void parserElement(Elements body,List<String> imgs,StringBuffer saveContent) {
		ListIterator<Element> iterator=body.listIterator();
		while(iterator.hasNext()) {
			Element element=iterator.next();
			if(("p".equals(element.tagName()))&&element.hasText()) {
				String text=element.text();
				
				if(saveList!=null) {
					for(String saveStr:saveList) {
						if(text.contains(saveStr)) {
							saveContent.append(text).append("\n");
						}
					}
				}
				
				if(endList!=null) {
					for(String endStr:endList) {
						if(text.contains(endStr)) {
							return;
						}
					}
				}
			}
			
			
			if("img".equals(element.tagName())) {
				imgs.add(element.attr("src"));
			}
			
			//向下递归
			parserElement(element.children(),imgs,saveContent);
		}
		
	}
	
	/**
	 * 不获取文本写入到文件
	 * @return
	 */
	private Optional<String> writerFile(String savePath,String title) {
		if(StringUtils.isEmpty(savePath)) {
			return Optional.empty();
		}
		StringBuffer sb=new StringBuffer();
		sb.append(savePath);
		if(StringUtils.isNotEmpty(title)) {
			sb.append(File.separator).append(title);
		}
		sb.append(File.separator).append("抓取封面文本.txt");
		return Optional.of(sb.toString());
	}

}
