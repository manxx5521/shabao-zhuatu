package com.xiaoshabao.zhuatu.core.pool;

import org.apache.commons.lang3.StringUtils;

import com.xiaoshabao.zhuatu.core.config.DownloadConfig;
import com.xiaoshabao.zhuatu.exception.ConnectException;
import com.xiaoshabao.zhuatu.http.HttpAble;
import com.xiaoshabao.zhuatu.http.HttpType;
import com.xiaoshabao.zhuatu.http.OkHttpManager;
import com.xiaoshabao.zhuatu.http.ProxyOkHttp;
import com.xiaoshabao.zhuatu.http.ZhuatuHttpManager;

public class DownloadTuTask implements Runnable {

	private String url;
	private String fileNamePath;
	private HttpType httpType;
	private DownloadConfig config;

	/**
	 * 下载文件
	 * 
	 * @param url
	 *            http://www.***.org/uploadfile/2017/0915/07/01.jpg
	 * @param fileNamePath
	 *            E:\\test\\gm\\01.jpg
	 * @param httpType 下载方式
	 */
	public DownloadTuTask(String url, String fileNamePath,DownloadConfig config) {
		this.url = url;
		this.fileNamePath = fileNamePath;
		this.httpType=config.getDwonloadType();
		this.config=config;
	}

	@Override
	public void run() {
		try {
			HttpAble httpAble;
			switch(httpType){
			case HTTPCLIENT:
				httpAble=ZhuatuHttpManager.getInstance();
				break;
//			case OKHTTP:
			default:
				if(StringUtils.isEmpty(config.getProxyIp())){
					httpAble=OkHttpManager.getInstance();
				}else{
					httpAble=ProxyOkHttp.getInstance(config.getProxyIp(), config.getProxyPort());
				}
				break;
			}
			httpAble.download(url, fileNamePath, 5,config.isTryProxy());
		} catch (ConnectException e) {
			ProxyOkHttp.getInstance("127.0.0.1", 1080).download(url, fileNamePath,2);
		}
		
		
	}

}
