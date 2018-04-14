package test;

import java.io.File;

import org.apache.commons.io.FileUtils;

public class Test {
	
	public static void main(String[] args){
		String path="E:\\test\\shabao-m\\resources\\plugins\\mm\\1024\\[原创][cl分享团出品]\"温致如猫\"90小少妇为艺术，公园赤裸与大自然零距离！大草坪直接开干！！第二章[47P]";
		try {
			String image=System.getProperty("user.dir")+"\\src\\main\\java\\com\\xiaoshabao\\zhuatu\\image\\404.png";
    		FileUtils.copyFile(new File(image), new File(path));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
