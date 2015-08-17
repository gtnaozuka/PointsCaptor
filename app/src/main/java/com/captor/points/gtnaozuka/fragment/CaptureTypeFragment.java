package com.captor.points.gtnaozuka.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.captor.points.gtnaozuka.adapter.ViewPagerAdapter;
import com.captor.points.gtnaozuka.pointscaptor.R;
import com.captor.points.gtnaozuka.tabs.SlidingTabLayout;
import com.captor.points.gtnaozuka.util.FragmentOperations;
import com.captor.points.gtnaozuka.util.Values;

public class CaptureTypeFragment extends Fragment {

    private AppCompatActivity context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_capture_type, container, false);

        CharSequence titles[] = {getResources().getString(R.string.by_distance), getResources().getString(R.string.by_time)};

        ViewPager viewPager = (ViewPager) rootView.findViewById(R.id.pager);
        viewPager.setAdapter(new ViewPagerAdapter(getChildFragmentManager(), titles, titles.length));

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
        context = (AppCompatActivity) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void initDefaultCapture(Integer type, Double value) {
        Bundle bundle = new Bundle();
        bundle.putInt(Values.TYPE_MSG, type);
        bundle.putDouble(Values.VALUE_MSG, value);

        FragmentOperations.newFragment(context, new DefaultCaptureFragment(), bundle, getResources().getString(R.string.fragment_default_capture));
    }
}
