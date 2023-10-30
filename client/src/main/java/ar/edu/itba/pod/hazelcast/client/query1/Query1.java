package ar.edu.itba.pod.hazelcast.client.query1;

import ar.edu.itba.pod.hazelcast.client.BaseQuery;
import ar.edu.itba.pod.hazelcast.client.QueryBuilder;
import ar.edu.itba.pod.hazelcast.client.query1.strategies.Query1Default;
import ar.edu.itba.pod.hazelcast.client.utils.Constants;
import ar.edu.itba.pod.hazelcast.client.utils.StrategyMapperImpl;

public class Query1 {
    public static void main(String[] args) {
        BaseQuery query = new QueryBuilder.Builder()
                .setArguments(args)
                .setQueryName("1")
                .setResultHeader(Constants.QUERY1_OUTPUT_CSV_HEADER)
                .setStrategy(StrategyMapperImpl.DEFAULT_STRATEGY, Query1Default::new)
                .build();

        query.run();
    }
}
