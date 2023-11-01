package ar.edu.itba.pod.hazelcast.api.mappers;

import ar.edu.itba.pod.hazelcast.api.models.AffluxCount;
import ar.edu.itba.pod.hazelcast.api.models.Trip;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.HazelcastInstanceAware;
import com.hazelcast.core.MultiMap;
import com.hazelcast.mapreduce.Context;
import com.hazelcast.mapreduce.Mapper;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class TripEndMapper implements Mapper<Integer, String, Integer, AffluxCount>, HazelcastInstanceAware {
    private String tripsByOriginMapName;
    private String tripsByDestinationMapName;

    private transient MultiMap<Integer, Trip> tripsByOrigin;
    private transient MultiMap<Integer, Trip> tripsByDestination;

    public TripEndMapper() {

    }

    public TripEndMapper(String tripsByOriginMapName, String tripsByDestinationMapName) {
        this.tripsByOriginMapName = tripsByOriginMapName;
        this.tripsByDestinationMapName = tripsByDestinationMapName;
    }

    @Override
    public void setHazelcastInstance(HazelcastInstance hazelcastInstance) {
        tripsByOrigin = hazelcastInstance.getMultiMap(tripsByOriginMapName);
        tripsByDestination = hazelcastInstance.getMultiMap(tripsByDestinationMapName);
    }

    @Override
    public void map(Integer stationId, String stationName, Context<Integer, AffluxCount> context) {
        Map<LocalDate, Integer> dateMap = new HashMap<>();

        for (Trip trip : tripsByOrigin.get(stationId))
            dateMap.compute(trip.getStartDateTime().toLocalDate(), (k, v) -> -1 + (v == null ? 0 : v));

        for (Trip trip : tripsByDestination.get(stationId))
            dateMap.compute(trip.getEndDateTime().toLocalDate(), (k, v) -> 1 + (v == null ? 0 : v));

        int positive = 0;
        int negative = 0;
        for (Integer value : dateMap.values()) {
            if (value < 0)
                negative++;
            else if (value > 0)
                positive++;
        }

        context.emit(stationId, new AffluxCount(positive, negative));
    }
}
