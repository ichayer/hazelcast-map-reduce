package ar.edu.itba.pod.hazelcast.api.mappers;

import ar.edu.itba.pod.hazelcast.api.models.StationIdAndDate;
import ar.edu.itba.pod.hazelcast.api.models.TripEnd;
import com.hazelcast.mapreduce.Context;
import com.hazelcast.mapreduce.Mapper;

public class TripEndMapper implements Mapper<String, TripEnd, StationIdAndDate, Integer> {
    @Override
    public void map(String key, TripEnd tripEnd, Context<StationIdAndDate, Integer> context) {
        context.emit(new StationIdAndDate(tripEnd.getStationId(), tripEnd.getDate()), tripEnd.getAfflux());
    }
}
