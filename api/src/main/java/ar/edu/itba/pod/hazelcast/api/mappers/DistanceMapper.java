package ar.edu.itba.pod.hazelcast.api.mappers;

import ar.edu.itba.pod.hazelcast.api.models.Bike;
import ar.edu.itba.pod.hazelcast.api.models.Station;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.HazelcastInstanceAware;
import com.hazelcast.mapreduce.Context;
import com.hazelcast.mapreduce.Mapper;

import java.util.Map;

public class DistanceMapper implements Mapper<Integer, Bike, Station, Double>, HazelcastInstanceAware {

    private HazelcastInstance hazelcastInstance;

    @Override
    public void map(Integer integer, Bike bike, Context<Station, Double> context) {
        final Map<Integer, Station> map = hazelcastInstance.getMap("g4-StationsMap");
        final Station origin = map.get(bike.getOrigin());
        final Station destination = map.get(bike.getDestination());
        context.emit(origin, origin.getCoordinates().distanceTo(destination.getCoordinates()));
    }

    @Override
    public void setHazelcastInstance(final HazelcastInstance hazelcastInstance) {
        this.hazelcastInstance = hazelcastInstance;
    }
}
