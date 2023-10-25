package ar.edu.itba.pod.hazelcast.api.mappers;

import com.hazelcast.mapreduce.Context;
import com.hazelcast.mapreduce.Mapper;

import java.util.Map;

public class TripsCountMapper implements Mapper<Integer, Map.Entry<Integer, Integer>, Map.Entry<Integer, Integer>, Integer> {

    @Override
    public void map(Integer integer, Map.Entry<Integer, Integer> integerIntegerEntry, Context<Map.Entry<Integer, Integer>, Integer> context) {
        context.emit(integerIntegerEntry, 1);
    }
}
