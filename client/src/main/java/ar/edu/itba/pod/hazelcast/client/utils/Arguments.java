package ar.edu.itba.pod.hazelcast.client.utils;

import ar.edu.itba.pod.hazelcast.client.exceptions.IllegalClientArgumentException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

public class Arguments {
    private final String[] addresses;
    private final String inPath;
    private final String outPath;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final Integer limit;
    private final String strategy;

    private Arguments(Builder builder) {
        this.addresses = builder.addresses;
        this.inPath = builder.inPath;

        String outPath1 = builder.outPath;
        if (outPath1.endsWith("/"))
            outPath1 = outPath1.substring(0, outPath1.length() - 1);
        this.outPath = outPath1;

        this.startDate = builder.startDate;
        this.endDate = builder.endDate;
        this.limit = builder.limit;

        if (builder.strategy == null || builder.strategy.isEmpty()) {
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

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
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
        private LocalDate startDate;
        private LocalDate endDate;
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

        private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        private static LocalDate parseDate(String date) {
            return LocalDate.parse(date, dateTimeFormatter);
        }

        public Builder startDate(String startDate) {
            this.startDate = parseDate(startDate);
            return this;
        }

        public Builder endDate(String endDate) {
            this.endDate =  parseDate(endDate);
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
}