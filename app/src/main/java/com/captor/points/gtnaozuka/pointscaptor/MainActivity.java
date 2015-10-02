package com.captor.points.gtnaozuka.pointscaptor;

import android.app.DialogFragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.captor.points.gtnaozuka.dialog.CapturedPointsDialog;
import com.captor.points.gtnaozuka.dialog.DiscardConfirmationDialog;
import com.captor.points.gtnaozuka.dialog.DistanceDialog;
import com.captor.points.gtnaozuka.dialog.DataDialog;
import com.captor.points.gtnaozuka.dialog.InfoDialog;
import com.captor.points.gtnaozuka.dialog.LanguageDialog;
import com.captor.points.gtnaozuka.dialog.StopConfirmationDialog;
import com.captor.points.gtnaozuka.dialog.TimeDialog;
import com.captor.points.gtnaozuka.fragment.BoundaryDefinitionFragment;
import com.captor.points.gtnaozuka.fragment.CaptureTypeFragment;
import com.captor.points.gtnaozuka.fragment.CustomCaptureFragment;
import com.captor.points.gtnaozuka.fragment.DataFragment;
import com.captor.points.gtnaozuka.fragment.DefaultCaptureFragment;
import com.captor.points.gtnaozuka.fragment.DrawerFragment;
import com.captor.points.gtnaozuka.fragment.FileManagerFragment;
import com.captor.points.gtnaozuka.util.DisplayToast;
import com.captor.points.gtnaozuka.util.operations.FragmentOperations;
import com.captor.points.gtnaozuka.util.Constants;

public class MainActivity extends AppCompatActivity implements LanguageDialog.LanguageListener,
        DrawerFragment.FragmentDrawerListener, DistanceDialog.DistanceListener,
        TimeDialog.TimeListener, CapturedPointsDialog.CapturedPointsListener,
        DiscardConfirmationDialog.DiscardConfirmationListener,
        StopConfirmationDialog.StopConfirmationListener, DataDialog.FileManagerListener {

    private Fragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragment = null;

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        DrawerFragment drawerFragment = (DrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), toolbar);
        drawerFragment.setDrawerListener(this);

        displayView(0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (fragment instanceof DefaultCaptureFragment) {
            DefaultCaptureFragment dcf = (DefaultCaptureFragment) fragment;
            dcf.resume();
        } else if (fragment instanceof CustomCaptureFragment) {
            CustomCaptureFragment ccf = (CustomCaptureFragment) fragment;
            ccf.resume();
        }
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
            if (fragment instanceof CustomCaptureFragment) {
                CustomCaptureFragment ccf = (CustomCaptureFragment) fragment;
                ccf.backPress();
            } else
                finish();
        } else {
            if (fragment instanceof DefaultCaptureFragment) {
                DefaultCaptureFragment dcf = (DefaultCaptureFragment) fragment;
                dcf.backPress();
            } else
                FragmentOperations.oldFragment(this);
        }
    }

    @Override
    public void onDrawerItemSelected(View view, int position) {
        if (fragment instanceof DefaultCaptureFragment) {
            DefaultCaptureFragment dcf = (DefaultCaptureFragment) fragment;
            dcf.changeDrawerItem(position);
        } else if (fragment instanceof CustomCaptureFragment) {
            CustomCaptureFragment ccf = (CustomCaptureFragment) fragment;
            ccf.changeDrawerItem(position);
        } else
            displayView(position);
    }

    public void displayView(int position) {
        String title = getString(R.string.app_name);
        String tag = null;
        switch (position) {
            case 0:
                fragment = new CaptureTypeFragment();
                title = getString(R.string.nav_item_default_capture);
                tag = getResources().getString(R.string.fragment_capture_type);
                break;
            case 1:
                fragment = new CustomCaptureFragment();
                title = getResources().getString(R.string.nav_item_custom_capture);
                tag = getResources().getString(R.string.fragment_custom_capture);
                break;
            case 2:
                fragment = new BoundaryDefinitionFragment();
                title = getResources().getString(R.string.nav_item_boundary_definition);
                tag = getResources().getString(R.string.fragment_boundary_definition);
                break;
            case 3:
                break;
            case 4:
                fragment = new FileManagerFragment();
                title = getResources().getString(R.string.nav_item_files_manager);
                tag = getResources().getString(R.string.fragment_file_manager);
        }

        if (fragment != null) {
            FragmentOperations.clearBackStack(this);
            FragmentOperations.changeFragment(this, fragment, tag);
            getSupportActionBar().setTitle(title);
        }
    }

    public void setFragment(Fragment fragment) {
        this.fragment = fragment;
    }

    public Fragment getFragment() {
        return fragment;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.info_button:
                showInfoDialog();
                return true;
            case R.id.language_button:
                showLanguageDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void showInfoDialog() {
        DialogFragment dialog = new InfoDialog();
        dialog.show(getFragmentManager(), "InfoDialog");
    }

    public void showLanguageDialog() {
        DialogFragment dialog = new LanguageDialog();
        dialog.show(getFragmentManager(), "LanguageDialog");
    }

    @Override
    public void setLanguage(DialogFragment dialog, String language) {
        dialog.dismiss();

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("Language", language);
        editor.apply();

        new Handler().post(new DisplayToast(getApplicationContext(), getResources().getString(R.string.reboot)));
    }

    public void startMeter1(View view) {
        CaptureTypeFragment ctf = (CaptureTypeFragment) fragment;
        ctf.initDefaultCapture(Constants.DISTANCE, 1.0);
    }

    public void startMeters2(View view) {
        CaptureTypeFragment ctf = (CaptureTypeFragment) fragment;
        ctf.initDefaultCapture(Constants.DISTANCE, 2.0);
    }

    public void startMeters5(View view) {
        CaptureTypeFragment ctf = (CaptureTypeFragment) fragment;
        ctf.initDefaultCapture(Constants.DISTANCE, 5.0);
    }

    public void startMeters10(View view) {
        CaptureTypeFragment ctf = (CaptureTypeFragment) fragment;
        ctf.initDefaultCapture(Constants.DISTANCE, 10.0);
    }

    public void startMeters15(View view) {
        CaptureTypeFragment ctf = (CaptureTypeFragment) fragment;
        ctf.initDefaultCapture(Constants.DISTANCE, 15.0);
    }

    public void startMeters20(View view) {
        CaptureTypeFragment ctf = (CaptureTypeFragment) fragment;
        ctf.initDefaultCapture(Constants.DISTANCE, 20.0);
    }

    public void startOtherDistance(View view) {
        DialogFragment dialog = new DistanceDialog();
        dialog.show(getFragmentManager(), "DistanceDialog");
    }

    public void startSecond1(View view) {
        CaptureTypeFragment ctf = (CaptureTypeFragment) fragment;
        ctf.initDefaultCapture(Constants.TIME, 1000.0);
    }

    public void startSeconds2(View view) {
        CaptureTypeFragment ctf = (CaptureTypeFragment) fragment;
        ctf.initDefaultCapture(Constants.TIME, 2000.0);
    }

    public void startSeconds5(View view) {
        CaptureTypeFragment ctf = (CaptureTypeFragment) fragment;
        ctf.initDefaultCapture(Constants.TIME, 5000.0);
    }

    public void startSeconds10(View view) {
        CaptureTypeFragment ctf = (CaptureTypeFragment) fragment;
        ctf.initDefaultCapture(Constants.TIME, 10000.0);
    }

    public void startSeconds15(View view) {
        CaptureTypeFragment ctf = (CaptureTypeFragment) fragment;
        ctf.initDefaultCapture(Constants.TIME, 15000.0);
    }

    public void startSeconds20(View view) {
        CaptureTypeFragment ctf = (CaptureTypeFragment) fragment;
        ctf.initDefaultCapture(Constants.TIME, 20000.0);
    }

    public void startOtherTime(View view) {
        DialogFragment dialog = new TimeDialog();
        dialog.show(getFragmentManager(), "TimeDialog");
    }

    @Override
    public void onDPositiveClick(DialogFragment dialog, Double value) {
        CaptureTypeFragment ctf = (CaptureTypeFragment) fragment;
        ctf.initDefaultCapture(Constants.DISTANCE, value);
    }

    @Override
    public void onTPositiveClick(DialogFragment dialog, Double value) {
        CaptureTypeFragment ctf = (CaptureTypeFragment) fragment;
        ctf.initDefaultCapture(Constants.TIME, value * 1000.0);
    }

    public void playRecord(View view) {
        DefaultCaptureFragment dcf = (DefaultCaptureFragment) fragment;
        dcf.playRecord();
    }

    public void saveNewPoint(View view) {
        CustomCaptureFragment ccf = (CustomCaptureFragment) fragment;
        ccf.saveNewPoint();
    }

    public void stopRecord(View view) {
        if (fragment instanceof DefaultCaptureFragment) {
            DefaultCaptureFragment dcf = (DefaultCaptureFragment) fragment;
            dcf.stopRecord();
        } else if (fragment instanceof CustomCaptureFragment) {
            CustomCaptureFragment ccf = (CustomCaptureFragment) fragment;
            ccf.stopRecord();
        }
    }

    public void startMapsActivity(View view) {
        if (fragment instanceof DefaultCaptureFragment) {
            DefaultCaptureFragment dcf = (DefaultCaptureFragment) fragment;
            dcf.startMapsActivity();
        } else if (fragment instanceof CustomCaptureFragment) {
            CustomCaptureFragment ccf = (CustomCaptureFragment) fragment;
            ccf.startMapsActivity();
        }
    }

    public void turnGpsOn(View view) {
        if (fragment instanceof DefaultCaptureFragment) {
            DefaultCaptureFragment dcf = (DefaultCaptureFragment) fragment;
            dcf.turnGpsOn();
        } else if (fragment instanceof CustomCaptureFragment) {
            CustomCaptureFragment ccf = (CustomCaptureFragment) fragment;
            ccf.turnGpsOn();
        }
    }

    @Override
    public void viewCapturedPoints(DialogFragment dialog) {
        if (fragment instanceof DefaultCaptureFragment) {
            DefaultCaptureFragment dcf = (DefaultCaptureFragment) fragment;
            dcf.viewCapturedPoints(dialog);
        } else if (fragment instanceof CustomCaptureFragment) {
            CustomCaptureFragment ccf = (CustomCaptureFragment) fragment;
            ccf.viewCapturedPoints(dialog);
        } else if (fragment instanceof FileManagerFragment) {
            Fragment child = fragment.getChildFragmentManager().getFragments().get(0);
            if (child instanceof DataFragment) {
                DataFragment df = (DataFragment) child;
                df.viewCapturedPoints(dialog);
            }
        }
    }

    @Override
    public void viewInGoogleMaps(DialogFragment dialog) {
        if (fragment instanceof DefaultCaptureFragment) {
            DefaultCaptureFragment dcf = (DefaultCaptureFragment) fragment;
            dcf.viewInGoogleMaps(true);
        } else if (fragment instanceof CustomCaptureFragment) {
            CustomCaptureFragment ccf = (CustomCaptureFragment) fragment;
            ccf.viewInGoogleMaps(true);
        } else if (fragment instanceof FileManagerFragment) {
            Fragment child = fragment.getChildFragmentManager().getFragments().get(0);
            if (child instanceof DataFragment) {
                DataFragment df = (DataFragment) child;
                df.viewInGoogleMaps();
            }
        }
    }

    @Override
    public void removeRepeatedData(DialogFragment dialog) {
        if (fragment instanceof DefaultCaptureFragment) {
            DefaultCaptureFragment dcf = (DefaultCaptureFragment) fragment;
            dcf.removeRepeatedData();
        } else if (fragment instanceof CustomCaptureFragment) {
            CustomCaptureFragment ccf = (CustomCaptureFragment) fragment;
            ccf.removeRepeatedData();
        } else if (fragment instanceof FileManagerFragment) {
            Fragment child = fragment.getChildFragmentManager().getFragments().get(0);
            if (child instanceof DataFragment) {
                DataFragment df = (DataFragment) child;
                df.removeRepeatedData();
            }
        }
    }

    @Override
    public void storeInMemory(DialogFragment dialog) {
        if (fragment instanceof DefaultCaptureFragment) {
            DefaultCaptureFragment dcf = (DefaultCaptureFragment) fragment;
            dcf.storeInMemory();
        } else if (fragment instanceof CustomCaptureFragment) {
            CustomCaptureFragment ccf = (CustomCaptureFragment) fragment;
            ccf.storeInMemory();
        }
    }

    @Override
    public void shareWithSomeone(DialogFragment dialog) {
        if (fragment instanceof DefaultCaptureFragment) {
            DefaultCaptureFragment dcf = (DefaultCaptureFragment) fragment;
            dcf.shareWithSomeone();
        } else if (fragment instanceof CustomCaptureFragment) {
            CustomCaptureFragment ccf = (CustomCaptureFragment) fragment;
            ccf.shareWithSomeone();
        } else if (fragment instanceof FileManagerFragment) {
            Fragment child = fragment.getChildFragmentManager().getFragments().get(0);
            if (child instanceof DataFragment) {
                DataFragment df = (DataFragment) child;
                df.shareWithSomeone();
            }
        }
    }

    @Override
    public void deleteFile(DialogFragment dialog) {
        Fragment child = fragment.getChildFragmentManager().getFragments().get(0);
        if (child instanceof DataFragment) {
            DataFragment df = (DataFragment) child;
            df.deleteFile(dialog);
        }
    }

    @Override
    public void startNewCapture(DialogFragment dialog) {
        if (fragment instanceof DefaultCaptureFragment) {
            DefaultCaptureFragment dcf = (DefaultCaptureFragment) fragment;
            dcf.startNewCapture(dialog);
        } else if (fragment instanceof CustomCaptureFragment) {
            CustomCaptureFragment ccf = (CustomCaptureFragment) fragment;
            ccf.startNewCapture(dialog);
        }
    }

    @Override
    public void onDCPositiveClick(DialogFragment dialog) {
        if (fragment instanceof DefaultCaptureFragment) {
            DefaultCaptureFragment dcf = (DefaultCaptureFragment) fragment;
            dcf.onDCPositiveClick();
        } else if (fragment instanceof CustomCaptureFragment) {
            CustomCaptureFragment ccf = (CustomCaptureFragment) fragment;
            ccf.onDCPositiveClick();
        }
    }

    @Override
    public void onDCNegativeClick(DialogFragment dialog) {
        if (fragment instanceof DefaultCaptureFragment) {
            DefaultCaptureFragment dcf = (DefaultCaptureFragment) fragment;
            dcf.onDCNegativeClick();
        } else if (fragment instanceof CustomCaptureFragment) {
            CustomCaptureFragment ccf = (CustomCaptureFragment) fragment;
            ccf.onDCNegativeClick();
        }
    }

    @Override
    public void onSCPositiveClick(DialogFragment dialog, int position) {
        if (fragment instanceof DefaultCaptureFragment) {
            DefaultCaptureFragment dcf = (DefaultCaptureFragment) fragment;
            dcf.onSCPositiveClick(position);
        } else if (fragment instanceof CustomCaptureFragment) {
            CustomCaptureFragment ccf = (CustomCaptureFragment) fragment;
            ccf.onSCPositiveClick(position);
        }
    }
}
