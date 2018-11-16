package com.xiaoshabao.zhuatu.core;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

public class ZhuatuUtil {
	/**
	 * 去除可能存在的特殊字符
	 */
	public static String formatTitleName(String title) {
		if(title==null||title.length()==0){
			return title;
		}
		title = title.replace("/", "");
		title = title.replace("\\", "");
		title = title.replace("|", "");
		title = title.replace("?", "");
		title = title.replace(":", "");
		title = title.replace("*", "");
		title = title.replace("<", "");
		title = title.replace(">", "");
		title = title.replace("amp;", "");
		title = title.replace("\"", "");
		
		int size=title.length();
		char re=' ';
		int begin=0;
		int end=size;
		//去除开头的空格
		for(int i=0;i<size;i++){
			if(re!=title.charAt(i)){
				begin=i;
				break;
			}
		}
		//去除尾部空格
		for(int i=size-1;i>=0;i--){
			if(re!=title.charAt(i)){
				end=i;
				break;
			}
		}
		try {
			title=title.substring(begin, end+1);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//去除尾部.
		if(title.endsWith(".")){
			title=title.substring(0, title.length()-1);
		}
		
		return title;
	}
	/**
	 * 去除可能存在的特殊字符
	 */
	public static String formatUrl(String url) {
		if(StringUtils.isEmpty(url)) {
			return url;
		}
		url=url.replace("\r","");
		url=url.replace("\n","");
		return url;
	}
	
	/**
	 * 格式话url
	 * @param url 
	 * @param webRoot 网站root路径：http://tu.fengniao.com
	 * @return 返回完整路径 http://tu.fengniao.com/1111
	 */
	public static String formatUrl(String url,String webRoot) {
		if(StringUtils.isEmpty(url)) {
			return url;
		}
		url=formatUrl(url);
		if(!url.startsWith("http")){
			if(url.startsWith("/")){
				url=url.substring(1,url.length());
			}
			url=webRoot+url;
		}
		
		return url;
	}
	
	public static void formatInfo(TuInfo info,String webRoot) {
		//更正url正确性
		info.setUrl(ZhuatuUtil.formatUrl(info.getUrl(),webRoot));
		info.setTitle(ZhuatuUtil.formatTitleName(info.getTitle()));
	}

	/**
	 * 将html写入到本地文件（测试使用）
	 * 
	 * @param html
	 */
	public static void writerHtml(String html) {
		File file = new File("E:\\test\\test\\11.html");
		try (PrintWriter pw = new PrintWriter(new FileWriter(file))) {
			pw.print(html);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 将html内容解析成纯文本内容
	 * @return
	 */
	public static String formatContentToTxt(String htmlStr) {
        String textStr = "";  
        java.util.regex.Pattern p_script;  
        java.util.regex.Matcher m_script;  
        java.util.regex.Pattern p_style;  
        java.util.regex.Matcher m_style;  
        java.util.regex.Pattern p_html;  
        java.util.regex.Matcher m_html;  
        try {  
            String regEx_script = "<[\\s]*?script[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?script[\\s]*?>"; // 定义script的正则表达式{或<script[^>]*?>[\\s\\S]*?<\\/script>  
            String regEx_style = "<[\\s]*?style[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?style[\\s]*?>"; // 定义style的正则表达式{或<style[^>]*?>[\\s\\S]*?<\\/style>  
            String regEx_html = "<[^>]+>"; // 定义HTML标签的正则表达式  
            p_script = Pattern.compile(regEx_script, Pattern.CASE_INSENSITIVE);  
            m_script = p_script.matcher(htmlStr);  
            htmlStr = m_script.replaceAll(""); // 过滤script标签  
            p_style = Pattern.compile(regEx_style, Pattern.CASE_INSENSITIVE);  
            m_style = p_style.matcher(htmlStr);  
            htmlStr = m_style.replaceAll(""); // 过滤style标签  
            p_html = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE);  
            m_html = p_html.matcher(htmlStr);  
            htmlStr = m_html.replaceAll(""); // 过滤html标签  
            textStr = htmlStr;  
            //剔除空格行  
            textStr=textStr.replaceAll("[ ]+", " ");  
            textStr=textStr.replaceAll("(?m)^\\s*$(\\n|\\r\\n)", "");
            
            textStr=textStr.replaceAll("&nbsp;", " ");//转成空格
        } catch (Exception e) {System.err.println("Html2Text: " + e.getMessage()); }  
        
        return textStr;// 返回文本字符串  
	}
	
}
