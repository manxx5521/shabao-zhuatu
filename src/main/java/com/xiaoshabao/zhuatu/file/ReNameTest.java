package com.xiaoshabao.zhuatu.file;

import java.io.File;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReNameTest {
	private final static Logger log = LoggerFactory.getLogger(ReNameTest.class);
	
//	private String rootPath="D:\\soft\\FLV vi\\[秀舞时代]\\最新日期\\"+"(专辑)20180429011413秀舞时代的自频道-优酷视频";
//	private String prefix="[秀舞时代] 2018.04.08 ";
	
	private String rootPath="J:\\vm\\热舞多组\\兰姐广场舞\\"+"精品";
	private String prefix="[兰姐广场舞] 2017.";
	
	@Test
	public void test(){
		log.info("开始===>");
		File pathFile=new File(rootPath);
		for(File file:pathFile.listFiles()){
			String name=file.getName().replace("_", " ");
			
			if(name.startsWith("秀舞时代 ")){
				name=name.replace("秀舞时代 ", "");
			}
			if(name.startsWith("[兰姐广场舞] 2018.")){
				name=name.replace("[兰姐广场舞] 2018.", "");
			}
			
			name=rootPath+"\\"+prefix+name;
			file.renameTo(new File(name));
			log.info("==重命名到=>{}",name);
		}
		log.info("结束===>");
	}
	
	
	
}
