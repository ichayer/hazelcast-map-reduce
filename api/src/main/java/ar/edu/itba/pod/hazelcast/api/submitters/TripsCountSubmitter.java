package ar.edu.itba.pod.hazelcast.api.submitters;

import ar.edu.itba.pod.hazelcast.api.models.dto.TripsCountDto;
import com.hazelcast.mapreduce.Collator;

import java.util.Map;
import java.util.Objects;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.Function;

public class TripsCountSubmitter implements Collator<Map.Entry<Map.Entry<Integer, Integer>, Integer>, SortedSet<TripsCountDto>> {

    private final Function<Integer, String> stationNameSource;

    public TripsCountSubmitter(Function<Integer, String> stationNameSource) {
        this.stationNameSource = stationNameSource;
    }

    @Override
    public SortedSet<TripsCountDto> collate(Iterable<Map.Entry<Map.Entry<Integer, Integer>, Integer>> values) {
        SortedSet<TripsCountDto> treeSet = new TreeSet<>();

        for (Map.Entry<Map.Entry<Integer, Integer>, Integer> value : values) {
            if (Objects.equals(value.getKey().getKey(), value.getKey().getValue()))
                continue;

            String startingStationName = stationNameSource.apply(value.getKey().getKey());
            if (startingStationName == null)
                continue;

            String finishingStationName = stationNameSource.apply(value.getKey().getValue());
            if (finishingStationName == null)
                continue;

            int counter = value.getValue();
            if (counter <= 0)
                continue;

            treeSet.add(new TripsCountDto(startingStationName, finishingStationName, counter));
        }

        return treeSet;
    }
}
