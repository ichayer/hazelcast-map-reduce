package ar.edu.itba.pod.hazelcast.api.reducers;

import ar.edu.itba.pod.hazelcast.api.models.Bike;
import com.hazelcast.mapreduce.Reducer;
import com.hazelcast.mapreduce.ReducerFactory;

import java.time.temporal.ChronoUnit;

public class LongestRidePerStationReducerFactory implements ReducerFactory<Integer, Bike, Bike> {

    @Override
    public Reducer<Bike, Bike> newReducer(Integer stationId) {
        return new LongestRidePerStationReducer();
    }

    private static class LongestRidePerStationReducer extends Reducer<Bike, Bike> {
        private Bike current;
        private int currentDuration;

        @Override
        public void beginReduce() {
            current = null;
        }

        @Override
        public void reduce(Bike bike) {
            int duration = (int) ChronoUnit.MINUTES.between(bike.getStartDateTime(), bike.getEndDateTime());
            if (current == null || (duration > currentDuration) || (duration == currentDuration && bike.getStartDateTime().isAfter(current.getStartDateTime()))) {
                currentDuration = duration;
                current = bike;
            }
        }

        @Override
        public Bike finalizeReduce() {
            return current;
        }
    }
}
