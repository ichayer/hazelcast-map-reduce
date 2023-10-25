package ar.edu.itba.pod.hazelcast.api.mappers;

import ar.edu.itba.pod.hazelcast.api.models.Bike;
import com.hazelcast.mapreduce.Context;
import com.hazelcast.mapreduce.Mapper;

/**
 * A mapper that maps each ride to a (originStationId, ride) tuple, discarding circular rides.
 */
public class LongestRidePerStationMapper implements Mapper<String, Bike, Integer, Bike> {
    @Override
    public void map(String key, Bike bike, Context<Integer, Bike> context) {
        // Ignore rides that start and end in the same station
        if (bike.getOrigin() == bike.getDestination())
            return;

        context.emit(bike.getOrigin(), bike);
    }
}
