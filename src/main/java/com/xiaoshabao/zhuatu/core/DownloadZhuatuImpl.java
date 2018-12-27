package com.xiaoshabao.zhuatu.core;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xiaoshabao.zhuatu.core.config.DownloadConfig;
import com.xiaoshabao.zhuatu.core.config.ZhuatuConfig;
import com.xiaoshabao.zhuatu.core.log.LogManager;
import com.xiaoshabao.zhuatu.core.pool.DownloadTuTask;
import com.xiaoshabao.zhuatu.core.pool.ZhuatuDownloadPool;

public class DownloadZhuatuImpl extends Decorator {
	private final static Logger log = LoggerFactory.getLogger(DownloadZhuatuImpl.class);
	
	private DownloadConfig config;
	public DownloadZhuatuImpl(ZhuatuParser parser) {
		super(parser);
	}
	

	@Override
	public void init(List<Service> serviceList, ZhuatuConfig bconfig) {
		super.init(serviceList, bconfig);
		
		if(!(bconfig instanceof DownloadConfig)) {
			return;
		}
		this.config = (DownloadConfig) bconfig;;

		// 加载本地文件
		if (config.isLoadLocalFile()) {
			if (StringUtils.isEmpty(this.config.getSavePath())) {
				log.error("启用了加载本地文件接口，但是没有配置本地文件目录");
				return;
			}
			log.debug("开始加载本地目录****");
			log.debug("-----");
			// 添加保存目录
			addProjectPath(config.getSavePath());
			// 添加查询目录
			for (String path : config.getExtSavePath()) {
				addProjectPath(path);
			}
			log.debug("结束加载本地目录****");
		}

		ZhuatuDownloadPool.init();
		
		//初始化线程池日志
		LogManager.getInstance();

	}

	/**向projects添加已经下载的目录*/
	private void addProjectPath(String root) {
		File path = new File(root);
		if (!path.exists() || path.isFile()) {
			path.mkdirs();
		}
		for (File file : path.listFiles()) {
			if (file.isDirectory()) {
				DataCache.getInstance().addProject(file.getName());
			}
		}
	}
	
	
	/**
	 * 解析当前页项目
	 */
	@Override
	public boolean doReturnProject(Service service, TuInfo info) {
		if( super.doReturnProject(service, info)){
			//如果时不需要访问的域名前缀
			for(String start:config.getNoUrl()){
				if(info.getUrl().startsWith(start)){
					log.info("链接在noUrl中无需访问。url->{}",info.getUrl());
					return false;
				}
			}
			
			
			// 如果是项目服务，进行项目比对排重
			if (service.isWaitProject()) {
				
				String name=info.getTitle();
				
				//判断是否是活跃程序
				synchronized (DownloadZhuatuImpl.class) {
					if(DataCache.getInstance().isActiveProject(name)){
						return false;
					}else {
						DataCache.getInstance().addActiveProject(name);
					}
				}
				
				if (config.getCheckProjects().contains(name)) {
					log.warn("进行重新 检查下载---> {}",name);
				}else if(!DataCache.getInstance().addProject(name)) {
					log.warn("项目 {} 未下载（项目已经存在）。", name);
					return false;
				}
				// 如果有优先项目，先执行优先项目，其他跳过
				if (config.getFirstProject().size() > 0) {
					//是优先项目标志
					boolean flag=false;
					for(String title:config.getFirstProject()){
						if(info.getTitle().contains(title)){
							flag=true;
							break;
						}
					}
					//不是优先项目就跳过
					if(!flag){
						return false;
					}
				}
				
				//判断是否需要将项目链接保存
				if(config.isSaveLink()) {
					try(OutputStream output= new FileOutputStream(config.getSavePath()+File.separator+"link.txt")){
						IOUtils.write(info.getUrl().getBytes(), output);
					}catch (Exception e) {
						log.warn("**保存链接失败-> {}。***********",info.getUrl());
					}
				}
				
				log.warn("**开始下载项目 {}。(目录：{})***********",info.getTitle(),config.getSavePath());
			}

			// 如果是需要下载的url
			if (service.isDownloadUrl()) {
				
				//自定义的url解析
				String downloadUrl =parserDowloadUrl(info.getUrl());
				
				//去除无用问号
				if (downloadUrl.contains("?")&&downloadUrl.lastIndexOf("/")<downloadUrl.lastIndexOf("?")) {
					downloadUrl = downloadUrl.substring(0, downloadUrl.indexOf("?"));
				}
				
				String fileName = ZhuatuUtil.formatTitleName(downloadUrl.substring(downloadUrl.lastIndexOf("/") + 1, downloadUrl.length()));
				
				//判断不下载的名字
				if (config.getNoDownloadName().size() > 0) {
					String baseName = FilenameUtils.getBaseName(fileName);
					if (config.getNoDownloadName().contains(baseName)) {
						return false;
					}
				}
				StringBuilder savePath=new StringBuilder();
				savePath.append(service.getSavePath()!=null?service.getSavePath():config.getSavePath());
				if(StringUtils.isNotEmpty(info.getTitle())){
					savePath.append(File.separator);
					savePath.append(info.getTitle());
				}
				savePath.append(File.separator);
				savePath.append(fileName);
				String saveName=savePath.toString();
				
				//检查下载
				if (config.getCheckProjects().contains(info.getTitle())) {
					File checkFile=new File(saveName);
					if(checkFile.exists()){
						log.info("文件{}已经存在略过。",fileName);
						return false;
					}
				}
				
				log.info("装载下载链接：{};{}" ,downloadUrl,LogManager.getInstance().getInfoAndRefresh());
				DownloadTuTask myTask = new DownloadTuTask(ZhuatuUtil.formatUrl(downloadUrl,config.getWebRoot()),saveName,config);
				ZhuatuDownloadPool.getInstance().execute(myTask);
			}
		}
		return true;
	}
	
	
	//当前项目解析完成后
	@Override
	public void afterPageProjet(Service service,TuInfo info) {
		super.afterPageProjet(service, info);
		
		if (service.isWaitProject()) {
			DataCache.getInstance().putActiveToProject(info.getTitle());
		}
	}


	
	

	@Override
	public void afterRuning() {
		super.afterRuning();
		ZhuatuDownloadPool.getInstance().shutdown();
		
		while(true) {
			//关闭后所有任务都已完成,则返回true
			if(ZhuatuDownloadPool.getInstance().isTerminated()) {
				break;
			}
			try {
				TimeUnit.SECONDS.sleep(5);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
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
