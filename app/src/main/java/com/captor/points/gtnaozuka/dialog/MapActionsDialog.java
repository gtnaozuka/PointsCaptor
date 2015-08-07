package com.captor.points.gtnaozuka.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import com.captor.points.gtnaozuka.pointscaptor.R;

public class MapActionsDialog extends DialogFragment {

    public interface MapActionsListener {
        public void changeMapType(DialogFragment dialog);
        public void takePhoto(DialogFragment dialog);
        public void sharePhoto(DialogFragment dialog);
    }

    private MapActionsListener maListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            maListener = (MapActionsListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement MapActionsListener");
        }
    }

    private String[] actions;
    private static final int MAX_ACTIONS = 3;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        createOptions();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.choose_action);
        builder.setSingleChoiceItems(actions, 0, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        maListener.changeMapType(MapActionsDialog.this);
                        break;
                    case 1:
                        maListener.takePhoto(MapActionsDialog.this);
                        break;
                    case 2:
                        maListener.sharePhoto(MapActionsDialog.this);
                }
            }
        });
        return builder.create();
    }

    private void createOptions() {
        actions = new String[MAX_ACTIONS];
        actions[0] = getResources().getString(R.string.change_map_type);
        actions[1] = getResources().getString(R.string.take_photo);
        actions[2] = getResources().getString(R.string.share_photo);
    }
}
