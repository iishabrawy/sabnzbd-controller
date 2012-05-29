package com.gmail.at.faint545.activities;

import org.json.JSONException;
import org.json.JSONObject;

import com.gmail.at.faint545.services.DownloadService;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class RemoteMessageHandler extends Handler {

	private RemoteMessageListener mCallback;

	public interface RemoteMessageListener {
		/**
		 * This method is called when the DownloadService completed the download for the history.
		 * @param result The history results as a JSONObject.
		 */
		public void onReceiveHistory(JSONObject result);

		/**
		 * This method is called when the DownloadService completed the queue for the history.
		 * @param result The queue results as a JSONObject.
		 */
		public void onReceiveQueue(JSONObject result);
		
		/**
		 * This method is called when a connection to the SABNzbd application has failed.
		 * @param message The error message to be shown.
		 */
		public void onDownloadFailure(String message);
	}

	public RemoteMessageHandler(Context context) {
		if(context instanceof RemoteMessageListener) {
			mCallback = (RemoteMessageListener) context;
		}
		else {
			throw new IllegalStateException("You must implement RemoteMessageListener!");
		}
	}

	@Override
	public void handleMessage(Message msg) {
		Bundle bundle = msg.getData();
		String result = bundle.getString("message");

		switch(msg.what) {
		
		case DownloadService.MSG_SUCCESS:
			try {
				JSONObject resultObject = new JSONObject(result);
				if(resultObject.has("history")) { // It's history results
					mCallback.onReceiveHistory(resultObject);
				}
				else if(resultObject.has("queue")) { // It's queue results
					mCallback.onReceiveQueue(resultObject);
				}
			} 
			catch (JSONException e) {
				e.printStackTrace();
			}
			break;
			
		case DownloadService.MSG_FAILURE:
			mCallback.onDownloadFailure(result);
			break;
		
		}
	}
}	