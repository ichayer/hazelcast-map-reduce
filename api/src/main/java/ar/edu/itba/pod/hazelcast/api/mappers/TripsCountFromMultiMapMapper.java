package ar.edu.itba.pod.hazelcast.api.mappers;

import ar.edu.itba.pod.hazelcast.api.models.Trip;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.HazelcastInstanceAware;
import com.hazelcast.core.MultiMap;
import com.hazelcast.mapreduce.Context;
import com.hazelcast.mapreduce.Mapper;

import java.util.AbstractMap;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class TripsCountFromMultiMapMapper implements Mapper<Integer, String, Map.Entry<Integer, Integer>, Integer>, HazelcastInstanceAware {
    private final String tripsMultiMapName;
    private transient MultiMap<Integer, Trip> tripsMap;

    public TripsCountFromMultiMapMapper(String tripsMultiMapName) {
        this.tripsMultiMapName = tripsMultiMapName;
    }

    @Override
    public void setHazelcastInstance(HazelcastInstance hazelcastInstance) {
        tripsMap = hazelcastInstance.getMultiMap(tripsMultiMapName);
    }

    @Override
    public void map(Integer stationId, String stationName, Context<Map.Entry<Integer, Integer>, Integer> context) {
        Collection<Trip> tripsStartingFromThatStation = tripsMap.get(stationId);

        Map<Integer, Integer> elementCounts = tripsStartingFromThatStation.stream()
                .collect(Collectors.groupingBy(Trip::getDestination, Collectors.summingInt(n->1)));

        for (Map.Entry<Integer, Integer> entry : elementCounts.entrySet()) {
            Integer destinationId = entry.getKey();
            Integer count = entry.getValue();
            context.emit(new AbstractMap.SimpleEntry<>(stationId, destinationId), count);
        }
    }
}