package ar.edu.itba.pod.hazelcast.api.prdicates;

import ar.edu.itba.pod.hazelcast.api.models.Trip;
import com.hazelcast.mapreduce.KeyPredicate;

public class MembersOnlyAndDifferentOriginPredicate implements KeyPredicate<Trip> {

    @Override
    public boolean evaluate(Trip trip) {

        return trip.isMember() && trip.getOrigin() != trip.getDestination();
    }

}
