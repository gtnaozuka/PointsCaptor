package com.captor.points.gtnaozuka.fragment;

import android.app.DialogFragment;
import android.location.GpsStatus;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.captor.points.gtnaozuka.dialog.StopConfirmationDialog;
import com.captor.points.gtnaozuka.pointscaptor.MainActivity;
import com.captor.points.gtnaozuka.pointscaptor.R;
import com.captor.points.gtnaozuka.util.DataOperations;
import com.captor.points.gtnaozuka.util.DisplayToast;
import com.captor.points.gtnaozuka.util.Values;
import com.google.android.gms.location.LocationServices;

public class CustomCaptureFragment extends CaptureFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_custom_capture, container, false);

        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ImageButton imgButton = (ImageButton) rootView.findViewById(R.id.mapsButton);
                imgButton.setEnabled(false);
                imgButton = (ImageButton) rootView.findViewById(R.id.btnPlus);
                imgButton.setEnabled(false);
                imgButton = (ImageButton) rootView.findViewById(R.id.btnStop);
                imgButton.setEnabled(false);
            }
        });

        bundle = this.getArguments();
        if (bundle != null) {
            dataPoint = bundle.getParcelableArrayList(Values.DATA_POINT_MSG);
            dataLocation = bundle.getParcelableArrayList(Values.DATA_LOCATION_MSG);
        } else
            bundle = new Bundle();

        initialize();

        return rootView;
    }

    @Override
    public void backPress() {
        if (pointsNum == 0) {
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    googleApiClient, this);
            context.finish();
        } else {
            context.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ImageButton imgButton = (ImageButton) rootView.findViewById(R.id.mapsButton);
                    imgButton.setEnabled(true);
                }
            });

            DialogFragment dialog = new StopConfirmationDialog();
            Bundle dialogBundle = new Bundle();
            dialogBundle.putInt(Values.POSITION_MSG, -1);
            dialog.setArguments(dialogBundle);
            dialog.show(context.getFragmentManager(), "StopConfirmationDialog");
        }
    }

    @Override
    public void changeDrawerItem(int position) {
        if (pointsNum == 0) {
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    googleApiClient, this);
            MainActivity ma = (MainActivity) context;
            ma.displayView(position);
        } else {
            context.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ImageButton imgButton = (ImageButton) rootView.findViewById(R.id.mapsButton);
                    imgButton.setEnabled(true);
                }
            });

            DialogFragment dialog = new StopConfirmationDialog();
            Bundle dialogBundle = new Bundle();
            dialogBundle.putInt(Values.POSITION_MSG, position);
            dialog.setArguments(dialogBundle);
            dialog.show(context.getFragmentManager(), "StopConfirmationDialog");
        }
    }

    public void saveNewPoint() {
        if (pointsNum == 0) {
            context.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ImageButton imgButton = (ImageButton) rootView.findViewById(R.id.btnStop);
                    imgButton.setEnabled(true);
                }
            });
        }

        dataPoint.add(DataOperations.convertLocationToPoint(this.location));
        dataLocation.add(DataOperations.createNewLocation(this.location));

        pointsNum++;
        updatePointsNumUI();
    }

    @Override
    public void stopRecord() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                googleApiClient, this);

        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ImageButton imgButton = (ImageButton) rootView.findViewById(R.id.btnStop);
                imgButton.setEnabled(false);
                imgButton = (ImageButton) rootView.findViewById(R.id.mapsButton);
                imgButton.setEnabled(true);
            }
        });

        onDCNegativeClick();
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
    public void onLocationChanged(android.location.Location location) {
        if (!this.isAdded())
            return;

        this.location = location;

        if (isConnecting) {
            context.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ImageButton imgButton = (ImageButton) rootView.findViewById(R.id.mapsButton);
                    imgButton.setEnabled(true);
                    imgButton = (ImageButton) rootView.findViewById(R.id.btnPlus);
                    imgButton.setEnabled(true);
                    if (pointsNum != 0) {
                        imgButton = (ImageButton) rootView.findViewById(R.id.btnStop);
                        imgButton.setEnabled(true);
                    }
                }
            });

            isConnecting = false;
        }

        updateLocationUI();
        updateAddressUI();
    }

    @Override
    public void onGpsStatusChanged(int event) {
        if (!this.isAdded())
            return;

        if (event == GpsStatus.GPS_EVENT_STARTED) {
            setOnProviderEnabled();
        } else if (event == GpsStatus.GPS_EVENT_STOPPED) {
            context.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ImageButton imgButton;
                    imgButton = (ImageButton) rootView.findViewById(R.id.btnPlus);
                    imgButton.setEnabled(false);
                    imgButton = (ImageButton) rootView.findViewById(R.id.btnStop);
                    imgButton.setEnabled(false);
                    imgButton = (ImageButton) rootView.findViewById(R.id.mapsButton);
                    imgButton.setEnabled(false);
                }
            });

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

            new Handler().post(new DisplayToast(context, getResources().getString(R.string.gps_disabled)));
        }
    }
}
