package ar.edu.itba.pod.hazelcast.client.query3;

import ar.edu.itba.pod.hazelcast.client.BaseQuery;
import ar.edu.itba.pod.hazelcast.client.QueryBuilder;
import ar.edu.itba.pod.hazelcast.client.query3.strategies.Query3Default;
import ar.edu.itba.pod.hazelcast.client.utils.Constants;
import ar.edu.itba.pod.hazelcast.client.utils.StrategyMapperImpl;

public class Query3 {
    public static void main(String[] args) {

        BaseQuery query = new QueryBuilder.Builder()
                .setArguments(args)
                .setOutputFileName(Constants.QUERY3_OUTPUT_TXT)
                .setStrategy(StrategyMapperImpl.DEFAULT_STRATEGY, Query3Default::new)
                .build();

        query.run();
    }
}
