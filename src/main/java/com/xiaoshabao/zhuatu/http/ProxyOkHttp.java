package com.xiaoshabao.zhuatu.http;

import java.net.InetSocketAddress;
import java.net.Proxy;

import okhttp3.OkHttpClient;


public class ProxyOkHttp extends BaseOkHttp{
	
	private volatile static ProxyOkHttp instance = null;
	
	private ProxyOkHttp(String host,Integer port) {
		client=new OkHttpClient()
        		.newBuilder().proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress(host, port))).build();
	}

	public static ProxyOkHttp getInstance(String host,Integer port) {
		if (instance == null) {
			synchronized (ProxyOkHttp.class) {
				if (instance == null) {
					instance = new ProxyOkHttp(host,port);
				}
			}
		}
		return instance;
	}

}
