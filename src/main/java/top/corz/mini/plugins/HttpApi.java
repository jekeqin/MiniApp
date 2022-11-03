package top.corz.mini.plugins;

import java.io.InputStream;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class HttpApi {

	public JSONObject httpPost(String url, String jsonString)
	{
		Map<String,String> header = new HashMap<String, String>();
		header.put("Content-type", "application/json;charset=utf-8");
		
		return httpPostWithHeader(header, url, jsonString);
	}
	
	public <T> T httpGet(Class<T> z, String url) {
		JSONObject json = httpGet(url);
		if( json==null )
			return null;
		return json.toJavaObject(z);
	}
	
	public JSONObject httpGet(String url, JSONObject data) {
		if( data!=null ) {
			List<String> list = new ArrayList<>();
			for(String key:data.keySet()) {
				list.add(key + "=" + data.getString(key));
			}
			if( url.indexOf("?")>0 ) {
				url += "&" + String.join("&", list);
			}else {
				url += "?" + String.join("&", list);
			}
		}
		return httpGet(url);
	}
	
	public JSONObject httpGet(String url) {
		log.debug("httpGet: url:{}", url);
		
		RequestConfig config = RequestConfig.custom()
				.setConnectionRequestTimeout(2000)		// 连接超时时间，2秒
				.setConnectTimeout( 10000 )			// 请求超时，半小时，10秒
				.build();
		CloseableHttpClient http = getClient();
		HttpGet req = new HttpGet(url);
		req.setConfig(config);

		try {
			CloseableHttpResponse response = http.execute(req);
			if( HttpStatus.SC_OK == response.getStatusLine().getStatusCode() )
			{
				String json = EntityUtils.toString(response.getEntity(), "UTF-8");
				if( json==null )
					return null;
				log.debug(json);
				return JSONObject.parseObject(json);
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public JSONObject httpPostWithHeader(Map<String, String> header, String url, String jsonString)
	{
		log.debug("httpPost: url:{}, data:{}", url, jsonString);
		
		RequestConfig config = RequestConfig.custom()
				.setConnectionRequestTimeout(2000)		// 连接超时时间，2秒
				.setConnectTimeout( 10000 )			// 请求超时，5秒
				.build();
		CloseableHttpClient http = getClient();
		HttpPost req = new HttpPost(url);
		req.setConfig(config);
		
		if( header!=null ) {
			for(String key : header.keySet()) {
				req.setHeader(key, header.get(key));
			}
		}

		StringEntity se = new StringEntity(jsonString, ContentType.APPLICATION_JSON);
		se.setContentEncoding("UTF-8");
		try {
			req.setEntity(se);
			CloseableHttpResponse response = http.execute(req);
			if( HttpStatus.SC_OK == response.getStatusLine().getStatusCode() )
			{
				String json = EntityUtils.toString(response.getEntity(), "UTF-8");
				if( json==null )
					return null;
				return JSONObject.parseObject(json);
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public JSONObject httpPostForm(String url, JSONObject data)
	{
		log.debug("httpPostForm: url:{}, data:{}", url, data);
		
		RequestConfig config = RequestConfig.custom()
				.setConnectionRequestTimeout(2000)		// 连接超时时间，2秒
				.setConnectTimeout( 10000 )			// 请求超时，5秒
				.build();
		CloseableHttpClient http = getClient();
		HttpPost req = new HttpPost(url);
		req.setConfig(config);

		List<String> list = new ArrayList<>();
		for(String key:data.keySet()) {
			list.add(key + "=" + data.getString(key));
		}
		
		StringEntity se = new StringEntity(String.join("&", list), ContentType.create("application/x-www-form-urlencoded", "utf-8"));
		se.setContentEncoding("UTF-8");
		try {
			req.setEntity(se);
			CloseableHttpResponse response = http.execute(req);
			if( HttpStatus.SC_OK == response.getStatusLine().getStatusCode() )
			{
				String json = EntityUtils.toString(response.getEntity(), "UTF-8");
				if( json==null )
					return null;
				return JSONObject.parseObject(json);
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public JSONObject httpPostStream(String url, InputStream stream, String type, String fileName)
	{
		log.debug("httpPost: url:{}", url);
		
		RequestConfig config = RequestConfig.custom()
				.setConnectionRequestTimeout(2000)		// 连接超时时间，2秒
				.setConnectTimeout( 10000 )			// 请求超时，5秒
				.build();
		CloseableHttpClient http = getClient();
		HttpPost req = new HttpPost(url);
		req.setConfig(config);

		try {
			MultipartEntityBuilder builder = MultipartEntityBuilder.create();
			builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
			builder.addBinaryBody("file", stream, ContentType.create(type), fileName);
			HttpEntity entity = builder.build();
			req.setEntity(entity);
			CloseableHttpResponse response = http.execute(req);
			if( HttpStatus.SC_OK == response.getStatusLine().getStatusCode() )
			{
				String json = EntityUtils.toString(response.getEntity(), "UTF-8");
				if( json==null )
					return null;
				return JSONObject.parseObject(json);
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private final CloseableHttpClient getClient()
	{
		try {
			SSLContext ctx = SSLContext.getInstance("SSL");
			X509TrustManager tm = new X509TrustManager() {
				public X509Certificate[] getAcceptedIssuers() {
					return null;
				}
				public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
				}
				public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
				}
			};
			ctx.init(null, new TrustManager[] {tm}, null);
			SSLConnectionSocketFactory ssf = new SSLConnectionSocketFactory(ctx, NoopHostnameVerifier.INSTANCE);
			
			CloseableHttpClient http = HttpClients.custom().setSSLSocketFactory(ssf).build();
			return http;
		}catch (Exception e) {
		}
		return HttpClients.createDefault();
	}
	
}
