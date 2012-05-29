package com.gmail.at.faint545.services;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Service;
import android.os.AsyncTask;

import com.gmail.at.faint545.utils.HttpResponseParser;

public class DownloadTask extends AsyncTask<HttpPost, Void, String> {
	
	private OnDownloadTaskFinished mCallback;
	
	public interface OnDownloadTaskFinished {
		public void onComplete(String results);
		public void onIncomplete();
	}
	
	public DownloadTask(Service service) {
		if(service instanceof OnDownloadTaskFinished)
			mCallback = (OnDownloadTaskFinished) service;
		else
			throw new IllegalStateException("You must implement OnDownloadTaskFinished!");
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
			return null;
		} 
		catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	@Override
  protected void onPostExecute(String result) {		
    if(result != null)
    	mCallback.onComplete(result);
    else
    	mCallback.onIncomplete();
  }		
}