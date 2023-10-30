package ar.edu.itba.pod.hazelcast.api.models;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.DataSerializable;

import java.io.IOException;
import java.time.LocalDate;

public class TripEnd implements DataSerializable {
    private int stationId;
    private LocalDate date;
    private int afflux;

    public TripEnd() {

    }

    public TripEnd(int stationId, LocalDate date, int afflux) {
        this.stationId = stationId;
        this.date = date;
        this.afflux = afflux;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeInt(stationId);
        out.writeObject(date);
        out.writeInt(afflux);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        stationId = in.readInt();
        date = in.readObject();
        afflux = in.readInt();
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

    public int getAfflux() {
        return afflux;
    }

    public void setAfflux(int afflux) {
        this.afflux = afflux;
    }
}
