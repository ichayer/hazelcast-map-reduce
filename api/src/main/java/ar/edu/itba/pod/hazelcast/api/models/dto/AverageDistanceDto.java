package ar.edu.itba.pod.hazelcast.api.models.dto;

import ar.edu.itba.pod.hazelcast.api.models.Station;

public class AverageDistanceDto implements Dto, Comparable<AverageDistanceDto> {

    private Station station;
    private double averageDistance;

    public AverageDistanceDto() {

    }

    public AverageDistanceDto(final Station station, double averageDistance) {
        this.station = station;
        this.averageDistance = Math.round(averageDistance * 100.0) / 100.0;
    }

    @Override
    public String toCsv() {
        return station.getName() + ";" + averageDistance;
    }

    @Override
    public int compareTo(AverageDistanceDto o) {
        int compare = Double.compare(o.averageDistance, this.averageDistance);
        if(compare == 0) {
            return String.CASE_INSENSITIVE_ORDER.compare(this.station.getName(), o.station.getName());
        }
        return compare;
    }

    public Station getStation() {
        return station;
    }

    public void setStation(Station station) {
        this.station = station;
    }

    public double getAverageDistance() {
        return averageDistance;
    }

    public void setAverageDistance(double averageDistance) {
        this.averageDistance = averageDistance;
    }
}
