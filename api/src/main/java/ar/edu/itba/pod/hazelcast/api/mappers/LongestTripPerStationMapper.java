package ar.edu.itba.pod.hazelcast.api.mappers;

import ar.edu.itba.pod.hazelcast.api.models.Trip;
import com.hazelcast.mapreduce.Context;
import com.hazelcast.mapreduce.Mapper;

/**
 * A mapper that maps each trip to a (originStationId, trip) tuple.
 */
public class LongestTripPerStationMapper implements Mapper<String, Trip, Integer, Trip> {
    @Override
    public void map(String key, Trip trip, Context<Integer, Trip> context) {
        context.emit(trip.getOrigin(), trip);
    }
}
