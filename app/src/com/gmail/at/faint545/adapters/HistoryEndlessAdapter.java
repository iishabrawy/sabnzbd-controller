package com.gmail.at.faint545.adapters;

import android.content.Context;
import android.util.Log;
import android.widget.ListAdapter;
import com.commonsware.cwac.endless.EndlessAdapter;

public class HistoryEndlessAdapter extends EndlessAdapter {

  private static final String LOGTAG = "HistoryEndlessAdapter";
  
  public HistoryEndlessAdapter(Context context, ListAdapter wrapped,
      int pendingResource) {
    super(context, wrapped, pendingResource);
    Log.i(LOGTAG,"Constructor");
  }

  public HistoryEndlessAdapter(ListAdapter wrapped) {
    super(wrapped);
    Log.i(LOGTAG,"Constructor");
  }

  @Override
  protected boolean cacheInBackground() throws Exception {
    Log.i(LOGTAG,"cacheInBackground");
    return true;
  }

  @Override
  protected void appendCachedData() {
    Log.i(LOGTAG,"appendCachedData");
  }
}
