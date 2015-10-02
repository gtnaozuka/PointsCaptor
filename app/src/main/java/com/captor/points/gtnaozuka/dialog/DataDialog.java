package com.captor.points.gtnaozuka.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import com.captor.points.gtnaozuka.pointscaptor.R;

public class DataDialog extends DialogFragment {

    public interface FileManagerListener {
        void viewCapturedPoints(DialogFragment dialog);

        void viewInGoogleMaps(DialogFragment dialog);

        void removeRepeatedData(DialogFragment dialog);

        void shareWithSomeone(DialogFragment dialog);

        void deleteFile(DialogFragment dialog);
    }

    private FileManagerListener fmListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            fmListener = (FileManagerListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement FileManagerListener");
        }
    }

    private String[] actions;
    private static final int MAX_ACTIONS = 5;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        createOptions();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.choose_action);
        builder.setSingleChoiceItems(actions, 0, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        fmListener.viewCapturedPoints(DataDialog.this);
                        break;
                    case 1:
                        fmListener.viewInGoogleMaps(DataDialog.this);
                        break;
                    case 2:
                        fmListener.removeRepeatedData(DataDialog.this);
                        break;
                    case 3:
                        fmListener.shareWithSomeone(DataDialog.this);
                        break;
                    case 4:
                        fmListener.deleteFile(DataDialog.this);
                }
            }
        });
        return builder.create();
    }

    private void createOptions() {
        actions = new String[MAX_ACTIONS];
        actions[0] = getResources().getString(R.string.view_captured_points);
        actions[1] = getResources().getString(R.string.view_in_google_maps);
        actions[2] = getResources().getString(R.string.remove_repeated_points);
        actions[3] = getResources().getString(R.string.share_with_someone);
        actions[4] = getResources().getString(R.string.delete_file);
    }
}

