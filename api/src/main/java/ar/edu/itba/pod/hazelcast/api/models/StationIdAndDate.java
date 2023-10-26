package ar.edu.itba.pod.hazelcast.api.models;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.DataSerializable;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Objects;

public class StationIdAndDate implements DataSerializable {
    private int stationId;
    private LocalDate date;

    public StationIdAndDate() {

    }

    public StationIdAndDate(int stationId, LocalDate date) {
        this.stationId = stationId;
        this.date = date;
    }

    public int getStationId() {
        return stationId;
    }

    public void setStationId(int stationId) {
        this.stationId = stationId;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StationIdAndDate that = (StationIdAndDate) o;
        return stationId == that.stationId && Objects.equals(date, that.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(stationId, date);
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeInt(stationId);
        out.writeObject(date);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        stationId = in.readInt();
        date = in.readObject();
    }
}
