package ar.edu.itba.pod.hazelcast.api.reducers;

import ar.edu.itba.pod.hazelcast.api.models.StationIdAndDate;
import com.hazelcast.mapreduce.Reducer;
import com.hazelcast.mapreduce.ReducerFactory;

public class BikeAffluxPerStationDayReducerFactory implements ReducerFactory<StationIdAndDate, Integer, Integer> {
    @Override
    public Reducer<Integer, Integer> newReducer(StationIdAndDate stationIdAndDate) {
        return new BikeAffluxPerStationDayReducer();
    }

    private static class BikeAffluxPerStationDayReducer extends Reducer<Integer, Integer> {
        private int sum;

        @Override
        public void beginReduce() {
            sum = 0;
        }

        @Override
        public void reduce(Integer value) {
            sum += value;
        }

        @Override
        public Integer finalizeReduce() {
            return sum;
        }
    }
}
