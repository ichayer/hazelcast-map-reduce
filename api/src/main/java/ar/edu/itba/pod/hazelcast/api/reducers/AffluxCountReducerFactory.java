package ar.edu.itba.pod.hazelcast.api.reducers;

import ar.edu.itba.pod.hazelcast.api.models.AffluxCount;
import com.hazelcast.mapreduce.Reducer;
import com.hazelcast.mapreduce.ReducerFactory;

public class AffluxCountReducerFactory implements ReducerFactory<Integer, AffluxCount, AffluxCount> {
    @Override
    public Reducer<AffluxCount, AffluxCount> newReducer(Integer integer) {
        return new AffluxCountReducer();
    }

    private static class AffluxCountReducer extends Reducer<AffluxCount, AffluxCount> {
        private AffluxCount affluxCount;

        @Override
        public void beginReduce() {
            this.affluxCount = new AffluxCount();
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
