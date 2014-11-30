package com.tromto.flat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class jParser {
	
	//initial variables
	static InputStream is = null;
	static JSONObject jsonObject = null;
	static String json = "";
	
	
	//constructor
	public jParser() {
	}
	
	public JSONObject makeHttpRequest (String url, List<NameValuePair> params){
		
		
		
		
		
		try {
			DefaultHttpClient httpclient = new DefaultHttpClient();
			String paramString = URLEncodedUtils.format(params, "utf-8");
			url += "?" + paramString;
			HttpGet httpGet = new HttpGet(url);
			 
		    HttpResponse httpResponse = httpclient.execute(httpGet);
	     	HttpEntity httpEntity = httpResponse.getEntity();
			is= httpEntity.getContent();
			
		
			
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
		
		try {
			BufferedReader reader = new BufferedReader (new InputStreamReader(is, "iso-8859-1"), 8);
			StringBuilder sb = new StringBuilder();
			String line = null;
			while((line = reader.readLine())!=null){
				sb.append(line+"\n");
			}
			is.close();
			json=sb.toString();
		} catch (Exception e) {
			//Log.e("Buffer Error", "Error converting result " + e.toString());
			
		}
		
		try {
			jsonObject = new JSONObject(json);
		} catch (JSONException e) {
			//Log.e("JSON Parser", "Error parsing data " + e.toString());

		}
		return jsonObject;
	}
	
	
}
