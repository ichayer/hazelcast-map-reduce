package ar.edu.itba.pod.hazelcast.api.models;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.DataSerializable;

import java.io.IOException;
import java.util.Objects;

// https://stackoverflow.com/questions/6754881/java-double-vs-bigdecimal-for-latitude-longitude
public class Coordinates implements DataSerializable {

    private static final int EARTH_RADIUS = 6371; // km

    private double latitude;
    private double longitude;

    public Coordinates() {

    }

    public Coordinates(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    private double haversine(double theta) {
        return Math.pow(Math.sin(theta / 2), 2);
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeDouble(latitude);
        out.writeDouble(longitude);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.latitude = in.readDouble();
        this.longitude = in.readDouble();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (! (o instanceof Coordinates))
            return false;
        Coordinates cord = (Coordinates) o;
        return this.longitude == cord.longitude && this.latitude == cord.latitude;
    }

    @Override
    public int hashCode() {
        return Objects.hash(latitude, longitude);
    }

    public double getLongitude() {
        return this.longitude;
    }

    public double getLatitude() {
        return this.latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    // https://www.geeksforgeeks.org/haversine-formula-to-find-distance-between-two-points-on-a-sphere/
    public double distanceTo(Coordinates other) {
        double fromLatitudeRadians = Math.toRadians(this.latitude);
        double toLatitudeRadians = Math.toRadians(other.latitude);

        double deltaLatitude = toLatitudeRadians - fromLatitudeRadians;
        double deltaLongitude = Math.toRadians(other.longitude - this.longitude);

        double centralAngle = haversine(deltaLatitude) + haversine(deltaLongitude)* Math.cos(fromLatitudeRadians) * Math.cos(toLatitudeRadians);

        return EARTH_RADIUS * 2 * Math.asin(Math.sqrt(centralAngle));
    }

    @Override
    public String toString() {
        return "Coordinates{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
}
