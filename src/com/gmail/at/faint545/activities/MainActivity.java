package com.gmail.at.faint545.activities;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockListActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.gmail.at.faint545.R;
import com.gmail.at.faint545.Remote;
import com.gmail.at.faint545.database.RemoteTableManager;

public class MainActivity extends SherlockListActivity {
  private ListAdapter listAdapter;
  private ListView listView;
  private ArrayList<Remote> remotes = new ArrayList<Remote>();
  

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main); 
    getSupportActionBar().setHomeButtonEnabled(false);
    listView = getListView();
    listAdapter = new ListAdapter(this, R.layout.main_row, remotes);
    listView.setAdapter(listAdapter); 
    registerForContextMenu(listView);
  }

  @Override
  protected void onStart() {
    super.onStart();
    loadRemotes();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getSupportMenuInflater().inflate(R.menu.main, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch(item.getItemId()) {
    case R.id.menu_add:
      startActivity(new Intent(this,UpdateRemoteActivity.class));
      break;
    }
    return true;
  }

  @Override
  protected void onListItemClick(ListView l, View v, int position, long id) {
    Intent intent = new Intent(this,ViewRemoteActivity.class);
    intent.putExtra(ViewRemoteActivity.EXTRA, remotes.get(position));
    startActivity(intent);
    super.onListItemClick(l, v, position, id);
  }

  /*
   * Loads all Remotes from SQLite database.
   * Run on a separate thread.
   */
  private void loadRemotes() {
    remotes.clear();
    new AsyncTask<Void, Void, Void>() {

      @Override
      protected Void doInBackground(Void... params) {
        RemoteTableManager remoteManager = new RemoteTableManager(getApplicationContext());
        remoteManager.open();
        remotes.addAll(remoteManager.getAllRemotes());
        remoteManager.close();
        return null;
      }

      @Override
      protected void onPostExecute(Void result) {
        listAdapter.notifyDataSetChanged();
      }
    }.execute();
  }  

  /*
   * Custom list adapter class for the ListView
   * that displays a list of Remotes.
   */
  private class ListAdapter extends ArrayAdapter<Remote> {
    private Context context;
    private int resId;
    private ArrayList<Remote> remotes;

    public ListAdapter(Context context, int resId, ArrayList<Remote> objects) {
      super(context, resId, objects);
      this.context = context;
      this.resId = resId;
      this.remotes = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
      TextView nick;
      final View options;
      if(convertView == null) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(resId, null);
      } 

      if(isEven(position)) {
        convertView.setBackgroundResource(R.drawable.listview_even_selector);
      }
      else {
        convertView.setBackgroundResource(R.drawable.listview_odd_selector);
      }

      nick = (TextView) convertView.findViewById(R.id.remote_nick);      
      options = convertView.findViewById(R.id.remote_options);     

      nick.setText(remotes.get(position).getPreference("nickname"));

      options.setOnClickListener(new OnClickListener() {
        
        @Override
        public void onClick(View v) {
          openContextMenu(v);
        }
      });
      return convertView;
    }

    private boolean isEven(int position) {      
      return position%2 == 0 ? true : false;
    }
  }

  @Override
  public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
    getMenuInflater().inflate(R.menu.action_mode_main, menu);
    super.onCreateContextMenu(menu, v, menuInfo);
  }

  @Override
  public boolean onContextItemSelected(android.view.MenuItem item) {
    AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
    switch(item.getItemId()) {
    case R.id.edit:
      Intent intent = new Intent(getApplicationContext(),UpdateRemoteActivity.class);
      intent.putExtra(UpdateRemoteActivity.KEY_REMOTE, remotes.get(info.position));
      startActivity(intent);
      break;
    case R.id.delete:
      confirmDelete(info.position);
      break;
    }
    return true;
  }

  @Override
  public void startActivity(Intent intent) {
    super.startActivity(intent);
    overridePendingTransition(R.anim.slide_enter_right, R.anim.slide_exit_left);
  }

  private void confirmDelete(final int position) {
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setTitle("Confrim Delete");      
    builder.setMessage("Are you sure you want to delete this remote?");
    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {        
      @Override
      public void onClick(DialogInterface dialog, int which) {
        RemoteTableManager manager = new RemoteTableManager(getApplicationContext());
        manager.open();
        boolean result = manager.remove(Integer.valueOf(remotes.get(position).getPreference(Remote.ID)));
        if(result)
          loadRemotes();
        manager.close();
      }
    });
    builder.setNegativeButton("No", null);
    builder.show();
  }
}