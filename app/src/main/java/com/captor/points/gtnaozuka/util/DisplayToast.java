package com.captor.points.gtnaozuka.util;

import android.content.Context;
import android.widget.Toast;

public class DisplayToast implements Runnable {

    private final Context context;
    private final String message;

    public DisplayToast(Context context, String message) {
        this.context = context;
        this.message = message;
    }

    @Override
    public void run() {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}
