package com.gmail.at.faint545.services;

import android.app.Service;
import android.content.Intent;
import android.os.*;
import com.gmail.at.faint545.services.SabTask.OnDownloadTaskFinished;
import org.apache.http.client.methods.HttpPost;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DownloadService extends Service implements OnDownloadTaskFinished {

  private Messenger mMessenger;
  private List<Messenger> mClientList = new ArrayList<Messenger>();
  private boolean mIncomplete = false; // A flag to control how many times we should send a message if incomplete.

  public static final int UNREGISTER_CLIENT = -1;
  public static final int REGISTER_CLIENT = 1;
  public static final int SUCCESS = 2;
  public static final int FAILURE = 3;

  // Actions supported by the SabNzbd API.
  public static final int ACTION_PAUSE = 4;
  public static final int ACTION_DELETE = 5;
  public static final int ACTION_RESUME = 6;
  public static final int ACTION_SET_SPEEDLIMIT = 7;

  private static final String LOGTAG = "DownloadService";

  public DownloadService() {
    super();
    IncomingMessageHandler handler = new IncomingMessageHandler();
    mMessenger = new Messenger(handler);
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    Bundle extras = intent.getExtras();
    HttpPost queuePost = SabPostFactory.getQueueInstance(extras);
    HttpPost historyPost = SabPostFactory.getHistoryInstance(extras);

    new SabTask(this).execute(queuePost);
    new SabTask(this).execute(historyPost);
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
    sendMessage(result, SUCCESS);
  }

  @Override
  public void onIncomplete() {
    if(!mIncomplete) {
      mIncomplete = true;
      sendMessage("Could not connect to SABNzbd. Please check your settings.", FAILURE);
    }
  }

  private class IncomingMessageHandler extends Handler {
    @Override
    public void handleMessage(Message msg) {
      switch(msg.what) {
        case REGISTER_CLIENT:
          mClientList.add(msg.replyTo);
          break;
        case UNREGISTER_CLIENT:
          mClientList.remove(msg.replyTo);
          break;
        case ACTION_PAUSE:
          HttpPost post = SabPostFactory.getPauseInstance(msg.getData());
          new SabTask(DownloadService.this).execute(post);
          break;
        case ACTION_DELETE:
          post = SabPostFactory.getDeleteInstance(msg.getData());
          new SabTask(DownloadService.this).execute(post);
          break;
        case ACTION_RESUME:
          post = SabPostFactory.getResumeInstance(msg.getData());
          new SabTask(DownloadService.this).execute(post);
          break;
        case ACTION_SET_SPEEDLIMIT:
          post = SabPostFactory.getSpeedLimitInstance(msg.getData());
          new SabTask(DownloadService.this).execute(post);
          break;
      }
    }
  }
}