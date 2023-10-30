package ar.edu.itba.pod.hazelcast.client.query4.strategies;

import ar.edu.itba.pod.hazelcast.api.combiners.AffluxPerStationCombinerFactory;
import ar.edu.itba.pod.hazelcast.api.combiners.BikeAffluxPerStationDayCombinerFactory;
import ar.edu.itba.pod.hazelcast.api.mappers.AffluxPerStationMapper;
import ar.edu.itba.pod.hazelcast.api.mappers.TripEndMapper;
import ar.edu.itba.pod.hazelcast.api.models.Station;
import ar.edu.itba.pod.hazelcast.api.models.StationIdAndDate;
import ar.edu.itba.pod.hazelcast.api.models.Trip;
import ar.edu.itba.pod.hazelcast.api.models.TripEnd;
import ar.edu.itba.pod.hazelcast.api.models.dto.AffluxCountDto;
import ar.edu.itba.pod.hazelcast.api.models.dto.Dto;
import ar.edu.itba.pod.hazelcast.api.reducers.AffluxPerStationReducerFactory;
import ar.edu.itba.pod.hazelcast.api.reducers.BikeAffluxPerStationDayReducerFactory;
import ar.edu.itba.pod.hazelcast.api.submitters.AffluxPerStationSubmitter;
import ar.edu.itba.pod.hazelcast.client.BaseStrategy;
import ar.edu.itba.pod.hazelcast.client.exceptions.IllegalClientArgumentException;
import ar.edu.itba.pod.hazelcast.client.utils.Arguments;
import ar.edu.itba.pod.hazelcast.client.utils.Constants;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ICompletableFuture;
import com.hazelcast.core.IList;
import com.hazelcast.core.IMap;
import com.hazelcast.mapreduce.Job;
import com.hazelcast.mapreduce.JobTracker;
import com.hazelcast.mapreduce.KeyValueSource;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Map;
import java.util.SortedSet;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

public class Query4TripEndsOnly extends BaseStrategy {
    private static final String JOB_TRACKER_NAME_1 = "afflux-days-1";
    private static final String JOB_TRACKER_NAME_2 = "afflux-days-2";
    private static final String COLLECTION_PREFIX = Constants.COLLECTION_PREFIX + "q4-";
    private static final String STATIONS_MAP_NAME = COLLECTION_PREFIX + "stations";
    private static final String TRIPS_LIST_NAME = COLLECTION_PREFIX + "trips";
    private static final String MIDDLE_MAP_NAME = COLLECTION_PREFIX + "tmp";

    private LocalDate startDate;
    private LocalDate endDate;

    private IMap<Integer, String> stationMap;
    private IList<TripEnd> tripEndsList;
    private IMap<StationIdAndDate, Integer> middleMap;

    @Override
    protected void initialize(Arguments args, HazelcastInstance hz) {
        startDate = args.getStartDate();
        endDate = args.getEndDate();

        if (startDate == null || endDate == null)
            throw new IllegalClientArgumentException("Both startDate and endDate parameters must be specified for this query");
        if (endDate.isBefore(startDate))
            throw new IllegalClientArgumentException("endDate must be equal or later than startDate");

        stationMap = hz.getMap(STATIONS_MAP_NAME);
        tripEndsList = hz.getList(TRIPS_LIST_NAME);
        middleMap = hz.getMap(MIDDLE_MAP_NAME);
    }

    @Override
    protected void clearCollections() {
        stationMap.clear();
        tripEndsList.clear();
        middleMap.clear();
    }

    @Override
    protected Consumer<Station> getStationsLambda(Arguments args, HazelcastInstance hz) {
        return station -> stationMap.put(station.getId(), station.getName());
    }

    @Override
    protected Consumer<Trip> getTripsLambda(Arguments args, HazelcastInstance hz) {
        return trip -> {
            LocalDate tripStartDate = trip.getStartDateTime().toLocalDate();
            LocalDate tripEndDate = trip.getEndDateTime().toLocalDate();

            // if (tripStartDate >= startDate && tripStartDate <= endDate)
            if (!tripStartDate.isBefore(startDate) && !tripStartDate.isAfter(endDate))
                tripEndsList.add(new TripEnd(trip.getOrigin(), tripStartDate, -1));

            // if (tripEndDate >= startDate && tripEndDate <= endDate)
            if (!tripEndDate.isBefore(startDate) && !tripEndDate.isAfter(endDate))
                tripEndsList.add(new TripEnd(trip.getDestination(), tripEndDate, 1));
        };
    }

    @Override
    protected Collection<? extends Dto> runClientImpl(Arguments args, HazelcastInstance hz) throws ExecutionException, InterruptedException {
        int totalDays = (int) ChronoUnit.DAYS.between(startDate, endDate) + 1;

        final JobTracker jt1 = hz.getJobTracker(JOB_TRACKER_NAME_1);
        final KeyValueSource<String, TripEnd> source1 = KeyValueSource.fromList(tripEndsList);
        final Job<String, TripEnd> job1 = jt1.newJob(source1);

        final ICompletableFuture<Map<StationIdAndDate, Integer>> future1 = job1
                .mapper(new TripEndMapper())
                .combiner(new BikeAffluxPerStationDayCombinerFactory())
                .reducer(new BikeAffluxPerStationDayReducerFactory())
                .submit();

        final Map<StationIdAndDate, Integer> result1 = future1.get();
        middleMap.putAll(result1);

        final JobTracker jt2 = hz.getJobTracker(JOB_TRACKER_NAME_2);
        final KeyValueSource<StationIdAndDate, Integer> source2 = KeyValueSource.fromMap(middleMap);
        final Job<StationIdAndDate, Integer> job2 = jt2.newJob(source2);

        final ICompletableFuture<SortedSet<AffluxCountDto>> future2 = job2
                .mapper(new AffluxPerStationMapper())
                .combiner(new AffluxPerStationCombinerFactory())
                .reducer(new AffluxPerStationReducerFactory())
                .submit(new AffluxPerStationSubmitter(stationMap::get, totalDays));

        return future2.get();
    }
}
