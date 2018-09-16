package com.xiaoshabao.zhuatu.service;

import com.xiaoshabao.zhuatu.service.able.HeavyAble;
import com.xiaoshabao.zhuatu.service.able.LoadFileAble;
import com.xiaoshabao.zhuatu.service.able.ZhuatuDownloadAble;

/**
 * 下载服务
 * <p>
 * 返回结果循环时，会把结果加入到下载连接池，进行下载文件
 * </p>
 */
public abstract class ZhuatuDownloadService implements ZhuatuService, ZhuatuDownloadAble, LoadFileAble,HeavyAble {

}
