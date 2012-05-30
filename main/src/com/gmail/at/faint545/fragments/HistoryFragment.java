package com.gmail.at.faint545.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;
import com.gmail.at.faint545.NzoItem;
import com.gmail.at.faint545.R;
import com.gmail.at.faint545.R.layout;
import com.gmail.at.faint545.Remote;
import com.gmail.at.faint545.activities.HistoryDetailsActivity;
import com.gmail.at.faint545.adapters.HistoryAdapter;
import com.gmail.at.faint545.adapters.SabAdapter;
import java.util.ArrayList;

public class HistoryFragment extends SabListFragment {
  public static final String EXTRA = "extra";

  private ArrayList<NzoItem> historyItems = new ArrayList<NzoItem>();
  private ArrayList<Boolean> checkedPositions = new ArrayList<Boolean>();

  private ListView listView;
  private HistoryAdapter listAdapter;

  // Saves the scrolled position in ListView
  // and it's offset from top.
  private int scrolledPosition, offsetFromTop; 

  // Request code to view history details.
  public static final int VIEW = R.id.view__history_details >> 17;

  private static final String TAG = "HistoryFragment";

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
    return view;
  }

  @Override
  public void onStart() {
    super.onStart();
    listView = getListView();
    listAdapter = new HistoryAdapter(getActivity(), layout.history_row, historyItems,
        checkedPositions);
    listView.setAdapter(listAdapter);
  }

  @Override
  public void onListItemClick(ListView l, View v, int position, long id) {
    Activity activity = getActivity();
    Intent intent = new Intent(activity,HistoryDetailsActivity.class); 
    intent.putExtra(HistoryDetailsActivity.KEY, historyItems.get(position));
    activity.startActivityForResult(intent,VIEW);
    activity.overridePendingTransition(R.anim.slide_enter_right, R.anim.slide_exit_left);
  }

  @Override
  public void updateItems(ArrayList<NzoItem> items) {
    historyItems.clear();
    historyItems.addAll(items);

    // Re-create checked positions when new data
    // arrives.
    checkedPositions.clear();
    for(int i = 0, max = historyItems.size(); i < max; i++) {
      checkedPositions.add(false);
    }
    listAdapter.notifyDataSetChanged();
  }

  @Override
  public void resetAdapter() {
    listAdapter.reset();
  }

  @Override
  public SabAdapter getAdapter() {
    return listAdapter;
  }

  @Override
  public void onPause() {
    scrolledPosition = listView.getFirstVisiblePosition();
    View firstVisibleChild = listView.getChildAt(0);
    offsetFromTop = (firstVisibleChild == null) ? 0 : firstVisibleChild.getTop();
    listAdapter.reset();
    super.onPause();
  }

  @Override
  public void onResume() {
    listView.setSelectionFromTop(scrolledPosition, offsetFromTop);    
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
}