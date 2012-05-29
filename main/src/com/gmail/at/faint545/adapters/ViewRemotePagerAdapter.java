package com.gmail.at.faint545.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import com.gmail.at.faint545.R.array;

import java.util.ArrayList;

public class ViewRemotePagerAdapter extends FragmentPagerAdapter {
  private ArrayList<Fragment> fragments = new ArrayList<Fragment>();
  private String[] pageTitles;

  public ViewRemotePagerAdapter(Context context, FragmentManager fm) {
    super(fm);
    pageTitles = context.getResources().getStringArray(array.title_array);
  }

  public void addFragment(Fragment fragment) {
    fragments.add(fragment);
  }

  public void addFragments(Fragment... fragments) {
    for(Fragment f : fragments) {
      addFragment(f);
    }
  }

  @Override
  public CharSequence getPageTitle(int position) {
    return pageTitles[position].toUpperCase();
  }

  @Override
  public Fragment getItem(int position) {
    return fragments.get(position);
  }

  @Override
  public int getCount() {
    return fragments.size();
  }
}