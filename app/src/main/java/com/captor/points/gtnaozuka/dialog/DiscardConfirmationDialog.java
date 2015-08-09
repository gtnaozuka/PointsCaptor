package com.captor.points.gtnaozuka.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import com.captor.points.gtnaozuka.pointscaptor.R;

public class DiscardConfirmationDialog extends DialogFragment {

    public interface DiscardConfirmationListener {
        void onDCPositiveClick(DialogFragment dialog);

        void onDCNegativeClick(DialogFragment dialog);
    }

    private DiscardConfirmationListener dcListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            dcListener = (DiscardConfirmationListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement DiscardConfirmationListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setIcon(R.drawable.ic_action_warning);
        builder.setTitle(R.string.warning);
        builder.setMessage(R.string.discard_confirmation);
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dcListener.onDCPositiveClick(DiscardConfirmationDialog.this);
            }
        });
        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dcListener.onDCNegativeClick(DiscardConfirmationDialog.this);
            }
        });
        return builder.create();
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        dcListener.onDCNegativeClick(DiscardConfirmationDialog.this);
    }
}
