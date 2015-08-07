package com.captor.points.gtnaozuka.pointscaptor;

import android.app.DialogFragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.captor.points.gtnaozuka.dialog.MapActionsDialog;
import com.captor.points.gtnaozuka.dialog.MapTypeDialog;
import com.captor.points.gtnaozuka.entity.Location;
import com.captor.points.gtnaozuka.util.Util;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements MapActionsDialog.MapActionsListener,
        MapTypeDialog.MapTypeListener {

    private GoogleMap map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    private void setUpMapIfNeeded() {
        if (map == null) {
            map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            if (map != null)
                setUpMap();
        }
    }

    private void setUpMap() {
        map.setMyLocationEnabled(true);
        map.getUiSettings().setZoomControlsEnabled(true);
        map.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                DialogFragment dialog = new MapActionsDialog();
                dialog.show(getFragmentManager(), "MapActionsDialog");
            }
        });

        Intent intent = getIntent();
        Integer status = intent.getIntExtra(Util.STATUS_MSG, 0);
        if (status.equals(Util.NOT_STARTED)) {
            setCameraPosition((Location) intent.getParcelableExtra(Util.CURRENT_LOCATION_MSG));
        } else {
            ArrayList<Location> dataLocation = intent.getParcelableArrayListExtra(Util.DATA_LOCATION_MSG);

            addMarker(dataLocation.get(0), getResources().getString(R.string.start));

            PolylineOptions plOptions = new PolylineOptions();
            plOptions.width(5).color(Color.BLUE);
            for (int i = 0; i < dataLocation.size() - 1; i++) {
                Location src = dataLocation.get(i);
                Location dest = dataLocation.get(i + 1);
                plOptions.add(new LatLng(src.getLatitude(), src.getLongitude()),
                        new LatLng(dest.getLatitude(), dest.getLongitude()));
            }
            map.addPolyline(plOptions);

            Location l = dataLocation.get(dataLocation.size() - 1);
            if (status.equals(Util.FINISHED))
                addMarker(l, getResources().getString(R.string.finish));
            setCameraPosition(l);
        }
    }

    private void setCameraPosition(Location l) {
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(l.getLatitude(), l.getLongitude()))
                .zoom(14)
                .build();
        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    private void addMarker(Location l, String title) {
        map.addMarker(new MarkerOptions().position(new LatLng(l.getLatitude(), l.getLongitude())).title(title));
    }

    @Override
    public void changeMapType(DialogFragment dialog) {
        dialog.dismiss();

        DialogFragment dialog2 = new MapTypeDialog();
        dialog2.show(getFragmentManager(), "MapTypeDialog");
    }

    @Override
    public void takePhoto(DialogFragment dialog) {
        dialog.dismiss();

        GoogleMap.SnapshotReadyCallback callback = new GoogleMap.SnapshotReadyCallback() {
            @Override
            public void onSnapshotReady(Bitmap snapshot) {
                File f = storePhoto(snapshot, "files");
                if (f == null)
                    return;

                Toast toast = Toast.makeText(getApplicationContext(), "'" + f.getName() +
                        "'" + getResources().getString(R.string.saved_successfully), Toast.LENGTH_SHORT);
                toast.show();
            }
        };

        map.snapshot(callback);
    }

    @Override
    public void sharePhoto(DialogFragment dialog) {
        dialog.dismiss();

        GoogleMap.SnapshotReadyCallback callback = new GoogleMap.SnapshotReadyCallback() {
            @Override
            public void onSnapshotReady(Bitmap snapshot) {
                File f = storePhoto(snapshot, "sent");
                if (f == null)
                    return;

                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setData(Uri.parse("mailto:"));
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.map_email_subject));
                intent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.map_email_body));
                intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(f));

                try {
                    startActivity(Intent.createChooser(intent, getResources().getString(R.string.choose_option)));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast toast = Toast.makeText(getApplicationContext(), R.string.no_email_client, Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        };

        map.snapshot(callback);
    }

    private File storePhoto(Bitmap snapshot, String lastFolder) {
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
        String filename = "GM_" + df.format(d) + ".png";
        Bitmap bitmap = snapshot;

        File f = null;
        try {
            File fDir = new File(directory);
            fDir.mkdirs();

            f = new File(fDir, filename);
            if (!f.exists())
                f.createNewFile();

            FileOutputStream fos = new FileOutputStream(f);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return f;
    }

    @Override
    public void setMapType(DialogFragment dialog, int type) {
        dialog.dismiss();

        map.setMapType(type);
    }
}
