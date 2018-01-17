package com.xiaoshabao.zhuatu.service.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/** 抓图文件读取 */
public class ZhuatuYoukuReader {
	private Map<String, ReadBean> data = new HashMap<String, ReadBean>();
	private String path;
	private String fileName;//不带扩展名的文件名，带路径

	/**
	 * E:\\test\\test
	 * 
	 * @param path
	 */
	public ZhuatuYoukuReader(String path) {
		this.path = path;
	}
	public void load(String title) {
		load(title,false);
	}
	/**
	 * 加载
	 * @param title
	 * @param readerTxt 是否加载本地文件内容
	 */
	public void load(String title,boolean readerTxt) {
		this.fileName = this.path + File.separator + title;
		data.clear();
		if(!readerTxt){
			return;
		}
		try {
			InputStreamReader inR = new InputStreamReader(new FileInputStream(
					fileName+".txt"));
			BufferedReader buf = new BufferedReader(inR);
			String line;
			while ((line = buf.readLine()) != null) {
				// name:测试,date:2017.02.01,have:true
				String name=null;
				String date=null;
				boolean have=false;
				String[] group=line.split(",");
				for(String prop:group){
					String[] attrs=prop.split(":");
					String attrName=attrs[0];
					String attrValue=attrs[1];
					if("name".equals(attrName)){
						name=attrValue;
					}else if("dateStr".equals(attrName)){
						date=attrValue;
					}else if("have".equals(attrName)){
						have=Boolean.valueOf(attrValue);
					}
				}
				this.put(name, date, have);
			}
			buf.close();
			inR.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 插入值
	 * 
	 * @param name
	 *            测试
	 * @param dateStr
	 *            2017.02.01
	 
	 */
	public void put(String name, String dateStr) {
		put(name,dateStr,false);
	}
	/**
	 * 插入值
	 * 
	 * @param name
	 *            测试
	 * @param dateStr
	 *            2017.02.01
	 * @param have 是否匹配过
	 */
	public void put(String name, String dateStr,boolean have) {
		this.data.put(name, new ReadBean(name, dateStr, have));
	}
	
	/**
	 * 把数据写回文件
	 */
	public void write(){
		PrintWriter pw;
		try {
			pw = new PrintWriter(new FileWriter(this.fileName+"_cp"+".txt"));
			for(ReadBean readBean:this.data.values()){
				pw.println(readBean.toString());
			}
			pw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		File file=new File(fileName);
		if(file.exists()){
			file.delete();
		}
		new File(this.fileName+"_cp"+".txt").renameTo(new File(fileName+".txt"));
	}
	
	public void logInfo(String msg){
		
	}

	class ReadBean {
		private String name;
		private String dateStr;
		private boolean have;

		public ReadBean(String name, String dateStr, boolean have) {
			super();
			this.name = name;
			this.dateStr = dateStr;
			this.have = have;
		}

		@Override
		public String toString() {
			return "name:" + name + ",dateStr:" + dateStr + ",have:"
					+ String.valueOf(have);
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getDateStr() {
			return dateStr;
		}

		public void setDateStr(String dateStr) {
			this.dateStr = dateStr;
		}

		public boolean isHave() {
			return have;
		}

		public void setHave(boolean have) {
			this.have = have;
		}
	}

}
