package ar.edu.itba.pod.hazelcast.api.submitters;

import ar.edu.itba.pod.hazelcast.api.models.Station;
import ar.edu.itba.pod.hazelcast.api.models.dto.TripsCountDto;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.HazelcastInstanceAware;
import com.hazelcast.mapreduce.Collator;

import java.util.Map;
import java.util.Objects;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.Function;

public class TripsCountSubmitter implements Collator<Map.Entry<Map.Entry<Integer, Integer>, Integer>, SortedSet<TripsCountDto>> {

    private final Function<Integer, Station> function;

    public TripsCountSubmitter(Function<Integer, Station> function) {
        this.function = function;
    }

    @Override
    public SortedSet<TripsCountDto> collate(Iterable<Map.Entry<Map.Entry<Integer, Integer>, Integer>> values) {
        SortedSet<TripsCountDto> treeSet = new TreeSet<>();

        for(Map.Entry<Map.Entry<Integer, Integer>, Integer> value: values) {
            Station startingStation = function.apply(value.getKey().getKey());
            Station finishingStation = function.apply(value.getKey().getValue());
            int counter = value.getValue();

            if(Objects.isNull(startingStation) || Objects.isNull(finishingStation))
                continue;

            if(startingStation.equals(finishingStation))
                continue;

            if(counter <= 0)
                continue;

            treeSet.add(new TripsCountDto(startingStation.getName(),finishingStation.getName(), counter));
        };

        return treeSet;
    }
}
