package ar.edu.itba.pod.hazelcast.api.submitters;

import ar.edu.itba.pod.hazelcast.api.models.Station;
import ar.edu.itba.pod.hazelcast.api.models.dto.AverageDistanceDto;
import com.hazelcast.mapreduce.Collator;
import java.util.Map;
import java.util.Objects;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;

public class AverageDistanceSubmitter implements Collator<Map.Entry<Station, Double>, SortedSet<AverageDistanceDto>> {

    private int limit;
    private final Function<Integer, Station> function;

    public AverageDistanceSubmitter(int limit, Function<Integer, Station> function) {
        this.limit = limit;
        this.function = function;
    }

    @Override
    public SortedSet<AverageDistanceDto> collate(Iterable<Map.Entry<Station, Double>> values) {
        SortedSet<AverageDistanceDto> treeSet = new TreeSet<>();

        for(Map.Entry<Station, Double> value : values) {
            Station station = function.apply(value.getKey().getId());
            double averageDistance = value.getValue();
            if (Objects.isNull(station))
                continue;

//            if(!(Double.compare(averageDistance, 0) > 0)) {
//                continue;
//            }

            treeSet.add(new AverageDistanceDto(station, value.getValue()));
        }

        if (limit > treeSet.size()) {
            limit = treeSet.size() - 1;
        }

        return treeSet.stream().limit(limit).collect(Collectors.toCollection(TreeSet::new));
    }
}
