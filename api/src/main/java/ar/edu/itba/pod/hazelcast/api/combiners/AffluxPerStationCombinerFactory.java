package ar.edu.itba.pod.hazelcast.api.combiners;

import ar.edu.itba.pod.hazelcast.api.models.AffluxCount;
import com.hazelcast.mapreduce.Combiner;
import com.hazelcast.mapreduce.CombinerFactory;

public class AffluxPerStationCombinerFactory implements CombinerFactory<Integer, AffluxCount, AffluxCount> {
    @Override
    public Combiner<AffluxCount, AffluxCount> newCombiner(Integer integer) {
        return new AffluxPerStationCombiner();
    }

    private static class AffluxPerStationCombiner extends Combiner<AffluxCount, AffluxCount> {
        private AffluxCount affluxCount = new AffluxCount();

        @Override
        public void combine(AffluxCount affluxCount) {
            this.affluxCount.add(affluxCount);
        }

        @Override
        public AffluxCount finalizeChunk() {
            return affluxCount;
        }

        @Override
        public void reset() {
            affluxCount = new AffluxCount();
        }
    }
}
