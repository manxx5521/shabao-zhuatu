package com.xiaoshabao.zhuatu.core;

import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Consumer;

public class DataCache {
	
	private static volatile DataCache instance=null;
	
	private Set<String> projects=Collections.synchronizedSet(new TreeSet<String>());
	
	private Set<String> activeProject=Collections.synchronizedSet(new TreeSet<String>());
	
	private DataCache() {
		
	}
	
	public static DataCache getInstance() {
		if(instance==null) {
			synchronized (DataCache.class) {
				if(instance==null) {
					instance=new DataCache();
				}
			}
		}
		return instance;
	}
	
	/**
	 * 添加项目，成功后执行function函数
	 * @param name
	 * @param function
	 */
	public void addProject(String name,Consumer<String> function) {
		if(projects.add(name)){
			function.accept(name);
		}
	}
	/**
	 * 添加项目
	 * @param name
	 * @return
	 */
	public boolean addProject(String name) {
		return projects.add(name);
	}
	/**
	 * 当前项目是否是活跃进程
	 * @param name
	 * @param function 是活跃程序时执行
	 */
	public void isActiveProject(String name,Consumer<String> function) {
		if(activeProject.contains(name)){
			function.accept(name);
		}
	}
	
	/**
	 * 当前项目是否是活跃进程
	 */
	public boolean isActiveProject(String name) {
		return activeProject.contains(name);
	}
	
	/**
	 * 添加活跃项目
	 */
	public boolean addActiveProject(String name) {
		return activeProject.add(name);
	}
	
	/**
	 * 删除活跃项目
	 */
	public boolean removeActiveProject(String name) {
		return activeProject.remove(name);
	}
	
	/**
	 * 将某个活跃项目放到非活跃项目
	 * @param name
	 */
	public void putActiveToProject(String name) {
		removeActiveProject(name);
		addProject(name);
	}
	
	
	
}
