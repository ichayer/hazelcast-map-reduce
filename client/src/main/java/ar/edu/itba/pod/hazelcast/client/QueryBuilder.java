package ar.edu.itba.pod.hazelcast.client;

import ar.edu.itba.pod.hazelcast.client.interfaces.Strategy;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class QueryBuilder extends BaseQuery {

    private QueryBuilder(Builder builder) {
        super(builder.arguments, builder.outputFileName, builder.strategies);
    }

    public static class Builder {

        private String[] arguments = null;
        private String outputFileName = null;
        private final Map<String, Supplier<Strategy>> strategies = new HashMap<>();

        public Builder setArguments(String[] arguments) {
            this.arguments = arguments;
            return this;
        }

        public Builder setOutputFileName(String outputFileName) {
            this.outputFileName = outputFileName;
            return this;
        }

        public Builder setStrategy(String strategyName, Supplier<Strategy> strategy) {
            strategies.put(strategyName, strategy);
            return this;
        }

        public BaseQuery build() {
            return new QueryBuilder(this);
        }
    }
}
