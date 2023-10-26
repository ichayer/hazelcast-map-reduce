package ar.edu.itba.pod.hazelcast.client.query4.strategies;

import ar.edu.itba.pod.hazelcast.api.mappers.AffluxPerStationMapper;
import ar.edu.itba.pod.hazelcast.api.mappers.BikeAffluxPerStationDayMapper;
import ar.edu.itba.pod.hazelcast.api.models.StationIdAndDate;
import ar.edu.itba.pod.hazelcast.api.models.Trip;
import ar.edu.itba.pod.hazelcast.api.models.dto.AffluxCountDto;
import ar.edu.itba.pod.hazelcast.api.reducers.AffluxPerStationReducerFactory;
import ar.edu.itba.pod.hazelcast.api.reducers.BikeAffluxPerStationDayReducerFactory;
import ar.edu.itba.pod.hazelcast.api.submitters.AffluxPerStationSubmitter;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.SortedSet;

public class Query4Default implements Strategy {
    private static final String JOB_TRACKER_NAME_1 = "afflux-days-1";
    private static final String JOB_TRACKER_NAME_2 = "afflux-days-2";
    private static final String COLLECTION_PREFIX = Constants.COLLECTION_PREFIX + "q4-";
    private static final String STATIONS_MAP_NAME = COLLECTION_PREFIX + "stations";
    private static final String TRIPS_LIST_NAME = COLLECTION_PREFIX + "trips";
    private static final String MIDDLE_MAP_NAME = COLLECTION_PREFIX + "tmp";
    private static final Logger logger = LoggerFactory.getLogger(Query4Default.class);

    // TODO: Try loading only trip-starts and trip-ends, then mapreduce with that simplified data.

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
        LocalDate startDate = arguments.getStartDate();
        LocalDate endDate = arguments.getEndDate();
        int totalDays = (int) ChronoUnit.DAYS.between(startDate, endDate) + 1;

        final IMap<Integer, String> stationMap = hz.getMap(STATIONS_MAP_NAME);
        final IList<Trip> tripsList = hz.getList(TRIPS_LIST_NAME);
        final IMap<StationIdAndDate, Integer> middleMap = hz.getMap(MIDDLE_MAP_NAME);

        final JobTracker jt1 = hz.getJobTracker(JOB_TRACKER_NAME_1);
        final KeyValueSource<String, Trip> source1 = KeyValueSource.fromList(tripsList);
        final Job<String, Trip> job1 = jt1.newJob(source1);

        final ICompletableFuture<Map<StationIdAndDate, Integer>> future1 = job1
                .mapper(new BikeAffluxPerStationDayMapper(startDate, endDate))
                .reducer(new BikeAffluxPerStationDayReducerFactory())
                .submit();

        try {
            final Map<StationIdAndDate, Integer> result1 = future1.get();
            middleMap.clear();
            middleMap.putAll(result1);

            final JobTracker jt2 = hz.getJobTracker(JOB_TRACKER_NAME_2);
            final KeyValueSource<StationIdAndDate, Integer> source2 = KeyValueSource.fromMap(middleMap);
            final Job<StationIdAndDate, Integer> job2 = jt2.newJob(source2);

            final ICompletableFuture<SortedSet<AffluxCountDto>> future2 = job2
                    .mapper(new AffluxPerStationMapper())
                    .reducer(new AffluxPerStationReducerFactory())
                    .submit(new AffluxPerStationSubmitter(stationMap::get, totalDays));

            final SortedSet<AffluxCountDto> result2 = future2.get();

            CsvHelper.printData(arguments.getOutPath() + Constants.QUERY4_OUTPUT_CSV, Constants.QUERY4_OUTPUT_CSV_HEADER, result2);
        } catch (Exception e) {
            logger.error("Error waiting for the computation to complete and retrieve its result in query 4", e);
        } finally {
            middleMap.clear();
            stationMap.clear();
            tripsList.clear();
        }
    }
}
