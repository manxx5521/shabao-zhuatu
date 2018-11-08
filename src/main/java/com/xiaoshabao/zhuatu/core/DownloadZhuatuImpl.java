package com.xiaoshabao.zhuatu.core;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

public class DownloadZhuatuImpl extends Decorator {
	private final static Logger log = LoggerFactory
			.getLogger(DownloadZhuatuImpl.class);
	
	/** 是否需要下载池 */
	protected boolean isNeedPool = false;
	
	private ZhuatuConfig config;
	private final static int TASK_TIME=5;
	private AtomicInteger time=new AtomicInteger(TASK_TIME);
	
	public DownloadZhuatuImpl(ZhuatuParser parser) {
		super(parser);
	}
	

	@Override
	public void init(List<ZhuatuService> serviceList,ZhuatuConfig config) {
		super.init(serviceList,config);
		
		this.config=config;
		for(ZhuatuService service:serviceList){
			initBeforSerivce(service);
		}
	}

	/**
	 * 初始化服务列表
	 */
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
			log.debug("开始加载本地目录****");
			log.debug("-----");
			//添加保存目录
			addProjectPath(config.getSavePath());
			//添加查询目录
			for(String path:config.getExtSavePath()) {
				addProjectPath(path);
			}
			log.debug("结束加载本地目录****");
		}
		
		if (service instanceof ZhuatuDownloadAble) {
			ZhuatuDownloadPool.init();
			
			//启动一个定时线程输出 线程池状态
			CompletableFuture.runAsync(() -> {
				while(true){
					try {
						Thread.sleep(1000);
						if(time.get()<1){
							log.info(info());
						}else{
							time.getAndDecrement();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				
			});
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
				DataCache.getInstance().addProject(file.getName());
			}
		}
	}
	
	
	/**
	 * 解析当前页项目
	 */
	@Override
	public boolean doReturnProject(ZhuatuService service, TuInfo info) {
		if( super.doReturnProject(service, info)){
			// 如果是项目服务，进行项目比对排重
			if (service instanceof ProjectAble) {
				
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

			// 需要等待相同内容连接池，无需特殊处理，通过线程池处理等待
			if (isNeedPool && service instanceof ZhuatuWaitAble) {
				// 等待线程完成
				System.out.println();
				/*ZhuatuDownloadPool.getInstance().waitActiveThread();
				if(ZhuatuDownloadPool.getInstance().getQueue().size()>100) {
					
				}*/
				
			}

			// 如果是需要下载的url
			if (service instanceof ZhuatuDownloadAble) {
				String fileNameUrl = info.getUrl();
				if (fileNameUrl.contains("?")&&fileNameUrl.lastIndexOf("/")<fileNameUrl.lastIndexOf("?")) {
					fileNameUrl = fileNameUrl.substring(0, fileNameUrl.indexOf("?"));
				}
				String fileName = ZhuatuUtil.formatTitleName(fileNameUrl.substring(fileNameUrl.lastIndexOf("/") + 1, fileNameUrl.length()));
				
				
				
				String downloadUrl=parserDowloadUrl(info.getUrl());
				//判断是否是不下载url
				if (config.getNoDownloadName().size() > 0) {
					String baseName=FilenameUtils.getBaseName(fileName);
					if(config.getNoDownloadName().contains(baseName)) {
						return false;
					}
				}
				
				String saveName=config.getSavePath() + File.separator +info.getTitle() + File.separator + fileName;
				
				if (config.getCheckProjects().contains(info.getTitle())) {
					File checkFile=new File(saveName);
					if(checkFile.exists()){
						log.info("文件{}已经存在略过。",fileName);
						return false;
					}
				}
				
				log.info("装载下载链接：{};{}" ,fileNameUrl,info());
				DownloadTuTask myTask = new DownloadTuTask(ZhuatuUtil.formatUrl(downloadUrl,config.getWebRoot()),saveName,config);
				ZhuatuDownloadPool.getInstance().execute(myTask);
			}
		}
		return true;
	}
	
	
	//当前项目解析完成后
	@Override
	public void afterPageProjet(ZhuatuService service, TuInfo info) {
		super.afterPageProjet(service, info);
		
		if (service instanceof ProjectAble) {
			DataCache.getInstance().putActiveToProject(info.getTitle());
		}
	}


	
	

	@Override
	public void afterRuning() {
		super.afterRuning();
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
	
	private String info(){
		time.set(TASK_TIME);
		StringBuilder sb=new StringBuilder();
		sb.append("{活跃下载：").append(ZhuatuDownloadPool.getInstance().getActiveCount()).append("}");
		return sb.toString(); 
	}
	
}
