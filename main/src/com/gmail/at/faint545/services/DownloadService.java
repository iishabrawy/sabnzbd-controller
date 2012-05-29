package com.gmail.at.faint545.services;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.http.client.methods.HttpPost;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

import com.gmail.at.faint545.services.DownloadTask.OnDownloadTaskFinished;

public class DownloadService extends Service implements OnDownloadTaskFinished {

	private Messenger mMessenger;
	private List<Messenger> mClientList = new ArrayList<Messenger>();
	private boolean mIncomplete = false; // A flag to control how many times we should send a message if incomplete.
	
	public static final int MSG_UNREGISTER_CLIENT = -1;
	public static final int MSG_REGISTER_CLIENT = 1;
	public static final int MSG_SUCCESS = 2;
	public static final int MSG_FAILURE = 3;
	
	private static final String LOGTAG = "DownloadService";

	public DownloadService() {
		super();
		IncomingMessageHandler handler = new IncomingMessageHandler(mClientList);
		mMessenger = new Messenger(handler);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Bundle extras = intent.getExtras();
		HttpPost queuePost = SabCmdFactory.getQueueUrl(extras);
		HttpPost historyPost = SabCmdFactory.getHistoryUrl(extras);
		
		new DownloadTask(this).execute(queuePost);
		new DownloadTask(this).execute(historyPost);
		return START_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mMessenger.getBinder();
	}
	
	private void sendMessage(String result,int what) {
		Iterator<Messenger> messengerIterator = mClientList.iterator();
		while(messengerIterator.hasNext()) {
			Messenger messenger = messengerIterator.next();
			try {
				Bundle bundle = new Bundle();
				bundle.putString("message", result);
				Message message = Message.obtain();
				message.what = what;
				message.setData(bundle);
				messenger.send(message);
			}
			catch (RemoteException e) {
				mClientList.remove(messenger);
			}
		}
	}
	
	@Override
  public void onComplete(String result) {
		sendMessage(result,MSG_SUCCESS);
  }
	
	@Override
  public void onIncomplete() {
		if(!mIncomplete) {
			mIncomplete = true;
			sendMessage("Could not connect to SABNzbd. Please check your settings.",MSG_FAILURE);			
		}		
  }	

	private static class IncomingMessageHandler extends Handler {

		private List<Messenger> mClients;

		public IncomingMessageHandler(List<Messenger> clients) {
			mClients = clients;
		}

		@Override
		public void handleMessage(Message msg) {
			switch(msg.what) {
			case MSG_REGISTER_CLIENT:
				mClients.add(msg.replyTo);				
				break;
			case MSG_UNREGISTER_CLIENT:
				mClients.remove(msg.replyTo);
				break;
			}
		}
	}
}