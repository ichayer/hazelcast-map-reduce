package ar.edu.itba.pod.hazelcast.client;

import ar.edu.itba.pod.hazelcast.api.mappers.TripsCountMapper;
import ar.edu.itba.pod.hazelcast.api.models.TripsCountDto;
import ar.edu.itba.pod.hazelcast.api.reducers.TripsCountReducerFactory;
import ar.edu.itba.pod.hazelcast.api.submitters.TripsCountSubmitter;
import ar.edu.itba.pod.hazelcast.client.utils.Arguments;
import ar.edu.itba.pod.hazelcast.client.utils.Constants;
import ar.edu.itba.pod.hazelcast.client.utils.CsvFileIterator;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ICompletableFuture;
import com.hazelcast.core.IMap;
import com.hazelcast.mapreduce.Job;
import com.hazelcast.mapreduce.JobTracker;
import com.hazelcast.mapreduce.KeyValueSource;

import java.util.Map;
import java.util.TreeSet;


public class Query1Client {

    public static void main(String[] args) {
        GenericClient g = new GenericClient();

        g.run(args, (Arguments arguments, HazelcastInstance hz) -> {

            CsvFileIterator.ParseStationsCsv(arguments.getInPath(), hz.getMap(Constants.STATIONS_MAP));
            CsvFileIterator.ParseBikesCsv(arguments.getInPath(), hz.getMap(Constants.BIKES_MAP));

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
                result.forEach(System.out::println);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
