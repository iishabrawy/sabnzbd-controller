package com.gmail.at.faint545.services;

import android.os.Bundle;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class SabPostFactory {

	/**
	 * Creates the proper HttPost to send to SABNzbd with
	 * user specified arguments.
	 * @param message
	 */
	public static HttpPost getQueueInstance(Bundle message) {
		HttpPost post = new HttpPost(message.getString("url")+"/api?");

		try {
			List<NameValuePair> parameters = buildBaseParams(message);
			parameters.add(new BasicNameValuePair("mode", "queue"));
			post.setEntity(new UrlEncodedFormEntity(parameters));
		}
		catch(UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return post;

	}

	public static HttpPost getHistoryInstance(Bundle message) {
		HttpPost post = new HttpPost(message.getString("url")+"/api?");

		try {
			List<NameValuePair> parameters = buildBaseParams(message);
			parameters.add(new BasicNameValuePair("mode", "history"));
			post.setEntity(new UrlEncodedFormEntity(parameters));
		}
		catch(UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return post;
	}

  public static HttpPost getPauseInstance(Bundle extras) {
    HttpPost post = new HttpPost(extras.getString("url")+"/api?");

    try {
      List<NameValuePair> parameters = buildBaseParams(extras);
      parameters.add(new BasicNameValuePair("mode", "queue"));
      parameters.add(new BasicNameValuePair("name", "pause"));
      parameters.add(new BasicNameValuePair("value", extras.getString("value")));
      post.setEntity(new UrlEncodedFormEntity(parameters));
    }
    catch(UnsupportedEncodingException e) {
      e.printStackTrace();
    }

    return post;
  }

  public static HttpPost getResumeInstance(Bundle extras) {
    HttpPost post = new HttpPost(extras.getString("url")+"/api?");

    try {
      List<NameValuePair> parameters = buildBaseParams(extras);
      parameters.add(new BasicNameValuePair("mode", "queue"));
      parameters.add(new BasicNameValuePair("name", "resume"));
      parameters.add(new BasicNameValuePair("value", extras.getString("value")));
      post.setEntity(new UrlEncodedFormEntity(parameters));
    }
    catch(UnsupportedEncodingException e) {
      e.printStackTrace();
    }

    return post;
  }

  /**
   * Generates the correct HttpPost to execute.
   * Required additional arguments:
   * <ul>
   *   <li>name - Name of the method to run. This will be <b>delete</b></li>
   *   <li>mode - The mode to operate in. This will be either <b>history</b> or <b>queue</b></li>
   *   <li>value - The NZO ids of selected items</li>
   * </ul>
   * @param message User specified arguments.
   * @return An HttpPost that is executable by an HttpClient.
   */
  public static HttpPost getDeleteInstance(Bundle message) {
    HttpPost post = new HttpPost(message.getString("url")+"/api?");

    try {
      List<NameValuePair> parameters = buildBaseParams(message);
      parameters.add(new BasicNameValuePair("name", "delete"));
      parameters.add(new BasicNameValuePair("mode",message.getString("mode")));
      parameters.add(new BasicNameValuePair("value", message.getString("value")));
      post.setEntity(new UrlEncodedFormEntity(parameters));
    }
    catch(UnsupportedEncodingException e) {
      e.printStackTrace();
    }

    return post;
  }

  public static HttpPost getSpeedLimitInstance(Bundle message) {
    HttpPost post = new HttpPost(message.getString("url")+"/api?");

    try {
      List<NameValuePair> parameters = buildBaseParams(message);
      parameters.add(new BasicNameValuePair("name", "speedlimit"));
      parameters.add(new BasicNameValuePair("mode","config"));
      parameters.add(new BasicNameValuePair("value", message.getString("value")));
      post.setEntity(new UrlEncodedFormEntity(parameters));
    }
    catch(UnsupportedEncodingException e) {
      e.printStackTrace();
    }

    return post;
  }

  /**
   * Builds the set of common parameters that will
   * be used by all commands.
   * @param message
   * @return A list of NameValuePairs.
   */
  private static List<NameValuePair> buildBaseParams(Bundle message) {
    List<NameValuePair> parameters = new ArrayList<NameValuePair>();
    parameters.add(new BasicNameValuePair("output", "json"));
    parameters.add(new BasicNameValuePair("apikey", message.getString("apikey")));
    parameters.add(new BasicNameValuePair("username", message.getString("username")));
    parameters.add(new BasicNameValuePair("password", message.getString("password")));
    return parameters;
  }
}