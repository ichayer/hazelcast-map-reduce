package ar.edu.itba.pod.hazelcast.api.combiners;

import ar.edu.itba.pod.hazelcast.api.models.StationAndDistance;
import com.hazelcast.mapreduce.CombinerFactory;
import com.hazelcast.mapreduce.Combiner;

import java.util.ArrayList;
import java.util.NavigableSet;
import java.util.TreeSet;

public class StationsSortedByDistanceCombinerFactory implements CombinerFactory<String, ArrayList<StationAndDistance>, ArrayList<StationAndDistance>> {
    private final int limit;

    public StationsSortedByDistanceCombinerFactory(int limit) {
        this.limit = limit;
    }

    @Override
    public Combiner<ArrayList<StationAndDistance>, ArrayList<StationAndDistance>> newCombiner(String key) {
        return new StationsSortedByDistanceCombiner(limit);
    }

    private static class StationsSortedByDistanceCombiner extends Combiner<ArrayList<StationAndDistance>, ArrayList<StationAndDistance>> {
        private final int limit;
        private NavigableSet<StationAndDistance> set = new TreeSet<>();

        public StationsSortedByDistanceCombiner(int limit) {
            this.limit = limit;
        }

        @Override
        public void reset() {
            set.clear();
        }

        @Override
        public void combine(ArrayList<StationAndDistance> values) {
            for (StationAndDistance stationAndDistance : values) {
                set.add(stationAndDistance);
                if (set.size() > limit)
                    set.pollLast();
            }
        }

        @Override
        public ArrayList<StationAndDistance> finalizeChunk() {
            return new ArrayList<>(set);
        }
    }
}
