package ar.edu.itba.pod.hazelcast.api.submitters;

import ar.edu.itba.pod.hazelcast.api.models.AffluxCount;
import ar.edu.itba.pod.hazelcast.api.models.dto.AffluxCountDto;
import com.hazelcast.mapreduce.Collator;

import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.Function;

public class AffluxPerStationSubmitter implements Collator<Map.Entry<Integer, AffluxCount>, SortedSet<AffluxCountDto>> {
    private final Function<Integer, String> stationNameSource;
    private final int totalDays;

    public AffluxPerStationSubmitter(Function<Integer, String> stationNameSource, int totalDays) {
        this.stationNameSource = stationNameSource;
        this.totalDays = totalDays;
    }

    @Override
    public SortedSet<AffluxCountDto> collate(Iterable<Map.Entry<Integer, AffluxCount>> values) {
        SortedSet<AffluxCountDto> set = new TreeSet<>();

        for (Map.Entry<Integer, AffluxCount> entry : values) {
            String stationName = stationNameSource.apply(entry.getKey());
            if (stationName == null)
                continue;

            AffluxCount a = entry.getValue();
            set.add(new AffluxCountDto(stationName, a.getPositive(), totalDays - a.getPositive() - a.getNegative(), a.getNegative()));
        }

        return set;
    }
}
