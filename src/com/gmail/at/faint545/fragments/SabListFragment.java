package com.gmail.at.faint545.fragments;

import java.util.List;

import com.actionbarsherlock.app.SherlockListFragment;
import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;
import com.gmail.at.faint545.Remote;
import com.gmail.at.faint545.activities.SabFragmentActivity;
import com.gmail.at.faint545.adapters.SabAdapter;
import com.gmail.at.faint545.nzo.NzoItem;

public abstract class SabListFragment extends SherlockListFragment {

  public abstract void updateItems(List<NzoItem> items);
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
