package com.gmail.at.faint545.database;

import com.gmail.at.faint545.Remote;

public class RemoteTable {
  public static final String NAME = "remotes";
  
  /* Column names */
  public static final String COL_ID = Remote.ID;
  public static final String COL_NICK = Remote.NICKNAME;
  public static final String COL_HOST = Remote.HOST;
  public static final String COL_PORT = Remote.PORT;
  public static final String COL_RINTERVAL = Remote.REFRESH_INTERVAL; // Refresh interval
  public static final String COL_API = Remote.APIKEY;
  public static final String COL_USERNAME = Remote.USERNAME;
  public static final String COL_PASSWORD = Remote.PASSWORD;
  
  /* Column indices */
  public static final int INDEX_ID = 0;
  public static final int INDEX_NICK = 1;
  public static final int INDEX_HOST = 2;
  public static final int INDEX_PORT = 3;
  public static final int INDEX_RINTERVAL = 4;
  public static final int INDEX_API = 5;
  public static final int INDEX_USERNAME = 6;
  public static final int INDEX_PASSWORD = 7;
  
//  public static final String CREATE = "CREATE TABLE " + NAME + " (" +
//  		COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
//      COL_NICK + " TEXT NOT NULL, " + 
//      COL_HOST + " TEXT NOT NULL, " +
//      COL_PORT + " TEXT NOT NULL, " +
//      COL_RINTERVAL + " INTEGER DEFAULT -1, " +
//      COL_API + " TEXT, " +
//      COL_USERNAME + " TEXT, " +
//      COL_PASSWORD + " TEXT);";
  
  public static final String CREATE = new StringBuilder()
    .append("CREATE TABLE ").append(NAME).append(" (")
    .append(COL_ID).append(" INTEGER PRIMARY KEY AUTOINCREMENT, ")
    .append(COL_NICK).append(" TEXT NOT NULL, ")
    .append(COL_HOST).append(" TEXT NOT NULL, ")
    .append(COL_PORT).append(" TEXT NOT NULL, ")
    .append(COL_RINTERVAL).append(" INTEGER DEFAULT 0, ")
    .append(COL_API).append(" TEXT, ")
    .append(COL_USERNAME).append(" TEXT, ")
    .append(COL_PASSWORD).append(" TEXT);").toString();  

}
