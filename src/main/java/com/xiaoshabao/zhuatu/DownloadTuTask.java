package com.xiaoshabao.zhuatu;

import com.xiaoshabao.zhuatu.http.ZhuatuHttpManager;

public class DownloadTuTask implements Runnable {

	private String url;
	private String fileNamePath;

	/**
	 * 下载文件
	 * 
	 * @param url
	 *            http://www.***.org/uploadfile/2017/0915/07/01.jpg
	 * @param fileNamePath
	 *            E:\\test\\gm\\01.jpg
	 */
	public DownloadTuTask(String url, String fileNamePath) {
		this.url = url;
		this.fileNamePath = fileNamePath;
	}

	@Override
	public void run() {
		ZhuatuHttpManager.getInstance().download5(url, fileNamePath);
	}

}
