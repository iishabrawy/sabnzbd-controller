package com.gmail.at.faint545.fragments;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;
import com.commonsware.cwac.endless.EndlessAdapter;
import com.gmail.at.faint545.HistoryItem;
import com.gmail.at.faint545.NzoItem;
import com.gmail.at.faint545.R;
import com.gmail.at.faint545.R.layout;
import com.gmail.at.faint545.Remote;
import com.gmail.at.faint545.activities.HistoryDetailsActivity;
import com.gmail.at.faint545.adapters.HistoryAdapter;
import com.gmail.at.faint545.adapters.SabAdapter;
import com.gmail.at.faint545.services.SabPostFactory;
import com.gmail.at.faint545.utils.HttpResponseParser;

public class HistoryFragment extends SabListFragment {
  private static final String LOGTAG = "HistoryFragment";
  public static final String EXTRA = "extra";

  private ArrayList<Boolean> checkedPositions = new ArrayList<Boolean>();

  private ListView mListView;
  private HistoryEndlessAdapter mEndlessListAdapter;

  // Saves the scrolled position in ListView
  // and it's offset from top.
  private int scrolledPosition, offsetFromTop; 

  // Request code to view history details.
  public static final int VIEW = R.id.view__history_details >> 17;

  private int mEndlessAdapterOffset = 0;

  private HistoryFragment() {}

  public static HistoryFragment getInstance(Remote remote) {    
    HistoryFragment fragment = new HistoryFragment();
    Bundle args = new Bundle();
    args.putParcelable(EXTRA, remote);
    fragment.setArguments(args);
    return fragment;
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(layout.history, null);
    mListView = (ListView) view.findViewById(android.R.id.list);
    return view;
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    SabAdapter mListAdapter = new HistoryAdapter(getActivity(), layout.history_row, new ArrayList<NzoItem>(),
        checkedPositions);
    mEndlessListAdapter = new HistoryEndlessAdapter(getActivity(),mListAdapter,R.layout.endless_row);
    mListView.setAdapter(mEndlessListAdapter);
    super.onActivityCreated(savedInstanceState);
  }

  @Override
  public void onListItemClick(ListView l, View v, int position, long id) {
    Activity activity = getActivity();
    Intent intent = new Intent(activity,HistoryDetailsActivity.class); 
    intent.putExtra(HistoryDetailsActivity.KEY, (NzoItem) getListAdapter().getItem(position));
    activity.startActivityForResult(intent,VIEW);
    activity.overridePendingTransition(R.anim.slide_enter_right, R.anim.slide_exit_left);
  }

  @Override
  public void updateItems(ArrayList<NzoItem> items) {
    getListAdapter().clearData();
    mEndlessAdapterOffset = 0; // Reset the offset back to zero.
    mEndlessListAdapter.keepOnAppending.set(true);
    // Re-create checked positions when new data
    // arrives.
    checkedPositions.clear();
    for(int i = 0, max = getListAdapter().getCount(); i < max; i++) {
      checkedPositions.add(false);
    }

    getListAdapter().notifyDataSetChanged();
  }

  @Override
  public void resetAdapter() {
    getListAdapter().reset();
  }

  @Override
  public void onPause() {
    scrolledPosition = mListView.getFirstVisiblePosition();
    View firstVisibleChild = mListView.getChildAt(0);
    offsetFromTop = (firstVisibleChild == null) ? 0 : firstVisibleChild.getTop();
    resetAdapter();
    super.onPause();
  }

  @Override
  public void onResume() {
    mListView.setSelectionFromTop(scrolledPosition, offsetFromTop);    
    super.onResume();
  }

  @Override
  public void inflateActionModeMenu(ActionMode mode, Menu menu) {
    return;
  }

  @Override
  public Remote getRemote() {
    return getArguments().getParcelable(EXTRA);
  }

  public class HistoryEndlessAdapter extends EndlessAdapter {

    private static final String LOGTAG = "HistoryEndlessAdapter";
    private List<NzoItem> historyCache = new ArrayList<NzoItem>();

    public HistoryEndlessAdapter(Context context, ListAdapter wrapped,
        int pendingResource) {
      super(context, wrapped, pendingResource);
    }

    public HistoryEndlessAdapter(ListAdapter wrapped) {
      super(wrapped);
    }

    @Override
    protected boolean cacheInBackground() throws Exception {
      // Execute the request.      
      HttpClient client = new DefaultHttpClient();
      HttpPost post = SabPostFactory.getHistoryInstance(getRemote(),mEndlessAdapterOffset);

      // We add 11 because we only show 10 items at a time, and next time,
      // we want to to begin loading from +1 where we left off. 
      mEndlessAdapterOffset += 11; 

      // Get the response/result.
      HttpResponse response = client.execute(post);
      String result = HttpResponseParser.parseResponse(response);

      JSONObject object = new JSONObject(result);
      JSONArray slots = object.getJSONObject("history").getJSONArray("slots");

      for(int i = 0, max = slots.length(); i < max; i++) {
        historyCache.add(new HistoryItem().buildFromJson(slots.getJSONObject(i)));
      }
      return historyCache.size() > 0;
    }

    @Override
    protected void appendCachedData() {
      SabAdapter adapter = (SabAdapter) getWrappedAdapter();
      for(NzoItem item : historyCache) {
        adapter.add(item);
      }
      historyCache.clear();
      adapter.notifyDataSetChanged();
    }
  }

  @Override
  public SabAdapter getListAdapter() {    
    return (SabAdapter) mEndlessListAdapter.getWrappedAdapter();
  }
}