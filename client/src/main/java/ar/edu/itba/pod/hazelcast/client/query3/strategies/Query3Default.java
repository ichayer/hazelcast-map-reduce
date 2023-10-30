package ar.edu.itba.pod.hazelcast.client.query3.strategies;

import ar.edu.itba.pod.hazelcast.api.combiners.LongestTripPerStationCombinerFactory;
import ar.edu.itba.pod.hazelcast.api.mappers.LongestTripPerStationMapper;
import ar.edu.itba.pod.hazelcast.api.models.Station;
import ar.edu.itba.pod.hazelcast.api.models.Trip;
import ar.edu.itba.pod.hazelcast.api.models.dto.Dto;
import ar.edu.itba.pod.hazelcast.api.models.dto.LongestTripDto;
import ar.edu.itba.pod.hazelcast.api.reducers.LongestTripPerStationReducerFactory;
import ar.edu.itba.pod.hazelcast.api.submitters.LongestTripPerStationSubmitter;
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

import java.util.Collection;
import java.util.SortedSet;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

public class Query3Default extends BaseStrategy {
    private static final String JOB_TRACKER_NAME = "longest-lasting-trip";
    private static final String COLLECTION_PREFIX = Constants.COLLECTION_PREFIX + "q3-";
    private static final String STATIONS_MAP_NAME = COLLECTION_PREFIX + "stations";
    private static final String TRIPS_LIST_NAME = COLLECTION_PREFIX + "trips";

    private IMap<Integer, String> stationMap;
    private IList<Trip> tripsList;

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
    protected Consumer<Station> getStationsLambda(Arguments args, HazelcastInstance hz) {
        return station -> stationMap.put(station.getId(), station.getName());
    }

    @Override
    protected Consumer<Trip> getTripsLambda(Arguments args, HazelcastInstance hz) {
        return tripsList::add;
    }

    @Override
    protected Collection<? extends Dto> runClientImpl(Arguments args, HazelcastInstance hz) throws ExecutionException, InterruptedException {
        final JobTracker jt = hz.getJobTracker(JOB_TRACKER_NAME);
        final KeyValueSource<String, Trip> source = KeyValueSource.fromList(tripsList);
        final Job<String, Trip> job = jt.newJob(source);

        final ICompletableFuture<SortedSet<LongestTripDto>> future = job
                .mapper(new LongestTripPerStationMapper())
                .combiner(new LongestTripPerStationCombinerFactory())
                .reducer(new LongestTripPerStationReducerFactory())
                .submit(new LongestTripPerStationSubmitter(stationMap::get));

        return future.get();
    }
}
