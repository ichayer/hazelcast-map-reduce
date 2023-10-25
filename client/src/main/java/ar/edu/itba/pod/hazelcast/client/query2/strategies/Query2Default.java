package ar.edu.itba.pod.hazelcast.client.query2.strategies;

import ar.edu.itba.pod.hazelcast.api.combiners.DistanceCombinerFactory;
import ar.edu.itba.pod.hazelcast.api.mappers.DistanceMapper;
import ar.edu.itba.pod.hazelcast.api.models.Trip;
import ar.edu.itba.pod.hazelcast.api.models.Coordinates;
import ar.edu.itba.pod.hazelcast.api.models.Station;
import ar.edu.itba.pod.hazelcast.api.models.dto.TripsCountDto;
import ar.edu.itba.pod.hazelcast.api.reducers.AverageDistanceReducer;
import ar.edu.itba.pod.hazelcast.client.interfaces.Strategy;
import ar.edu.itba.pod.hazelcast.client.utils.Arguments;
import ar.edu.itba.pod.hazelcast.client.utils.Constants;
import ar.edu.itba.pod.hazelcast.client.utils.CsvHelper;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ICompletableFuture;
import com.hazelcast.core.IMap;
import com.hazelcast.mapreduce.Job;
import com.hazelcast.mapreduce.JobTracker;
import com.hazelcast.mapreduce.KeyValueSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.TreeSet;


public class Query2Default implements Strategy {

    private static final Logger logger = LoggerFactory.getLogger(Query2Default.class);
    private static final String JOB_TRACKER_NAME = "top-n-stations";


    @Override
    public void loadData(Arguments args, HazelcastInstance hz) {
        IMap<Integer, Station> stationMap = hz.getMap(Constants.STATIONS_MAP);
        IMap<Integer, Trip> tripsMap = hz.getMap(Constants.TRIPS_MAP);
        CsvHelper.readData(args.getInPath() + Constants.STATIONS_CSV, (fields, id) -> {
            int stationPk = Integer.parseInt(fields[0]);
            double latitude = Double.parseDouble(fields[2]);
            double longitude = Double.parseDouble(fields[3]);
            stationMap.put(stationPk, new Station(stationPk, fields[1], new Coordinates(latitude, longitude)));
        });

        CsvHelper.readData(args.getInPath() + Constants.TRIPS_CSV, (fields, id) -> {
            int startStation = Integer.parseInt(fields[1]);
            int endStation = Integer.parseInt(fields[3]);
            boolean isMember = Integer.parseInt(fields[4]) != 0;
            if (startStation != endStation && isMember) {
                tripsMap.put(id, new Trip(null, null, startStation, endStation, isMember));
            }
        });
    }

    @Override
    public void runClient(Arguments arguments, HazelcastInstance hz) {
        final JobTracker jt = hz.getJobTracker(JOB_TRACKER_NAME);
        final IMap<Integer, Trip> list = hz.getMap(Constants.TRIPS_MAP);
        final KeyValueSource<Integer, Trip> source = KeyValueSource.fromMap(list);
        final Job<Integer, Trip> job = jt.newJob(source);

        final ICompletableFuture<TreeSet<TripsCountDto>> future;
        job
                .mapper(new DistanceMapper())
                .combiner(new DistanceCombinerFactory())
                .reducer(new AverageDistanceReducer())
                .submit();

//        try {
//            final TreeSet<TopNStationsDto> result = future.get();
//            CsvHelper.printData(arguments.getOutPath() + Constants.QUERY2_OUTPUT_CSV, Constants.QUERY2_OUTPUT_CSV_HEADER, result);
//        } catch (Exception e) {
//            logger.error("Error waiting for the computation to complete and retrieve its result in query 1");
//        }
    }
}
