package com.xiaoshabao.zhuatu.file;

import java.io.File;

import org.junit.Test;

public class CpCreateDir {
	
	@Test
	public void test(){
		//源dir
		String ydir="J:\\vm\\图片系列\\名站图片\\temp";
		//要创建到的位置
		String todir="J:\\vm\\图片系列\\名站图片\\1024记载";
		File root=new File(ydir);
		if(!root.exists()||!root.isDirectory()){
			throw new RuntimeException("源目录不是一个文件夹");
		}
		for(File file:root.listFiles()){
			File newFile=new File(todir+"\\"+file.getName());
			if(!newFile.exists()){
				newFile.mkdirs();
			}
			
		}
	}

}
