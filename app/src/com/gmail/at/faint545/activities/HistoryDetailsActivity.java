package com.gmail.at.faint545.activities;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockListActivity;
import com.actionbarsherlock.view.MenuItem;
import com.gmail.at.faint545.R;
import com.gmail.at.faint545.nzo.HistoryItem;
import com.gmail.at.faint545.utils.SabConstants;
import com.viewpagerindicator.R.drawable;

public class HistoryDetailsActivity extends SherlockListActivity {
  public static final String KEY = "item";
  private HistoryItem historyItem;
  private ArrayList<String[]> itemDetails = new ArrayList<String[]>();
  private ListView listView;
  private HistoryDetailsAdapter listAdapter;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    historyItem = getIntent().getParcelableExtra(KEY);
    setTitle(historyItem.getName());
    buildListData();
    setContentView(R.layout.history_details);
    setTitle(historyItem.getName());
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    setupListView();
    super.onCreate(savedInstanceState);
  }

  @Override
  public void setContentView(int layoutResId) {
    super.setContentView(layoutResId);
    if(historyItem.getStatus().equalsIgnoreCase(SabConstants.FAILED)) {
      findViewById(R.id.btn__retry).setVisibility(View.VISIBLE);
    }
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch(item.getItemId()) {
    case android.R.id.home:
      onBackPressed();
    break;
    }
    return true;
  }

  /*
   * Build the data that the ListView will show
   */
  private void buildListData() {
    itemDetails.add(new String[]{"Name",historyItem.getName()});
    itemDetails.add(new String[]{"Size",historyItem.getSize()});
    itemDetails.add(new String[]{"Elapsed Time",historyItem.getElapsedTime()});
    itemDetails.add(new String[]{"Status",historyItem.getStatus()});
    itemDetails.add(new String[]{"Finished On",historyItem.getCompletedOn(HistoryItem.LENGTH_LONG)});      
    itemDetails.add(new String[]{"Category",historyItem.getCategory()});
    itemDetails.add(new String[]{"Path",historyItem.getPath()});
  }

  private void setupListView() {
    listView = getListView();    
    listAdapter = new HistoryDetailsAdapter(this, R.layout.history_details_row, itemDetails);
    listView.setAdapter(listAdapter);
  }

  public void onItemRetry(View view) {
    Intent data = new Intent();
    data.putExtra(ViewRemoteActivity.EXTRA, historyItem.getId());
    setResult(RESULT_OK,data);
    finish();
    overridePendingTransition(R.anim.slide_enter_left, R.anim.slide_exit_right);
  }
  
  private static class HistoryDetailsAdapter extends BaseAdapter {
    private Context context;
    private int rowId;
    private ArrayList<String[]> data;

    public HistoryDetailsAdapter(Context context, int rowId, ArrayList<String[]> object) {
      this.data = object;
      this.rowId = rowId;
      this.context = context;
    }

    @Override
    public int getCount() {
      return data.size();
    }

    @Override
    public Object getItem(int position) {
      return data.get(position);
    }

    @Override
    public long getItemId(int position) {
      return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
      String[] detail = (String[]) getItem(position);

      if(convertView == null) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(rowId, null);
      }  

      TextView title = (TextView) convertView.findViewById(R.id.history_details_row_title);
      TextView value = (TextView) convertView.findViewById(R.id.history_details_row_value);

      title.setText(detail[0]);
      value.setText(detail[1]);

      return convertView;
    }
    
    private boolean isEven(int position) {
      return position%2 == 0 ? true : false;
    } 

    @Override
    public boolean isEnabled(int position) {
      // Disable clicks
      return false;
    }    
  }

  @Override
  public void onBackPressed() {
    setResult(RESULT_CANCELED);
    finish();
    overridePendingTransition(R.anim.slide_enter_left, R.anim.slide_exit_right);
  }
}