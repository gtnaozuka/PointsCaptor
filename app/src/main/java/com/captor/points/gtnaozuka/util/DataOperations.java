package com.captor.points.gtnaozuka.util;

import android.content.Context;

import com.captor.points.gtnaozuka.entity.Location;
import com.captor.points.gtnaozuka.entity.Point;
import com.captor.points.gtnaozuka.pointscaptor.R;

import java.util.ArrayList;
import java.util.LinkedHashSet;

public class DataOperations {

    public static double calculateDistance(Location src, Location dest) {
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

    public static Point convertLocationToPoint(android.location.Location l) {
        double r = 6371.0;

        Point p = new Point();
        p.setX(r * Math.cos(l.getLatitude()) * Math.cos(l.getLongitude()));
        p.setY(r * Math.cos(l.getLatitude()) * Math.sin(l.getLongitude()));
        return p;
    }

    public static Location createNewLocation(android.location.Location l) {
        Location newLocation = new Location();
        newLocation.setLatitude(l.getLatitude());
        newLocation.setLongitude(l.getLongitude());
        return newLocation;
    }

    public static String convertPointsToString(Context context, ArrayList<Point> dataPoint) {
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

    public static String convertLocationsToString(Context context, ArrayList<Location> dataPoint) {
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

    public static ArrayList<Location> removeRepeatedLocations(ArrayList<Location> data) {
        LinkedHashSet<Location> set = new LinkedHashSet<>(data);
        return new ArrayList<>(set);
    }

    public static ArrayList<Point> removeRepeatedPoints(ArrayList<Point> data) {
        LinkedHashSet<Point> set = new LinkedHashSet<>(data);
        return new ArrayList<>(set);
    }
}
