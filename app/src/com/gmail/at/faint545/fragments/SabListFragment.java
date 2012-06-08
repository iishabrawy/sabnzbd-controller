package com.gmail.at.faint545.fragments;

import java.util.List;

import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;

import com.actionbarsherlock.app.SherlockListFragment;
import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;
import com.gmail.at.faint545.R;
import com.gmail.at.faint545.Remote;
import com.gmail.at.faint545.activities.SabFragmentActivity;
import com.gmail.at.faint545.adapters.SabAdapter;
import com.gmail.at.faint545.nzo.NzoItem;

public abstract class SabListFragment extends SherlockListFragment implements OnScrollListener {

	private View mNoConnectionStub;
	private boolean mIsConnectionUp;
	private boolean mListIsScrolling;
	
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

	/**
	 * Gets all the checked items in the ListView.
	 * @return A comma separated string of all checked items.
	 */
	public String getCheckedIds() {
		return getListAdapter().getCheckedIds();
	}

	public SabFragmentActivity getSabActivity() {
		return (SabFragmentActivity) getActivity();
	}

	public abstract SabAdapter getListAdapter();

	public void onConnectionUp() {
	  mIsConnectionUp = true;
		if(mNoConnectionStub != null)
			mNoConnectionStub.setVisibility(View.GONE);		
	}

	public void onConnectionDown() {
	  mIsConnectionUp = false;
		if(mNoConnectionStub == null)
			mNoConnectionStub = getView().findViewById(R.id.no_connection_stub);
		
		if(mNoConnectionStub != null)
			mNoConnectionStub.setVisibility(View.VISIBLE);
		else
			throw new NullPointerException("You must use a View with an ID of R.id.no_connection_stub");
	}
	
	public boolean isConnectionUp() {
	  return mIsConnectionUp;
	}
	
  @Override
  public void onScrollStateChanged(AbsListView view, int scrollState) {
    if(scrollState != SCROLL_STATE_IDLE)
      mListIsScrolling = true;
    else 
      mListIsScrolling = false;
  }
  
  @Override
  public void onScroll(AbsListView view, int firstVisibleItem,
      int visibleItemCount, int totalItemCount) {    
  }
  
  public boolean isListScrolling() {
    return mListIsScrolling;
  }
}
