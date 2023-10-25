package ar.edu.itba.pod.hazelcast.api.models;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.DataSerializable;

import java.io.IOException;
import java.time.LocalDateTime;

public class Trip implements DataSerializable {

    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private int origin;
    private int destination;
    private boolean isMember;

    public Trip() {

    }

    public Trip(LocalDateTime startDateTime, LocalDateTime endDateTime, int origin, int destination, boolean isMember) {
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.origin = origin;
        this.destination = destination;
        this.isMember = isMember;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeObject(startDateTime);
        out.writeObject(endDateTime);
        out.writeInt(origin);
        out.writeInt(destination);
        out.writeBoolean(isMember);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.startDateTime = in.readObject();
        this.endDateTime = in.readObject();
        this.origin = in.readInt();
        this.destination = in.readInt();
        this.isMember = in.readBoolean();
    }

    public LocalDateTime getStartDateTime() {
        return startDateTime;
    }

    public LocalDateTime getEndDateTime() {
        return endDateTime;
    }

    public int getOrigin() {
        return origin;
    }

    public int getDestination() {
        return destination;
    }

    public boolean isMember() {
        return isMember;
    }

    public void setStartDateTime(LocalDateTime startDateTime) {
        this.startDateTime = startDateTime;
    }

    public void setEndDateTime(LocalDateTime endDateTime) {
        this.endDateTime = endDateTime;
    }

    public void setDestination(int destination) {
        this.destination = destination;
    }

    public void setMember(boolean member) {
        isMember = member;
    }

    public void setOrigin(int origin) {
        this.origin = origin;
    }

    @Override
    public String toString() {
        return "Trip{" +
                "startDateTime=" + startDateTime +
                ", endDateTime=" + endDateTime +
                ", origin=" + origin +
                ", destination=" + destination +
                ", isMember=" + isMember +
                '}';
    }
}
