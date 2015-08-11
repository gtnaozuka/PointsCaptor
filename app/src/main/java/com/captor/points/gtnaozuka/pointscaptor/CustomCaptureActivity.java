package com.captor.points.gtnaozuka.pointscaptor;

import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.captor.points.gtnaozuka.dialog.CapturedPointsDialog;
import com.captor.points.gtnaozuka.dialog.DiscardConfirmationDialog;
import com.captor.points.gtnaozuka.dialog.StopConfirmationDialog;
import com.captor.points.gtnaozuka.entity.Point;
import com.captor.points.gtnaozuka.util.Util;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CustomCaptureActivity extends MenuActivity implements LocationListener,
        CapturedPointsDialog.CapturedPointsListener,
        DiscardConfirmationDialog.DiscardConfirmationListener,
        StopConfirmationDialog.StopConfirmationListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        GpsStatus.Listener {

    private Integer pointsNum;
    private boolean isConnecting, needsRequestLocation;
    private ArrayList<Point> dataPoint;
    private ArrayList<com.captor.points.gtnaozuka.entity.Location> dataLocation;
    private Location location;
    private List<Address> addresses;

    private LocationRequest locationRequest;
    private GoogleApiClient googleApiClient;
    private static final long INTERVAL = 10000, FASTEST_INTERVAL = 5000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_capture);

        ImageButton imgButton = (ImageButton) findViewById(R.id.mapsButton);
        imgButton.setEnabled(false);
        imgButton = (ImageButton) findViewById(R.id.btnPlus);
        imgButton.setEnabled(false);
        imgButton = (ImageButton) findViewById(R.id.btnStop);
        imgButton.setEnabled(false);

        pointsNum = 0;
        needsRequestLocation = false;
        dataPoint = new ArrayList<>();
        dataLocation = new ArrayList<>();

        locationRequest = new LocationRequest();
        locationRequest.setInterval(INTERVAL);
        locationRequest.setFastestInterval(FASTEST_INTERVAL);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        googleApiClient.connect();

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.addGpsStatusListener(this);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            setOnProviderEnabled();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (needsRequestLocation) {
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    googleApiClient, locationRequest, this);
            needsRequestLocation = false;
        }
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        LocationServices.FusedLocationApi.requestLocationUpdates(
                googleApiClient, locationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d("connection", "suspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d("connection", "failed");
    }

    @Override
    public void onLocationChanged(Location location) {
        this.location = location;

        if (isConnecting) {
            ImageButton imgButton = (ImageButton) findViewById(R.id.mapsButton);
            imgButton.setEnabled(true);
            imgButton = (ImageButton) findViewById(R.id.btnPlus);
            imgButton.setEnabled(true);
            /*if (status == PAUSED) {
                imgButton = (ImageButton) findViewById(R.id.btnStop);
                imgButton.setEnabled(true);
            }*/

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
            /*if (status == PLAYED)
                pauseRecord(false);*/
            /*if (status != PLAYED) {
                imgButton = (ImageButton) findViewById(R.id.mapsButton);
                imgButton.setEnabled(false);
            }*/
            imgButton = (ImageButton) findViewById(R.id.btnPlus);
            imgButton.setEnabled(false);
            imgButton = (ImageButton) findViewById(R.id.btnStop);
            imgButton.setEnabled(false);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    TextView txtView = (TextView) findViewById(R.id.txtLocation);
                    txtView.setText(R.string.location);
                    ProgressBar pBar = (ProgressBar) findViewById(R.id.progressBar);
                    pBar.setVisibility(View.GONE);
                    ImageButton imgButton = (ImageButton) findViewById(R.id.gpsButton);
                    imgButton.setVisibility(View.VISIBLE);
                }
            });

            Toast toast = Toast.makeText(getApplicationContext(), R.string.gps_disabled, Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    private void updateLocationUI() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ProgressBar pBar = (ProgressBar) findViewById(R.id.progressBar);
                pBar.setVisibility(View.GONE);

                TextView txtView = (TextView) findViewById(R.id.txtLocation);
                txtView.setText(getResources().getString(R.string.latitude_dots) + location.getLatitude() + "\n" +
                        getResources().getString(R.string.longitude_dots) + location.getLongitude() + "\n");
            }
        });
    }

    private void updateAddressUI() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                    addresses = geocoder.getFromLocation(location.getLatitude(),
                            location.getLongitude(), 1);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            TextView txtView = (TextView) findViewById(R.id.txtLocation);
                            txtView.setText(txtView.getText() +
                                    addresses.get(0).getAddressLine(0) + ", " + addresses.get(0).getAddressLine(1) + ", " +
                                    addresses.get(0).getAddressLine(2));
                        }
                    });
                } catch (IOException e) {
                    Log.d("ioexception", "thrown");
                }
            }
        }).start();
    }

    public void saveNewPoint(View view) {
        saveNewPoint();
    }

    public void stopRecord(View view) {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                googleApiClient, this);

        ImageButton imgButton = (ImageButton) findViewById(R.id.btnStop);
        imgButton.setEnabled(false);
        imgButton = (ImageButton) findViewById(R.id.mapsButton);
        imgButton.setEnabled(true);

        DialogFragment dialog = new CapturedPointsDialog();
        dialog.setCancelable(false);
        dialog.show(getFragmentManager(), "CapturedPointsDialog");
    }

    private void saveNewPoint() {
        dataPoint.add(Util.convertLocToPoint(this.location));
        dataLocation.add(Util.createNewLocation(this.location));

        pointsNum++;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final TextView txtView = (TextView) findViewById(R.id.txtPointsNumber);
                txtView.setText(pointsNum.toString());
            }
        });
    }

    @Override
    public void viewCapturedPoints(DialogFragment dialog) {
        Intent intent = new Intent(this, CapturedPointsActivity.class);
        intent.putParcelableArrayListExtra(Util.DATA_POINT_MSG, dataPoint);
        intent.putParcelableArrayListExtra(Util.DATA_LOCATION_MSG, dataLocation);
        startActivity(intent);
    }

    @Override
    public void viewInGoogleMaps(DialogFragment dialog) {
        startMapsActivity();
    }

    @Override
    public void storeInMemory(DialogFragment dialog) {
        File f = storeFile("files");
        if (f == null)
            return;

        Toast toast = Toast.makeText(getApplicationContext(), "'" + f.getName() +
                "'" + getResources().getString(R.string.saved_successfully), Toast.LENGTH_SHORT);
        toast.show();
    }

    @Override
    public void shareWithSomeone(DialogFragment dialog) {
        File f = storeFile("sent");
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
            Toast toast = Toast.makeText(getApplicationContext(), R.string.no_email_client, Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    @Override
    public void startNewCapture(DialogFragment dialog) {
        dialog.dismiss();

        DialogFragment dialog2 = new DiscardConfirmationDialog();
        dialog2.show(getFragmentManager(), "DiscardConfirmationDialog");
    }

    @Override
    public void removeRepeatedData(DialogFragment dialog) {
        dataLocation = Util.removeRepeatedLocations(dataLocation);
        dataPoint = Util.removeRepeatedPoints(dataPoint);

        Toast toast = Toast.makeText(getApplicationContext(), getResources().getString(R.string.removed_successfully), Toast.LENGTH_SHORT);
        toast.show();
    }

    private File storeFile(String lastFolder) {
        if (!Util.isExternalStorageWritable()) {
            Toast toast = Toast.makeText(getApplicationContext(), R.string.unauthorized_access,
                    Toast.LENGTH_SHORT);
            toast.show();
            return null;
        }

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH.mm.ss", Locale.getDefault());
        Date d = new Date();

        String directory = Environment.getExternalStorageDirectory().getAbsolutePath() +
                File.separator + "Android" + File.separator + "data" +
                File.separator + getPackageName() + File.separator + lastFolder;
        String filename = "PC_" + df.format(d) + ".dat";
        String content = Util.convertListLocationToString(this, dataLocation) + "\n\n\n" +
                Util.convertListPointToString(this, dataPoint);

        File f = null;
        try {
            File fDir = new File(directory);
            fDir.mkdirs();

            f = new File(fDir, filename);
            if (!f.exists())
                f.createNewFile();

            FileOutputStream fos = new FileOutputStream(f);
            fos.write(content.getBytes());
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return f;
    }

    @Override
    public void onDCPositiveClick(DialogFragment dialog) {
        LocationServices.FusedLocationApi.requestLocationUpdates(
                googleApiClient, locationRequest, this);
        dataPoint = new ArrayList<>();
        dataLocation = new ArrayList<>();
        pointsNum = 0;

        TextView txtView = (TextView) findViewById(R.id.txtPointsNumber);
        txtView.setText(pointsNum.toString());
    }

    @Override
    public void onDCNegativeClick(DialogFragment dialog) {
        DialogFragment dialog2 = new CapturedPointsDialog();
        dialog2.setCancelable(false);
        dialog2.show(getFragmentManager(), "CapturedPointsDialog");
    }

    @Override
    public void onSCPositiveClick(DialogFragment dialog) {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                googleApiClient, this);
        finish();
    }

    @Override
    public void onBackPressed() {
        if (pointsNum.equals(0)) {
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    googleApiClient, this);
            finish();
        } else {
            /*if (status == PLAYED)
                pauseRecord(true);*/

            DialogFragment dialog = new StopConfirmationDialog();
            dialog.show(getFragmentManager(), "StopConfirmationDialog");
        }
    }

    public void startMapsActivity(View view) {
        needsRequestLocation = true;
        LocationServices.FusedLocationApi.removeLocationUpdates(
                googleApiClient, this);
        startMapsActivity();
    }

    private void startMapsActivity() {
        Intent intent = new Intent(this, MapsActivity.class);
        if (pointsNum < 2) {
            intent.putExtra(Util.STATUS_MSG, Util.NOT_STARTED);
            intent.putExtra(Util.CURRENT_LOCATION_MSG, Util.createNewLocation(this.location));
        } else {
            intent.putParcelableArrayListExtra(Util.DATA_LOCATION_MSG, dataLocation);
            /*if (status == PAUSED)
                intent.putExtra(Util.STATUS_MSG, Util.STARTED);
            else if (status == STOPPED)
                intent.putExtra(Util.STATUS_MSG, Util.FINISHED);*/
        }
        startActivity(intent);
    }

    public void turnGpsOn(View view) {
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(intent);
    }

    private void setOnProviderEnabled() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ImageButton imgButton = (ImageButton) findViewById(R.id.gpsButton);
                imgButton.setVisibility(View.GONE);
                ProgressBar pBar = (ProgressBar) findViewById(R.id.progressBar);
                pBar.setVisibility(View.VISIBLE);
            }
        });

        isConnecting = true;
    }
}
