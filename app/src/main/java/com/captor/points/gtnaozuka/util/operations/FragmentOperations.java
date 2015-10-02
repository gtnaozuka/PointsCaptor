package com.captor.points.gtnaozuka.util.operations;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.captor.points.gtnaozuka.pointscaptor.MainActivity;
import com.captor.points.gtnaozuka.pointscaptor.R;

public class FragmentOperations {

    public static void newFragment(AppCompatActivity context, Fragment fragment, Bundle bundle, String tag) {
        fragment.setArguments(bundle);
        changeFragment(context, fragment, tag);

        MainActivity ma = (MainActivity) context;
        ma.setFragment(fragment);
    }

    public static void oldFragment(AppCompatActivity context) {
        FragmentManager fragmentManager = context.getSupportFragmentManager();
        fragmentManager.popBackStackImmediate();

        MainActivity ma = (MainActivity) context;
        ma.setFragment(getCurrentFragment(fragmentManager));
    }

    public static void changeFragment(AppCompatActivity context, Fragment fragment, String tag) {
        FragmentManager fragmentManager = context.getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container_body, fragment, tag);
        fragmentTransaction.addToBackStack(tag);
        fragmentTransaction.commit();
        fragmentManager.executePendingTransactions();
    }

    private static Fragment getCurrentFragment(FragmentManager fragmentManager) {
        String fragmentTag = fragmentManager.getBackStackEntryAt(fragmentManager.getBackStackEntryCount() - 1).getName();
        return fragmentManager.findFragmentByTag(fragmentTag);
    }

    public static void clearBackStack(AppCompatActivity context) {
        FragmentManager fragmentManager = context.getSupportFragmentManager();
        for (int i = 0; i < fragmentManager.getBackStackEntryCount(); i++) {
            fragmentManager.popBackStackImmediate();
        }
    }
}
