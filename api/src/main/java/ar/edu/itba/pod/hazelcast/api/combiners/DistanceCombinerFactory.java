package ar.edu.itba.pod.hazelcast.api.combiners;

import ar.edu.itba.pod.hazelcast.api.models.Station;
import com.hazelcast.mapreduce.Combiner;
import com.hazelcast.mapreduce.CombinerFactory;

import java.util.AbstractMap;
import java.util.Map;

public class DistanceCombinerFactory implements CombinerFactory<Station, Double, Map.Entry<Double, Integer>> {

    @Override
    public Combiner<Double, Map.Entry<Double, Integer>> newCombiner(Station station) {
        return new DistanceReducer();
    }

    private static class DistanceReducer extends Combiner<Double, Map.Entry<Double, Integer>> {

        int count = 0;
        double distanceSum = 0;

        @Override
        public void combine(Double aDouble) {
            distanceSum += aDouble;
            count++;
        }

        @Override
        public void reset() {
            count = 0;
            distanceSum = 0;
        }

        @Override
        public Map.Entry<Double, Integer> finalizeChunk() {
            return new AbstractMap.SimpleEntry<>(distanceSum, count);
        }
    }
}
