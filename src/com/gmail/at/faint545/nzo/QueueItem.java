package com.gmail.at.faint545.nzo;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

import com.gmail.at.faint545.utils.SabConstants;

public class QueueItem extends NzoItem {
  private String timeleft;
  private int percentDone;

  public QueueItem() {
    super();
  }

  public String getEta() {
    return timeleft;
  }
  
  public int getPercentDone() {
    return percentDone;
  }

  @Override
  public QueueItem buildFromJson(JSONObject object) throws JSONException {    
    timeleft = object.getString(SabConstants.TIMELEFT);
    percentDone = object.getInt(SabConstants.PERCENTAGE);
    return (QueueItem) super.buildFromJson(object);
  }

  ////////////////////////////////////////////////////////////////////////
  // Parcelable stuff below here.
  ////////////////////////////////////////////////////////////////////////

  public QueueItem(Parcel in) {
    super(in);
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    super.writeToParcel(dest, flags);
    dest.writeString(timeleft);
    dest.writeInt(percentDone);
  }

  @Override
  public void readFromParcel(Parcel in) {
    super.readFromParcel(in);
    timeleft = in.readString();
    percentDone = in.readInt();
  }

  public static final Parcelable.Creator<QueueItem> CREATOR = new Parcelable.Creator<QueueItem>() {
    @Override
    public QueueItem createFromParcel(Parcel in) {
      return new QueueItem(in);
    }

    @Override
    public QueueItem[] newArray(int arg0) {
      return new QueueItem[arg0];
    }   
  };  
}
