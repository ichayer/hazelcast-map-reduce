package ar.edu.itba.pod.hazelcast.client;

import ar.edu.itba.pod.hazelcast.api.mappers.TripsCountMapper;
import ar.edu.itba.pod.hazelcast.api.models.TripsCountDto;
import ar.edu.itba.pod.hazelcast.api.reducers.TripsCountReducerFactory;
import ar.edu.itba.pod.hazelcast.api.submitters.TripsCountSubmitter;
import ar.edu.itba.pod.hazelcast.client.utils.Arguments;
import ar.edu.itba.pod.hazelcast.client.utils.Constants;
import ar.edu.itba.pod.hazelcast.client.utils.CsvFileIterator;
import ar.edu.itba.pod.hazelcast.client.utils.CsvHelper;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ICompletableFuture;
import com.hazelcast.core.IMap;
import com.hazelcast.mapreduce.Job;
import com.hazelcast.mapreduce.JobTracker;
import com.hazelcast.mapreduce.KeyValueSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.TreeSet;


public class Query1 extends GenericClient {

    private static final String QUERY_RESULT = "/query1.csv";
    private static final String OUTPUT_TXT_NAME = "/time1.txt";
    private static final String OUTPUT_CSV_HEADER = "station_a;station_b;trips_between_a_b";
    private static final Logger logger = LoggerFactory.getLogger(Query1.class);

    public static void main(String[] args) {
        GenericClient client = new Query1();
        client.run(args, OUTPUT_TXT_NAME);
    }

    @Override
    public void loadData(Arguments args, HazelcastInstance hz) {
        CsvFileIterator.ParseStationsCsv(args.getInPath(), hz.getMap(Constants.STATIONS_MAP));
        CsvFileIterator.ParseBikesCsv(args.getInPath(), hz.getMap(Constants.BIKES_MAP));
    }

    @Override
    public void runClient(Arguments arguments, HazelcastInstance hz) {
        final JobTracker jt = hz.getJobTracker("travel-count");
        final IMap<Integer, Map.Entry<Integer, Integer>> list = hz.getMap(Constants.BIKES_MAP);
        final KeyValueSource<Integer, Map.Entry<Integer, Integer>> source = KeyValueSource.fromMap(list);
        final Job<Integer, Map.Entry<Integer, Integer>> job = jt.newJob(source);

        TripsCountSubmitter t = new TripsCountSubmitter();
        t.setHazelcastInstance(hz);

        final ICompletableFuture<TreeSet<TripsCountDto>> future = job
                .mapper(new TripsCountMapper())
                .reducer(new TripsCountReducerFactory())
                .submit(t);

        try {
            final TreeSet<TripsCountDto> result = future.get();
            CsvHelper.printData(arguments.getOutPath() + QUERY_RESULT, OUTPUT_CSV_HEADER, result);
        } catch (Exception e) {
            logger.error("Error waiting for the computation to complete and retrieve its result in query 1");
        }
    }
}
