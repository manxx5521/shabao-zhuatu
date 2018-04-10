package com.xiaoshabao.zhuatu;

import com.xiaoshabao.zhuatu.http.HttpType;
import com.xiaoshabao.zhuatu.http.OkHttpManager;
import com.xiaoshabao.zhuatu.http.ZhuatuHttpManager;

public class DownloadTuTask implements Runnable {

	private String url;
	private String fileNamePath;
	private HttpType httpType;

	/**
	 * 下载文件
	 * 
	 * @param url
	 *            http://www.***.org/uploadfile/2017/0915/07/01.jpg
	 * @param fileNamePath
	 *            E:\\test\\gm\\01.jpg
	 * @param httpType 下载方式
	 */
	public DownloadTuTask(String url, String fileNamePath,HttpType httpType) {
		this.url = url;
		this.fileNamePath = fileNamePath;
		this.httpType=httpType;
	}

	@Override
	public void run() {
		switch(httpType){
		case HTTPCLIENT:
			ZhuatuHttpManager.getInstance().download5(url, fileNamePath);
			break;
		case OKHTTP:
			OkHttpManager.getInstance().download5(url, fileNamePath);
			break;
		default:
			break;
		}
		
	}

}
