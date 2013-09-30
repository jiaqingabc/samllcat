package com.zjq.datasync.tools;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;

public class MyHttpClient {
	
	private HttpClient client = null;

	public MyHttpClient() {
		HttpParams params = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(params, 20*1000);
		params.setParameter("charset", HTTP.UTF_8);
		this.client = new DefaultHttpClient(params);
	}
	
	public void doGet(){
		
		
		
	}
	
	public HttpClient getHttpClient(){
		return this.client;
	}
	
	public HttpResponse doPost(URI uri, List<NameValuePair> params){
		HttpPost post = new HttpPost(uri);
		HttpResponse response = null;
		
		try {
			
			post.setEntity(new UrlEncodedFormEntity(params));
			response = this.client.execute(post);
			
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return response;
	}
}
