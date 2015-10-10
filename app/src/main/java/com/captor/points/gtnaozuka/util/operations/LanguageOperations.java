package com.captor.points.gtnaozuka.util.operations;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;

import java.util.Locale;

public class LanguageOperations {

    public static void loadLanguage(Context context, String language) {
        Locale locale = new Locale(language);
        Resources res = context.getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration config = res.getConfiguration();
        config.locale = locale;
        res.updateConfiguration(config, dm);
    }

    public static Locale getCurrentLocale(Context context) {
        Resources res = context.getResources();
        Configuration config = res.getConfiguration();
        return config.locale;
    }
}
