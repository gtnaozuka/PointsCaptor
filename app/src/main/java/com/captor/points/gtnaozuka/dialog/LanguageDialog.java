package com.captor.points.gtnaozuka.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import com.captor.points.gtnaozuka.pointscaptor.R;

public class LanguageDialog extends DialogFragment {

    public interface LanguageListener {
        public void setLanguage(DialogFragment dialog, String language);
    }

    private LanguageListener lListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            lListener = (LanguageListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement LanguageListener");
        }
    }

    private String[] options;
    private static final int MAX_OPTIONS = 2;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        createOptions();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.choose_option);
        builder.setSingleChoiceItems(options, 0, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        lListener.setLanguage(LanguageDialog.this, "en_US");
                        break;
                    case 1:
                        lListener.setLanguage(LanguageDialog.this, "pt_BR");
                }
            }
        });
        return builder.create();
    }

    private void createOptions() {
        options = new String[MAX_OPTIONS];
        options[0] = getResources().getString(R.string.english);
        options[1] = getResources().getString(R.string.portuguese);
    }
}
