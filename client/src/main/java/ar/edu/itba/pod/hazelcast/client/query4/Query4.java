package ar.edu.itba.pod.hazelcast.client.query4;

import ar.edu.itba.pod.hazelcast.client.BaseQuery;
import ar.edu.itba.pod.hazelcast.client.QueryBuilder;
import ar.edu.itba.pod.hazelcast.client.query4.strategies.Query4Default;
import ar.edu.itba.pod.hazelcast.client.utils.Constants;
import ar.edu.itba.pod.hazelcast.client.utils.StrategyMapperImpl;

public class Query4 {
    public static void main(String[] args) {

        BaseQuery query = new QueryBuilder.Builder()
                .setArguments(args)
                .setOutputFileName(Constants.QUERY4_OUTPUT_TXT)
                .setStrategy(StrategyMapperImpl.DEFAULT_STRATEGY, Query4Default::new)
                .build();

        query.run();
    }
}
