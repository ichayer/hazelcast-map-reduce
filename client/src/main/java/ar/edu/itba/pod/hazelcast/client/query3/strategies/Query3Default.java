package ar.edu.itba.pod.hazelcast.client.query3.strategies;

import ar.edu.itba.pod.hazelcast.api.mappers.LongestTripPerStationMapper;
import ar.edu.itba.pod.hazelcast.api.models.Trip;
import ar.edu.itba.pod.hazelcast.api.models.Coordinates;
import ar.edu.itba.pod.hazelcast.api.models.Station;
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
    private static final Logger logger = LoggerFactory.getLogger(Query3Default.class);

    @Override
    public void loadData(Arguments args, HazelcastInstance hz) {
        IMap<Integer, Station> stationMap = hz.getMap(Constants.STATIONS_MAP);
        stationMap.clear();
        IList<Trip> tripsList = hz.getList(Constants.TRIPS_LIST);
        tripsList.clear();

        CsvHelper.ReadData(args.getInPath() + Constants.STATIONS_CSV, (fields, id) -> {
            int stationPk = Integer.parseInt(fields[0]);
            String stationName = fields[1];
            double latitude = Double.parseDouble(fields[2]);
            double longitude = Double.parseDouble(fields[3]);
            stationMap.put(stationPk, new Station(stationPk, stationName, new Coordinates(latitude, longitude)));
        });

        CsvHelper.ReadData(args.getInPath() + Constants.TRIPS_CSV, (fields, id) -> {
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
        final IList<Trip> list = hz.getList(Constants.TRIPS_LIST);
        IMap<Integer, Station> stationMap = hz.getMap(Constants.STATIONS_MAP);

        final KeyValueSource<String, Trip> source = KeyValueSource.fromList(list);
        final Job<String, Trip> job = jt.newJob(source);

        final ICompletableFuture<SortedSet<LongestTripDto>> future = job
                .mapper(new LongestTripPerStationMapper())
                .reducer(new LongestTripPerStationReducerFactory())
                .submit(new LongestTripPerStationSubmitter(id -> stationMap.get(id).getName()));

        try {
            final SortedSet<LongestTripDto> result = future.get();
            CsvHelper.printData(arguments.getOutPath() + Constants.QUERY3_OUTPUT_CSV, Constants.QUERY3_OUTPUT_CSV_HEADER, result);
        } catch (Exception e) {
            logger.error("Error waiting for the computation to complete and retrieve its result in query 3", e);
        }
    }
}
