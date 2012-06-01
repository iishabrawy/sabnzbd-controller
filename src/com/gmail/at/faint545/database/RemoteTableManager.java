package com.gmail.at.faint545.database;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.gmail.at.faint545.Remote;

public class RemoteTableManager {
  private SQLiteDatabase database;
  private DatabaseManager dbManager;

  public RemoteTableManager(Context context) {
    dbManager = new DatabaseManager(context);
  }

  public void open() {
    database = dbManager.getWritableDatabase();
  }

  public boolean insertOrUpdate(Remote remote) {
    ContentValues values = new ContentValues();
    values.put(RemoteTable.COL_NICK, remote.getPreference(Remote.NICKNAME));
    values.put(RemoteTable.COL_API, remote.getPreference(Remote.APIKEY));
    values.put(RemoteTable.COL_PASSWORD, remote.getPreference(Remote.PASSWORD));
    values.put(RemoteTable.COL_USERNAME, remote.getPreference(Remote.USERNAME));
    values.put(RemoteTable.COL_HOST, remote.getPreference(Remote.HOST));
    values.put(RemoteTable.COL_PORT, remote.getPreference(Remote.PORT));
    values.put(RemoteTable.COL_RINTERVAL, remote.getPreference(Remote.REFRESH_INTERVAL));
    
    String id = remote.getPreference(Remote.ID);
    if(id == null) { // Insert      
      long result = database.insert(RemoteTable.NAME, null, values);
      return (result != -1);
    }
    else { // Update
      long result = database.update(RemoteTable.NAME, values, RemoteTable.COL_ID + "=" + id, null);
      return (result > 0);
    }
  }

  public ArrayList<Remote> getAllRemotes() {
    ArrayList<Remote> remotes = new ArrayList<Remote>();
    Cursor cursor = database.query(RemoteTable.NAME, null, null, null, null, null, null);
    while(cursor.moveToNext()) {
      remotes.add(cursorToRemote(cursor));
    }
    return remotes;
  }

  private Remote cursorToRemote(Cursor c) {    
    Remote r = new Remote();
    r.updatePreference(Remote.ID, c.getString(RemoteTable.INDEX_ID));    
    r.updatePreference(Remote.NICKNAME, c.getString(RemoteTable.INDEX_NICK));
    r.updatePreference(Remote.HOST, c.getString(RemoteTable.INDEX_HOST));
    r.updatePreference(Remote.PORT, c.getString(RemoteTable.INDEX_PORT));
    r.updatePreference(Remote.REFRESH_INTERVAL, c.getString(RemoteTable.INDEX_RINTERVAL));
    r.updatePreference(Remote.APIKEY, c.getString(RemoteTable.INDEX_API));
    r.updatePreference(Remote.USERNAME, c.getString(RemoteTable.INDEX_USERNAME));
    r.updatePreference(Remote.PASSWORD, c.getString(RemoteTable.INDEX_PASSWORD));
    return r;
  }

  public void close() {
    dbManager.close();
    database.close();
  }

  public boolean remove(int id) {
    int result = database.delete(RemoteTable.NAME, RemoteTable.COL_ID + "=" + id, null);
    if(result > 0)
      return true;
    else
      return false;
  }
}
