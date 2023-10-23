package ar.edu.itba.pod.hazelcast.api.combiners;

import com.hazelcast.mapreduce.Combiner;
import com.hazelcast.mapreduce.CombinerFactory;

import java.util.Map;

public class TripsCountCombinerFactory implements CombinerFactory<Map.Entry<Integer, Integer>, Integer, Integer> {

    @Override
    public Combiner<Integer, Integer> newCombiner(Map.Entry<Integer, Integer> entry) {
        return new TripsCountReducer();
    }

    private static class TripsCountReducer extends Combiner<Integer, Integer> {
        private int count = 0;

        @Override
        public void combine(Integer value) {
            count += value;
        }

        @Override
        public void reset() {
            count = 0;
        }

        @Override
        public Integer finalizeChunk() {
            return count;
        }
    }
}
