package ar.edu.itba.pod.hazelcast.api.models.dto;

import java.util.Objects;

public class AffluxCountDto implements Dto, Comparable<AffluxCountDto> {
    private String stationName;
    private int positive;
    private int neutral;
    private int negative;

    public AffluxCountDto() {

    }

    public AffluxCountDto(String stationName, int positive, int neutral, int negative) {
        this.stationName = stationName;
        this.positive = positive;
        this.neutral = neutral;
        this.negative = negative;
    }

    @Override
    public String toCsv() {
        return String.format("%s;%d;%d;%d", stationName, positive, neutral, negative);
    }

    public String getStationName() {
        return stationName;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
    }

    public int getPositive() {
        return positive;
    }

    public void setPositive(int positive) {
        this.positive = positive;
    }

    public int getNeutral() {
        return neutral;
    }

    public void setNeutral(int neutral) {
        this.neutral = neutral;
    }

    public int getNegative() {
        return negative;
    }

    public void setNegative(int negative) {
        this.negative = negative;
    }

    @Override
    public int compareTo(AffluxCountDto other) {
        int cmp = Integer.compare(other.positive, positive);
        return cmp != 0 ? cmp : stationName.compareToIgnoreCase(other.stationName);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AffluxCountDto that = (AffluxCountDto) o;
        return positive == that.positive && neutral == that.neutral && negative == that.negative && Objects.equals(stationName, that.stationName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(stationName, positive, neutral, negative);
    }
}
