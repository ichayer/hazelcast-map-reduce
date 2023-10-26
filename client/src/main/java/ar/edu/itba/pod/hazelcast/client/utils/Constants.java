package ar.edu.itba.pod.hazelcast.client.utils;

public final class Constants {
    public static final String COLLECTION_PREFIX = "g4-";

    public static final String TRIPS_MAP = COLLECTION_PREFIX + "TripsMap";
    public static final String TRIPS_LIST = COLLECTION_PREFIX + "TripsList";
    public static final String STATIONS_MAP = COLLECTION_PREFIX + "StationsMap";
    public static final String TRIPS_CSV = "/bikes.csv";
    public static final String STATIONS_CSV = "/stations.csv";

    public static final String QUERY1_OUTPUT_TXT = "/time1.txt";
    public static final String QUERY1_OUTPUT_CSV = "/query1.csv";
    public static final String QUERY1_OUTPUT_CSV_HEADER = "station_a;station_b;trips_between_a_b";

    public static final String QUERY2_OUTPUT_CSV = "/query2.csv";
    public static final String QUERY2_OUTPUT_TXT = "/time2.txt";

    public static final String QUERY3_OUTPUT_TXT = "/time3.txt";
    public static final String QUERY3_OUTPUT_CSV = "/query3.csv";
    public static final String QUERY3_OUTPUT_CSV_HEADER = "start_station;end_station;start_date;minutes";

    public static final String QUERY4_OUTPUT_CSV = "/query4.csv";
    public static final String QUERY4_OUTPUT_TXT = "/time4.txt";
    public static final String QUERY4_OUTPUT_CSV_HEADER = "station;pos_afflux;neutral_afflux;negative_afflux";

    public static final String DEFAULT_PATHNAME =  "pathname";

    // Suppress default constructor for noninstantiability
    private Constants() {
        throw new AssertionError();
    }
}
