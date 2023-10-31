package ar.edu.itba.pod.hazelcast.api.reducers;

import ar.edu.itba.pod.hazelcast.api.models.StationAndDistance;
import com.hazelcast.mapreduce.Reducer;
import com.hazelcast.mapreduce.ReducerFactory;

import java.util.ArrayList;
import java.util.NavigableSet;
import java.util.TreeSet;

public class StationsSortedByDistanceReducerFactory implements ReducerFactory<String, ArrayList<StationAndDistance>, ArrayList<StationAndDistance>> {
    private final int limit;

    public StationsSortedByDistanceReducerFactory(int limit) {
        this.limit = limit;
    }

    @Override
    public Reducer<ArrayList<StationAndDistance>, ArrayList<StationAndDistance>> newReducer(String key) {
        return new StationsSortedByDistanceReducer(limit);
    }

    private static class StationsSortedByDistanceReducer extends Reducer<ArrayList<StationAndDistance>, ArrayList<StationAndDistance>> {
        private final int limit;
        private NavigableSet<StationAndDistance> set = new TreeSet<>();

        public StationsSortedByDistanceReducer(int limit) {
            this.limit = limit;
        }

        @Override
        public void reduce(ArrayList<StationAndDistance> values) {
            for (StationAndDistance stationAndDistance : values) {
                set.add(stationAndDistance);
                if (set.size() > limit)
                    set.pollLast();
            }
        }

        @Override
        public ArrayList<StationAndDistance> finalizeReduce() {
            return new ArrayList<>(set);
        }
    }
}
