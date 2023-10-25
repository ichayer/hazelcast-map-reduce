package ar.edu.itba.pod.hazelcast.api.reducers;

import ar.edu.itba.pod.hazelcast.api.models.Trip;
import com.hazelcast.mapreduce.Reducer;
import com.hazelcast.mapreduce.ReducerFactory;

import java.time.temporal.ChronoUnit;

public class LongestTripPerStationReducerFactory implements ReducerFactory<Integer, Trip, Trip> {

    @Override
    public Reducer<Trip, Trip> newReducer(Integer stationId) {
        return new LongestTripPerStationReducer();
    }

    private static class LongestTripPerStationReducer extends Reducer<Trip, Trip> {
        private Trip current;
        private int currentDuration;

        @Override
        public void beginReduce() {
            current = null;
        }

        @Override
        public void reduce(Trip trip) {
            int duration = (int) ChronoUnit.MINUTES.between(trip.getStartDateTime(), trip.getEndDateTime());
            if (current == null || (duration > currentDuration) || (duration == currentDuration && trip.getStartDateTime().isAfter(current.getStartDateTime()))) {
                currentDuration = duration;
                current = trip;
            }
        }

        @Override
        public Trip finalizeReduce() {
            return current;
        }
    }
}
