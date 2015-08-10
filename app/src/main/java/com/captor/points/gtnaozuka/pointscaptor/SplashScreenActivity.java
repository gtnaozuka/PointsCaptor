package com.captor.points.gtnaozuka.pointscaptor;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

import com.captor.points.gtnaozuka.util.Util;

import java.io.File;

public class SplashScreenActivity extends AppCompatActivity {

    private static final int TIMEOUT = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        Util.loadLanguage(this, sharedPref.getString("Language", "en"));

        String directory = Environment.getExternalStorageDirectory().getAbsolutePath() +
                File.separator + "Android" + File.separator + "data" +
                File.separator + getPackageName() + File.separator + "sent";
        Util.delete(new File(directory));

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashScreenActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, TIMEOUT);
    }
}
