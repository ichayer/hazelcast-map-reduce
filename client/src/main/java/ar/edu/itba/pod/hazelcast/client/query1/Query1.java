package ar.edu.itba.pod.hazelcast.client.query1;

import ar.edu.itba.pod.hazelcast.api.mappers.TripsCountMapper;
import ar.edu.itba.pod.hazelcast.api.models.Coordinates;
import ar.edu.itba.pod.hazelcast.api.models.Station;
import ar.edu.itba.pod.hazelcast.api.models.TripsCountDto;
import ar.edu.itba.pod.hazelcast.api.reducers.TripsCountReducerFactory;
import ar.edu.itba.pod.hazelcast.api.submitters.TripsCountSubmitter;
import ar.edu.itba.pod.hazelcast.client.GenericQuery;
import ar.edu.itba.pod.hazelcast.client.query1.strategies.Query1Default;
import ar.edu.itba.pod.hazelcast.client.query1.strategies.Query1OtraEstrategia;
import ar.edu.itba.pod.hazelcast.client.utils.Arguments;
import ar.edu.itba.pod.hazelcast.client.utils.Constants;
import ar.edu.itba.pod.hazelcast.client.utils.CsvHelper;
import ar.edu.itba.pod.hazelcast.client.utils.StrategyMapperImpl;
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
import java.util.TreeSet;


public class Query1 extends GenericQuery {
    private static final String OUTPUT_TXT_NAME = "/time1.txt";
    public static void main(String[] args) {
        GenericQuery client = new Query1();
        client.run(args, OUTPUT_TXT_NAME, new StrategyMapperImpl(Map.of(
                StrategyMapperImpl.DEFAULT_STRATEGY, Query1Default::new,
                "Nombre_de_Estrategia", Query1OtraEstrategia::new
        )));
    }
}
