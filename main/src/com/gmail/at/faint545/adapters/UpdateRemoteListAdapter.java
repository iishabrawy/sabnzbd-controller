package com.gmail.at.faint545.adapters;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.gmail.at.faint545.Attribute;
import com.gmail.at.faint545.R;

public class UpdateRemoteListAdapter extends BaseAdapter {
	
	private int mRowId;
	private Context mContext;
	private List<Attribute> mAttrs;
	
	public UpdateRemoteListAdapter(Context context, int rowId, List<Attribute> attrs) {
		mRowId = rowId;
		mContext = context;
		mAttrs = attrs;
	}
	
	@Override
	public int getCount() {
		return mAttrs.size();
	}

	@Override
	public Object getItem(int position) {
		return mAttrs.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		Attribute attr = (Attribute) getItem(position);
		String value = attr.getValue();
		String title = attr.getKey();
		
		if(view == null) {
			LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(mRowId, null);
		}
		
		TextView titleTextView = (TextView) view.findViewById(R.id.update_remote_title);
		TextView valueTextView = (TextView) view.findViewById(R.id.update_remote_value);
		TextView separatorTextView = (TextView) view.findViewById(R.id.update_remote_separator);
				
		if(position == 0 || position == 4) {
			separatorTextView.setVisibility(View.VISIBLE);
			separatorTextView.setText(attr.getCategory());
		}
		else {
			separatorTextView.setVisibility(View.GONE);
		}
		
		titleTextView.setText(title);
		valueTextView.setText(value);
		
		return view;
	}
}