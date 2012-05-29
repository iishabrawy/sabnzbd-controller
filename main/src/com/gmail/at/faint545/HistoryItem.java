package com.gmail.at.faint545;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

import com.gmail.at.faint545.utils.SabConstants;
import com.gmail.at.faint545.utils.StringUtils;

public class HistoryItem extends NzoItem {
  private String path;
  private long completedOn;
  private long elapsedTime;

  public static final int LENGTH_SHORT = 0, LENGTH_LONG = 1;

  public HistoryItem(){
    super();
  }

  public String getPath() {
    return path;
  }

  public String getCompletedOn(int flag) {
    switch(flag) {
    case LENGTH_LONG:
      return StringUtils.unixTimeToDate(completedOn);
    case LENGTH_SHORT:
      return StringUtils.unixTimeToShortDate(completedOn);
    default:
      return StringUtils.unixTimeToDate(completedOn);
    }    
  }

  public String getElapsedTime() {
    return StringUtils.normalizeSeconds(elapsedTime);
  }

  @Override
  public NzoItem buildFromJson(JSONObject object) throws JSONException {    
    path = object.getString(SabConstants.STORAGE);    
    elapsedTime = object.getLong(SabConstants.DOWNLOAD_TIME);    
    completedOn = object.getLong(SabConstants.COMPLETED);
    return super.buildFromJson(object);
  }

  ////////////////////////////////////////////////////////////////////////
  // Parcelable stuff below here.
  ////////////////////////////////////////////////////////////////////////

  public HistoryItem(Parcel in) {
    super(in);
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    super.writeToParcel(dest, flags);
    dest.writeString(path);
    dest.writeLong(completedOn);
    dest.writeLong(elapsedTime);
  }

  @Override
  public void readFromParcel(Parcel in) {
    super.readFromParcel(in);
    path = in.readString();
    completedOn = in.readLong();
    elapsedTime = in.readLong();
  }

  public static final Parcelable.Creator<HistoryItem> CREATOR = new Parcelable.Creator<HistoryItem>() {
    @Override
    public HistoryItem createFromParcel(Parcel in) {
      return new HistoryItem(in);
    }

    @Override
    public HistoryItem[] newArray(int arg0) {
      return new HistoryItem[arg0];
    }   
  };   
}
