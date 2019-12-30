package com.ijianjian.core.common.util;

import java.io.*;
import java.net.ConnectException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.HttpsURLConnection;

/**
 * @author GWCheng
 *
 */
public class HttpUtil {

private static final CloseableHttpClient httpclient = HttpClients.createDefault();

/**
 * 发送HttpGet请求
 * 
 * @param url
 * @return
 */
public static String sendGet(String url) {

	CloseableHttpResponse response = null;
	String result = null;
	Integer status = 0;
	try {
		HttpGet httpget = new HttpGet(url);
		response = httpclient.execute(httpget);
		HttpEntity entity = response.getEntity();
		if (entity != null) {
			result = EntityUtils.toString(entity);
		}
		status = response.getStatusLine().getStatusCode();
	} catch (ParseException | IOException e) {
		e.printStackTrace();
	} catch (IllegalArgumentException e) {
		return e.getMessage();
	} finally {
		if (response != null) {
			try {
				response.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	return status + "___" + result;
}

/**
 * 发送HttpPost请求，参数为map
 * 
 * @param url
 * @param map
 * @return
 */
public static String sendPost(String url, Map<String, String> map) {
	List<NameValuePair> formparams = new ArrayList<NameValuePair>();
	for (Map.Entry<String, String> entry : map.entrySet()) {
		formparams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
	}
	UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams, Consts.UTF_8);
	HttpPost httppost = new HttpPost(url);
	httppost.setEntity(entity);
	CloseableHttpResponse response = null;
	try {
		response = httpclient.execute(httppost);
	} catch (IOException e) {
		e.printStackTrace();
	}
	HttpEntity entity1 = response.getEntity();
	String result = null;
	try {
		result = EntityUtils.toString(entity1);
	} catch (ParseException | IOException e) {
		e.printStackTrace();
	}
	return result;
}

/**
 * 发送不带参数的HttpPost请求
 * 
 * @param url
 * @return
 */
public static String sendPost(String url) {
	HttpPost httppost = new HttpPost(url);
	CloseableHttpResponse response = null;
	try {
		response = httpclient.execute(httppost);
	} catch (IOException e) {
		e.printStackTrace();
	}
	HttpEntity entity = response.getEntity();
	String result = null;
	try {
		result = EntityUtils.toString(entity);
	} catch (ParseException | IOException e) {
		e.printStackTrace();
	}
	return result;
}

public static String httpsRequest(String requestUrl, String requestMethod, String outputStr) {
	try {
		URL url = new URL(requestUrl);
		HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
		conn.setDoOutput(true);
		conn.setDoInput(true);
		conn.setUseCaches(false);
		// 设置请求方式（GET/POST）
		conn.setRequestMethod(requestMethod);
		conn.setRequestProperty("content-type", "application/x-www-form-urlencoded");
		// 当outputStr不为null时向输出流写数据
		if (null != outputStr) {
			OutputStream outputStream = conn.getOutputStream();
			// 注意编码格式
			outputStream.write(outputStr.getBytes("UTF-8"));
			outputStream.close();
		}
		// 从输入流读取返回内容
		InputStream inputStream = conn.getInputStream();
		InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");
		BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
		String str;
		StringBuilder buffer = new StringBuilder();
		while ((str = bufferedReader.readLine()) != null) {
			buffer.append(str);
		}
		// 释放资源
		bufferedReader.close();
		inputStreamReader.close();
		inputStream.close();
		conn.disconnect();
		return buffer.toString();
	} catch (ConnectException ce) {
		System.out.println("连接超时：{}");
	} catch (Exception e) {
		System.out.println("https请求异常：{}");
	}
	return null;
}
}