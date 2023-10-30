package ar.edu.itba.pod.hazelcast.api.mappers;

import ar.edu.itba.pod.hazelcast.api.models.Trip;
import ar.edu.itba.pod.hazelcast.api.models.Station;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.HazelcastInstanceAware;
import com.hazelcast.core.IMap;
import com.hazelcast.mapreduce.Context;
import com.hazelcast.mapreduce.Mapper;

public class DistanceMapper implements Mapper<String, Trip, Station, Double>, HazelcastInstanceAware {
    private String mapName;
    private HazelcastInstance hazelcastInstance;

    public DistanceMapper(String mapName) {
        this.mapName = mapName;
    }

    @Override
    public void map(String key, Trip trip, Context<Station, Double> context) {
        final IMap<Integer, Station> map = hazelcastInstance.getMap(mapName);
        final Station origin = map.get(trip.getOrigin());
        final Station destination = map.get(trip.getDestination());
        context.emit(origin, origin.getCoordinates().distanceTo(destination.getCoordinates()));
    }

    @Override
    public void setHazelcastInstance(HazelcastInstance hazelcastInstance) {
        this.hazelcastInstance = hazelcastInstance;
    }
}
