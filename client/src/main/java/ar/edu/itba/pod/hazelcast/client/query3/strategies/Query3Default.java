package ar.edu.itba.pod.hazelcast.client.query3.strategies;

import ar.edu.itba.pod.hazelcast.api.mappers.LongestTripPerStationMapper;
import ar.edu.itba.pod.hazelcast.api.models.Trip;
import ar.edu.itba.pod.hazelcast.api.models.dto.LongestTripDto;
import ar.edu.itba.pod.hazelcast.api.reducers.LongestTripPerStationReducerFactory;
import ar.edu.itba.pod.hazelcast.api.submitters.LongestTripPerStationSubmitter;
import ar.edu.itba.pod.hazelcast.client.interfaces.Strategy;
import ar.edu.itba.pod.hazelcast.client.utils.Arguments;
import ar.edu.itba.pod.hazelcast.client.utils.Constants;
import ar.edu.itba.pod.hazelcast.client.utils.CsvHelper;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ICompletableFuture;
import com.hazelcast.core.IList;
import com.hazelcast.core.IMap;
import com.hazelcast.mapreduce.Job;
import com.hazelcast.mapreduce.JobTracker;
import com.hazelcast.mapreduce.KeyValueSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.SortedSet;

public class Query3Default implements Strategy {
    private static final String JOB_TRACKER_NAME = "longest-lasting-trip";
    private static final String COLLECTION_PREFIX = Constants.COLLECTION_PREFIX + "q3-";
    private static final String STATIONS_MAP_NAME = COLLECTION_PREFIX + "stations";
    private static final String TRIPS_LIST_NAME = COLLECTION_PREFIX + "trips";
    private static final Logger logger = LoggerFactory.getLogger(Query3Default.class);

    @Override
    public void loadData(Arguments args, HazelcastInstance hz) {
        final IMap<Integer, String> stationMap = hz.getMap(STATIONS_MAP_NAME);
        stationMap.clear();
        final IList<Trip> tripsList = hz.getList(TRIPS_LIST_NAME);
        tripsList.clear();

        CsvHelper.readData(args.getInPath() + Constants.STATIONS_CSV, (fields, id) -> {
            int stationPk = Integer.parseInt(fields[0]);
            String stationName = fields[1];
            stationMap.put(stationPk, stationName);
        });

        CsvHelper.readData(args.getInPath() + Constants.TRIPS_CSV, (fields, id) -> {
            LocalDateTime startDate = LocalDateTime.parse(fields[0].replace(' ', 'T'));
            int startStation = Integer.parseInt(fields[1]);
            LocalDateTime endDate = LocalDateTime.parse(fields[2].replace(' ', 'T'));
            int endStation = Integer.parseInt(fields[3]);
            boolean isMember = Integer.parseInt(fields[4]) != 0;
            tripsList.add(new Trip(startDate, endDate, startStation, endStation, isMember));
        });
    }

    @Override
    public void runClient(Arguments arguments, HazelcastInstance hz) {
        final JobTracker jt = hz.getJobTracker(JOB_TRACKER_NAME);
        final IMap<Integer, String> stationMap = hz.getMap(STATIONS_MAP_NAME);
        final IList<Trip> tripsList = hz.getList(TRIPS_LIST_NAME);

        final KeyValueSource<String, Trip> source = KeyValueSource.fromList(tripsList);
        final Job<String, Trip> job = jt.newJob(source);

        final ICompletableFuture<SortedSet<LongestTripDto>> future = job
                .mapper(new LongestTripPerStationMapper())
                .reducer(new LongestTripPerStationReducerFactory())
                .submit(new LongestTripPerStationSubmitter(stationMap::get));

        try {
            final SortedSet<LongestTripDto> result = future.get();
            CsvHelper.printData(arguments.getOutPath() + Constants.QUERY3_OUTPUT_CSV, Constants.QUERY3_OUTPUT_CSV_HEADER, result);
        } catch (Exception e) {
            logger.error("Error waiting for the computation to complete and retrieve its result in query 3", e);
        } finally {
            // TODO: Should we clear the collections after we're done?
            stationMap.clear();
            tripsList.clear();
        }
    }
}
