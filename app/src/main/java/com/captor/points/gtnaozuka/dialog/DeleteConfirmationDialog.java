package com.captor.points.gtnaozuka.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import com.captor.points.gtnaozuka.pointscaptor.R;
import com.captor.points.gtnaozuka.util.Constants;

public class DeleteConfirmationDialog extends DialogFragment {

    private int type;

    public interface DeleteConfirmationListener {
        void onDelCPositiveClick(DialogFragment dialog, int type);
    }

    private DeleteConfirmationListener dcListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            dcListener = (DeleteConfirmationListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement DeleteConfirmationListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        this.type = getArguments().getInt(Constants.TYPE_MSG);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setIcon(R.drawable.dialog_warning);
        builder.setTitle(R.string.warning);
        builder.setMessage(R.string.are_you_sure);
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dcListener.onDelCPositiveClick(DeleteConfirmationDialog.this, type);
            }
        });
        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });
        return builder.create();
    }
}