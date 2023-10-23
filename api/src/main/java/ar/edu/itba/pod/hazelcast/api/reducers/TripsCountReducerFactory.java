package ar.edu.itba.pod.hazelcast.api.reducers;

import com.hazelcast.mapreduce.Reducer;
import com.hazelcast.mapreduce.ReducerFactory;

import java.util.Map;

public class TripsCountReducerFactory implements ReducerFactory<Map.Entry<Integer, Integer>, Integer, Integer> {
    @Override
    public Reducer<Integer, Integer> newReducer(Map.Entry<Integer, Integer> entry) {
        return new TripsCountReducer();
    }

    private static class TripsCountReducer extends Reducer<Integer, Integer> {
        private int count = 0;

        @Override
        public void beginReduce() {
            count = 0;
        }

        @Override
        public void reduce(Integer integer) {
            count += integer;
        }

        @Override
        public Integer finalizeReduce() {
            return count;
        }
    }
}
