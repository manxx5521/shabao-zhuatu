package com.xiaoshabao.zhuatu.core;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import com.xiaoshabao.zhuatu.DownloadTuTask;
import com.xiaoshabao.zhuatu.TuInfo;
import com.xiaoshabao.zhuatu.ZhuatuConfig;
import com.xiaoshabao.zhuatu.ZhuatuDownloadPool;
import com.xiaoshabao.zhuatu.ZhuatuUtil;
import com.xiaoshabao.zhuatu.service.ZhuatuService;
import com.xiaoshabao.zhuatu.service.able.LoadFileAble;
import com.xiaoshabao.zhuatu.service.able.ProjectAble;
import com.xiaoshabao.zhuatu.service.able.ZhuatuDownloadAble;
import com.xiaoshabao.zhuatu.service.able.ZhuatuWaitAble;

public class DownloadZhuatuImpl extends ZhuatuToHeavy {
	/** 是否需要下载池 */
	private boolean isNeedPool = false;
	/** 存储已经下载的项目列表 */
	protected List<String> projects = new LinkedList<String>();

	/**
	 * 初始化服务列表
	 */
	@Override
	protected void initBeforSerivce(ZhuatuService service) {
		if (service instanceof ZhuatuDownloadAble) {
			isNeedPool = true;
		}

		// 加载本地文件
		if (service instanceof LoadFileAble) {
			if (StringUtils.isEmpty(this.config.getSavePath())) {
				log.error("启用了加载本地文件接口，但是没有配置本地文件目录");
				return;
			} 
			//添加保存目录
			addProjectPath(config.getSavePath());
			//添加查询目录
			for(String path:config.getExtSavePath()) {
				addProjectPath(path);
			}
			
		}
		
		if (service instanceof ZhuatuDownloadAble) {
			ZhuatuDownloadPool.init();
		}
	}
	
	/**向projects添加已经下载的目录*/
	private void addProjectPath(String root) {
		File path = new File(root);
		if (!path.exists() || path.isFile()) {
			path.mkdirs();
		}
		for (File file : path.listFiles()) {
			if (file.isDirectory()) {
				projects.add(file.getName());
			}
		}
	}

	/**
	 * 解析当前页项目
	 */
	@Override
	protected boolean exeCurrPageProjet(ZhuatuService service, TuInfo tuInfo) {
		// 如果是项目服务，进行项目比对排重
		if (service instanceof ProjectAble) {
			if (projects.contains(ZhuatuUtil.formatTitleName(tuInfo.getTitle()))) {
				log.warn("项目 {} 未下载（项目已经存在）。", tuInfo.getTitle());
				return false;
			}
			// 如果有优先项目，先执行优先项目，其他跳过
			if (config.getFirstProject().size() > 0) {
				//是优先项目标志
				boolean flag=false;
				for(String title:config.getFirstProject()){
					if(tuInfo.getTitle().contains(title)){
						flag=true;
						break;
					}
				}
				//不是优先项目就跳过
				if(!flag){
					return false;
				}
			}

			log.warn("**开始下载项目 {}。(目录：{})***********",ZhuatuUtil.formatTitleName(tuInfo.getTitle()),config.getSavePath());
		}

		// 需要等待相同内容连接池
		if (isNeedPool && service instanceof ZhuatuWaitAble) {
			// 等待现成
			ZhuatuDownloadPool.getInstance().waitActiveThread();
		}

		// 如果是需要下载的url
		if (service instanceof ZhuatuDownloadAble) {
			String fileNameUrl = tuInfo.getUrl();
			if (fileNameUrl.contains("?")&&fileNameUrl.lastIndexOf("/")<fileNameUrl.lastIndexOf("?")) {
				fileNameUrl = fileNameUrl.substring(0, fileNameUrl.indexOf("?"));
			}
			String fileName = ZhuatuUtil.formatTitleName(fileNameUrl.substring(fileNameUrl.lastIndexOf("/") + 1, fileNameUrl.length()));
			
			String downloadUrl=parserDowloadUrl(tuInfo.getUrl());
			//判断是否是不下载url
			if (config.getNoDownloadName().size() > 0) {
				String baseName=FilenameUtils.getBaseName(fileName);
				if(config.getNoDownloadName().contains(baseName)) {
					return false;
				}
			}
			
			log.info("装载下载链接：" + fileNameUrl);
			DownloadTuTask myTask = new DownloadTuTask(ZhuatuUtil.formatUrl(downloadUrl,config.getWebRoot())
					,config.getSavePath() + File.separator
						+ZhuatuUtil.formatTitleName(tuInfo.getTitle()) + File.separator + fileName
					,config.getDwonloadType());
			ZhuatuDownloadPool.getInstance().execute(myTask);
		}
		return true;
	}

	@Override
	public void start(String url, List<ZhuatuService> zhuatuServices, ZhuatuConfig config) {
		super.start(url, zhuatuServices, config);
		ZhuatuDownloadPool.getInstance().shutdown();
	}
	
	/**
	 * 根据配置config信息，调整下载url
	 * @param url
	 * @return
	 */
	private String parserDowloadUrl(String url) {
		if(config.getDownlaodUrlParser()!=null) {
			url=config.getDownlaodUrlParser().apply(url);
		}
		return url;
	}
	
}
