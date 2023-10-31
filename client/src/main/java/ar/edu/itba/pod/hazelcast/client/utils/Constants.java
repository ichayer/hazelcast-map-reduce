package ar.edu.itba.pod.hazelcast.client.utils;

public final class Constants {
    public static final String COLLECTION_PREFIX = "g4-";

    public static final String TRIPS_CSV = "/bikes.csv";
    public static final String STATIONS_CSV = "/stations.csv";

    public static final String QUERY1_OUTPUT_CSV_HEADER = "station_a;station_b;trips_between_a_b";
    public static final String QUERY2_OUTPUT_CSV_HEADER = "station;avg_distance";
    public static final String QUERY3_OUTPUT_CSV_HEADER = "start_station;end_station;start_date;minutes";
    public static final String QUERY4_OUTPUT_CSV_HEADER = "station;pos_afflux;neutral_afflux;negative_afflux";

    public static final String QUERY_OUTPUT_FILE_NAME = "query%s.csv";
    public static final String TIME_OUTPUT_FILE_NAME = "time%s.txt";

    public static final String LOG4J_PARAM_NAME = "pathname";

    // Suppress default constructor for noninstantiability
    private Constants() {
        throw new AssertionError();
    }
}
