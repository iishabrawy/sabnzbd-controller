/* 
 * Copyright 2011 Alex Fu
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 		
 * 		http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.gmail.at.faint545;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

public class Remote implements Parcelable {

	public static final String NICKNAME = "nickname", HOST = "host",
			PORT = "port", REFRESH_INTERVAL = "refresh_interval",
			APIKEY = "apikey", USERNAME = "username", PASSWORD = "password",
			ID = "_id";

	private Map<String,String> mPreferences;

	/**
	 * Remote constructor
	 */
	public Remote() {
		mPreferences = new LinkedHashMap<String, String>();
	}

	public void addPreference(String key) {
		mPreferences.put(key, null);
	}

	public void updatePreference(String key, String value) {
		mPreferences.put(key, value);
	}

	public String getPreference(String key) {
		return mPreferences.get(key);
	}

  public Long getRefreshInterval() {
    String intervalAsString = mPreferences.get(REFRESH_INTERVAL);
    long realInterval = 0;

    if(intervalAsString != null) {
      realInterval = Long.valueOf(intervalAsString);
    }

    return realInterval;
  }

  public boolean hasRefreshInterval() {
    return getRefreshInterval() != 0;
  }
	
	public String getUrl() {
		String host = mPreferences.get(HOST);
		String port = mPreferences.get(PORT);
		try {
	    URI uri = new URI("http://"+host+":"+port);
	    return uri.toString();
    } 
		catch (URISyntaxException e) {
	    e.printStackTrace();
	    return null;
    }
	}
	
	public String getUsername() {
		return mPreferences.get(USERNAME);		
	}
	
	public String getPassword() {
		return mPreferences.get(PASSWORD);		
	}
	
	public String getAPIKey() {
		return mPreferences.get(APIKEY);		
	}		

	/**
	 * Below here is for parcelable
	 */

	Remote (Parcel in) {
		try {
			mPreferences = new LinkedHashMap<String, String>();
			readFromParcel(in);
		}
		catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {  	
		dest.writeInt(mPreferences.size()); // Store the size first...		
		for (String key: mPreferences.keySet()) {
			dest.writeString(key); // Then write the key...
			dest.writeString(mPreferences.get(key)); // then write the actual value.
		}
	}

	private void readFromParcel(Parcel in) throws URISyntaxException {
    int count = in.readInt();
    for (int i = 0; i < count; i++) {
        mPreferences.put(in.readString(), in.readString());
    }
	}

	public static final Parcelable.Creator<Remote> CREATOR = new Parcelable.Creator<Remote>() {
		@Override
		public Remote createFromParcel(Parcel arg0) {
			return new Remote(arg0);
		}

		@Override
		public Remote[] newArray(int arg0) {
			return new Remote[arg0];
		}   
	};
}
