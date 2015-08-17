package com.captor.points.gtnaozuka.pointscaptor;

import android.app.DialogFragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.captor.points.gtnaozuka.dialog.MapActionsDialog;
import com.captor.points.gtnaozuka.dialog.MapTypeDialog;
import com.captor.points.gtnaozuka.entity.Location;
import com.captor.points.gtnaozuka.util.FileOperations;
import com.captor.points.gtnaozuka.util.Values;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.File;
import java.util.ArrayList;

public class MapsActivity extends AppCompatActivity implements MapActionsDialog.MapActionsListener,
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
        Integer status = intent.getIntExtra(Values.STATUS_MSG, 0);
        if (status.equals(Values.NOT_STARTED)) {
            setCameraPosition((Location) intent.getParcelableExtra(Values.CURRENT_LOCATION_MSG));
        } else {
            ArrayList<Location> dataLocation = intent.getParcelableArrayListExtra(Values.DATA_LOCATION_MSG);

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
            if (status.equals(Values.FINISHED))
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
                File f = FileOperations.storePhoto(MapsActivity.this, FileOperations.FILES_PATH, snapshot);
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
                File f = FileOperations.storePhoto(MapsActivity.this, FileOperations.SENT_PATH, snapshot);
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

    @Override
    public void setMapType(DialogFragment dialog, int type) {
        dialog.dismiss();

        map.setMapType(type);
    }
}
