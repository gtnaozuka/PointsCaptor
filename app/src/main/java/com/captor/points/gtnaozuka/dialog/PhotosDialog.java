package com.captor.points.gtnaozuka.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import com.captor.points.gtnaozuka.pointscaptor.R;

public class PhotosDialog extends DialogFragment {

    public interface PhotosListener {
        void sharePhoto(DialogFragment dialog);

        void deletePhoto(DialogFragment dialog);
    }

    private PhotosListener pListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            pListener = (PhotosListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement PhotosListener");
        }
    }

    private String[] actions;
    private static final int MAX_ACTIONS = 2;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        createOptions();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.choose_action);
        builder.setSingleChoiceItems(actions, 0, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        pListener.sharePhoto(PhotosDialog.this);
                        break;
                    case 1:
                        pListener.deletePhoto(PhotosDialog.this);
                        break;
                }
            }
        });
        return builder.create();
    }

    private void createOptions() {
        actions = new String[MAX_ACTIONS];
        actions[0] = getResources().getString(R.string.share_photo);
        actions[1] = getResources().getString(R.string.delete_photo);
    }
}
