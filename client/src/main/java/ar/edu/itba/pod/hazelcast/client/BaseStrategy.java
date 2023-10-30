package ar.edu.itba.pod.hazelcast.client;

import ar.edu.itba.pod.hazelcast.api.models.Coordinates;
import ar.edu.itba.pod.hazelcast.api.models.Station;
import ar.edu.itba.pod.hazelcast.api.models.Trip;
import ar.edu.itba.pod.hazelcast.api.models.dto.Dto;
import ar.edu.itba.pod.hazelcast.client.exceptions.QueryException;
import ar.edu.itba.pod.hazelcast.client.interfaces.Strategy;
import ar.edu.itba.pod.hazelcast.client.utils.Arguments;
import ar.edu.itba.pod.hazelcast.client.utils.Constants;
import ar.edu.itba.pod.hazelcast.client.utils.CsvHelper;
import com.hazelcast.core.HazelcastInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

public abstract class BaseStrategy implements Strategy {
    private static final Logger logger = LoggerFactory.getLogger(BaseStrategy.class);

    @Override
    public void loadData(Arguments args, HazelcastInstance hz) {
        initialize(args, hz);
        clearCollections();

        Consumer<Station> stationConsumer = getStationsLambda(args, hz);
        if (stationConsumer == null) {
            logger.info("Skipped loading stations, as not required by query strategy");
        } else {
            logger.info("Loading stations");
            CsvHelper.readData(args.getInPath() + Constants.STATIONS_CSV, fields -> {
                int stationPk = Integer.parseInt(fields[0]);
                double latitude = Double.parseDouble(fields[2]);
                double longitude = Double.parseDouble(fields[3]);
                stationConsumer.accept(new Station(stationPk, fields[1], new Coordinates(latitude, longitude)));
            });
        }

        Consumer<Trip> tripConsumer = getTripsLambda(args, hz);
        if (tripConsumer == null) {
            logger.info("Skipped loading trips, as not required by query strategy");
        } else {
            logger.info("Loading trips");
            CsvHelper.readData(args.getInPath() + Constants.TRIPS_CSV, fields -> {
                LocalDateTime startDate = LocalDateTime.parse(fields[0].replace(' ', 'T'));
                int startStation = Integer.parseInt(fields[1]);
                LocalDateTime endDate = LocalDateTime.parse(fields[2].replace(' ', 'T'));
                int endStation = Integer.parseInt(fields[3]);
                boolean isMember = Integer.parseInt(fields[4]) != 0;
                tripConsumer.accept(new Trip(startDate, endDate, startStation, endStation, isMember));
            });
        }
    }

    protected abstract void initialize(Arguments args, HazelcastInstance hz);

    protected abstract void clearCollections();

    protected abstract Consumer<Station> getStationsLambda(Arguments args, HazelcastInstance hz);

    protected abstract Consumer<Trip> getTripsLambda(Arguments args, HazelcastInstance hz);

    protected abstract Collection<? extends Dto> runClientImpl(Arguments args, HazelcastInstance hz) throws ExecutionException, InterruptedException;

    public final Collection<? extends Dto> runClient(Arguments args, HazelcastInstance hz) {
        try {
            return runClientImpl(args, hz);
        } catch (Exception e) {
            throw new QueryException(e);
        } finally {
            clearCollections();
        }
    }
}
