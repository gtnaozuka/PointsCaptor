package com.captor.points.gtnaozuka.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.captor.points.gtnaozuka.pointscaptor.R;

public class BoundaryDialog extends DialogFragment {

    private int lrMin, ulMin;
    private static final int MAX_FACTOR = 100;

    public static BoundaryDialog newInstance(int lrMin, int ulMin) {
        BoundaryDialog bd = new BoundaryDialog();

        bd.lrMin = lrMin;
        bd.ulMin = ulMin;

        return bd;
    }

    public interface BoundaryListener {
        void onBPositiveClick(DialogFragment dialog, int leftRight, int upperLower);
    }

    private BoundaryListener bListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            bListener = (BoundaryListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement BoundaryListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LinearLayout.LayoutParams paramsTextView = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        paramsTextView.setMargins(30, 30, 30, 0);

        TextView lrTextView = new TextView(getActivity());
        lrTextView.setText(getResources().getString(R.string.boundary_1));

        TextView ulTextView = new TextView(getActivity());
        ulTextView.setText(getResources().getString(R.string.boundary_2));

        LinearLayout layoutTextView = new LinearLayout(getActivity());
        layoutTextView.setOrientation(LinearLayout.HORIZONTAL);
        layoutTextView.addView(lrTextView, paramsTextView);
        layoutTextView.addView(ulTextView, paramsTextView);

        LinearLayout.LayoutParams paramsPicker = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        paramsPicker.setMargins(40, 30, 40, 0);

        final NumberPicker lrPicker = new NumberPicker(getActivity());
        lrPicker.setMinValue(lrMin);
        lrPicker.setMaxValue(MAX_FACTOR * lrMin);

        final NumberPicker ulPicker = new NumberPicker(getActivity());
        ulPicker.setMinValue(ulMin);
        ulPicker.setMaxValue(MAX_FACTOR * ulMin);

        LinearLayout layoutPicker = new LinearLayout(getActivity());
        layoutPicker.setOrientation(LinearLayout.HORIZONTAL);
        layoutPicker.addView(lrPicker, paramsPicker);
        layoutPicker.addView(ulPicker, paramsPicker);

        LinearLayout layout = new LinearLayout(getActivity());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(layoutTextView);
        layout.addView(layoutPicker);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.refinement);
        builder.setView(layout);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                bListener.onBPositiveClick(BoundaryDialog.this, lrPicker.getValue(), ulPicker.getValue());
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });
        return builder.create();
    }
}
