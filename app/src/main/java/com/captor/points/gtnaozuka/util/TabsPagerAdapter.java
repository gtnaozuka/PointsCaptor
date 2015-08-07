package com.captor.points.gtnaozuka.util;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.captor.points.gtnaozuka.fragment.DistanceFragment;
import com.captor.points.gtnaozuka.fragment.TimeFragment;

public class TabsPagerAdapter extends FragmentPagerAdapter {

    public TabsPagerAdapter(FragmentManager fm) {
        super(fm);
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
    public int getCount() {
        return 2;
    }
}
