package ar.edu.itba.pod.hazelcast.api.mappers;

import ar.edu.itba.pod.hazelcast.api.models.AffluxCount;
import ar.edu.itba.pod.hazelcast.api.models.StationIdAndDate;
import com.hazelcast.mapreduce.Context;
import com.hazelcast.mapreduce.Mapper;

/**
 * Maps each (StationIdAndDate, bikeInflux) tuple to a (stationId, AffluxCount) tuple.
 */
public class AffluxPerStationMapper implements Mapper<StationIdAndDate, Integer, Integer, AffluxCount> {
    @Override
    public void map(StationIdAndDate stationIdAndDate, Integer bikeInflux, Context<Integer, AffluxCount> context) {
        AffluxCount afflux = new AffluxCount(bikeInflux > 0 ? 1 : 0, bikeInflux < 0 ? 1 : 0);
        context.emit(stationIdAndDate.getStationId(), afflux);
    }
}
