package com.gmail.at.faint545.services;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.AsyncTask;

import com.gmail.at.faint545.utils.ExceptionHandler;
import com.gmail.at.faint545.utils.HttpResponseParser;

public class SabTask extends AsyncTask<HttpPost, Void, String> {
  private static final String LOGTAG = "SabTask";
	
	private SabTaskListener mCallback;
	
	public interface SabTaskListener {
		public void onTaskCompleted(String results);
	}
	
	public SabTask(Object callback) {
		if(callback instanceof SabTaskListener)
			mCallback = (SabTaskListener) callback;
		else
			throw new IllegalStateException(callback.getClass().getName() + " must implement OnDownloadTaskFinished!");
	}

	@Override
	protected String doInBackground(HttpPost... params) {
		HttpClient client = new DefaultHttpClient();
		HttpPost post = params[0]; // The HttpPost to execute.
		try {
			HttpResponse response = client.execute(post);
			return HttpResponseParser.parseResponse(response);
		} 
		catch (ClientProtocolException e) {
			e.printStackTrace();
			return ExceptionHandler.translate(e);
		} 
		catch (IOException e) {
			e.printStackTrace();
			return ExceptionHandler.translate(e);
		}
	}
	
	@Override
  protected void onPostExecute(String result) {
    if(result != null)
    	mCallback.onTaskCompleted(result);
  }		
}