package com.captor.points.gtnaozuka.pointscaptor;

import android.app.ActionBar;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
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
import com.captor.points.gtnaozuka.entity.Point;
import com.captor.points.gtnaozuka.dialog.StopConfirmationDialog;
import com.captor.points.gtnaozuka.util.Util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CaptureActivity extends MenuActivity implements LocationListener,
        CapturedPointsDialog.CapturedPointsListener,
        DiscardConfirmationDialog.DiscardConfirmationListener,
        StopConfirmationDialog.StopConfirmationListener {

    private Integer type, pointsNum;
    private Double value;
    private int status;
    private boolean isConnecting, needsRequestLocation;
    private ArrayList<Point> dataPoint;
    private ArrayList<com.captor.points.gtnaozuka.entity.Location> dataLocation;
    private LocationManager locationManager;
    private Location location;
    private static final int PLAYED = 1, PAUSED = 0, STOPPED = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture);

        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        ImageButton imgButton = (ImageButton) findViewById(R.id.mapsButton);
        imgButton.setEnabled(false);
        imgButton = (ImageButton) findViewById(R.id.btnPlay);
        imgButton.setEnabled(false);
        imgButton = (ImageButton) findViewById(R.id.btnStop);
        imgButton.setEnabled(false);

        Intent intent = getIntent();
        type = intent.getIntExtra(Util.TYPE_MSG, 0);
        value = intent.getDoubleExtra(Util.VALUE_MSG, 0.0);
        pointsNum = 0;
        status = STOPPED;
        needsRequestLocation = false;
        dataPoint = new ArrayList<>();
        dataLocation = new ArrayList<>();

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            setOnProviderEnabled();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (needsRequestLocation) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
            needsRequestLocation = false;
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        this.location = location;

        if (isConnecting) {
            ImageButton imgButton = (ImageButton) findViewById(R.id.mapsButton);
            imgButton.setEnabled(true);
            imgButton = (ImageButton) findViewById(R.id.btnPlay);
            imgButton.setEnabled(true);
            if (status == PAUSED) {
                imgButton = (ImageButton) findViewById(R.id.btnStop);
                imgButton.setEnabled(true);
            }

            isConnecting = false;
        }

        ProgressBar pBar = (ProgressBar) findViewById(R.id.progressBar);
        pBar.setVisibility(View.GONE);
        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(this.location.getLatitude(),
                    this.location.getLongitude(), 1);

            TextView txtView = (TextView) findViewById(R.id.txtLocation);
            txtView.setText(getResources().getString(R.string.latitude_dots) + this.location.getLatitude() + "\n" +
                    getResources().getString(R.string.longitude_dots) + this.location.getLongitude() + "\n" +
                    addresses.get(0).getAddressLine(0) + ", " +  addresses.get(0).getAddressLine(1) + ", " +
                    addresses.get(0).getAddressLine(2));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d("Status", "changed");
    }

    @Override
    public void onProviderEnabled(String provider) {
        setOnProviderEnabled();
    }

    @Override
    public void onProviderDisabled(String provider) {
        ImageButton imgButton;
        if (status == PLAYED)
            pauseRecord(false);
        else {
            imgButton = (ImageButton) findViewById(R.id.mapsButton);
            imgButton.setEnabled(false);
        }
        imgButton = (ImageButton) findViewById(R.id.btnPlay);
        imgButton.setEnabled(false);
        imgButton = (ImageButton) findViewById(R.id.btnStop);
        imgButton.setEnabled(false);

        TextView txtView = (TextView) findViewById(R.id.txtLocation);
        txtView.setText(R.string.location);
        ProgressBar pBar = (ProgressBar) findViewById(R.id.progressBar);
        pBar.setVisibility(View.GONE);
        imgButton = (ImageButton) findViewById(R.id.gpsButton);
        imgButton.setVisibility(View.VISIBLE);

        Toast toast = Toast.makeText(getApplicationContext(), R.string.gps_disabled, Toast.LENGTH_SHORT);
        toast.show();
    }

    public void playRecord(View view) {
        ImageButton imgButton = (ImageButton) findViewById(R.id.btnPlay);
        if (status == STOPPED) {
            status = PLAYED;

            Runnable r = null;
            if (type.equals(Util.DISTANCE)) {
                r = threadByDistance();
            } else if (type.equals(Util.TIME)) {
                r = threadByTime();
            }
            Thread t = new Thread(r);
            t.start();

            imgButton.setImageResource(R.drawable.ic_action_pause);
            imgButton = (ImageButton) findViewById(R.id.btnStop);
            imgButton.setEnabled(true);
            imgButton = (ImageButton) findViewById(R.id.mapsButton);
            imgButton.setEnabled(false);
        } else if (status == PAUSED) {
            status = PLAYED;

            imgButton.setImageResource(R.drawable.ic_action_pause);
            imgButton = (ImageButton) findViewById(R.id.mapsButton);
            imgButton.setEnabled(false);
        } else if (status == PLAYED) {
            pauseRecord(true);
        }
    }

    private void pauseRecord(boolean enabled) {
        status = PAUSED;

        ImageButton imgButton = (ImageButton) findViewById(R.id.btnPlay);
        imgButton.setImageResource(R.drawable.ic_action_play);
        imgButton = (ImageButton) findViewById(R.id.mapsButton);
        imgButton.setEnabled(enabled);
    }

    public void stopRecord(View view) {
        status = STOPPED;
        locationManager.removeUpdates(this);

        ImageButton imgButton = (ImageButton) findViewById(R.id.btnPlay);
        imgButton.setImageResource(R.drawable.ic_action_play);
        imgButton = (ImageButton) findViewById(R.id.btnStop);
        imgButton.setEnabled(false);
        imgButton = (ImageButton) findViewById(R.id.mapsButton);
        imgButton.setEnabled(true);

        DialogFragment dialog = new CapturedPointsDialog();
        dialog.setCancelable(false);
        dialog.show(getFragmentManager(), "CapturedPointsDialog");
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
                            com.captor.points.gtnaozuka.entity.Location dest = Util.createNewLocation(location);
                            if (Util.getDistance(src, dest) >= value) {
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
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
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
        locationManager.removeUpdates(this);
        finish();
    }

    @Override
    public void onBackPressed() {
        if (pointsNum.equals(0)) {
            locationManager.removeUpdates(this);
            finish();
        } else {
            if (status == PLAYED)
                pauseRecord(true);

            DialogFragment dialog = new StopConfirmationDialog();
            dialog.show(getFragmentManager(), "StopConfirmationDialog");
        }
    }

    public void startMapsActivity(View view) {
        needsRequestLocation = true;
        locationManager.removeUpdates(this);
        startMapsActivity();
    }

    private void startMapsActivity() {
        Intent intent = new Intent(this, MapsActivity.class);
        if (pointsNum < 2) {
            intent.putExtra(Util.STATUS_MSG, Util.NOT_STARTED);
            intent.putExtra(Util.CURRENT_LOCATION_MSG, Util.createNewLocation(this.location));
        } else {
            intent.putParcelableArrayListExtra(Util.DATA_LOCATION_MSG, dataLocation);
            if (status == PAUSED)
                intent.putExtra(Util.STATUS_MSG, Util.STARTED);
            else if (status == STOPPED)
                intent.putExtra(Util.STATUS_MSG, Util.FINISHED);
        }
        startActivity(intent);
    }

    public void turnGpsOn(View view) {
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(intent);
    }

    private void setOnProviderEnabled() {
        ImageButton imgButton = (ImageButton) findViewById(R.id.gpsButton);
        imgButton.setVisibility(View.GONE);
        ProgressBar pBar = (ProgressBar) findViewById(R.id.progressBar);
        pBar.setVisibility(View.VISIBLE);

        isConnecting = true;
    }
}
