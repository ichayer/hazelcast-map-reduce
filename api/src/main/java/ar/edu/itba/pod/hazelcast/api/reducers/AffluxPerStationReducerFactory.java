package ar.edu.itba.pod.hazelcast.api.reducers;

import ar.edu.itba.pod.hazelcast.api.models.AffluxCount;
import com.hazelcast.mapreduce.Reducer;
import com.hazelcast.mapreduce.ReducerFactory;

public class AffluxPerStationReducerFactory implements ReducerFactory<Integer, AffluxCount, AffluxCount> {
    @Override
    public Reducer<AffluxCount, AffluxCount> newReducer(Integer integer) {
        return new AffluxPerStationReducer();
    }

    private static class AffluxPerStationReducer extends Reducer<AffluxCount, AffluxCount> {
        private AffluxCount affluxCount;

        @Override
        public void beginReduce() {
            affluxCount = new AffluxCount();
        }

        @Override
        public void reduce(AffluxCount affluxCount) {
            this.affluxCount.add(affluxCount);
        }

        @Override
        public AffluxCount finalizeReduce() {
            return affluxCount;
        }
    }
}
