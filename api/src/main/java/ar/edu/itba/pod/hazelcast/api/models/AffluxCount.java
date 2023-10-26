package ar.edu.itba.pod.hazelcast.api.models;

import ar.edu.itba.pod.hazelcast.api.models.dto.Dto;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.DataSerializable;

import java.io.IOException;
import java.util.Objects;

public class AffluxCount implements DataSerializable {
    private int positive;
    private int negative;

    public AffluxCount() {

    }

    public AffluxCount(int positive, int negative) {
        this.positive = positive;
        this.negative = negative;
    }

    public int getPositive() {
        return positive;
    }

    public void setPositive(int positive) {
        this.positive = positive;
    }

    public int getNegative() {
        return negative;
    }

    public void setNegative(int negative) {
        this.negative = negative;
    }

    public void add(AffluxCount other) {
        this.positive += other.positive;
        this.negative += other.negative;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AffluxCount that = (AffluxCount) o;
        return positive == that.positive && negative == that.negative;
    }

    @Override
    public int hashCode() {
        return Objects.hash(positive, negative);
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeInt(positive);
        out.writeInt(negative);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        positive = in.readInt();
        negative = in.readInt();
    }
}
