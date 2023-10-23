package ar.edu.itba.pod.hazelcast.api.models;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.DataSerializable;

import java.io.IOException;
import java.util.Objects;

public class TripsCountDto implements Comparable<TripsCountDto>, DataSerializable, Dto {

    private String stationFrom;
    private String stationTo;
    private int count;


    public TripsCountDto(String stationFrom, String stationTo, int count) {
        this.stationFrom = stationFrom;
        this.stationTo = stationTo;
        this.count = count;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TripsCountDto that = (TripsCountDto) o;
        return count == that.count && Objects.equals(stationFrom, that.stationFrom) && Objects.equals(stationTo, that.stationTo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(stationFrom, stationTo, count);
    }

    @Override
    public int compareTo(TripsCountDto o) {
        int compare = this.count - o.count;
        if (compare == 0) {
            compare = this.stationFrom.compareTo(o.stationFrom);
            if (compare == 0) {
                return this.stationTo.compareTo(o.stationTo);
            }
            return compare;
        }
        return compare;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeInt(count);
        out.writeUTF(stationFrom);
        out.writeUTF(stationTo);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.count = in.readInt();
        this.stationFrom = in.readUTF();
        this.stationTo = in.readUTF();
    }

    @Override
    public String toString() {
        return "TripsCountDto{" +
                "stationFrom='" + stationFrom + '\'' +
                ", stationTo='" + stationTo + '\'' +
                ", count=" + count +
                '}';
    }

    @Override
    public String toCsv() {
        return stationFrom + ";" + stationTo + ";" + count;
    }
}