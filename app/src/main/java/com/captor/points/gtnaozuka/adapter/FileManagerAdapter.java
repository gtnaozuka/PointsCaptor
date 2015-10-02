package com.captor.points.gtnaozuka.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.captor.points.gtnaozuka.fragment.DataFragment;
import com.captor.points.gtnaozuka.fragment.PhotosFragment;

public class FileManagerAdapter extends FragmentStatePagerAdapter {

    private CharSequence titles[];
    private int num;

    public FileManagerAdapter(FragmentManager fm, CharSequence titles[], int num) {
        super(fm);

        this.titles = titles;
        this.num = num;
    }

    @Override
    public Fragment getItem(int index) {
        switch (index) {
            case 0:
                return new DataFragment();
            case 1:
                return new PhotosFragment();
        }
        return null;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }

    @Override
    public int getCount() {
        return num;
    }
}
