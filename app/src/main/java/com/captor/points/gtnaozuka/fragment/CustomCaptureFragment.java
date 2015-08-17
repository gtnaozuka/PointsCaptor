package com.captor.points.gtnaozuka.fragment;

import android.app.DialogFragment;
import android.content.Context;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.captor.points.gtnaozuka.dialog.CapturedPointsDialog;
import com.captor.points.gtnaozuka.dialog.StopConfirmationDialog;
import com.captor.points.gtnaozuka.pointscaptor.MainActivity;
import com.captor.points.gtnaozuka.pointscaptor.R;
import com.captor.points.gtnaozuka.util.DataOperations;
import com.captor.points.gtnaozuka.util.Values;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;

public class CustomCaptureFragment extends CaptureFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_custom_capture, container, false);

        ImageButton imgButton = (ImageButton) rootView.findViewById(R.id.mapsButton);
        imgButton.setEnabled(false);
        imgButton = (ImageButton) rootView.findViewById(R.id.btnPlus);
        imgButton.setEnabled(false);
        imgButton = (ImageButton) rootView.findViewById(R.id.btnStop);
        imgButton.setEnabled(false);

        bundle = this.getArguments();
        if (bundle != null) {
            dataPoint = bundle.getParcelableArrayList(Values.DATA_POINT_MSG);
            dataLocation = bundle.getParcelableArrayList(Values.DATA_LOCATION_MSG);
        } else
            bundle = new Bundle();

        if (dataPoint == null) {
            dataPoint = new ArrayList<>();
            dataLocation = new ArrayList<>();
            pointsNum = 0;
        } else {
            pointsNum = dataPoint.size();
            context.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    final TextView txtView = (TextView) rootView.findViewById(R.id.txtPointsNumber);
                    txtView.setText(pointsNum.toString());
                }
            });

            DialogFragment dialog = new CapturedPointsDialog();
            dialog.setCancelable(false);
            dialog.show(context.getFragmentManager(), "CapturedPointsDialog");
        }
        needsRequestLocation = false;

        locationRequest = new LocationRequest();
        locationRequest.setInterval(INTERVAL);
        locationRequest.setFastestInterval(FASTEST_INTERVAL);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        googleApiClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        googleApiClient.connect();

        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        locationManager.addGpsStatusListener(this);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            setOnProviderEnabled();
        }

        return rootView;
    }

    @Override
    public void backPress() {
        if (pointsNum.equals(0)) {
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    googleApiClient, this);
            context.finish();
        } else {
            ImageButton imgButton = (ImageButton) rootView.findViewById(R.id.mapsButton);
            imgButton.setEnabled(true);

            DialogFragment dialog = new StopConfirmationDialog();
            Bundle dialogBundle = new Bundle();
            dialogBundle.putInt(Values.POSITION_MSG, -1);
            dialog.setArguments(dialogBundle);
            dialog.show(context.getFragmentManager(), "StopConfirmationDialog");
        }
    }

    @Override
    public void changeDrawerItem(int position) {
        if (pointsNum.equals(0)) {
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    googleApiClient, this);
            MainActivity ma = (MainActivity) context;
            ma.displayView(position);
        } else {
            ImageButton imgButton = (ImageButton) rootView.findViewById(R.id.mapsButton);
            imgButton.setEnabled(true);

            DialogFragment dialog = new StopConfirmationDialog();
            Bundle dialogBundle = new Bundle();
            dialogBundle.putInt(Values.POSITION_MSG, position);
            dialog.setArguments(dialogBundle);
            dialog.show(context.getFragmentManager(), "StopConfirmationDialog");
        }
    }

    public void saveNewPoint() {
        if (pointsNum == 0) {
            ImageButton imgButton = (ImageButton) rootView.findViewById(R.id.btnStop);
            imgButton.setEnabled(true);
        }

        dataPoint.add(DataOperations.convertLocationToPoint(this.location));
        dataLocation.add(DataOperations.createNewLocation(this.location));

        pointsNum++;
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final TextView txtView = (TextView) rootView.findViewById(R.id.txtPointsNumber);
                txtView.setText(pointsNum.toString());
            }
        });
    }

    @Override
    public void stopRecord() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                googleApiClient, this);

        ImageButton imgButton = (ImageButton) rootView.findViewById(R.id.btnStop);
        imgButton.setEnabled(false);
        imgButton = (ImageButton) rootView.findViewById(R.id.mapsButton);
        imgButton.setEnabled(true);

        DialogFragment dialog = new CapturedPointsDialog();
        dialog.setCancelable(false);
        dialog.show(context.getFragmentManager(), "CapturedPointsDialog");
    }

    @Override
    public void onSCPositiveClick(int position) {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                googleApiClient, this);
        if (position == -1)
            context.finish();
        else {
            MainActivity ma = (MainActivity) context;
            ma.displayView(position);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        this.location = location;

        if (isConnecting) {
            ImageButton imgButton = (ImageButton) rootView.findViewById(R.id.mapsButton);
            imgButton.setEnabled(true);
            imgButton = (ImageButton) rootView.findViewById(R.id.btnPlus);
            imgButton.setEnabled(true);
            if (pointsNum != 0) {
                imgButton = (ImageButton) rootView.findViewById(R.id.btnStop);
                imgButton.setEnabled(true);
            }

            isConnecting = false;
        }

        updateLocationUI();
        updateAddressUI();
    }

    @Override
    public void onGpsStatusChanged(int event) {
        if (event == GpsStatus.GPS_EVENT_STARTED) {
            setOnProviderEnabled();
        } else if (event == GpsStatus.GPS_EVENT_STOPPED) {
            ImageButton imgButton;
            imgButton = (ImageButton) rootView.findViewById(R.id.btnPlus);
            imgButton.setEnabled(false);
            imgButton = (ImageButton) rootView.findViewById(R.id.btnStop);
            imgButton.setEnabled(false);
            imgButton = (ImageButton) rootView.findViewById(R.id.mapsButton);
            imgButton.setEnabled(false);

            context.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    TextView txtView = (TextView) rootView.findViewById(R.id.txtLocation);
                    txtView.setText(R.string.location);
                    ProgressBar pBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
                    pBar.setVisibility(View.GONE);
                    ImageButton imgButton = (ImageButton) rootView.findViewById(R.id.gpsButton);
                    imgButton.setVisibility(View.VISIBLE);
                }
            });

            Toast toast = Toast.makeText(context, R.string.gps_disabled, Toast.LENGTH_SHORT);
            toast.show();
        }
    }
}
