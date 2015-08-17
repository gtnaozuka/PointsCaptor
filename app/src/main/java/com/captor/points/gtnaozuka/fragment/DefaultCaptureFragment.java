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
import com.captor.points.gtnaozuka.util.FragmentOperations;
import com.captor.points.gtnaozuka.util.Values;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;

public class DefaultCaptureFragment extends CaptureFragment {

    private Integer type;
    private Double value;
    private int status;
    private static final int PLAYED = 1, PAUSED = 0, STOPPED = -1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_default_capture, container, false);

        ImageButton imgButton = (ImageButton) rootView.findViewById(R.id.mapsButton);
        imgButton.setEnabled(false);
        imgButton = (ImageButton) rootView.findViewById(R.id.btnPlay);
        imgButton.setEnabled(false);
        imgButton = (ImageButton) rootView.findViewById(R.id.btnStop);
        imgButton.setEnabled(false);

        bundle = this.getArguments();
        if (bundle != null) {
            type = bundle.getInt(Values.TYPE_MSG, 0);
            value = bundle.getDouble(Values.VALUE_MSG, 0.0);
            dataPoint = bundle.getParcelableArrayList(Values.DATA_POINT_MSG);
            dataLocation = bundle.getParcelableArrayList(Values.DATA_LOCATION_MSG);
        }

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
        status = STOPPED;
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
            FragmentOperations.oldFragment(context);
        } else {
            if (status == PLAYED)
                pauseRecord(true);

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
            if (status == PLAYED)
                pauseRecord(true);

            DialogFragment dialog = new StopConfirmationDialog();
            Bundle dialogBundle = new Bundle();
            dialogBundle.putInt(Values.POSITION_MSG, position);
            dialog.setArguments(dialogBundle);
            dialog.show(context.getFragmentManager(), "StopConfirmationDialog");
        }
    }

    public void playRecord() {
        ImageButton imgButton = (ImageButton) rootView.findViewById(R.id.btnPlay);
        if (status == STOPPED) {
            status = PLAYED;

            Runnable r = null;
            if (type.equals(Values.DISTANCE)) {
                r = threadByDistance();
            } else if (type.equals(Values.TIME)) {
                r = threadByTime();
            }
            Thread t = new Thread(r);
            t.start();

            imgButton.setImageResource(R.drawable.ic_action_pause);
            imgButton = (ImageButton) rootView.findViewById(R.id.btnStop);
            imgButton.setEnabled(true);
            imgButton = (ImageButton) rootView.findViewById(R.id.mapsButton);
            imgButton.setEnabled(false);
        } else if (status == PAUSED) {
            status = PLAYED;

            imgButton.setImageResource(R.drawable.ic_action_pause);
            imgButton = (ImageButton) rootView.findViewById(R.id.mapsButton);
            imgButton.setEnabled(false);
        } else if (status == PLAYED) {
            pauseRecord(true);
        }
    }

    @Override
    public void stopRecord() {
        status = STOPPED;
        LocationServices.FusedLocationApi.removeLocationUpdates(
                googleApiClient, this);

        ImageButton imgButton = (ImageButton) rootView.findViewById(R.id.btnPlay);
        imgButton.setImageResource(R.drawable.ic_action_play);
        imgButton = (ImageButton) rootView.findViewById(R.id.btnStop);
        imgButton.setEnabled(false);
        imgButton = (ImageButton) rootView.findViewById(R.id.mapsButton);
        imgButton.setEnabled(true);

        DialogFragment dialog = new CapturedPointsDialog();
        dialog.setCancelable(false);
        dialog.show(context.getFragmentManager(), "CapturedPointsDialog");
    }

    private void pauseRecord(boolean enabled) {
        status = PAUSED;

        ImageButton imgButton = (ImageButton) rootView.findViewById(R.id.btnPlay);
        imgButton.setImageResource(R.drawable.ic_action_play);
        imgButton = (ImageButton) rootView.findViewById(R.id.mapsButton);
        imgButton.setEnabled(enabled);
    }

    private Runnable threadByDistance() {
        return new Runnable() {
            public void run() {
                while (status != STOPPED) {
                    if (status == PLAYED) {
                        if (pointsNum.equals(0)) {
                            saveNewPoint();
                        } else {
                            com.captor.points.gtnaozuka.entity.Location src = dataLocation.get(pointsNum - 1);
                            com.captor.points.gtnaozuka.entity.Location dest = DataOperations.createNewLocation(location);
                            if (DataOperations.calculateDistance(src, dest) >= value) {
                                saveNewPoint();
                            }
                        }
                    }
                }
            }
        };
    }

    private Runnable threadByTime() {
        return new Runnable() {
            public void run() {
                while (status != STOPPED) {
                    if (status == PLAYED) {
                        saveNewPoint();
                        try {
                            Thread.sleep(value.longValue());
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        };
    }

    private void saveNewPoint() {
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
    public void onSCPositiveClick(int position) {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                googleApiClient, this);
        if (position == -1) {
            FragmentOperations.oldFragment(context);
        } else {
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
            imgButton = (ImageButton) rootView.findViewById(R.id.btnPlay);
            imgButton.setEnabled(true);
            if (status == PAUSED) {
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
            if (status == PLAYED)
                pauseRecord(false);
            else {
                imgButton = (ImageButton) rootView.findViewById(R.id.mapsButton);
                imgButton.setEnabled(false);
            }
            imgButton = (ImageButton) rootView.findViewById(R.id.btnPlay);
            imgButton.setEnabled(false);
            imgButton = (ImageButton) rootView.findViewById(R.id.btnStop);
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
