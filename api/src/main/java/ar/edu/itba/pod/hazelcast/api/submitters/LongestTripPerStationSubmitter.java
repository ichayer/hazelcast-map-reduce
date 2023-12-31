package ar.edu.itba.pod.hazelcast.api.submitters;

import ar.edu.itba.pod.hazelcast.api.models.Trip;
import ar.edu.itba.pod.hazelcast.api.models.dto.LongestTripDto;
import com.hazelcast.mapreduce.Collator;

import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.Function;

public class LongestTripPerStationSubmitter implements Collator<Map.Entry<Integer, Trip>, SortedSet<LongestTripDto>> {
    private final Function<Integer, String> stationNameSource;

    public LongestTripPerStationSubmitter(Function<Integer, String> stationNameSource) {
        this.stationNameSource = stationNameSource;
    }

    @Override
    public SortedSet<LongestTripDto> collate(Iterable<Map.Entry<Integer, Trip>> values) {
        SortedSet<LongestTripDto> set = new TreeSet<>();

        for (Map.Entry<Integer, Trip> entry : values) {
            Trip trip = entry.getValue();
            String originStationName = stationNameSource.apply(trip.getOrigin());
            if (originStationName == null)
                continue;
            String destinationStationName = stationNameSource.apply(trip.getDestination());
            if (destinationStationName == null)
                continue;

            set.add(new LongestTripDto(
                    originStationName,
                    destinationStationName,
                    trip.getStartDateTime(),
                    (int) ChronoUnit.MINUTES.between(trip.getStartDateTime(), trip.getEndDateTime())
            ));
        }

        return set;
    }
}
