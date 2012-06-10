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

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import com.gmail.at.faint545.R;
import com.gmail.at.faint545.R.layout;
import com.gmail.at.faint545.Remote;
import com.gmail.at.faint545.activities.HistoryDetailsActivity;
import com.gmail.at.faint545.adapters.EndlessAdapter;
import com.gmail.at.faint545.adapters.HistoryAdapter;
import com.gmail.at.faint545.adapters.SabAdapter;
import com.gmail.at.faint545.factories.SabPostFactory;
import com.gmail.at.faint545.nzo.HistoryItem;
import com.gmail.at.faint545.nzo.NzoItem;
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

	private Messenger mMessenger = new Messenger(new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch(msg.what) {
			case R.id.connection_down:        
				onConnectionDown();
				break;
			case R.id.connection_up:
				onConnectionUp();
				break;
			}
			super.handleMessage(msg);
		}

	});

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
	public void updateItems(List<NzoItem> items) {
		getListAdapter().clearData();
		getListAdapter().addAll(items);

		// Re-create checked positions when new data
		// arrives.
		checkedPositions.clear();
		for(int i = 0, max = getListAdapter().getCount(); i < max; i++) {
			checkedPositions.add(false);
		}

		setNoConnectionVisibility(View.GONE);
		mEndlessListAdapter.reset();		
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

	@Override
	public SabAdapter getListAdapter() {    
		return (SabAdapter) mEndlessListAdapter.getWrappedAdapter();
	}

	private void setNoConnectionVisibility(int visibility) {
		View view = getView().findViewById(R.id.no_connection_stub);
		if(view != null) {
			view.setVisibility(visibility);
		}
	}

	private class HistoryEndlessAdapter extends EndlessAdapter {

		private static final String LOGTAG = "HistoryEndlessAdapter";
		private List<NzoItem> cache = new ArrayList<NzoItem>();

		private int mOffset = 0;

		public HistoryEndlessAdapter(Context context, ListAdapter wrapped,
				int pendingResource) {
			super(context, wrapped, pendingResource);
		}

		public HistoryEndlessAdapter(ListAdapter wrapped) {
			super(wrapped);
		}

		@Override
		protected boolean cacheInBackground() {
			// Get the proper HttpPost      
			HttpClient client = new DefaultHttpClient();
			HttpPost post = SabPostFactory.getHistoryInstance(getRemote(),mOffset);

			// We add 11 because we only show 10 items at a time, and next time,
			// we want to to begin loading from +1 where we left off. 
			mOffset += 11; 

			try {
				// Execute the HttpPost
				HttpResponse response = client.execute(post);
				String result = HttpResponseParser.parseResponse(response);

				JSONObject object = new JSONObject(result);

				// Check results
				if(object.has("history")) {
					JSONArray slots = object.getJSONObject("history").getJSONArray("slots");
					for(int i = 0, max = slots.length(); i < max; i++) {
						cache.add(new HistoryItem().buildFromJson(slots.getJSONObject(i)));
					}

					onConnectionUp();
					return cache.size() > 0;
				}
				else if(object.has("error")) {
					onConnectionDown();					
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
				onConnectionDown();
				e.printStackTrace();
			} 
			return false;
		}

		private void onConnectionUp() {
			try {
				Message m = Message.obtain();
				m.what = R.id.connection_up;
				mMessenger.send(m);
			} 
			catch (RemoteException e) {
				e.printStackTrace();
			}
		}

		private void onConnectionDown() {
			try {
				Message m = Message.obtain();
				m.what = R.id.connection_down;
				mMessenger.send(m);
			} 
			catch (RemoteException re) {
				re.printStackTrace();
			}
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