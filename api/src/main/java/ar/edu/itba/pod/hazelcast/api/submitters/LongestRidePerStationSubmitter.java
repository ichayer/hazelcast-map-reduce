package ar.edu.itba.pod.hazelcast.api.submitters;

import ar.edu.itba.pod.hazelcast.api.models.Bike;
import ar.edu.itba.pod.hazelcast.api.models.dto.LongestTripDto;
import com.hazelcast.mapreduce.Collator;

import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.Function;

public class LongestRidePerStationSubmitter implements Collator<Map.Entry<Integer, Bike>, SortedSet<LongestTripDto>> {
    private final Function<Integer, String> stationNameSource;

    public LongestRidePerStationSubmitter(Function<Integer, String> stationNameSource) {
        this.stationNameSource = stationNameSource;
    }

    @Override
    public SortedSet<LongestTripDto> collate(Iterable<Map.Entry<Integer, Bike>> values) {
        SortedSet<LongestTripDto> set = new TreeSet<>();

        values.forEach(entry -> {
            Bike bike = entry.getValue();
            set.add(new LongestTripDto(
                    stationNameSource.apply(bike.getOrigin()),
                    stationNameSource.apply(bike.getDestination()),
                    bike.getStartDateTime(),
                    (int) ChronoUnit.MINUTES.between(bike.getStartDateTime(), bike.getEndDateTime())
            ));
        });

        return set;
    }
}
