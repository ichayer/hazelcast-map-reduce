package ar.edu.itba.pod.hazelcast.api.reducers;

import ar.edu.itba.pod.hazelcast.api.models.Station;
import com.hazelcast.mapreduce.Reducer;
import com.hazelcast.mapreduce.ReducerFactory;

import java.util.Map;

public class AverageDistanceReducer implements ReducerFactory<Station, Map.Entry<Double, Integer>, Double> {

    @Override
    public Reducer<Map.Entry<Double, Integer>, Double> newReducer(Station integer) {
        return new AvgDistanceReducer();
    }

    private static class AvgDistanceReducer extends Reducer<Map.Entry<Double, Integer>, Double> {
        private double distance = 0;
        private int count = 0;

        @Override
        public void beginReduce() {
            distance = count = 0;
        }

        @Override
        public void reduce(Map.Entry<Double, Integer> entry) {
            count += entry.getValue();
            distance += entry.getKey();
        }

        @Override
        public Double finalizeReduce() {
            return (count > 0)? distance / count : count;
        }
    }
}
