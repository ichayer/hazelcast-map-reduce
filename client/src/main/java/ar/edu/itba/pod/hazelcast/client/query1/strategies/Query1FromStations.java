package ar.edu.itba.pod.hazelcast.client.query1.strategies;

import ar.edu.itba.pod.hazelcast.api.combiners.StationsSortedByDistanceCombinerFactory;
import ar.edu.itba.pod.hazelcast.api.combiners.TripsCountCombinerFactory;
import ar.edu.itba.pod.hazelcast.api.mappers.StationToDistanceMapper;
import ar.edu.itba.pod.hazelcast.api.mappers.TripsCountFromMultiMapMapper;
import ar.edu.itba.pod.hazelcast.api.models.Station;
import ar.edu.itba.pod.hazelcast.api.models.StationAndDistance;
import ar.edu.itba.pod.hazelcast.api.models.Trip;
import ar.edu.itba.pod.hazelcast.api.models.dto.Dto;
import ar.edu.itba.pod.hazelcast.api.models.dto.TripsCountDto;
import ar.edu.itba.pod.hazelcast.api.reducers.StationsSortedByDistanceReducerFactory;
import ar.edu.itba.pod.hazelcast.api.reducers.TripsCountReducerFactory;
import ar.edu.itba.pod.hazelcast.api.submitters.TripsCountSubmitter;
import ar.edu.itba.pod.hazelcast.client.BaseStrategy;
import ar.edu.itba.pod.hazelcast.client.utils.Arguments;
import ar.edu.itba.pod.hazelcast.client.utils.Constants;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ICompletableFuture;
import com.hazelcast.core.IMap;
import com.hazelcast.core.MultiMap;
import com.hazelcast.mapreduce.Job;
import com.hazelcast.mapreduce.JobTracker;
import com.hazelcast.mapreduce.KeyValueSource;

import java.util.Collection;
import java.util.SortedSet;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

public class Query1FromStations extends BaseStrategy {

    private static final String JOB_TRACKER_NAME = "travel-count";
    private static final String COLLECTION_PREFIX = Constants.COLLECTION_PREFIX + "q1-";
    private static final String STATIONS_MAP_NAME = COLLECTION_PREFIX + "stations";
    private static final String TRIPS_MAP_NAME = COLLECTION_PREFIX + "trips";

    private transient IMap<Integer, String> stationsMap;
    private transient MultiMap<Integer, Trip> tripsMap;


    @Override
    protected void initialize(Arguments args, HazelcastInstance hz) {
        stationsMap = hz.getMap(STATIONS_MAP_NAME);
        tripsMap = hz.getMultiMap(TRIPS_MAP_NAME);
    }

    @Override
    protected void clearCollections() {
        stationsMap.clear();
        tripsMap.clear();
    }

    @Override
    protected Consumer<Station> getStationsLambda(Arguments args, HazelcastInstance hz) {
        return station -> stationsMap.put(station.getId(), station.getName());
    }

    @Override
    protected Consumer<Trip> getTripsLambda(Arguments args, HazelcastInstance hz) {
        return trip -> {
            if (trip.getOrigin() != trip.getDestination())
                tripsMap.put(trip.getOrigin(), trip);
        };
    }

    @Override
    protected Collection<? extends Dto> runClientImpl(Arguments args, HazelcastInstance hz) throws ExecutionException, InterruptedException {
        final JobTracker jt = hz.getJobTracker(JOB_TRACKER_NAME);
        final KeyValueSource<Integer, String> source = KeyValueSource.fromMap(stationsMap);
        final Job<Integer, String> job = jt.newJob(source);

        final ICompletableFuture<SortedSet<TripsCountDto>> future = job
                .mapper(new TripsCountFromMultiMapMapper(TRIPS_MAP_NAME))
                .combiner(new TripsCountCombinerFactory())
                .reducer(new TripsCountReducerFactory())
                .submit(new TripsCountSubmitter(stationsMap::get));

        return future.get();
    }
}
