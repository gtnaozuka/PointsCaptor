package com.captor.points.gtnaozuka.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.captor.points.gtnaozuka.pointscaptor.R;

public class DistanceFragment extends Fragment {

    public DistanceFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_distance, container, false);
    }
}
