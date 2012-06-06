package com.gmail.at.faint545.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseManager extends SQLiteOpenHelper {
  public static final String DB_NAME = "sabnzbd";
  private static final int VER = 4;

  public DatabaseManager(Context context) {
    super(context, DB_NAME, null, VER);
  }

  @Override
  public void onCreate(SQLiteDatabase db) {
    db.execSQL(RemoteTable.CREATE);
  }

  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    db.execSQL("DROP TABLE IF EXISTS " + RemoteTable.NAME);
    onCreate(db);
  }
}