package com.gmail.at.faint545.services;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;

import android.os.Bundle;

public class SabCmdFactory {

	/**
	 * Creates the proper command to send to SABNzbd with
	 * user specified arguments.
	 * @param bundle
	 */
	public static HttpPost getQueueUrl(Bundle extras) {
		HttpPost postr = new HttpPost(extras.getString("url")+"/api?");

		try {
			List<NameValuePair> parameters = new ArrayList<NameValuePair>();
			parameters.add(new BasicNameValuePair("mode", "queue"));
			parameters.add(new BasicNameValuePair("output", "json"));
			parameters.add(new BasicNameValuePair("apikey", extras.getString("apikey")));
			parameters.add(new BasicNameValuePair("username", extras.getString("username")));
			parameters.add(new BasicNameValuePair("password", extras.getString("password")));
			postr.setEntity(new UrlEncodedFormEntity(parameters));
		}
		catch(UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return postr;

	}

	public static HttpPost getHistoryUrl(Bundle extras) {
		HttpPost postr = new HttpPost(extras.getString("url")+"/api?");

		try {
			List<NameValuePair> parameters = new ArrayList<NameValuePair>();
			parameters.add(new BasicNameValuePair("mode", "history"));
			parameters.add(new BasicNameValuePair("output", "json"));
			parameters.add(new BasicNameValuePair("apikey", extras.getString("apikey")));
			parameters.add(new BasicNameValuePair("username", extras.getString("username")));
			parameters.add(new BasicNameValuePair("password", extras.getString("password")));
			postr.setEntity(new UrlEncodedFormEntity(parameters));
		}
		catch(UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return postr;
	}
}
