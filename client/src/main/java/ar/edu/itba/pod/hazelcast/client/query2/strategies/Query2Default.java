package ar.edu.itba.pod.hazelcast.client.query2.strategies;

import ar.edu.itba.pod.hazelcast.api.combiners.DistanceCombinerFactory;
import ar.edu.itba.pod.hazelcast.api.mappers.DistanceMapper;
import ar.edu.itba.pod.hazelcast.api.models.dto.AverageDistanceDto;
import ar.edu.itba.pod.hazelcast.api.models.Trip;
import ar.edu.itba.pod.hazelcast.api.models.Station;
import ar.edu.itba.pod.hazelcast.api.models.dto.Dto;
import ar.edu.itba.pod.hazelcast.api.reducers.AverageDistanceReducerFactory;
import ar.edu.itba.pod.hazelcast.api.submitters.AverageDistanceSubmitter;
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

import java.util.Collection;
import java.util.SortedSet;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;


public class Query2Default extends BaseStrategy {
    private static final String JOB_TRACKER_NAME = "top-n-stations";
    private static final String COLLECTION_PREFIX = Constants.COLLECTION_PREFIX + "q2-";
    private static final String STATIONS_MAP_NAME = COLLECTION_PREFIX + "stations";
    private static final String TRIPS_LIST_NAME = COLLECTION_PREFIX + "trips";

    private int limit;

    private IMap<Integer, Station> stationMap;
    private IList<Trip> tripsList;

    @Override
    protected void initialize(Arguments args, HazelcastInstance hz) {
        Integer limit1 = args.getLimit();
        if (limit1 == null || limit1 <= 0)
            throw new IllegalClientArgumentException("Must specify a limit greater than 0");
        limit = limit1;

        stationMap = hz.getMap(STATIONS_MAP_NAME);
        tripsList = hz.getList(TRIPS_LIST_NAME);
    }

    @Override
    protected void clearCollections() {
        stationMap.clear();
        tripsList.clear();
    }

    @Override
    protected Consumer<Station> getStationsLambda(Arguments args, HazelcastInstance hz) {
        return station -> stationMap.put(station.getId(), station);
    }

    @Override
    protected Consumer<Trip> getTripsLambda(Arguments args, HazelcastInstance hz) {
        return trip -> {
            if (trip.isMember())
                tripsList.add(trip);
        };
    }

    @Override
    protected Collection<? extends Dto> runClientImpl(Arguments args, HazelcastInstance hz) throws ExecutionException, InterruptedException {
        final JobTracker jt = hz.getJobTracker(JOB_TRACKER_NAME);
        final KeyValueSource<String, Trip> source = KeyValueSource.fromList(tripsList);
        final Job<String, Trip> job = jt.newJob(source);

        final ICompletableFuture<SortedSet<AverageDistanceDto>> future = job
                .mapper(new DistanceMapper(STATIONS_MAP_NAME))
                .combiner(new DistanceCombinerFactory())
                .reducer(new AverageDistanceReducerFactory())
                .submit(new AverageDistanceSubmitter(limit, stationId -> stationMap.get(stationId).getName()));

        // TODO: Consider doing another strategy where instead of finding the top 10 via a submitter, the results of
        // the map are re-loaded into a distributed map and a second mapreduce operation finds the top N

        return future.get();
    }
}
