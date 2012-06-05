package com.gmail.at.faint545.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.gmail.at.faint545.R;
import com.gmail.at.faint545.interfaces.CheckChangedListener;
import com.gmail.at.faint545.nzo.NzoItem;
import com.gmail.at.faint545.nzo.QueueItem;

import java.util.ArrayList;
import java.util.List;

public class QueueAdapter extends SabAdapter {
  private ArrayList<NzoItem> items;
  private ArrayList<Boolean> checkedPositions;
  private Context context;
  private int rowId;
  private int checkCount;

  private static final String LOGTAG = "QueueAdapter";

  public QueueAdapter(Context context, int rowId, ArrayList<NzoItem> items, ArrayList<Boolean> checkedPositions) {
    this.items = items;
    this.context = context;
    this.rowId = rowId;
    this.checkedPositions = checkedPositions;
  }

  @Override
  public int getCount() {
    return items.size();
  }

  @Override
  public Object getItem(int position) {
    return items.get(position);
  }

  @Override
  public long getItemId(int position) {
    return position;
  }

  @Override
  public View getView(final int position, View convertView, ViewGroup parent) {
    
    // Setup view(s).

    ViewHolder viewHolder = null;
    if(convertView == null) {
      viewHolder = new ViewHolder();
      LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      convertView = inflater.inflate(rowId, null);      
      viewHolder.filename = (TextView)    convertView.findViewById(R.id.queue_row_filename);
      viewHolder.timeleft = (TextView)    convertView.findViewById(R.id.queue_row_timeleft);
      viewHolder.progress = (ProgressBar) convertView.findViewById(R.id.queue_row_progress);
      viewHolder.checkbox = (CheckBox)    convertView.findViewById(R.id.queue_row_checkbox);
      convertView.setTag(viewHolder);
    }      
    viewHolder = (ViewHolder) convertView.getTag();

    if(isEven(position)) {
      convertView.setBackgroundResource(R.drawable.listview_even_selector);
    }
    else {
      convertView.setBackgroundResource(R.drawable.listview_odd_selector);
    }  

    // Apply padding to the row.
    // See http://code.google.com/p/android/issues/detail?id=27235
    convertView.findViewById(R.id.queue_row_root).setPadding(6, 6, 6, 6);      

    QueueItem item = (QueueItem) getItem(position);      
    if(item != null) {
      viewHolder.filename.setText(item.getName());
      viewHolder.timeleft.setText("Time Left: " + item.getEta());
      viewHolder.progress.setProgress(item.getPercentDone());
    }

    if(!checkedPositions.isEmpty()) {
      viewHolder.checkbox.setChecked(checkedPositions.get(position));
    }

    // Setup listeners

    viewHolder.checkbox.setOnClickListener(new OnClickListener() {          
      @Override
      public void onClick(View v) {  
        CheckBox checkBox = (CheckBox) v;
        checkedPositions.set(position, checkBox.isChecked());
        checkCount = checkBox.isChecked() ? checkCount+1 : checkCount-1;
        ((CheckChangedListener) context).onCheckChanged(checkBox.isChecked(),checkCount);
      }
    });
    return convertView;
  }

  ////////////////////////////////////////////////////////////////////////
  // Helper methods
  ////////////////////////////////////////////////////////////////////////

  public void setAllChecked(boolean flag) {
    for(int i = 0, max = getCount(); i < max; i++) {
      checkedPositions.set(i, flag);
    }
    notifyDataSetChanged();
  }

  public String getCheckedIds() {
    StringBuilder ids = new StringBuilder();
    for(int i = 0, max = checkedPositions.size(); i < max; i++) {
      if(checkedPositions.get(i)) {
        ids.append(items.get(i).getId()).append(",");
      }
    }

    if(ids.length() < 1) {
      return null;
    }
    else {
      return ids.substring(0,ids.length()-1).toString();
    }
  }    

  public void reset() {
    setAllChecked(false);
    checkCount = 0;
    notifyDataSetChanged();
  }


  /*
   * Class to cache child views of the ListView's row.
   */
  private static class ViewHolder {
    TextView filename, timeleft;
    ProgressBar progress;
    CheckBox checkbox;
  }


  @Override
  public void add(NzoItem item) {
    items.add(item);
    checkedPositions.add(false);
  }

  @Override
  public void addAll(List<NzoItem> items) {
  	this.items.addAll(items);
    for(int i = 0, max = items.size(); i < max; i++) {
      checkedPositions.add(false);
    }    
  }

  @Override
  public void clearData() {
  	items.clear();
  	checkedPositions.clear();
  }
}