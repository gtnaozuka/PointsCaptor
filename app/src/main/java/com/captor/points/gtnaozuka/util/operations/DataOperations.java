package com.captor.points.gtnaozuka.util.operations;

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

    public static ArrayList<Point> convertStringToPoints(ArrayList<String> strPoint) {
        ArrayList<Point> points = new ArrayList<>();

        for (int i = 1; i < strPoint.size(); i++) {
            String[] values = strPoint.get(i).split("\t\t\t");

            Point p = new Point();
            p.setX(Double.parseDouble(values[1]));
            p.setY(Double.parseDouble(values[2]));

            points.add(p);
        }

        return points;
    }

    public static ArrayList<Location> convertStringToLocations(ArrayList<String> strLocation) {
        ArrayList<Location> locations = new ArrayList<>();

        for (int i = 1; i < strLocation.size(); i++) {
            String[] values = strLocation.get(i).split("\t\t\t");

            Location l = new Location();
            l.setLatitude(Double.parseDouble(values[1]));
            l.setLongitude(Double.parseDouble(values[2]));

            locations.add(l);
        }

        return locations;
    }

    /*public static double[] convertStringToDoubleArray(ArrayList<String> strPoint) {
        double[] points = new double[3 * (strPoint.size() - 1)];

        for (int i = 1; i < strPoint.size(); i++) {
            String[] values = strPoint.get(i).split("\t\t\t");

            int index = 3 * (i - 1);
            points[index] = Double.parseDouble(values[1]);
            points[index + 1] = Double.parseDouble(values[2]);
            points[index + 2] = 0.0;
        }

        return points;
    }*/

    public static float[] convertPointsToFloatArray(ArrayList<Point> points) {
        float[] vertices = new float[3 * points.size()];

        for (int i = 0; i < vertices.length; i += 3) {
            Point p = points.get(i / 3);

            vertices[i] = (float) -p.getX();
            vertices[i + 1] = (float) p.getY();
            vertices[i + 2] = 0.0f;
        }

        return vertices;
    }

    public static float[] centralize(float[] vertices) {
        float[] center = calculateCenter(vertices);

        for (int i = 0; i < vertices.length; i += 3) {
            vertices[i] -= center[0];
            vertices[i + 1] -= center[1];
        }

        return vertices;
    }

    private static float[] calculateCenter(float[] vertices) {
        float sumX = 0.0f, sumY = 0.0f;

        for (int i = 0; i < vertices.length; i += 3) {
            sumX += vertices[i];
            sumY += vertices[i + 1];
        }

        float length = (float) vertices.length / 3.0f;
        return new float[]{sumX / length, sumY / length};
    }

    public static float[] calculateTransformedVertices(float[] mvpMatrix, float[] vertices) {
        float[] transformedVertices = new float[vertices.length];

        for (int i = 0; i < vertices.length; i += 3) {
            transformedVertices[i] = mvpMatrix[0] * vertices[i] + mvpMatrix[4] * vertices[i + 1] +
                    mvpMatrix[8] * vertices[i + 2] + mvpMatrix[12];
            transformedVertices[i + 1] = mvpMatrix[1] * vertices[i] + mvpMatrix[5] * vertices[i + 1] +
                    mvpMatrix[9] * vertices[i + 2] + mvpMatrix[13];
            transformedVertices[i + 2] = mvpMatrix[2] * vertices[i] + mvpMatrix[6] * vertices[i + 1] +
                    mvpMatrix[10] * vertices[i + 2] + mvpMatrix[14];
        }

        return transformedVertices;
    }

    public static float[] calculateGeometrySize(float[] vertices) {
        float minX = vertices[0], maxX = vertices[0], minY = vertices[1], maxY = vertices[1];

        for (int i = 3; i < vertices.length; i += 3) {
            minX = Math.min(minX, vertices[i]);
            maxX = Math.max(maxX, vertices[i]);
            minY = Math.min(minY, vertices[i + 1]);
            maxY = Math.max(maxY, vertices[i + 1]);
        }

        return new float[]{maxX - minX, maxY - minY};
    }

    public static float calculateDistance(float a, float b, float x, float y) {
        return (float) Math.sqrt(Math.pow(x - a, 2.0f) + Math.pow(y - b, 2.0f));
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
