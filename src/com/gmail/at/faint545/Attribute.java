package com.gmail.at.faint545;

public class Attribute {
	
	private String mKey, mValue, mCategory;
	
	public Attribute(String key, String value, String category) {
		mKey = key;
		mValue = value;
		mCategory = category;
	}

	public void setValue(String value) {
		mValue = value;
	}
	
	public String getKey() {
		return mKey;
	}

	public String getValue() {
		return mValue;
	}
	
	public String getCategory() {
		return mCategory;
	}	
}
