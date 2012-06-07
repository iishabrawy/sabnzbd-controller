package com.gmail.at.faint545.services;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.AsyncTask;

import com.gmail.at.faint545.status.ResultStatus;
import com.gmail.at.faint545.utils.ExceptionHandler;
import com.gmail.at.faint545.utils.HttpResponseParser;

public class SabTask extends AsyncTask<HttpPost, Void, ResultStatus<String>> {
	private static final String LOGTAG = "SabTask";

	private SabTaskListener mCallback;

	public interface SabTaskListener {
		public void onTaskCompleted(ResultStatus<String> status);
	}

	public SabTask(Object callback) {
		if(callback instanceof SabTaskListener)
			mCallback = (SabTaskListener) callback;
		else
			throw new IllegalStateException(callback.getClass().getName() + " must implement OnDownloadTaskFinished!");
	}

	@Override
	protected ResultStatus<String> doInBackground(HttpPost... params) {
		HttpClient client = new DefaultHttpClient();		
		HttpPost post = params[0]; // The HttpPost to execute.
		ResultStatus<String> status = new ResultStatus<String>();

		try {
			HttpResponse response = client.execute(post);
			String result = HttpResponseParser.parseResponse(response);
			if(result != null) {
				status  = new com.gmail.at.faint545.status.ResultStatus<String>();
				status.code = HttpStatus.SC_OK;
				status.data = result;
			}
			return status;
		} 
		catch (ClientProtocolException e) {
			e.printStackTrace();
			status.code = HttpStatus.SC_BAD_REQUEST;
			status.data = ExceptionHandler.translate(e);
			return status;
		} 
		catch (IOException e) {
			e.printStackTrace();
			status.code = HttpStatus.SC_SERVICE_UNAVAILABLE;
			status.data = ExceptionHandler.translate(e);
			return status;
		}
	}

	@Override
	protected void onPostExecute(ResultStatus<String> status) {
		mCallback.onTaskCompleted(status);
	}		
}