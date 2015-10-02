package com.captor.points.gtnaozuka.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import com.captor.points.gtnaozuka.pointscaptor.R;
import com.captor.points.gtnaozuka.util.Constants;

public class StopConfirmationDialog extends DialogFragment {

    private int position;

    public interface StopConfirmationListener {
        void onSCPositiveClick(DialogFragment dialog, int position);
    }

    private StopConfirmationListener scListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            scListener = (StopConfirmationListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement StopConfirmationListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        this.position = getArguments().getInt(Constants.POSITION_MSG);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setIcon(R.drawable.ic_action_warning);
        builder.setTitle(R.string.warning);
        builder.setMessage(R.string.discard_confirmation);
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                scListener.onSCPositiveClick(StopConfirmationDialog.this, position);
            }
        });
        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });
        return builder.create();
    }
}
