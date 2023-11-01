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
    private MultiMap<Integer, Trip> tripsMap;

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

        Map<Trip, Integer> elementCounts = tripsStartingFromThatStation.stream()
                .collect(Collectors.groupingBy(e -> e, Collectors.summingInt(n->1)));

        for (Map.Entry<Trip, Integer> entry : elementCounts.entrySet()) {
            Integer destinationId = entry.getKey().getDestination();
            Integer count = entry.getValue();
            System.out.println("kk " + destinationId + " e " + count);
            context.emit(new AbstractMap.SimpleEntry<>(stationId, destinationId), count);
        }
    }
}