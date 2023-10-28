package ar.edu.itba.pod.hazelcast.api.combiners;

import ar.edu.itba.pod.hazelcast.api.models.StationIdAndDate;
import com.hazelcast.mapreduce.Combiner;
import com.hazelcast.mapreduce.CombinerFactory;

public class BikeAffluxPerStationDayCombinerFactory implements CombinerFactory<StationIdAndDate, Integer, Integer> {
    @Override
    public Combiner<Integer, Integer> newCombiner(StationIdAndDate stationIdAndDate) {
        return new BikeAffluxPerStationDayCombiner();
    }

    private static class BikeAffluxPerStationDayCombiner extends Combiner<Integer, Integer> {
        private int sum = 0;

        @Override
        public void combine(Integer value) {
            sum += value;
        }

        @Override
        public Integer finalizeChunk() {
            return sum;
        }

        @Override
        public void reset() {
            sum = 0;
        }
    }
}
