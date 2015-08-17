package com.captor.points.gtnaozuka.fragment;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.GpsStatus;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.captor.points.gtnaozuka.dialog.CapturedPointsDialog;
import com.captor.points.gtnaozuka.dialog.DiscardConfirmationDialog;
import com.captor.points.gtnaozuka.entity.Location;
import com.captor.points.gtnaozuka.entity.Point;
import com.captor.points.gtnaozuka.pointscaptor.MapsActivity;
import com.captor.points.gtnaozuka.pointscaptor.R;
import com.captor.points.gtnaozuka.util.DataOperations;
import com.captor.points.gtnaozuka.util.FileOperations;
import com.captor.points.gtnaozuka.util.FragmentOperations;
import com.captor.points.gtnaozuka.util.Values;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public abstract class CaptureFragment extends Fragment implements LocationListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        GpsStatus.Listener {

    protected Integer pointsNum;
    protected boolean isConnecting, needsRequestLocation;
    protected ArrayList<Point> dataPoint;
    protected ArrayList<Location> dataLocation;
    protected android.location.Location location;
    protected List<Address> addresses;

    protected LocationRequest locationRequest;
    protected GoogleApiClient googleApiClient;
    protected static final long INTERVAL = 10000, FASTEST_INTERVAL = 5000;

    protected View rootView;
    protected AppCompatActivity context;
    protected Bundle bundle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

    public void resume() {
        if (needsRequestLocation) {
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    googleApiClient, locationRequest, this);
            needsRequestLocation = false;
        }
    }

    public abstract void backPress();

    public abstract void changeDrawerItem(int position);

    public abstract void stopRecord();

    public void startMapsActivity() {
        needsRequestLocation = true;
        LocationServices.FusedLocationApi.removeLocationUpdates(
                googleApiClient, this);
        viewInGoogleMaps(false);
    }

    public void turnGpsOn() {
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(intent);
    }

    public void viewCapturedPoints(DialogFragment dialog) {
        dialog.dismiss();

        bundle.putParcelableArrayList(Values.DATA_POINT_MSG, dataPoint);
        bundle.putParcelableArrayList(Values.DATA_LOCATION_MSG, dataLocation);

        FragmentOperations.newFragment(context, new CapturedPointsFragment(), bundle, getResources().getString(R.string.fragment_captured_points));
    }

    public void viewInGoogleMaps(boolean finished) {
        Intent intent = new Intent(context, MapsActivity.class);
        if (pointsNum == 0) {
            intent.putExtra(Values.STATUS_MSG, Values.NOT_STARTED);
            intent.putExtra(Values.CURRENT_LOCATION_MSG, DataOperations.createNewLocation(this.location));
        } else {
            intent.putParcelableArrayListExtra(Values.DATA_LOCATION_MSG, dataLocation);
            if (!finished)
                intent.putExtra(Values.STATUS_MSG, Values.STARTED);
            else
                intent.putExtra(Values.STATUS_MSG, Values.FINISHED);
        }
        startActivity(intent);
    }

    public void removeRepeatedData() {
        dataLocation = DataOperations.removeRepeatedLocations(dataLocation);
        dataPoint = DataOperations.removeRepeatedPoints(dataPoint);

        Toast toast = Toast.makeText(context, getResources().getString(R.string.removed_successfully), Toast.LENGTH_SHORT);
        toast.show();
    }

    public void storeInMemory() {
        String content = DataOperations.convertLocationsToString(context, dataLocation) + "\n\n\n" +
                DataOperations.convertPointsToString(context, dataPoint);
        File f = FileOperations.storeFile(context, FileOperations.FILES_PATH, content);
        if (f == null)
            return;

        Toast toast = Toast.makeText(context, "'" + f.getName() +
                "'" + getResources().getString(R.string.saved_successfully), Toast.LENGTH_SHORT);
        toast.show();
    }

    public void shareWithSomeone() {
        String content = DataOperations.convertLocationsToString(context, dataLocation) + "\n\n\n" +
                DataOperations.convertPointsToString(context, dataPoint);
        File f = FileOperations.storeFile(context, FileOperations.SENT_PATH, content);
        if (f == null)
            return;

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setData(Uri.parse("mailto:"));
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.email_subject));
        intent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.email_body));
        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(f));

        try {
            startActivity(Intent.createChooser(intent, getResources().getString(R.string.choose_option)));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast toast = Toast.makeText(context, R.string.no_email_client, Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    public void startNewCapture(DialogFragment dialog) {
        dialog.dismiss();

        DialogFragment dialog2 = new DiscardConfirmationDialog();
        dialog2.show(context.getFragmentManager(), "DiscardConfirmationDialog");
    }

    public void onDCPositiveClick() {
        LocationServices.FusedLocationApi.requestLocationUpdates(
                googleApiClient, locationRequest, this);
        dataPoint = new ArrayList<>();
        dataLocation = new ArrayList<>();
        pointsNum = 0;

        TextView txtView = (TextView) rootView.findViewById(R.id.txtPointsNumber);
        txtView.setText(pointsNum.toString());
    }

    public void onDCNegativeClick() {
        DialogFragment dialog2 = new CapturedPointsDialog();
        dialog2.setCancelable(false);
        dialog2.show(context.getFragmentManager(), "CapturedPointsDialog");
    }

    public abstract void onSCPositiveClick(int position);

    @Override
    public void onConnected(Bundle connectionHint) {
        LocationServices.FusedLocationApi.requestLocationUpdates(
                googleApiClient, locationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d("Connection", "suspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d("Connection", "failed");
    }

    protected void updateLocationUI() {
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ProgressBar pBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
                pBar.setVisibility(View.GONE);

                TextView txtView = (TextView) rootView.findViewById(R.id.txtLocation);
                txtView.setText(getResources().getString(R.string.latitude_dots) + location.getLatitude() + "\n" +
                        getResources().getString(R.string.longitude_dots) + location.getLongitude() + "\n");
            }
        });
    }

    protected void updateAddressUI() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                    addresses = geocoder.getFromLocation(location.getLatitude(),
                            location.getLongitude(), 1);

                    context.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            TextView txtView = (TextView) rootView.findViewById(R.id.txtLocation);
                            txtView.setText(txtView.getText() +
                                    addresses.get(0).getAddressLine(0) + ", " + addresses.get(0).getAddressLine(1) + ", " +
                                    addresses.get(0).getAddressLine(2));
                        }
                    });
                } catch (IOException e) {
                    Log.d("IOException", "thrown");
                }
            }
        }).start();
    }

    protected void setOnProviderEnabled() {
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ImageButton imgButton = (ImageButton) rootView.findViewById(R.id.gpsButton);
                imgButton.setVisibility(View.GONE);
                ProgressBar pBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
                pBar.setVisibility(View.VISIBLE);
            }
        });

        isConnecting = true;
    }
}
