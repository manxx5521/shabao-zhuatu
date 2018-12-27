package com.xiaoshabao.zhuatu.http;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;

import javax.net.ssl.SSLContext;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * HttpClient 实现的链接
 */
public class HttpClientManager implements HttpAble{
	protected Logger log = LoggerFactory.getLogger(getClass());
	private volatile static HttpClientManager instance = null;
	
	private CloseableHttpClient httpClient;
	private CloseableHttpClient httpsClient;
	
	
	private static final String[] USER_AGENT = {
			"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/28.0.1500.95 Safari/537.36",
			"Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; WOW64; Trident/5.0; SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729; Media Center PC 6.0; .NET4.0C; .NET4.0E; LBBROWSER)",
			"Mozilla/5.0 (Windows; U; Windows NT 5.2) Gecko/2008070208 Firefox/3.0.1",
			"Mozilla/5.0 (Windows; U; Windows NT 5.2) AppleWebKit/525.13 (KHTML, like Gecko) Version/3.1 Safari/525.13",
			"Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.8.1.12) Gecko/20080219 Firefox/2.0.0.12 Navigator/9.0.0.6",
			"Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; WOW64; Trident/5.0; SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729; Media Center PC 6.0; .NET4.0C; .NET4.0E; QQBrowser/7.0.3698.400)",
			"Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; WOW64; Trident/5.0)",
			"Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN; rv:1.9.1.9) Gecko/20100315 Firefox/3.5.9" };

	

	private HttpClientManager() {
		httpsClient = generateClient();
		httpClient = HttpClients.createDefault();
	}

	public static HttpClientManager getInstance() {
		if (instance == null) {
			synchronized (HttpClientManager.class) {
				if (instance == null) {
					instance = new HttpClientManager();
				}
			}
		}
		return instance;
	}
	private CloseableHttpClient generateClient() {
		return generateClient(null);
	}

	private CloseableHttpClient generateClient(HttpHost httpHost) {

		SSLContext sslcontext = SSLContexts.createSystemDefault();
		Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
				.register("http", PlainConnectionSocketFactory.INSTANCE)
				.register("https", new SSLConnectionSocketFactory(sslcontext)).build();
		// http连接池管理，服务于多个执行进程的连接请求
		PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(
				socketFactoryRegistry);
		connectionManager.setMaxTotal(200);
		connectionManager.setDefaultMaxPerRoute(20);

		RequestConfig requestConfig = RequestConfig.custom().setProxy(httpHost).build();

		HttpClientBuilder httpClientBuilder = HttpClients.custom().setUserAgent(randomUserAgent())
				.setConnectionManager(connectionManager).setDefaultRequestConfig(requestConfig);
		return httpClientBuilder.build();
	}

	private String randomUserAgent() {
		return USER_AGENT[(int) (Math.random() * USER_AGENT.length)];
	}
	

	@Override
	public boolean download(String url, String pathName) throws IOException {
		
		
		try {
			//创建url传入，可以解决部分编码问题
			URL url1= new URL(url);
			URI uri = new URI(url1.getProtocol(), url1.getHost(), url1.getPath(), url1.getQuery(), null);
			
			// 创建get请求
			HttpGet httpGet = new HttpGet(uri);
			
			HttpEntity entity = null;
			// 自定义超时时间等
			RequestConfig requestConfig = RequestConfig.custom()
					// socket超时
					.setSocketTimeout(DOWNLOAD_READ_TIME_OUT) 
					// connect超时
					.setConnectTimeout(50000) 
					.build();
			httpGet.setConfig(requestConfig);
			this.setHearder(httpGet);
			
			try (CloseableHttpClient httpClient = getHttpClient(url);
					CloseableHttpResponse response = httpClient.execute(httpGet);){
				int statusCode = response.getStatusLine().getStatusCode();
				if (statusCode == HttpStatus.SC_OK) {
					entity = response.getEntity();
					if (entity != null) {
						try (InputStream instream = entity.getContent()) {
							byte[] image = IOUtils.toByteArray(instream);
							FileUtils.writeByteArrayToFile(new File(pathName), image);
							return true;
						} catch (IOException e) {
							log.error("写入文件时发生错误：->{}",url);
							return false;
						}
					}else {
						log.error("未能正常下载文件 url返回实体为空");
					}
				} else {
					log.error("未能正常返回 下载文件结果，返回状态{}" , statusCode);
				}
			}/* catch (IOException e) {
				e.printStackTrace();
			}*/
		} catch (MalformedURLException e) {
			log.error("下载url存在格式问题：->{}",url);
			e.printStackTrace();
		} catch (URISyntaxException e) {
			log.error("下载url存在格式问题：->{}",url);
			e.printStackTrace();
		}
		return false;
		
	}

	private CloseableHttpClient getHttpClient(String url) {
		if (url.startsWith(HttpClientManager.HTTPS)) {
			return httpsClient;
		} else {
			return httpClient;
		}
	}

	/**
	 * get请求
	 * 
	 * @param url
	 * @param charset
	 * @throws ClientProtocolException
	 *             发送http get请求未能正常建立连接或者访问资源
	 * @throws IOException
	 *             发送http get请求时资源未能正常关闭！！
	 */
	@Override
	public String doGet(String url, Charset charset) throws IOException{
		HttpGet httpGet = new HttpGet(url);
		HttpEntity entity = null;
		String result = null;

		this.setHearder(httpGet);

		try(// 创建默认的httpClient实例.
				CloseableHttpClient httpClient=HttpClients.createDefault();
				// 执行请求
				CloseableHttpResponse response = httpClient.execute(httpGet);) {
			entity = response.getEntity();
			// 获得响应内容
			result = EntityUtils.toString(entity, charset);
		}
		return result;

	}
	
	@Override
	public String doPost(String url, Charset charset) throws IOException {
		HttpPost httpPost = new HttpPost(url);
		HttpEntity entity = null;
		String result = null;

		this.setHearder(httpPost);

		try(// 创建默认的httpClient实例.
				CloseableHttpClient httpClient = HttpClients.createDefault();
				// 执行请求
				CloseableHttpResponse response = httpClient.execute(httpPost);) {
			entity = response.getEntity();
			// 获得响应内容
			result = EntityUtils.toString(entity, charset);
		} 
		return result;
	}

	/**
	 * 设置header
	 */
	private void setHearder(HttpRequestBase httpRequest) {
		httpRequest.setHeader("Accept", "text/html, */*; q=0.01");
		httpRequest.setHeader("Accept-Encoding", "gzip, deflate,sdch");
		httpRequest.setHeader("Accept-Language", "zh-CN,zh;q=0.8");
		httpRequest.setHeader("Connection", "keep-alive");
		httpRequest.setHeader("User-Agent",
				"Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/37.0.2062.124 Safari/537.36)");
	}

	


}
