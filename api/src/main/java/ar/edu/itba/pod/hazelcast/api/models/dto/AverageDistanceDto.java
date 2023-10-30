package ar.edu.itba.pod.hazelcast.api.models.dto;

public class AverageDistanceDto implements Dto, Comparable<AverageDistanceDto> {
    private String stationName;
    private double averageDistance;

    public AverageDistanceDto() {

    }

    public AverageDistanceDto(String stationName, double averageDistance) {
        this.stationName = stationName;
        this.averageDistance = Math.round(averageDistance * 100.0) / 100.0;
    }

    @Override
    public String toCsv() {
        return stationName + ";" + averageDistance;
    }

    @Override
    public int compareTo(AverageDistanceDto o) {
        int compare = Double.compare(o.averageDistance, this.averageDistance);
        if(compare == 0) {
            return String.CASE_INSENSITIVE_ORDER.compare(this.stationName, o.stationName);
        }
        return compare;
    }

    public String getStationName() {
        return stationName;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
    }

    public double getAverageDistance() {
        return averageDistance;
    }

    public void setAverageDistance(double averageDistance) {
        this.averageDistance = averageDistance;
    }
}
