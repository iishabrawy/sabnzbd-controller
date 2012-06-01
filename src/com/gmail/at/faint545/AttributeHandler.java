package com.gmail.at.faint545;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import android.content.Context;

public class AttributeHandler {

	private List<Attribute> mAttributes = new ArrayList<Attribute>();
	private String[] mAttrTitles, mAttrValues;
	private Context mContext;

	public AttributeHandler(Context context, String[] attrTitles, String[] attrValues) {
		mAttrTitles = attrTitles;
		mAttrValues = attrValues;
		mContext = context;
		
		// Check the length of both titles and values to make sure 
		// they are the same.
		if(mAttrTitles.length != mAttrValues.length) {
			throw new InvalidParameterException("There are more titles than values or vice versa.");
		}
	}

	public AttributeHandler(Context context, String[] attrTitles) {
		mAttrTitles = attrTitles;
		mAttrValues = new String[mAttrTitles.length];
		mContext = context;
		
		// Create empty values
		for(int i = 0, max = mAttrTitles.length; i < max; i++) {
			mAttrValues[i] = "";
		}
	}
	
	public List<Attribute> execute() {
		for(int i = 0, max = mAttrTitles.length; i < max; i++) {
			String category = null;
			
			if(i == 0) category = mContext.getString(R.string.general);
			else if(i == 4) category = mContext.getString(R.string.auth);
			
			mAttributes.add(new Attribute(mAttrTitles[i], mAttrValues[i], category));
		}
		return mAttributes;
	}
}
