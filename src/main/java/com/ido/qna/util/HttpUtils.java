package com.ido.qna.util;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

public class HttpUtils {
	
	public static String  httpsGet(String url) throws IOException{
//		String httpsURL = "https://your.https.url.here/";
		String result= "";
	    URL myurl = new URL(url);
	    HttpsURLConnection con = (HttpsURLConnection)myurl.openConnection();
	    InputStream ins = con.getInputStream();
	    InputStreamReader isr = new InputStreamReader(ins);
	    BufferedReader in = new BufferedReader(isr);

	    String inputLine="";

	    while ((inputLine = in.readLine()) != null)
	    {
	      result+=inputLine;
	    }
	    
	    in.close();
	    
	    return result;
		
		
	}
	
	public static void main(String [] args){
	String url = "https://api.weixin.qq.com/sns/jscode2session?appid=APPID&secret=SECRET&js_code=JSCODE&grant_type=authorization_code";
	try {
		HttpUtils.httpsGet(url);
	} catch (IOException e) {
		e.printStackTrace();
	}
	}

}
