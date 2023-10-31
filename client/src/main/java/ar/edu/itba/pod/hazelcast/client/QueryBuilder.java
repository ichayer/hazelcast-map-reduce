package ar.edu.itba.pod.hazelcast.client;

import ar.edu.itba.pod.hazelcast.client.interfaces.Strategy;
import ar.edu.itba.pod.hazelcast.client.utils.Parser;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class QueryBuilder {

    private QueryBuilder() {
        throw new AssertionError();
    }

    public static class Builder {
        private String[] arguments = null;
        private String queryName = null;
        private String resultHeader;
        private final Map<String, Supplier<Strategy>> strategies = new HashMap<>();

        public Builder setArguments(String[] arguments) {
            this.arguments = arguments;
            return this;
        }

        public Builder setQueryName(String queryName) {
            this.queryName = queryName;
            return this;
        }

        public Builder setResultHeader(String resultHeader) {
            this.resultHeader = resultHeader;
            return this;
        }

        public Builder setStrategy(String strategyName, Supplier<Strategy> strategy) {
            strategies.put(strategyName, strategy);
            return this;
        }

        public BaseQuery build() {
            return new BaseQuery(Parser.parse(this.arguments), queryName, resultHeader, strategies);
        }
    }
}
