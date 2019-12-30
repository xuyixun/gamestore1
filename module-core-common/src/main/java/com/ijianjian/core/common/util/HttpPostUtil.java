package com.ijianjian.core.common.util;

import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HttpPostUtil {
public static String send(String url, Map<String, Object> params) {
	Charset charset = StandardCharsets.UTF_8;
	CloseableHttpClient httpClient = HttpClients.createDefault();
	HttpPost request = new HttpPost(url);
	System.out.println("request 　「" + request.getRequestLine() + "」");
	CloseableHttpResponse response = null;
	String responseData = null;
	try {
		if (params != null && !params.isEmpty()) {
			List<NameValuePair> requestParams = new ArrayList<>();
			params.forEach((key, value) -> requestParams.add(new BasicNameValuePair(key, String.valueOf(value))));
			UrlEncodedFormEntity u = new UrlEncodedFormEntity(requestParams, charset);
			// request.setEntity(EntityBuilder.create().setParameters(requestParams).setContentType(ContentType.create("application/x-www-form-urlencoded",
			// Consts.UTF_8)).build());
			request.setEntity(u);
		}
		response = httpClient.execute(request);
		int status = response.getStatusLine().getStatusCode();
		System.out.println("HTTP :" + status);
		if (status == HttpStatus.SC_OK) {
			responseData = EntityUtils.toString(response.getEntity(), charset);
			System.out.println(responseData);
		}
	} catch (IOException e) {
		e.printStackTrace();
	} finally {
		try {
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
	return responseData;
}

public static String post(String url, String body) {
	String responseData = null;
	Charset charset = StandardCharsets.UTF_8;
	CloseableHttpClient httpClient = null;
	HttpPost httpPost = null;
	try {
		httpClient = HttpClients.createDefault();
		RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(20000).setConnectTimeout(20000).build();
		httpPost = new HttpPost(url);
		httpPost.setConfig(requestConfig);
		httpPost.setEntity(new StringEntity(body));
		CloseableHttpResponse response = httpClient.execute(httpPost);
		HttpEntity httpEntity = response.getEntity();
		responseData= EntityUtils.toString(httpEntity, charset);
		//System.out.println(EntityUtils.toString(httpEntity, charset));
	} catch (ClientProtocolException e) {
		e.printStackTrace();
	} catch (IOException e) {
		e.printStackTrace();
	} finally {
		try {
			if (httpPost != null) {
				httpPost.releaseConnection();
			}
			if (httpClient != null) {
				httpClient.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	return responseData;
}
}
