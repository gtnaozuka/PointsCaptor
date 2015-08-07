package com.captor.points.gtnaozuka.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import com.captor.points.gtnaozuka.pointscaptor.R;
import com.google.android.gms.maps.GoogleMap;

public class MapTypeDialog extends DialogFragment {

    public interface MapTypeListener {
        public void setMapType(DialogFragment dialog, int type);
    }

    private MapTypeListener mtListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mtListener = (MapTypeListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement MapTypeListener");
        }
    }

    private String[] options;
    private static final int MAX_OPTIONS = 4;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        createOptions();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.choose_option);
        builder.setSingleChoiceItems(options, 0, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        mtListener.setMapType(MapTypeDialog.this, GoogleMap.MAP_TYPE_NORMAL);
                        break;
                    case 1:
                        mtListener.setMapType(MapTypeDialog.this, GoogleMap.MAP_TYPE_HYBRID);
                        break;
                    case 2:
                        mtListener.setMapType(MapTypeDialog.this, GoogleMap.MAP_TYPE_SATELLITE);
                        break;
                    case 3:
                        mtListener.setMapType(MapTypeDialog.this, GoogleMap.MAP_TYPE_TERRAIN);
                }
            }
        });
        return builder.create();
    }

    private void createOptions() {
        options = new String[MAX_OPTIONS];
        options[0] = getResources().getString(R.string.normal);
        options[1] = getResources().getString(R.string.hybrid);
        options[2] = getResources().getString(R.string.satellite);
        options[3] = getResources().getString(R.string.terrain);
    }
}
