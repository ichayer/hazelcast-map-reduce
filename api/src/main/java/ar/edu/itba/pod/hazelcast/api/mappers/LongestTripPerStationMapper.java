package ar.edu.itba.pod.hazelcast.api.mappers;

import ar.edu.itba.pod.hazelcast.api.models.Trip;
import com.hazelcast.mapreduce.Context;
import com.hazelcast.mapreduce.Mapper;

/**
 * A mapper that maps each trip to a (originStationId, trip) tuple, discarding circular trips.
 */
public class LongestTripPerStationMapper implements Mapper<String, Trip, Integer, Trip> {
    @Override
    public void map(String key, Trip trip, Context<Integer, Trip> context) {
        // Ignore trips that start and end in the same station
        if (trip.getOrigin() == trip.getDestination())
            return;

        context.emit(trip.getOrigin(), trip);
    }
}
