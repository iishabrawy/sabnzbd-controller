package com.gmail.at.faint545.fragments;

import com.actionbarsherlock.app.SherlockListFragment;
import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;
import com.gmail.at.faint545.Remote;
import com.gmail.at.faint545.adapters.SabAdapter;
import com.gmail.at.faint545.nzo.NzoItem;

import java.util.ArrayList;

public abstract class SabListFragment extends SherlockListFragment {

  public abstract void updateItems(ArrayList<NzoItem> items);
  public abstract void resetAdapter();
  public abstract void inflateActionModeMenu(ActionMode mode, Menu menu);
  public abstract Remote getRemote();

  /**
   * Sets all items in the ListView to be checked.
   * @param value
   */
  public void setAllChecked(boolean value) {
    getListAdapter().setAllChecked(value);
  }

  public String getCheckedIds() {
    return getListAdapter().getCheckedIds();
  }
 
  public SabFragmentActivity getSabActivity() {
    return (SabFragmentActivity) getActivity();
  }
  
  public abstract SabAdapter getListAdapter();
}
