package com.gmail.at.faint545.fragments;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;
import com.commonsware.cwac.endless.EndlessAdapter;
import com.gmail.at.faint545.R;
import com.gmail.at.faint545.Remote;
import com.gmail.at.faint545.adapters.QueueAdapter;
import com.gmail.at.faint545.adapters.SabAdapter;
import com.gmail.at.faint545.factories.SabPostFactory;
import com.gmail.at.faint545.nzo.NzoItem;
import com.gmail.at.faint545.nzo.QueueItem;
import com.gmail.at.faint545.utils.HttpResponseParser;

public class QueueFragment extends SabListFragment {
  private static final String LOGTAG = "QueueFragment";
  public static final String EXTRA = "extra";

  private ArrayList<NzoItem> queueItems = new ArrayList<NzoItem>();
  private ArrayList<Boolean> checkedPositions = new ArrayList<Boolean>();
  private ListView mListView;
  private QueueEndlessAdapter mEndlessListAdapter;

  private Messenger mMessenger = new Messenger(new Handler() {

    @Override
    public void handleMessage(Message msg) {
      switch(msg.what) {
      case R.id.no_connection:        
        setNoConnectionVisibility(View.VISIBLE);
        break;
      }
      super.handleMessage(msg);
    }

  });

  private QueueFragment() {}

  public static QueueFragment getInstance(Remote remote) {
    QueueFragment fragment = new QueueFragment();
    Bundle args = new Bundle();
    args.putParcelable(EXTRA, remote);
    fragment.setArguments(args);
    return fragment;
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, 
      Bundle savedInstanceState) {    
    View view = inflater.inflate(R.layout.queue, null);
    mListView = (ListView) view.findViewById(android.R.id.list);
    return view;
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    SabAdapter listAdapter = new QueueAdapter(getActivity(), R.layout.queue_row, queueItems, checkedPositions);
    mEndlessListAdapter = new QueueEndlessAdapter(getActivity(), listAdapter, R.layout.endless_row);
    mListView.setAdapter(mEndlessListAdapter);
    super.onActivityCreated(savedInstanceState);
  }

  @Override
  public void onListItemClick(ListView l, View v, int position, long id) {
    super.onListItemClick(l, v, position, id);
  }

  /*
   * Method to update the current adapters data.
   */
  @Override
  public void updateItems(List<NzoItem> items) {
    getListAdapter().clearData();
    getListAdapter().addAll(items);
    
    checkedPositions.clear();
    for(int i = 0, max = queueItems.size(); i < max; i++) {
      checkedPositions.add(false);
    }
    
    setNoConnectionVisibility(View.GONE);
    mEndlessListAdapter.reset();
    getListAdapter().notifyDataSetChanged();
  }

  @Override
  public SabAdapter getListAdapter() {
    return (SabAdapter) mEndlessListAdapter.getWrappedAdapter();
  }

  @Override
  public void onPause() {
    mEndlessListAdapter.reset();
    super.onPause();
  }

  public void resetAdapter() {
    getListAdapter().reset();
  }

  @Override
  public void inflateActionModeMenu(ActionMode mode, Menu menu) {
    mode.getMenuInflater().inflate(R.menu.action_mode_queue, menu);
  }

  @Override
  public Remote getRemote() {
    return getArguments().getParcelable(EXTRA);
  }

  private void setNoConnectionVisibility(int visibility) {
    View view = getView().findViewById(R.id.no_connection_stub);
    if(view != null) {
      view.setVisibility(visibility);
    }
  }

  private class QueueEndlessAdapter extends EndlessAdapter {

    private static final String LOGTAG = "HistoryEndlessAdapter";
    private List<NzoItem> cache = new ArrayList<NzoItem>();
    private int mOffset = 0;

    public QueueEndlessAdapter(Context context, ListAdapter wrapped,
        int pendingResource) {
      super(context, wrapped, pendingResource);
    }

    public QueueEndlessAdapter(ListAdapter wrapped) {
      super(wrapped);
    }

    @Override
    protected boolean cacheInBackground() {
      // Execute the request.      
      HttpClient client = new DefaultHttpClient();
      HttpPost post = SabPostFactory.getQueueInstance(getRemote(),mOffset);

      // We add 11 because we only show 10 items at a time, and next time,
      // we want to to begin loading from +1 where we left off. 
      mOffset += 11; 

      try {
        // Get the response/result.
        HttpResponse response = client.execute(post);
        String result = HttpResponseParser.parseResponse(response);

        JSONObject object = new JSONObject(result);
        if(object.has("queue")) {
          JSONArray slots = object.getJSONObject("queue").getJSONArray("slots");

          for(int i = 0, max = slots.length(); i < max; i++) {
            cache.add(new QueueItem().buildFromJson(slots.getJSONObject(i)));
          }
          return cache.size() > 0;
        }
        else {
          return false;
        }
      }
      catch(JSONException e) {
        e.printStackTrace();
      } 
      catch (ClientProtocolException e) {
        e.printStackTrace();
      } 
      catch (IOException e) {
        try {
          Message m = Message.obtain();
          m.what = R.id.no_connection;
          mMessenger.send(m);
        } 
        catch (RemoteException re) {
          re.printStackTrace();
        }
        e.printStackTrace();
      }
      return false;
    }

    @Override
    protected void appendCachedData() {
      SabAdapter adapter = (SabAdapter) getWrappedAdapter();
      for(NzoItem item : cache) {
        adapter.add(item);
      }
      cache.clear();
      adapter.notifyDataSetChanged();
    }

    public void reset() {
      mOffset = 0;
      super.reset();
    }
  }
}