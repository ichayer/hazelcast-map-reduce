package ar.edu.itba.pod.hazelcast.api.models.dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class LongestTripDto implements Dto, Comparable<LongestTripDto> {
    private static final DateTimeFormatter printFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private String startStationName;
    private String endStationName;
    private LocalDateTime startDate;
    private int durationMinutes;

    public LongestTripDto() {

    }

    public LongestTripDto(String startStationName, String endStationName, LocalDateTime startDate, int durationMinutes) {
        this.startStationName = startStationName;
        this.endStationName = endStationName;
        this.startDate = startDate;
        this.durationMinutes = durationMinutes;
    }

    @Override
    public String toCsv() {
        return String.format("%s;%s;%s;%d", startStationName, endStationName, startDate.format(printFormatter), durationMinutes);
    }

    @Override
    public int compareTo(LongestTripDto other) {
        int cmp = Integer.compare(other.durationMinutes, this.durationMinutes);
        if (cmp != 0)
            return cmp;
        return startStationName.compareToIgnoreCase(endStationName);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LongestTripDto that = (LongestTripDto) o;
        return durationMinutes == that.durationMinutes && Objects.equals(startStationName, that.startStationName) && Objects.equals(endStationName, that.endStationName) && Objects.equals(startDate, that.startDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(startStationName, endStationName, startDate, durationMinutes);
    }
}
