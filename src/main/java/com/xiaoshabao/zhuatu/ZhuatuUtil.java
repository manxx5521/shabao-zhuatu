package com.xiaoshabao.zhuatu;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class ZhuatuUtil {
	/**
	 * 去除可能存在的特殊字符(目前只做等于配置使用)
	 * 
	 * @return
	 */
	public static String parserTitleName(String title) {
		title = title.replace("/", "");
		title = title.replace("\\", "");
		title = title.replace("|", "");
		title = title.replace("?", "");
		title = title.replace(":", "");
		title = title.replace("*", "");
		title = title.replace("<", "");
		title = title.replace(">", "");
		title = title.replace("amp;", "");
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
