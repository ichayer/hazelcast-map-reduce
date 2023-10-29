package ar.edu.itba.pod.hazelcast.client.query2;

import ar.edu.itba.pod.hazelcast.client.BaseQuery;
import ar.edu.itba.pod.hazelcast.client.QueryBuilder;
import ar.edu.itba.pod.hazelcast.client.query2.strategies.Query2Default;
import ar.edu.itba.pod.hazelcast.client.utils.Constants;
import ar.edu.itba.pod.hazelcast.client.utils.StrategyMapperImpl;

public class Query2 {
    public static void main(String[] args) {

        BaseQuery query = new QueryBuilder.Builder()
                .setArguments(args)
                .setOutputFileName(Constants.QUERY2_OUTPUT_TXT)
                .setStrategy(StrategyMapperImpl.DEFAULT_STRATEGY, Query2Default::new)
                .build();

        query.run();
    }
}
