package ar.edu.itba.pod.hazelcast.client.query4;

import ar.edu.itba.pod.hazelcast.client.GenericQuery;
import ar.edu.itba.pod.hazelcast.client.query4.strategies.Query4Default;
import ar.edu.itba.pod.hazelcast.client.utils.Constants;
import ar.edu.itba.pod.hazelcast.client.utils.StrategyMapperImpl;

import java.util.Map;

public class Query4 extends GenericQuery {
    public static void main(String[] args) {
        GenericQuery client = new Query4();
        client.run(args, Constants.QUERY4_OUTPUT_TXT, new StrategyMapperImpl(Map.of(
                StrategyMapperImpl.DEFAULT_STRATEGY, Query4Default::new
        )));
    }
}
