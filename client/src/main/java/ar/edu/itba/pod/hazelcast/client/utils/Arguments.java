package ar.edu.itba.pod.hazelcast.client.utils;

import ar.edu.itba.pod.hazelcast.client.exceptions.IllegalClientArgumentException;
import ar.edu.itba.pod.hazelcast.client.interfaces.StrategyMapper;

import java.util.Arrays;
import java.util.List;

public class Arguments {
    private final String[] addresses;
    private final String inPath;
    private final String outPath;
    private final Integer limit;
    private final String strategy;


    private Arguments(Builder builder) {
        this.addresses = builder.addresses;
        this.inPath = builder.inPath;
        this.outPath = builder.outPath;
        this.limit = builder.limit;

        if(builder.strategy == null || builder.strategy.isEmpty()){
            this.strategy = StrategyMapperImpl.DEFAULT_STRATEGY;
        } else {
            this.strategy = builder.strategy;
        }

        if (addresses == null || addresses.length == 0 || inPath == null || outPath == null) {
            throw new IllegalClientArgumentException("The parameters -Daddresses, -DinPath and -DoutPath must be provided");
        }
    }

    public String[] getAddresses() {
        return addresses;
    }

    public String getInPath() {
        return inPath;
    }

    public String getOutPath() {
        return outPath;
    }

    public Integer getLimit() {
        return limit;
    }

    @Override
    public String toString() {
        return "Arguments{" +
                "addresses=" + Arrays.toString(addresses) +
                ", inPath='" + inPath + '\'' +
                ", outPath='" + outPath + '\'' +
                ", limit=" + limit +
                '}';
    }

    public String getStrategy() {
        return strategy;
    }

    public static class Builder {
        private String[] addresses;
        private String inPath;
        private String outPath;
        private Integer limit;

        private String strategy;

        public Builder addresses(String[] addresses) {
            this.addresses = addresses;
            return this;
        }

        public Builder strategy(String strategy) {
            this.strategy = strategy;
            return this;
        }

        public Builder inPath(String inPath) {
            this.inPath = inPath;
            return this;
        }

        public Builder outPath(String outPath) {
            this.outPath = outPath;
            return this;
        }

        public Builder limit(Integer limit) {
            this.limit = limit;
            return this;
        }

        public Arguments build() {
            return new Arguments(this);
        }
    }

    public static Builder newBuilder() {
        return new Builder();
    }
}