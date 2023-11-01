package ar.edu.itba.pod.hazelcast.client.query4.strategies;

import ar.edu.itba.pod.hazelcast.api.mappers.TripEndMapper;
import ar.edu.itba.pod.hazelcast.api.models.Station;
import ar.edu.itba.pod.hazelcast.api.models.Trip;
import ar.edu.itba.pod.hazelcast.api.models.dto.AffluxCountDto;
import ar.edu.itba.pod.hazelcast.api.models.dto.Dto;
import ar.edu.itba.pod.hazelcast.api.reducers.AffluxCountReducerFactory;
import ar.edu.itba.pod.hazelcast.api.submitters.AffluxPerStationSubmitter;
import ar.edu.itba.pod.hazelcast.client.BaseStrategy;
import ar.edu.itba.pod.hazelcast.client.exceptions.IllegalClientArgumentException;
import ar.edu.itba.pod.hazelcast.client.utils.Arguments;
import ar.edu.itba.pod.hazelcast.client.utils.Constants;
import com.hazelcast.core.*;
import com.hazelcast.mapreduce.Job;
import com.hazelcast.mapreduce.JobTracker;
import com.hazelcast.mapreduce.KeyValueSource;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.SortedSet;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

public class Query4FromStations extends BaseStrategy {
    private static final String JOB_TRACKER_NAME = "afflux-days-1";
    private static final String COLLECTION_PREFIX = Constants.COLLECTION_PREFIX + "q4-";
    private static final String STATIONS_MAP_NAME = COLLECTION_PREFIX + "stations";
    private static final String TRIPS_BY_ORIGIN_MULTIMAP_NAME = COLLECTION_PREFIX + "tripOrg";
    private static final String TRIPS_BY_DESTINATION_MULTIMAP_NAME = COLLECTION_PREFIX + "tripDst";

    private LocalDate startDate;
    private LocalDate endDate;

    private IMap<Integer, String> stationMap;
    private MultiMap<Integer, Trip> tripsByOrigin;
    private MultiMap<Integer, Trip> tripsByDestination;

    @Override
    protected void initialize(Arguments args, HazelcastInstance hz) {
        startDate = args.getStartDate();
        endDate = args.getEndDate();

        if (startDate == null || endDate == null)
            throw new IllegalClientArgumentException("Both startDate and endDate parameters must be specified for this query");
        if (endDate.isBefore(startDate))
            throw new IllegalClientArgumentException("endDate must be equal or later than startDate");

        stationMap = hz.getMap(STATIONS_MAP_NAME);
        tripsByOrigin = hz.getMultiMap(TRIPS_BY_ORIGIN_MULTIMAP_NAME);
        tripsByDestination = hz.getMultiMap(TRIPS_BY_DESTINATION_MULTIMAP_NAME);
    }

    @Override
    protected void clearCollections() {
        stationMap.clear();
        tripsByOrigin.clear();
        tripsByDestination.clear();
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
                tripsByOrigin.put(trip.getOrigin(), trip);

            // if (tripEndDate >= startDate && tripEndDate <= endDate)
            if (!tripEndDate.isBefore(startDate) && !tripEndDate.isAfter(endDate))
                tripsByDestination.put(trip.getDestination(), trip);
        };
    }

    @Override
    protected Collection<? extends Dto> runClientImpl(Arguments args, HazelcastInstance hz) throws ExecutionException, InterruptedException {
        int totalDays = (int) ChronoUnit.DAYS.between(startDate, endDate) + 1;

        final JobTracker jt = hz.getJobTracker(JOB_TRACKER_NAME);
        final KeyValueSource<Integer, String> source = KeyValueSource.fromMap(stationMap);
        final Job<Integer, String> job = jt.newJob(source);

        final ICompletableFuture<SortedSet<AffluxCountDto>> future = job
                .mapper(new TripEndMapper(TRIPS_BY_ORIGIN_MULTIMAP_NAME, TRIPS_BY_DESTINATION_MULTIMAP_NAME))
                .reducer(new AffluxCountReducerFactory())
                .submit(new AffluxPerStationSubmitter(stationMap::get, totalDays));

        return future.get();
    }
}
