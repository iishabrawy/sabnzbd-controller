package com.gmail.at.faint545.nzo;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

import com.gmail.at.faint545.utils.SabConstants;

public class NzoItem implements Parcelable {
  private String status;  
  private String id;
  private String name;
  private String category;
  private String size;
  
  public NzoItem(){}
  
  public String getStatus() {
    return status;
  }

  public String getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getCategory() {
    return category;
  }

  public String getSize() {
    return size;
  }

  public NzoItem buildFromJson(JSONObject object) throws JSONException {
    id = object.getString(SabConstants.NZO_ID);
    size = object.getString(SabConstants.SIZE);
    status = object.getString(SabConstants.STATUS);
    
    if(object.has(SabConstants.CATEGORY)) {
      category = object.getString(SabConstants.CATEGORY);
    }
    else if(object.has(SabConstants.CAT)) {
      category = object.getString(SabConstants.CAT);
    }
    
    if(object.has(SabConstants.NAME)) {
      name = object.getString(SabConstants.NAME);
    }
    else if(object.has(SabConstants.FILENAME)) {
      name = object.getString(SabConstants.FILENAME);
    }
    return this;
  }

  ////////////////////////////////////////////////////////////////////////
  // Parcelable stuff below here.
  ////////////////////////////////////////////////////////////////////////
  
  public NzoItem(Parcel in) {
    readFromParcel(in);
  }
  
  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(status);
    dest.writeString(id);
    dest.writeString(name);
    dest.writeString(category);
    dest.writeString(size);
  }
  
  public void readFromParcel(Parcel in) {
    status = in.readString();
    id = in.readString();
    name = in.readString();
    category = in.readString();
    size = in.readString();
  }
  
  public static final Parcelable.Creator<NzoItem> CREATOR = new Parcelable.Creator<NzoItem>() {
    @Override
    public NzoItem createFromParcel(Parcel in) {
      return new NzoItem(in);
    }

    @Override
    public NzoItem[] newArray(int arg0) {
      return new NzoItem[arg0];
    }   
  };  
}
