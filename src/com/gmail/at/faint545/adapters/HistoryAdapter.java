package com.gmail.at.faint545.adapters;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.gmail.at.faint545.HistoryItem;
import com.gmail.at.faint545.NzoItem;
import com.gmail.at.faint545.R;
import com.gmail.at.faint545.fragments.CheckChangedListener;
import com.gmail.at.faint545.utils.SabConstants;

public class HistoryAdapter extends SabAdapter {
  private static final String LOGTAG = "HistoryAdapter";
  
  private ArrayList<NzoItem> items;
  private ArrayList<Boolean> checkedPositions;
  private Context context;
  private int rowId;    
  private int checkCount;

  public HistoryAdapter(Activity context, int rowId, ArrayList<NzoItem> items,
      ArrayList<Boolean> checkedPositions) {
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
    HistoryItem item = (HistoryItem) getItem(position);

    if(convertView == null) {
      viewHolder = new ViewHolder();
      LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      convertView = inflater.inflate(rowId, null);      

      viewHolder.name     = (TextView)  convertView.findViewById(R.id.history_row_name);
      viewHolder.size     = (TextView)  convertView.findViewById(R.id.history_row_size);
      viewHolder.time     = (TextView)  convertView.findViewById(R.id.history_row_time);
      viewHolder.status   = (ImageView) convertView.findViewById(R.id.history_row_status);
      viewHolder.checkbox = (CheckBox)  convertView.findViewById(R.id.history_row_checkbox);

      convertView.setTag(viewHolder);
    }
    viewHolder = (ViewHolder) convertView.getTag();     

    if(isEven(position)) {
      convertView.setBackgroundResource(R.drawable.listview_even_selector);
    }
    else {
      convertView.setBackgroundResource(R.drawable.listview_odd_selector);
    }

    convertView.findViewById(R.id.root).setPadding(10, 10, 10, 10);

    if(item != null) {        

      String status = item.getStatus();
      Drawable indicator = null;
      Resources resources = context.getResources();
      if(status.equalsIgnoreCase(SabConstants.COMPLETED)) {
        indicator = resources.getDrawable(R.drawable.indicator_done);          
      }
      else if(status.equalsIgnoreCase(SabConstants.FAILED)) {
        indicator = resources.getDrawable(R.drawable.indicator_fail);
      }
      else if(status.equalsIgnoreCase(SabConstants.EXTRACTING)) {
        indicator = resources.getDrawable(R.drawable.indicator_unpack);
      }
      else if(status.equalsIgnoreCase(SabConstants.VERIFYING)) {
        indicator = resources.getDrawable(R.drawable.indicator_verify);
      }
      else if(status.equalsIgnoreCase(SabConstants.QUEUED)) {
        indicator = resources.getDrawable(R.drawable.indicator_queued);
      }

      viewHolder.name.setText(item.getName());
      viewHolder.status.setImageDrawable(indicator);
      viewHolder.size.setText(item.getSize());
      viewHolder.time.setText(item.getCompletedOn(HistoryItem.LENGTH_SHORT));
    }

    if(!checkedPositions.isEmpty() && position < checkedPositions.size()) {
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

  @Override
  public void setAllChecked(boolean flag) {
    for(int i = 0, max = getCount(); i < max; i++) {
      checkedPositions.set(i, flag);
    }
    notifyDataSetChanged();
  }

  @Override
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
      return ids.substring(0, ids.length()-1).toString();
    }
  }    

  @Override
  public void reset() {
    setAllChecked(false);
    checkCount = 0;
    notifyDataSetChanged();
  }

  /*
   * Class to cache child views of the ListView's row.
   */
  private static class ViewHolder {
    TextView name, size, time;
    ImageView status;
    CheckBox checkbox;
  }

  @Override
  public void add(NzoItem item) {
    items.add(item);
    checkedPositions.add(false);
    notifyDataSetChanged();
  }

  @Override
  public void addAll(List<NzoItem> items) {
    this.items.addAll(items);
    for(int i = 0, max = items.size(); i < max; i++) {
      checkedPositions.add(false);
    }
    notifyDataSetChanged();    
  }

  @Override
  public void clearData() {
    items.clear();
    checkedPositions.clear();
  }
}