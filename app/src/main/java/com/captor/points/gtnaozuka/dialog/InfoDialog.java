package com.captor.points.gtnaozuka.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.webkit.WebView;

import com.captor.points.gtnaozuka.pointscaptor.R;

public class InfoDialog extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        WebView webView = new WebView(getActivity());
        String message = "<html><body><p align=\"justify\" style=\"color:black;\">" +
                getResources().getString(R.string.info_message) +
                "</p></body></html>";
        webView.loadDataWithBaseURL(null, message, "text/html", "utf-8", null);
        webView.setBackgroundColor(Color.TRANSPARENT);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.info);
        builder.setView(webView);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });
        return builder.create();
    }
}
