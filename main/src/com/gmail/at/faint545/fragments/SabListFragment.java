package com.gmail.at.faint545.fragments;

import java.util.ArrayList;

import com.actionbarsherlock.app.SherlockListFragment;
import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;
import com.gmail.at.faint545.NzoItem;
import com.gmail.at.faint545.Remote;
import com.gmail.at.faint545.adapters.SabAdapter;

public abstract class SabListFragment extends SherlockListFragment {

  public abstract void updateItems(ArrayList<NzoItem> items);
  public abstract SabAdapter getAdapter();
  public abstract void resetAdapter();
  public abstract void inflateActionModeMenu(ActionMode mode, Menu menu);
  public abstract Remote getRemote();
}
