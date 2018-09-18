package com.xiaoshabao.zhuatu;

import com.xiaoshabao.zhuatu.exception.ConnectException;
import com.xiaoshabao.zhuatu.http.HttpType;
import com.xiaoshabao.zhuatu.http.OkHttpManager;
import com.xiaoshabao.zhuatu.http.ProxyOkHttp;
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
		try {
			switch(httpType){
			case HTTPCLIENT:
				ZhuatuHttpManager.getInstance().download5(url, fileNamePath);
				break;
			case OKHTTP:
				OkHttpManager.getInstance().download(url, fileNamePath,5);
				break;
			default:
				break;
			}
		} catch (ConnectException e) {
			ProxyOkHttp.getInstance("127.0.0.1", 1080).download(url, fileNamePath,2);
		}
		
		
	}

}
