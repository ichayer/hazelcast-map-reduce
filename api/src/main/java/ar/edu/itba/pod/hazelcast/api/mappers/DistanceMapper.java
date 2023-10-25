package ar.edu.itba.pod.hazelcast.api.mappers;

import ar.edu.itba.pod.hazelcast.api.models.Trip;
import ar.edu.itba.pod.hazelcast.api.models.Station;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.HazelcastInstanceAware;
import com.hazelcast.mapreduce.Context;
import com.hazelcast.mapreduce.Mapper;

import java.util.Map;

public class DistanceMapper implements Mapper<Integer, Trip, Station, Double>, HazelcastInstanceAware {

    private HazelcastInstance hazelcastInstance;

    @Override
    public void map(Integer integer, Trip trip, Context<Station, Double> context) {
        final Map<Integer, Station> map = hazelcastInstance.getMap("g4-StationsMap");
        final Station origin = map.get(trip.getOrigin());
        final Station destination = map.get(trip.getDestination());
        context.emit(origin, origin.getCoordinates().distanceTo(destination.getCoordinates()));
    }

    @Override
    public void setHazelcastInstance(final HazelcastInstance hazelcastInstance) {
        this.hazelcastInstance = hazelcastInstance;
    }
}
