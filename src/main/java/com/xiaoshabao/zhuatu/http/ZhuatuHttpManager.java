package com.xiaoshabao.zhuatu.http;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
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
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xiaoshabao.zhuatu.RequestMethod;
import com.xiaoshabao.zhuatu.RetryFactory;
import com.xiaoshabao.zhuatu.ZhuatuConfig;

/**
 * HTTP管理
 */
public class ZhuatuHttpManager {
	protected Logger log = LoggerFactory.getLogger(getClass());
	private volatile static ZhuatuHttpManager instance = null;

	private static final String HTTP = "http";
	private static final String HTTPS = "https";
	private static SSLConnectionSocketFactory sslsf = null;
	private static PoolingHttpClientConnectionManager cm = null;
	private static SSLContextBuilder builder = null;
	static {
		try {
			builder = new SSLContextBuilder();
			// 全部信任 不做身份鉴定
			builder.loadTrustMaterial(null, new TrustStrategy() {
				@Override
				public boolean isTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
					return true;
				}
			});
			sslsf = new SSLConnectionSocketFactory(builder.build(),
					new String[] { "SSLv2Hello", "SSLv3", "TLSv1", "TLSv1.2" }, null, NoopHostnameVerifier.INSTANCE);
			Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
					.register(HTTP, new PlainConnectionSocketFactory()).register(HTTPS, sslsf).build();
			cm = new PoolingHttpClientConnectionManager(registry);
			cm.setMaxTotal(200);// max connection
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private ZhuatuHttpManager() {
	}

	public static ZhuatuHttpManager getInstance() {
		if (instance == null) {
			synchronized (ZhuatuHttpManager.class) {
				if (instance == null) {
					instance = new ZhuatuHttpManager();
				}
			}
		}
		return instance;
	}

	/**
	 * 默认自动请求5次
	 * 
	 * @return 返回响应内容
	 */
	public String doHTTPAuto5(String url) {
		return doHTTPAuto5(url, new ZhuatuConfig());
	}

	/**
	 * 默认自动请求5次
	 * 
	 * @return 返回响应内容
	 */
	public String doHTTPAuto5(String url, ZhuatuConfig config) {
		return new RetryFactory<String, String>(url, "访问URL").execute(tempUrl -> {
			return this.doHTTPAuto(tempUrl, config);
		});
	}

	/**
	 * 请求分发
	 */
	private String doHTTPAuto(String url, ZhuatuConfig config) throws ClientProtocolException, IOException {
		if (url.startsWith(ZhuatuHttpManager.HTTPS)) {
			if (config.getMethod().equals(RequestMethod.GET)) {
				return this.doGet(url, config.getCharset());
			} else {
				return this.doPost(url, config.getCharset());
			}
		} else {
			if (config.getMethod().equals(RequestMethod.GET)) {
				return this.doGet(url, config.getCharset());
			} else {
				return this.doPost(url, config.getCharset());
			}
		}
	}

	public void download5(String url, String pathName) {
		new RetryFactory<DownloadInfo, Boolean>(new DownloadInfo(url, pathName), "下载文件").execute(info -> {
			this.download(info.url, info.pathName);
			log.info("下载文件成功 url->{}", info.url);
			return Boolean.TRUE;
		});
	}

	class DownloadInfo {
		String url;
		String pathName;

		public DownloadInfo(String url, String pathName) {
			this.url = url;
			this.pathName = pathName;
		}
	}

	private void download(String url, String pathName) throws Exception {
		HttpGet httpGet = new HttpGet(url);// 创建get请求
		CloseableHttpClient httpClient = null;
		CloseableHttpResponse response = null;
		HttpEntity entity = null;
		// 自定义超时时间等
		RequestConfig requestConfig = RequestConfig.custom()

				.setSocketTimeout(1000 * 60 * 15) // socket超时
				.setConnectTimeout(50000) // connect超时
				.build();
		httpGet.setConfig(requestConfig);
		this.setHearder(httpGet);

		try {
			httpClient = getHttpClient(url);

			response = httpClient.execute(httpGet);
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == HttpStatus.SC_OK) {
				entity = response.getEntity();
				if (entity != null) {
					try (InputStream instream = entity.getContent()) {
						byte[] image = IOUtils.toByteArray(instream);
						FileUtils.writeByteArrayToFile(new File(pathName), image);
					}
				}else {
					throw new Exception("未能正常下载文件 url返回实体为空");
				}
			} else {
				throw new Exception("未能正常返回 下载文件结果，返回状态" + statusCode);
			}
		} finally {
			if (response != null) {
				response.close();
			}
			if (httpClient != null) {
				httpClient.close();
			}
		}
	}

	public CloseableHttpClient getHttpClient(String url) {
		CloseableHttpClient httpClient = null;
		if (url.startsWith(ZhuatuHttpManager.HTTPS)) {
			/*
			 * httpClient = HttpClients.custom()
			 * .setSSLSocketFactory(sslsf).setConnectionManager(cm)
			 * .setConnectionManagerShared(true).build();
			 */
			httpClient = HttpClientManager.generateClient();
		} else {
			httpClient = HttpClients.createDefault();
		}
		return httpClient;
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
	private String doGet(String url, String charset) throws ClientProtocolException, IOException {
		HttpGet httpGet = new HttpGet(url);
		CloseableHttpClient httpClient = null;
		CloseableHttpResponse response = null;
		HttpEntity entity = null;
		String responseContent = null;

		this.setHearder(httpGet);

		try {
			// 创建默认的httpClient实例.
			httpClient = HttpClients.createDefault();
			// 执行请求
			response = httpClient.execute(httpGet);
			entity = response.getEntity();
			responseContent = EntityUtils.toString(entity, charset);// 获得响应内容
		} finally {
			if (response != null) {
				response.close();
			}
			if (httpClient != null) {
				httpClient.close();
			}
		}
		return responseContent;

	}

	/**
	 * 发送post请求
	 * 
	 * @return String 返回字符串，可转换成JSON
	 * @throws IOException
	 * @throws ClientProtocolException
	 */
	public String doPost(String url, String charset) throws ClientProtocolException, IOException {
		HttpPost httpPost = new HttpPost(url);
		CloseableHttpClient httpClient = null;
		CloseableHttpResponse response = null;
		HttpEntity entity = null;
		String result = null;

		this.setHearder(httpPost);

		try {
			// 创建默认的httpClient实例.
			httpClient = HttpClients.createDefault();
			// 执行请求
			response = httpClient.execute(httpPost);
			entity = response.getEntity();
			result = EntityUtils.toString(entity, charset);// 获得响应内容
		} finally {
			try {
				// 关闭连接,释放资源
				if (response != null) {
					response.close();
				}
				if (httpClient != null) {
					httpClient.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	/**
	 * 设置header
	 */
	public void setHearder(HttpRequestBase httpRequest) {
		httpRequest.setHeader("Accept", "text/html, */*; q=0.01");
		httpRequest.setHeader("Accept-Encoding", "gzip, deflate,sdch");
		httpRequest.setHeader("Accept-Language", "zh-CN,zh;q=0.8");
		httpRequest.setHeader("Connection", "keep-alive");
		httpRequest.setHeader("User-Agent",
				"Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/37.0.2062.124 Safari/537.36)");
	}

}
