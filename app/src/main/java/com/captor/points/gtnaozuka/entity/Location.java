package com.captor.points.gtnaozuka.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class Location implements Parcelable {
    private double latitude;
    private double longitude;

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
    }

    private Location(Parcel in) {
        this.latitude = in.readDouble();
        this.longitude = in.readDouble();
    }

    public Location() {
    }

    public static final Parcelable.Creator<Location> CREATOR = new Parcelable.Creator<Location>() {
        @Override
        public Location createFromParcel(Parcel source) {
            return new Location(source);
        }

        @Override
        public Location[] newArray(int size) {
            return new Location[size];
        }
    };

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        Location location = (Location) o;
        return Double.compare(location.getLatitude(), getLatitude()) == 0 && Double.compare(location.getLongitude(), getLongitude()) == 0;

    }

    @Override
    public int hashCode() {
        long temp = Double.doubleToLongBits(getLatitude());
        int result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(getLongitude());
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
}
