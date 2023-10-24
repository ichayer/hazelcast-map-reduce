package ar.edu.itba.pod.hazelcast.client.query1;

import ar.edu.itba.pod.hazelcast.client.GenericQuery;
import ar.edu.itba.pod.hazelcast.client.query1.strategies.Query1Default;
import ar.edu.itba.pod.hazelcast.client.utils.StrategyMapperImpl;

import java.util.Map;


public class Query1 extends GenericQuery {
    private static final String OUTPUT_TXT_NAME = "/time1.txt";
    public static void main(String[] args) {
        GenericQuery client = new Query1();
        client.run(args, OUTPUT_TXT_NAME, new StrategyMapperImpl(Map.of(
                StrategyMapperImpl.DEFAULT_STRATEGY, Query1Default::new
        )));
    }
}
