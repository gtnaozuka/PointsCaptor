package com.captor.points.gtnaozuka.pointscaptor;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

import com.captor.points.gtnaozuka.util.operations.FileOperations;
import com.captor.points.gtnaozuka.util.operations.LanguageOperations;

import java.io.File;

public class SplashScreenActivity extends AppCompatActivity {

    private static final int TIMEOUT = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        LanguageOperations.loadLanguage(this, sharedPref.getString("Language", "en"));

        FileOperations.definePaths(getPackageName());
        FileOperations.delete(new File(FileOperations.SENT_PATH));

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
