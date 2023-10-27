package ar.edu.itba.pod.hazelcast.api.submitters;

import ar.edu.itba.pod.hazelcast.api.models.Station;
import ar.edu.itba.pod.hazelcast.api.models.dto.AverageDistanceDto;
import com.hazelcast.mapreduce.Collator;
import java.util.Map;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class AverageDistanceSubmitter implements Collator<Map.Entry<Station, Double>, TreeSet<AverageDistanceDto>> {

    private int limit;

    public AverageDistanceSubmitter(int limit) {
        this.limit = limit;
    }

    @Override
    public TreeSet<AverageDistanceDto> collate(Iterable<Map.Entry<Station, Double>> values) {
        TreeSet<AverageDistanceDto> treeSet = new TreeSet<>();

        values.forEach((stationEntry -> {
            treeSet.add(new AverageDistanceDto(stationEntry.getKey(), stationEntry.getValue()));
        }));

        if (limit > treeSet.size()) {
            limit = treeSet.size() - 1;
        }

        return treeSet.stream().limit(limit).collect(Collectors.toCollection(TreeSet::new));
    }
}
