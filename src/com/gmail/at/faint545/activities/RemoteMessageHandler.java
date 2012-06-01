package com.gmail.at.faint545.activities;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import com.gmail.at.faint545.services.DownloadService;
import org.json.JSONException;
import org.json.JSONObject;

public class RemoteMessageHandler extends Handler {
  private static final String LOGTAG = "RemoteMessageHandler";

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

    public void onStatusReceived(boolean status, String errorMsg);
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
		
		case DownloadService.SUCCESS:
			try {
				JSONObject resultObject = new JSONObject(result);
				if(resultObject.has("history")) { // It's history results
					mCallback.onReceiveHistory(resultObject);
				}
				else if(resultObject.has("queue")) { // It's queue results
					mCallback.onReceiveQueue(resultObject);
				}
        else if(resultObject.has("status")) { // It's an action result
          boolean status = resultObject.getBoolean("status");
          String error = resultObject.has("error") ? resultObject.getString("error") : null;
          mCallback.onStatusReceived(status,error);
        }
			} 
			catch (JSONException e) {
				e.printStackTrace();
			}
			break;
			
		case DownloadService.FAILURE:
			mCallback.onDownloadFailure(result);
			break;
		
		}
	}
}	