package ar.edu.itba.pod.hazelcast.client;

import ar.edu.itba.pod.hazelcast.client.interfaces.Strategy;
import ar.edu.itba.pod.hazelcast.client.utils.Arguments;
import ar.edu.itba.pod.hazelcast.client.utils.Constants;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;

public abstract class BaseStrategy implements Strategy {

    private final String tripsIMap;
    private final String stationsImap;

    protected BaseStrategy(String tripsIMap, String stationsIMap) {
        this.stationsImap = stationsIMap;
        this.tripsIMap = tripsIMap;
    }

    public <K, V> IMap<K,V> getTripsMap(HazelcastInstance hz) {
        return hz.getMap(tripsIMap);
    }

    public <K, V> IMap<K,V> getStationsIMap(HazelcastInstance hz) {
        return hz.getMap(stationsImap);
    }

    @Override
    public void loadData(Arguments arguments, HazelcastInstance hz) {
        clearIMaps(hz);
        loadStationsFromCsv(arguments.getInPath() + Constants.STATIONS_CSV);
        loadTripsFromCsv(arguments.getInPath() + Constants.TRIPS_CSV);
    }

    protected abstract void loadStationsFromCsv(String filePath);

    protected abstract void loadTripsFromCsv(String filePath);

    protected abstract void clearIMaps(HazelcastInstance hz);
}
