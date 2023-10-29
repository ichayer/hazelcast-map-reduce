package ar.edu.itba.pod.hazelcast.client.query1.strategies;

import ar.edu.itba.pod.hazelcast.api.mappers.TripsCountMapper;
import ar.edu.itba.pod.hazelcast.api.models.Coordinates;
import ar.edu.itba.pod.hazelcast.api.models.Station;
import ar.edu.itba.pod.hazelcast.api.models.dto.TripsCountDto;
import ar.edu.itba.pod.hazelcast.api.reducers.TripsCountReducerFactory;
import ar.edu.itba.pod.hazelcast.api.submitters.TripsCountSubmitter;
import ar.edu.itba.pod.hazelcast.client.BaseStrategy;
import ar.edu.itba.pod.hazelcast.client.interfaces.Strategy;
import ar.edu.itba.pod.hazelcast.client.utils.Arguments;
import ar.edu.itba.pod.hazelcast.client.utils.Constants;
import ar.edu.itba.pod.hazelcast.client.utils.CsvHelper;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ICompletableFuture;
import com.hazelcast.core.IMap;
import com.hazelcast.mapreduce.Job;
import com.hazelcast.mapreduce.JobTracker;
import com.hazelcast.mapreduce.KeyValueSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.AbstractMap;
import java.util.Map;
import java.util.SortedSet;

public class Query1Default extends BaseStrategy {

    private static final Logger logger = LoggerFactory.getLogger(Query1Default.class);
    private static final String JOB_TRACKER_NAME = "travel-count";
    private IMap<Integer, Station> stationIMap;
    private IMap<Integer, Map.Entry<Integer, Integer>> tripsIMap;

    public Query1Default() {
        super(Constants.STATIONS_MAP, Constants.TRIPS_MAP);
    }

    @Override
    protected void clearIMaps(HazelcastInstance hz) {
        this.stationIMap = getStationsIMap(hz);
        stationIMap.clear();
        this.tripsIMap = getTripsMap(hz);
        tripsIMap.clear();
    }

    @Override
    protected void loadStationsFromCsv(String filePath) {
        CsvHelper.readData(filePath, (fields, id) -> {
            int stationPk = Integer.parseInt(fields[0]);
            double latitude = Double.parseDouble(fields[2]);
            double longitude = Double.parseDouble(fields[3]);
            this.stationIMap.put(stationPk, new Station(stationPk, fields[1], new Coordinates(latitude, longitude)));
        });
    }

    @Override
    protected void loadTripsFromCsv(String filePath) {
        CsvHelper.readData(filePath, (fields, id) -> {
            int startStation = Integer.parseInt(fields[1]);
            int endStation = Integer.parseInt(fields[3]);
            this.tripsIMap.put(id, new AbstractMap.SimpleEntry<>(startStation, endStation));
        });
    }

    @Override
    public void runClient(Arguments arguments, HazelcastInstance hz) {
        final JobTracker jt = hz.getJobTracker(JOB_TRACKER_NAME);
        final KeyValueSource<Integer, Map.Entry<Integer, Integer>> source = KeyValueSource.fromMap(this.tripsIMap);
        final Job<Integer, Map.Entry<Integer, Integer>> job = jt.newJob(source);

        final ICompletableFuture<SortedSet<TripsCountDto>> future = job
                .mapper(new TripsCountMapper())
                .reducer(new TripsCountReducerFactory())
                .submit(new TripsCountSubmitter(this.stationIMap::get));

        try {
            final SortedSet<TripsCountDto> result = future.get();
            CsvHelper.printData(arguments.getOutPath() + Constants.QUERY1_OUTPUT_CSV, Constants.QUERY1_OUTPUT_CSV_HEADER, result);
        } catch (Exception e) {
            logger.error("Error waiting for the computation to complete and retrieve its result in query 1", e);
        } finally {
            this.stationIMap.clear();
            this.tripsIMap.clear();
        }
    }
}
