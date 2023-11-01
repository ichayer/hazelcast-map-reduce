package ar.edu.itba.pod.hazelcast.api.mappers;

import ar.edu.itba.pod.hazelcast.api.models.Station;
import ar.edu.itba.pod.hazelcast.api.models.StationAndDistance;
import ar.edu.itba.pod.hazelcast.api.models.Trip;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.HazelcastInstanceAware;
import com.hazelcast.core.IMap;
import com.hazelcast.core.MultiMap;
import com.hazelcast.mapreduce.Context;
import com.hazelcast.mapreduce.Mapper;

import java.util.ArrayList;
import java.util.Collection;

public class StationToDistanceMapper implements Mapper<Integer, Station, String, ArrayList<StationAndDistance>>, HazelcastInstanceAware {
    private String tripsMultiMapName;
    private String stationsMapName;
    private transient MultiMap<Integer, Trip> tripsMap;
    private transient IMap<Integer, Station> stationsMap;

    public StationToDistanceMapper(String stationsMapName, String tripsMultiMapName) {
        this.stationsMapName = stationsMapName;
        this.tripsMultiMapName = tripsMultiMapName;
    }

    @Override
    public void setHazelcastInstance(HazelcastInstance hazelcastInstance) {
        tripsMap = hazelcastInstance.getMultiMap(tripsMultiMapName);
        stationsMap = hazelcastInstance.getMap(stationsMapName);
    }

    @Override
    public void map(Integer stationId, Station station, Context<String, ArrayList<StationAndDistance>> context) {
        Collection<Trip> tripsStartingFromThatStation = tripsMap.get(stationId);

        double totalDistance = 0;
        int totalTrips = 0;

        for (Trip trip : tripsStartingFromThatStation) {
            Station destination = stationsMap.get(trip.getDestination());
            if (destination == null)
                continue;

            totalDistance += station.getCoordinates().distanceTo(destination.getCoordinates());
            totalTrips++;
        }

        if (totalTrips != 0) {
            ArrayList<StationAndDistance> arr = new ArrayList<>(1);
            arr.add(new StationAndDistance(station, totalDistance / totalTrips));
            context.emit("result", arr);
        }
    }
}
