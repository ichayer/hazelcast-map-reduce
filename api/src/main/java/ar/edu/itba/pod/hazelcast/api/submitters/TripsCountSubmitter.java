package ar.edu.itba.pod.hazelcast.api.submitters;

import ar.edu.itba.pod.hazelcast.api.models.Station;
import ar.edu.itba.pod.hazelcast.api.models.TripsCountDto;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.HazelcastInstanceAware;
import com.hazelcast.mapreduce.Collator;

import java.io.Serializable;
import java.util.Map;
import java.util.TreeSet;

public class TripsCountSubmitter implements Collator<Map.Entry<Map.Entry<Integer,Integer>, Integer>, TreeSet<TripsCountDto>>, HazelcastInstanceAware{

    private HazelcastInstance hazelcastInstance;

    @Override
    public TreeSet<TripsCountDto> collate(Iterable<Map.Entry<Map.Entry<Integer, Integer>, Integer>> values) {
        TreeSet<TripsCountDto> t = new TreeSet<>();
        Map<Integer, Station> map = hazelcastInstance.getMap("g4-StationsMap");
        values.forEach((v) -> t.add(new TripsCountDto(map.get(v.getKey().getKey()).getName(), map.get(v.getKey().getValue()).getName(), v.getValue())));
        return t;
    }

    @Override
    public void setHazelcastInstance(HazelcastInstance hazelcastInstance) {
        this.hazelcastInstance = hazelcastInstance;
    }
}
