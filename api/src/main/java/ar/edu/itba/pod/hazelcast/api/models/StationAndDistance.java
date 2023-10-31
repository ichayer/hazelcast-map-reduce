package ar.edu.itba.pod.hazelcast.api.models;

import ar.edu.itba.pod.hazelcast.api.models.dto.Dto;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.DataSerializable;

import java.io.IOException;
import java.util.Objects;

public class StationAndDistance implements Comparable<StationAndDistance>, DataSerializable, Dto {
    private Station station;
    private double distance;

    public StationAndDistance() {

    }

    public StationAndDistance(Station station, double distance) {
        this.station = station;
        this.distance = distance;
    }

    public Station getStation() {
        return station;
    }

    public void setStation(Station station) {
        this.station = station;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        station.writeData(out);
        out.writeDouble(distance);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        station = new Station();
        station.readData(in);
        distance = in.readDouble();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StationAndDistance that = (StationAndDistance) o;
        return Double.compare(that.distance, distance) == 0 && Objects.equals(station, that.station);
    }

    @Override
    public int hashCode() {
        return Objects.hash(station, distance);
    }

    @Override
    public int compareTo(StationAndDistance other) {
        int cmp = Double.compare(other.distance, this.distance);
        return cmp != 0 ? cmp : station.getName().compareToIgnoreCase(other.station.getName());
    }

    @Override
    public String toCsv() {
        return String.format("%s;%.2f", station.getName(), distance);
    }
}
