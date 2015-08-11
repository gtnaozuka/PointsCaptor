package com.captor.points.gtnaozuka.tabs;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.captor.points.gtnaozuka.fragment.DistanceFragment;
import com.captor.points.gtnaozuka.fragment.TimeFragment;

public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    private CharSequence titles[];
    private int numbOfTabs;

    public ViewPagerAdapter(FragmentManager fm, CharSequence titles[], int numbOfTabs) {
        super(fm);

        this.titles = titles;
        this.numbOfTabs = numbOfTabs;
    }

    @Override
    public Fragment getItem(int index) {
        switch (index) {
            case 0:
                return new DistanceFragment();
            case 1:
                return new TimeFragment();
        }
        return null;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }

    @Override
    public int getCount() {
        return numbOfTabs;
    }
}
