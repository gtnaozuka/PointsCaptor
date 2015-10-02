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
import com.captor.points.gtnaozuka.entity.Location;
import com.captor.points.gtnaozuka.pointscaptor.MainActivity;
import com.captor.points.gtnaozuka.pointscaptor.R;
import com.captor.points.gtnaozuka.util.operations.DataOperations;
import com.captor.points.gtnaozuka.util.DisplayToast;
import com.captor.points.gtnaozuka.util.operations.FragmentOperations;
import com.captor.points.gtnaozuka.util.Constants;
import com.google.android.gms.location.LocationServices;

public class DefaultCaptureFragment extends CaptureFragment {

    private Integer type;
    private Double value;
    private int status;
    private static final int PLAYED = 1, PAUSED = 0, STOPPED = -1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_default_capture, container, false);

        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ImageButton imgButton = (ImageButton) rootView.findViewById(R.id.mapsButton);
                imgButton.setEnabled(false);
                imgButton = (ImageButton) rootView.findViewById(R.id.btnPlay);
                imgButton.setEnabled(false);
                imgButton = (ImageButton) rootView.findViewById(R.id.btnStop);
                imgButton.setEnabled(false);
            }
        });

        bundle = this.getArguments();
        if (bundle != null) {
            type = bundle.getInt(Constants.TYPE_MSG, 0);
            value = bundle.getDouble(Constants.VALUE_MSG, 0.0);
            dataPoint = bundle.getParcelableArrayList(Constants.DATA_POINT_MSG);
            dataLocation = bundle.getParcelableArrayList(Constants.DATA_LOCATION_MSG);
        }

        status = STOPPED;
        initialize();

        return rootView;
    }

    @Override
    public void backPress() {
        if (pointsNum == 0) {
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    googleApiClient, this);
            FragmentOperations.oldFragment(context);
        } else {
            if (status == PLAYED)
                pauseRecord(true);

            DialogFragment dialog = new StopConfirmationDialog();
            Bundle dialogBundle = new Bundle();
            dialogBundle.putInt(Constants.POSITION_MSG, -1);
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
            if (status == PLAYED)
                pauseRecord(true);

            DialogFragment dialog = new StopConfirmationDialog();
            Bundle dialogBundle = new Bundle();
            dialogBundle.putInt(Constants.POSITION_MSG, position);
            dialog.setArguments(dialogBundle);
            dialog.show(context.getFragmentManager(), "StopConfirmationDialog");
        }
    }

    public void playRecord() {
        if (status == STOPPED) {
            status = PLAYED;

            Runnable r = null;
            if (type.equals(Constants.DISTANCE)) {
                r = threadByDistance();
            } else if (type.equals(Constants.TIME)) {
                r = threadByTime();
            }
            Thread t = new Thread(r);
            t.start();

            context.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ImageButton imgButton = (ImageButton) rootView.findViewById(R.id.btnPlay);
                    imgButton.setImageResource(R.drawable.ic_action_pause);
                    imgButton = (ImageButton) rootView.findViewById(R.id.btnStop);
                    imgButton.setEnabled(true);
                    imgButton = (ImageButton) rootView.findViewById(R.id.mapsButton);
                    imgButton.setEnabled(false);
                }
            });
        } else if (status == PAUSED) {
            status = PLAYED;

            context.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ImageButton imgButton = (ImageButton) rootView.findViewById(R.id.btnPlay);
                    imgButton.setImageResource(R.drawable.ic_action_pause);
                    imgButton = (ImageButton) rootView.findViewById(R.id.mapsButton);
                    imgButton.setEnabled(false);
                }
            });
        } else if (status == PLAYED) {
            pauseRecord(true);
        }
    }

    @Override
    public void stopRecord() {
        status = STOPPED;
        LocationServices.FusedLocationApi.removeLocationUpdates(
                googleApiClient, this);

        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ImageButton imgButton = (ImageButton) rootView.findViewById(R.id.btnPlay);
                imgButton.setImageResource(R.drawable.ic_action_play);
                imgButton = (ImageButton) rootView.findViewById(R.id.btnStop);
                imgButton.setEnabled(false);
                imgButton = (ImageButton) rootView.findViewById(R.id.mapsButton);
                imgButton.setEnabled(true);
            }
        });

        onDCNegativeClick();
    }

    private void pauseRecord(final boolean enabled) {
        status = PAUSED;

        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ImageButton imgButton = (ImageButton) rootView.findViewById(R.id.btnPlay);
                imgButton.setImageResource(R.drawable.ic_action_play);
                imgButton = (ImageButton) rootView.findViewById(R.id.mapsButton);
                imgButton.setEnabled(enabled);
            }
        });
    }

    private Runnable threadByDistance() {
        return new Runnable() {
            public void run() {
                while (status != STOPPED) {
                    if (status == PLAYED) {
                        if (pointsNum == 0) {
                            saveNewPoint();
                        } else {
                            Location src = dataLocation.get(pointsNum - 1);
                            Location dest = DataOperations.createNewLocation(location);
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
        updatePointsNumUI();
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
                    imgButton = (ImageButton) rootView.findViewById(R.id.btnPlay);
                    imgButton.setEnabled(true);
                    if (status == PAUSED) {
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
            if (status == PLAYED)
                pauseRecord(false);
            context.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ImageButton imgButton;
                    if (status != PLAYED) {
                        imgButton = (ImageButton) rootView.findViewById(R.id.mapsButton);
                        imgButton.setEnabled(false);
                    }
                    imgButton = (ImageButton) rootView.findViewById(R.id.btnPlay);
                    imgButton.setEnabled(false);
                    imgButton = (ImageButton) rootView.findViewById(R.id.btnStop);
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
