package com.gmail.at.faint545.services;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;

import com.gmail.at.faint545.Remote;

public class SabPostFactory {

	/**
	 * Creates the proper HttPost to send to SABNzbd with
	 * user specified arguments.
	 * @param message
	 */
	public static HttpPost getQueueInstance(Remote remote) {
		HttpPost post = new HttpPost(remote.getUrl()+"/api?");

		try {
			List<NameValuePair> parameters = buildBaseParams(remote);
			parameters.add(new BasicNameValuePair("mode", "queue"));
			post.setEntity(new UrlEncodedFormEntity(parameters));
		}
		catch(UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return post;

	}

	public static HttpPost getHistoryInstance(Remote remote,int offset) {
	  HttpPost post = new HttpPost(remote.getUrl()+"/api?");

		try {
			List<NameValuePair> parameters = buildBaseParams(remote);
			parameters.add(new BasicNameValuePair("mode", "history"));
			parameters.add(new BasicNameValuePair("start", String.valueOf(offset)));
			post.setEntity(new UrlEncodedFormEntity(parameters));
		}
		catch(UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return post;
	}

  public static HttpPost getPauseInstance(Remote remote, String value) {
    HttpPost post = new HttpPost(remote.getUrl()+"/api?");

    try {
      List<NameValuePair> parameters = buildBaseParams(remote);
      parameters.add(new BasicNameValuePair("mode", "queue"));
      parameters.add(new BasicNameValuePair("name", "pause"));
      parameters.add(new BasicNameValuePair("value", value));
      post.setEntity(new UrlEncodedFormEntity(parameters));
    }
    catch(UnsupportedEncodingException e) {
      e.printStackTrace();
    }

    return post;
  }

  public static HttpPost getResumeInstance(Remote remote, String value) {
    HttpPost post = new HttpPost(remote.getUrl()+"/api?");

    try {
      List<NameValuePair> parameters = buildBaseParams(remote);
      parameters.add(new BasicNameValuePair("mode", "queue"));
      parameters.add(new BasicNameValuePair("name", "resume"));
      parameters.add(new BasicNameValuePair("value", value));
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
  public static HttpPost getDeleteInstance(Remote remote,String mode,String value) {
    HttpPost post = new HttpPost(remote.getUrl()+"/api?");

    try {
      List<NameValuePair> parameters = buildBaseParams(remote);
      parameters.add(new BasicNameValuePair("name", "delete"));
      parameters.add(new BasicNameValuePair("mode", mode));
      parameters.add(new BasicNameValuePair("value", value));
      post.setEntity(new UrlEncodedFormEntity(parameters));
    }
    catch(UnsupportedEncodingException e) {
      e.printStackTrace();
    }

    return post;
  }

  public static HttpPost getSpeedLimitInstance(Remote remote, String value) {
    HttpPost post = new HttpPost(remote.getUrl()+"/api?");

    try {
      List<NameValuePair> parameters = buildBaseParams(remote);
      parameters.add(new BasicNameValuePair("name", "speedlimit"));
      parameters.add(new BasicNameValuePair("mode","config"));
      parameters.add(new BasicNameValuePair("value", value));
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
  private static List<NameValuePair> buildBaseParams(Remote remote) {
    List<NameValuePair> parameters = new ArrayList<NameValuePair>();
    parameters.add(new BasicNameValuePair("output", "json"));
    parameters.add(new BasicNameValuePair("apikey", remote.getAPIKey()));
    parameters.add(new BasicNameValuePair("username", remote.getUsername()));
    parameters.add(new BasicNameValuePair("password", remote.getPassword()));
    parameters.add(new BasicNameValuePair("limit", "10"));
    return parameters;
  }
}