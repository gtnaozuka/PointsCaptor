package com.captor.points.gtnaozuka.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.captor.points.gtnaozuka.adapter.FileManagerAdapter;
import com.captor.points.gtnaozuka.pointscaptor.R;
import com.captor.points.gtnaozuka.tabs.SlidingTabLayout;

public class FileManagerFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_file_manager, container, false);

        CharSequence titles[] = {getResources().getString(R.string.data), getResources().getString(R.string.photos)};

        ViewPager viewPager = (ViewPager) rootView.findViewById(R.id.pager);
        viewPager.setAdapter(new FileManagerAdapter(getChildFragmentManager(), titles, titles.length));

        SlidingTabLayout stl = (SlidingTabLayout) rootView.findViewById(R.id.tabs);
        stl.setDistributeEvenly(true);
        stl.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.normal_button);
            }
        });
        stl.setViewPager(viewPager);

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
