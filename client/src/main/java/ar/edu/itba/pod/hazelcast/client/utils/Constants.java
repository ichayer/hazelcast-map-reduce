package ar.edu.itba.pod.hazelcast.client.utils;

public final class Constants {
    public static final String COLLECTION_PREFIX = "g4-";

    public static final String BIKES_MAP = COLLECTION_PREFIX + "BikesMap";
    public static final String BIKES_LIST = COLLECTION_PREFIX + "BikesList";
    public static final String STATIONS_MAP = COLLECTION_PREFIX + "StationsMap";
    public static final String BIKES_CSV = "/bikes.csv";
    public static final String STATIONS_CSV = "/stations.csv";

    public static final String QUERY1_OUTPUT_TXT = "/time1.txt";
    public static final String QUERY1_OUTPUT_CSV = "/query1.csv";
    public static final String QUERY1_OUTPUT_CSV_HEADER = "station_a;station_b;trips_between_a_b";

    public static final String QUERY2_OUTPUT_CSV = "/query2.csv";
    public static final String QUERY2_OUTPUT_TXT = "/time2.txt";

    public static final String QUERY3_OUTPUT_TXT = "/time3.txt";
    public static final String QUERY3_OUTPUT_CSV = "/query3.csv";
    public static final String QUERY3_OUTPUT_CSV_HEADER = "start_station;end_station;start_date;minutes";

    // Suppress default constructor for noninstantiability
    private Constants() {
        throw new AssertionError();
    }
}
