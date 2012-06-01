package com.gmail.at.faint545.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.app.SherlockFragment;
import com.gmail.at.faint545.R;
import com.gmail.at.faint545.Remote;

public class StatusFragment extends SherlockFragment {
  public static final String KEY = "remote";
  
  private StatusFragment() {}
  
  public static StatusFragment getInstance(Remote remote) {
    StatusFragment fragment = new StatusFragment();
    Bundle args = new Bundle();
    args.putParcelable(KEY, remote);
    fragment.setArguments(args);
    return fragment;
  }
  
  @Override
  public void onCreate(Bundle savedInstanceState) {    
    super.onCreate(savedInstanceState);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.status, null);
    return view;
  }
}
