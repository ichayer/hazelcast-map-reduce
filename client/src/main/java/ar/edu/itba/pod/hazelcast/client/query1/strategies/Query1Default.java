package ar.edu.itba.pod.hazelcast.client.query1.strategies;

import ar.edu.itba.pod.hazelcast.api.mappers.TripsCountMapper;
import ar.edu.itba.pod.hazelcast.api.models.Station;
import ar.edu.itba.pod.hazelcast.api.models.Trip;
import ar.edu.itba.pod.hazelcast.api.models.dto.Dto;
import ar.edu.itba.pod.hazelcast.api.models.dto.TripsCountDto;
import ar.edu.itba.pod.hazelcast.api.reducers.TripsCountReducerFactory;
import ar.edu.itba.pod.hazelcast.api.submitters.TripsCountSubmitter;
import ar.edu.itba.pod.hazelcast.client.BaseStrategy;
import ar.edu.itba.pod.hazelcast.client.utils.Arguments;
import ar.edu.itba.pod.hazelcast.client.utils.Constants;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ICompletableFuture;
import com.hazelcast.core.IList;
import com.hazelcast.core.IMap;
import com.hazelcast.mapreduce.Job;
import com.hazelcast.mapreduce.JobTracker;
import com.hazelcast.mapreduce.KeyValueSource;

import java.util.AbstractMap;
import java.util.Collection;
import java.util.Map;
import java.util.SortedSet;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

public class Query1Default extends BaseStrategy {
    private static final String JOB_TRACKER_NAME = "travel-count";
    private static final String COLLECTION_PREFIX = Constants.COLLECTION_PREFIX + "q1-";
    private static final String STATIONS_MAP_NAME = COLLECTION_PREFIX + "stations";
    private static final String TRIPS_LIST_NAME = COLLECTION_PREFIX + "trips";

    private IMap<Integer, String> stationMap;
    private IList<AbstractMap.SimpleEntry<Integer, Integer>> tripsList;

    @Override
    protected void initialize(Arguments args, HazelcastInstance hz) {
        stationMap = hz.getMap(STATIONS_MAP_NAME);
        tripsList = hz.getList(TRIPS_LIST_NAME);
    }

    @Override
    protected void clearCollections() {
        stationMap.clear();
        tripsList.clear();
    }

    @Override
    protected Consumer<Station> getStationsLambda(Arguments args, HazelcastInstance h) {
        return station -> stationMap.put(station.getId(), station.getName());
    }

    @Override
    protected Consumer<Trip> getTripsLambda(Arguments args, HazelcastInstance h) {
        return trip -> tripsList.add(new AbstractMap.SimpleEntry<>(trip.getOrigin(), trip.getDestination()));
    }

    @Override
    protected Collection<? extends Dto> runClientImpl(Arguments args, HazelcastInstance hz) throws ExecutionException, InterruptedException {
        final JobTracker jt = hz.getJobTracker(JOB_TRACKER_NAME);
        final KeyValueSource<String, Map.Entry<Integer, Integer>> source = KeyValueSource.fromList(tripsList);
        final Job<String, Map.Entry<Integer, Integer>> job = jt.newJob(source);

        final ICompletableFuture<SortedSet<TripsCountDto>> future = job
                .mapper(new TripsCountMapper())
                .reducer(new TripsCountReducerFactory())
                .submit(new TripsCountSubmitter(stationMap::get));

        return future.get();
    }
}
