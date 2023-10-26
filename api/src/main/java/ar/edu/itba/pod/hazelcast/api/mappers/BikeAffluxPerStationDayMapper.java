package ar.edu.itba.pod.hazelcast.api.mappers;

import ar.edu.itba.pod.hazelcast.api.models.StationIdAndDate;
import ar.edu.itba.pod.hazelcast.api.models.Trip;
import com.hazelcast.mapreduce.Context;
import com.hazelcast.mapreduce.Mapper;

import java.time.LocalDate;

/**
 * A mapper that maps each trip to two (StationIdAndDate, int netBikesReceived) tuples, one for the start of the trip and
 * another for the destination.
 */
public class BikeAffluxPerStationDayMapper implements Mapper<String, Trip, StationIdAndDate, Integer> {
    private LocalDate startDate;
    private LocalDate endDate;

    public BikeAffluxPerStationDayMapper() {

    }

    public BikeAffluxPerStationDayMapper(LocalDate startDate, LocalDate endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }

    @Override
    public void map(String key, Trip trip, Context<StationIdAndDate, Integer> context) {
        LocalDate tripStartDate = trip.getStartDateTime().toLocalDate();
        LocalDate tripEndDate = trip.getEndDateTime().toLocalDate();

        // if (tripStartDate >= startDate && tripStartDate <= endDate)
        if (!tripStartDate.isBefore(startDate) && !tripStartDate.isAfter(endDate))
            context.emit(new StationIdAndDate(trip.getOrigin(), trip.getStartDateTime().toLocalDate()), -1);

        // if (tripEndDate >= startDate && tripEndDate <= endDate)
        if (!tripEndDate.isBefore(startDate) && !tripEndDate.isAfter(endDate))
            context.emit(new StationIdAndDate(trip.getDestination(), trip.getEndDateTime().toLocalDate()), 1);
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
}
