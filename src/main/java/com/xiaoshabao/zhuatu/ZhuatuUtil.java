package com.xiaoshabao.zhuatu;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

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
	public static String formatUrl(String title) {
		title=title.replace("\r","");
		title=title.replace("\n","");
		return title;
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
	
}
