package com.captor.points.gtnaozuka.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.captor.points.gtnaozuka.pointscaptor.R;

public class TimeDialog extends DialogFragment {

    public interface TimeListener {
        void onTPositiveClick(DialogFragment dialog, Double value);
    }

    private TimeListener tListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            tListener = (TimeListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement TimeListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final EditText editText = new EditText(getActivity().getApplicationContext());
        editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        editText.setHint(R.string.time);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(50, 30, 50, 0);

        LinearLayout layout = new LinearLayout(getActivity());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(editText, params);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.input_time);
        builder.setView(layout);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                String input = editText.getText().toString();
                if (input.matches("")) {
                    Toast toast = Toast.makeText(getActivity(), R.string.fill_blank_field, Toast.LENGTH_SHORT);
                    toast.show();
                    return;
                }
                Double value = Double.valueOf(input);
                if (value.equals(0.0)) {
                    Toast toast = Toast.makeText(getActivity(), input +
                            getResources().getString(R.string.not_valid), Toast.LENGTH_SHORT);
                    toast.show();
                    return;
                }
                tListener.onTPositiveClick(TimeDialog.this, value);
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });
        return builder.create();
    }
}