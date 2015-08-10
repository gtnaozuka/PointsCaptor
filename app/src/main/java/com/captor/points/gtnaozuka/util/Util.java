package com.captor.points.gtnaozuka.util;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Environment;
import android.util.DisplayMetrics;

import com.captor.points.gtnaozuka.entity.Location;
import com.captor.points.gtnaozuka.entity.Point;
import com.captor.points.gtnaozuka.pointscaptor.R;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Locale;

public class Util {

    public static final String TYPE_MSG = "com.captor.points.gtnaozuka.pointscaptor.TYPE";
    public static final String VALUE_MSG = "com.captor.points.gtnaozuka.pointscaptor.VALUE";
    public static final String DATA_POINT_MSG = "com.captor.points.gtnaozuka.pointscaptor.DATA_POINT";
    public static final String DATA_LOCATION_MSG = "com.captor.points.gtnaozuka.pointscaptor.DATA_LOCATION";
    public static final String CURRENT_LOCATION_MSG = "com.captor.points.gtnaozuka.pointscaptor.CURRENT_LOCATION";
    public static final String STATUS_MSG = "com.captor.points.gtnaozuka.pointscaptor.STATUS";

    public static final int DISTANCE = 1, TIME = 2;
    public static final int NOT_STARTED = 1, STARTED = 2, FINISHED = 3;

    public static double getDistance(Location src, Location dest) {
        double r = 6371000.0;
        double phi1 = Math.toRadians(src.getLatitude());
        double phi2 = Math.toRadians(dest.getLatitude());
        double dPhi = Math.toRadians(dest.getLatitude() - src.getLatitude());
        double dLambda = Math.toRadians(dest.getLongitude() - src.getLongitude());

        double a = Math.pow(Math.sin(dPhi / 2), 2) + Math.cos(phi1) * Math.cos(phi2) *
                Math.pow(Math.sin(dLambda / 2), 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return r * c;
    }

    public static Point convertLocToPoint(android.location.Location l) {
        double r = 6371.0;

        Point p = new Point();
        p.setX(r * Math.cos(l.getLatitude()) * Math.cos(l.getLongitude()));
        p.setY(r * Math.cos(l.getLatitude()) * Math.sin(l.getLongitude()));
        return p;
    }

    public static Location createNewLocation(android.location.Location l) {
        Location l2 = new Location();
        l2.setLatitude(l.getLatitude());
        l2.setLongitude(l.getLongitude());
        return l2;
    }

    public static String convertListPointToString(Context context, ArrayList<Point> dataPoint) {
        String str = context.getResources().getString(R.string.n) + "\t\t\t" +
                context.getResources().getString(R.string.x) + "\t\t\t" +
                context.getResources().getString(R.string.y) + "\n";
        int i = 1;
        for (Point p : dataPoint) {
            str += i + "\t\t\t" + p.getX() + "\t\t\t" + p.getY() + "\n";
            i++;
        }
        return str;
    }

    public static String convertListLocationToString(Context context, ArrayList<Location> dataPoint) {
        String str = context.getResources().getString(R.string.n) + "\t\t\t" +
                context.getResources().getString(R.string.latitude) + "\t\t\t" +
                context.getResources().getString(R.string.longitude) + "\n";
        int i = 1;
        for (Location l : dataPoint) {
            str += i + "\t\t\t" + l.getLatitude() + "\t\t\t" + l.getLongitude() + "\n";
            i++;
        }
        return str;
    }

    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    public static void delete(File file) {
        if (!file.exists())
            return;
        if (file.isDirectory()) {
            for (File f : file.listFiles()) {
                delete(f);
            }
        }
        file.delete();
    }

    public static void loadLanguage(Context context, String language) {
        Locale locale = new Locale(language);
        Resources res = context.getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration config = res.getConfiguration();
        config.locale = locale;
        res.updateConfiguration(config, dm);
    }

    public static ArrayList<Location> removeRepeatedLocations(ArrayList<Location> data) {
        LinkedHashSet<Location> set = new LinkedHashSet<>(data);
        return new ArrayList<>(set);
    }

    public static ArrayList<Point> removeRepeatedPoints(ArrayList<Point> data) {
        LinkedHashSet<Point> set = new LinkedHashSet<>(data);
        return new ArrayList<>(set);
    }
}
