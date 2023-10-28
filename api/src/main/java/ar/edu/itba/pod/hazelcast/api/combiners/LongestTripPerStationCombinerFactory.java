package ar.edu.itba.pod.hazelcast.api.combiners;

import ar.edu.itba.pod.hazelcast.api.models.Trip;
import com.hazelcast.mapreduce.Combiner;
import com.hazelcast.mapreduce.CombinerFactory;

import java.time.temporal.ChronoUnit;

public class LongestTripPerStationCombinerFactory implements CombinerFactory<Integer, Trip, Trip> {
    @Override
    public Combiner<Trip, Trip> newCombiner(Integer integer) {
        return new LongestTripPerStationCombiner();
    }

    private static class LongestTripPerStationCombiner extends Combiner<Trip, Trip> {
        private Trip current;
        private int currentDuration;

        @Override
        public void combine(Trip trip) {
            int duration = (int) ChronoUnit.MINUTES.between(trip.getStartDateTime(), trip.getEndDateTime());
            if (current == null || (duration > currentDuration) || (duration == currentDuration && trip.getStartDateTime().isAfter(current.getStartDateTime()))) {
                currentDuration = duration;
                current = trip;
            }
        }

        @Override
        public Trip finalizeChunk() {
            return current;
        }

        @Override
        public void reset() {
            current = null;
        }
    }
}
