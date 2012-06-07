package com.gmail.at.faint545.services;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

import com.gmail.at.faint545.Remote;
import com.gmail.at.faint545.activities.ViewRemoteActivity;
import com.gmail.at.faint545.connectors.RequestHandler.Request;
import com.gmail.at.faint545.connectors.RequestHandler.RequestListener;
import com.gmail.at.faint545.connectors.RequestRouter;
import com.gmail.at.faint545.nzo.HistoryItem;
import com.gmail.at.faint545.nzo.NzoItem;
import com.gmail.at.faint545.nzo.QueueItem;

public class RequestReceiver extends Service implements RequestListener {

  private Messenger mMessenger;
  private List<Messenger> mClientList = new ArrayList<Messenger>();
  private RequestRouter mRequestRouter;

  public static final int UNREGISTER_CLIENT = -1;
  public static final int REGISTER_CLIENT = 1;

  private static final String LOGTAG = "RequestReceiver";

  public RequestReceiver() {
    super();
    IncomingHandler handler = new IncomingHandler();
    mMessenger = new Messenger(handler);
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    mRequestRouter.processRequest(Request.DOWNLOAD, null);
    return START_STICKY;
  }

  @Override
  public IBinder onBind(Intent intent) {
    return mMessenger.getBinder();
  }

  private void sendMessage(Message message) {
    Iterator<Messenger> client = mClientList.iterator();
    while(client.hasNext()) {
      Messenger messenger = client.next();
      try {
        messenger.send(message);
      }
      catch (RemoteException e) {
        mClientList.remove(messenger);
      }
    }
  }

  private class IncomingHandler extends Handler {
    @Override
    public void handleMessage(Message msg) {

      switch(msg.what) {
      case REGISTER_CLIENT:
        Remote remote = (Remote) msg.obj;
        if(remote == null)
          throw new NullPointerException("Couldn't get a Remote when registering using Message.obj.");
        mClientList.add(msg.replyTo);
        mRequestRouter = new RequestRouter(remote,RequestReceiver.this);
        break;
      case UNREGISTER_CLIENT:
        mClientList.remove(msg.replyTo);
        break;
      default:
        mRequestRouter.processRequest(Request.fromInt(msg.what), msg.getData());
        break;
      }
      
    }
  }

  @Override
  public void onQueueReceived(JSONArray queue) {
    List<NzoItem> items = new ArrayList<NzoItem>();
    for(int i = 0, max = queue.length(); i < max; i++) {
      try {        
        items.add(new QueueItem().buildFromJson(queue.getJSONObject(i)));
      } 
      catch (JSONException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
    
    Message m = Message.obtain();
    m.obj = items;
    m.what = ViewRemoteActivity.QUEUE;
    sendMessage(m);
  }

  @Override
  public void onHistoryReceived(JSONArray queue) {
    List<NzoItem> items = new ArrayList<NzoItem>();
    for(int i = 0, max = queue.length(); i < max; i++) {
      try {        
        items.add(new HistoryItem().buildFromJson(queue.getJSONObject(i)));
      } 
      catch (JSONException e) {
        e.printStackTrace();
      }
    }
    
    Message m = Message.obtain();
    m.obj = items;
    m.what = ViewRemoteActivity.HISTORY;
    sendMessage(m);
  }

  @Override
  public void onStatusReceived(boolean status) {
    Message m = Message.obtain();
    m.obj = status;
    m.what = ViewRemoteActivity.STATUS;
    sendMessage(m);
  }

	@Override
  public void onErrorReceived(String message) {
		Message m = Message.obtain();
		m.obj = message;
		m.what = ViewRemoteActivity.ERROR;
		sendMessage(m);
  }
}