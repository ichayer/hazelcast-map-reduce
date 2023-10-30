package ar.edu.itba.pod.hazelcast.api.submitters;

import ar.edu.itba.pod.hazelcast.api.models.Station;
import ar.edu.itba.pod.hazelcast.api.models.dto.AverageDistanceDto;
import com.hazelcast.mapreduce.Collator;

import java.util.*;
import java.util.function.Function;

public class AverageDistanceSubmitter implements Collator<Map.Entry<Station, Double>, SortedSet<AverageDistanceDto>> {
    private int limit;
    private final Function<Integer, String> stationNameSource;

    public AverageDistanceSubmitter(int limit, Function<Integer, String> stationNameSource) {
        this.limit = limit;
        this.stationNameSource = stationNameSource;
    }

    @Override
    public SortedSet<AverageDistanceDto> collate(Iterable<Map.Entry<Station, Double>> values) {
        NavigableSet<AverageDistanceDto> set = new TreeSet<>();

        for (Map.Entry<Station, Double> value : values) {
            String stationName = stationNameSource.apply(value.getKey().getId());
            double averageDistance = value.getValue();
            if (stationName == null || averageDistance <= 0)
                continue;

            set.add(new AverageDistanceDto(stationName, value.getValue()));
            if (set.size() > limit)
                set.pollLast();
        }

        return set;
    }
}
