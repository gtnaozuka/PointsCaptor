package com.captor.points.gtnaozuka.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.captor.points.gtnaozuka.pointscaptor.R;
import com.captor.points.gtnaozuka.util.DisplayToast;

public class DistanceDialog extends DialogFragment {

    public interface DistanceListener {
        void onDPositiveClick(DialogFragment dialog, Double value);
    }

    private DistanceListener dListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            dListener = (DistanceListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement DistanceListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final EditText editText = new EditText(getActivity().getApplicationContext());
        editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        editText.setHint(R.string.distance);
        editText.setTextColor(getResources().getColor(R.color.text_color));
        editText.setHintTextColor(getResources().getColor(R.color.secondary_text_default_material_light));

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(50, 30, 50, 0);

        LinearLayout layout = new LinearLayout(getActivity());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(editText, params);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.input_distance);
        builder.setView(layout);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                String input = editText.getText().toString();
                if (input.matches("")) {
                    new Handler().post(new DisplayToast(getActivity(), getResources().getString(R.string.fill_blank_field)));
                    return;
                }
                Double value = Double.valueOf(input);
                if (value.equals(0.0)) {
                    new Handler().post(new DisplayToast(getActivity(), input +
                            getResources().getString(R.string.not_valid)));
                    return;
                }
                dListener.onDPositiveClick(DistanceDialog.this, value);
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });
        return builder.create();
    }
}
