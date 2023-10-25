package ar.edu.itba.pod.hazelcast.client.query2;

import ar.edu.itba.pod.hazelcast.client.GenericQuery;
import ar.edu.itba.pod.hazelcast.client.utils.Constants;
import ar.edu.itba.pod.hazelcast.client.utils.StrategyMapperImpl;
import ar.edu.itba.pod.hazelcast.client.query2.strategies.Query2Default;

import java.util.Map;

public class Query2 extends GenericQuery{
    public static void main(String[] args) {
        GenericQuery client = new Query2();
        client.run(args, Constants.QUERY2_OUTPUT_TXT, new StrategyMapperImpl(Map.of(
                StrategyMapperImpl.DEFAULT_STRATEGY, Query2Default::new
        )));
    }
}
