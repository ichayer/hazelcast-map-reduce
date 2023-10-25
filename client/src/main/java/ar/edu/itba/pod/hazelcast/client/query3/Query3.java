package ar.edu.itba.pod.hazelcast.client.query3;

import ar.edu.itba.pod.hazelcast.client.GenericQuery;
import ar.edu.itba.pod.hazelcast.client.query3.strategies.Query3Default;
import ar.edu.itba.pod.hazelcast.client.utils.Constants;
import ar.edu.itba.pod.hazelcast.client.utils.StrategyMapperImpl;

import java.util.Map;

public class Query3 extends GenericQuery {
    public static void main(String[] args) {
        GenericQuery client = new Query3();
        client.run(args, Constants.QUERY3_OUTPUT_TXT, new StrategyMapperImpl(Map.of(
                StrategyMapperImpl.DEFAULT_STRATEGY, Query3Default::new
        )));
    }
}
