package com.gmail.at.faint545.fragments;

import java.util.ArrayList;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;
import com.gmail.at.faint545.NzoItem;
import com.gmail.at.faint545.R;
import com.gmail.at.faint545.Remote;
import com.gmail.at.faint545.adapters.QueueAdapter;
import com.gmail.at.faint545.adapters.SabAdapter;

public class QueueFragment extends SabListFragment {
  private static final String LOGTAG = "QueueFragment";
  public static final String EXTRA = "extra";

  private ArrayList<NzoItem> queueItems = new ArrayList<NzoItem>();
  private ArrayList<Boolean> checkedPositions = new ArrayList<Boolean>();
  private ListView listView;
  private QueueAdapter listAdapter;

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
    return view;
  }

  @Override
  public void onStart() {
    super.onStart();
    listView = getListView();
    listAdapter = new QueueAdapter(getActivity(), R.layout.queue_row, queueItems, checkedPositions);
    listView.setAdapter(listAdapter);    
  }

  @Override
  public void onListItemClick(ListView l, View v, int position, long id) {
    super.onListItemClick(l, v, position, id);
  }

  /*
   * Method to update the current adapters data.
   */
  @Override
  public void updateItems(ArrayList<NzoItem> items) {
    Log.i(LOGTAG,"updateItems");
    queueItems.clear();
    queueItems.addAll(items);
    checkedPositions.clear();
    
    for(int i = 0, max = queueItems.size(); i < max; i++) {
      checkedPositions.add(false);
    }
    listAdapter.notifyDataSetChanged();
  }

  @Override
  public SabAdapter getAdapter() {
    return listAdapter;
  }
  
  @Override
  public void onPause() {
    listAdapter.reset();
    super.onPause();
  }

  public void resetAdapter() {
    listAdapter.reset();
  }

  @Override
  public void inflateActionModeMenu(ActionMode mode, Menu menu) {
    mode.getMenuInflater().inflate(R.menu.action_mode_queue, menu);
  }

  @Override
  public Remote getRemote() {
    return getArguments().getParcelable(EXTRA);
  }
}