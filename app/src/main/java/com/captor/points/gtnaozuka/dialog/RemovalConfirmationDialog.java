package com.captor.points.gtnaozuka.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import com.captor.points.gtnaozuka.pointscaptor.R;

public class RemovalConfirmationDialog extends DialogFragment {

    public interface RemovalConfirmationListener {
        void onRCPositiveClick(DialogFragment dialog);
    }

    private RemovalConfirmationListener rcListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            rcListener = (RemovalConfirmationListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement RemovalConfirmationListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setIcon(R.drawable.dialog_warning);
        builder.setTitle(R.string.warning);
        builder.setMessage(R.string.are_you_sure);
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                rcListener.onRCPositiveClick(RemovalConfirmationDialog.this);
            }
        });
        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });
        return builder.create();
    }
}