package com.captor.points.gtnaozuka.pointscaptor;

import android.app.ActionBar;
import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.content.Intent;

import com.captor.points.gtnaozuka.dialog.DistanceDialog;
import com.captor.points.gtnaozuka.dialog.TimeDialog;
import com.captor.points.gtnaozuka.util.TabsPagerAdapter;
import com.captor.points.gtnaozuka.util.Util;

public class MainActivity extends MenuActivity implements ActionBar.TabListener,
        DistanceDialog.DistanceListener, TimeDialog.TimeListener {

    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TabsPagerAdapter tpa = new TabsPagerAdapter(getSupportFragmentManager());
        final ActionBar actionBar = getActionBar();

        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(tpa);
        viewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.addTab(actionBar.newTab().setText(R.string.by_distance).setTabListener(this));
        actionBar.addTab(actionBar.newTab().setText(R.string.by_time).setTabListener(this));
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
        Log.d("Tab", "selected");
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
        Log.d("Tab", "reselected");
    }

    private void startCaptureActivity(Integer type, Double value) {
        Intent intent = new Intent(this, CaptureActivity.class);
        intent.putExtra(Util.TYPE_MSG, type);
        intent.putExtra(Util.VALUE_MSG, value);
        startActivity(intent);
    }

    public void startMeter1(View view) {
        startCaptureActivity(Util.DISTANCE, 1.0);
    }

    public void startMeters2(View view) {
        startCaptureActivity(Util.DISTANCE, 2.0);
    }

    public void startMeters5(View view) {
        startCaptureActivity(Util.DISTANCE, 5.0);
    }

    public void startMeters10(View view) {
        startCaptureActivity(Util.DISTANCE, 10.0);
    }

    public void startMeters15(View view) {
        startCaptureActivity(Util.DISTANCE, 15.0);
    }

    public void startMeters20(View view) {
        startCaptureActivity(Util.DISTANCE, 20.0);
    }

    public void startOtherDistance(View view) {
        DialogFragment dialog = new DistanceDialog();
        dialog.show(getFragmentManager(), "DistanceDialog");
    }

    public void startSecond1(View view) {
        startCaptureActivity(Util.TIME, 1000.0);
    }

    public void startSeconds2(View view) {
        startCaptureActivity(Util.TIME, 2000.0);
    }

    public void startSeconds5(View view) {
        startCaptureActivity(Util.TIME, 5000.0);
    }

    public void startSeconds10(View view) {
        startCaptureActivity(Util.TIME, 10000.0);
    }

    public void startSeconds15(View view) {
        startCaptureActivity(Util.TIME, 15000.0);
    }

    public void startSeconds20(View view) {
        startCaptureActivity(Util.TIME, 20000.0);
    }

    public void startOtherTime(View view) {
        DialogFragment dialog = new TimeDialog();
        dialog.show(getFragmentManager(), "TimeDialog");
    }

    @Override
    public void onDPositiveClick(DialogFragment dialog, Double value) {
        startCaptureActivity(Util.DISTANCE, value);
    }

    @Override
    public void onTPositiveClick(DialogFragment dialog, Double value) {
        startCaptureActivity(Util.TIME, value * 1000.0);
    }
}
