package ar.edu.itba.pod.hazelcast.client.query2.strategies;

import ar.edu.itba.pod.hazelcast.api.mappers.StationToDistanceMapper;
import ar.edu.itba.pod.hazelcast.api.models.Station;
import ar.edu.itba.pod.hazelcast.api.models.StationAndDistance;
import ar.edu.itba.pod.hazelcast.api.models.Trip;
import ar.edu.itba.pod.hazelcast.api.models.dto.Dto;
import ar.edu.itba.pod.hazelcast.api.reducers.StationsSortedByDistanceReducerFactory;
import ar.edu.itba.pod.hazelcast.client.BaseStrategy;
import ar.edu.itba.pod.hazelcast.client.exceptions.IllegalClientArgumentException;
import ar.edu.itba.pod.hazelcast.client.utils.Arguments;
import ar.edu.itba.pod.hazelcast.client.utils.Constants;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ICompletableFuture;
import com.hazelcast.core.IMap;
import com.hazelcast.core.MultiMap;
import com.hazelcast.mapreduce.Job;
import com.hazelcast.mapreduce.JobTracker;
import com.hazelcast.mapreduce.KeyValueSource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

public class Query2FromStations extends BaseStrategy {
    private static final String JOB_TRACKER_NAME = "top-n-stations";
    private static final String COLLECTION_PREFIX = Constants.COLLECTION_PREFIX + "q2-";
    private static final String STATIONS_MAP_NAME = COLLECTION_PREFIX + "stations";
    private static final String TRIPS_MULTIMAP_NAME = COLLECTION_PREFIX + "trips";

    private int limit;

    private IMap<Integer, Station> stationsMap;
    private MultiMap<Integer, Trip> tripsMap;

    @Override
    protected void initialize(Arguments args, HazelcastInstance hz) {
        Integer limit1 = args.getLimit();
        if (limit1 == null || limit1 <= 0)
            throw new IllegalClientArgumentException("Must specify a limit greater than 0");
        limit = limit1;

        stationsMap = hz.getMap(STATIONS_MAP_NAME);
        tripsMap = hz.getMultiMap(TRIPS_MULTIMAP_NAME);
    }

    @Override
    protected void clearCollections() {
        stationsMap.clear();
        tripsMap.clear();
    }

    @Override
    protected Consumer<Station> getStationsLambda(Arguments args, HazelcastInstance hz) {
        return station -> stationsMap.put(station.getId(), station);
    }

    @Override
    protected Consumer<Trip> getTripsLambda(Arguments args, HazelcastInstance hz) {
        return trip -> {
            if (trip.isMember() && trip.getOrigin() != trip.getDestination())
                tripsMap.put(trip.getOrigin(), trip);
        };
    }

    @Override
    protected Collection<? extends Dto> runClientImpl(Arguments args, HazelcastInstance hz) throws ExecutionException, InterruptedException {
        final JobTracker jt = hz.getJobTracker(JOB_TRACKER_NAME);
        final KeyValueSource<Integer, Station> source = KeyValueSource.fromMap(stationsMap);
        final Job<Integer, Station> job = jt.newJob(source);

        final ICompletableFuture<Map<String, ArrayList<StationAndDistance>>> future = job
                .mapper(new StationToDistanceMapper(STATIONS_MAP_NAME, TRIPS_MULTIMAP_NAME))
                .reducer(new StationsSortedByDistanceReducerFactory(limit))
                .submit();

        return future.get().get("result");
    }
}
