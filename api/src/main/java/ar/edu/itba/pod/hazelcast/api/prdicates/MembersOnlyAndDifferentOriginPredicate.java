package ar.edu.itba.pod.hazelcast.api.prdicates;

import ar.edu.itba.pod.hazelcast.api.models.Bike;
import com.hazelcast.mapreduce.KeyPredicate;

public class MembersOnlyAndDifferentOriginPredicate implements KeyPredicate<Bike> {

    @Override
    public boolean evaluate(Bike bike) {

        return bike.isMember() && bike.getOrigin() != bike.getDestination();
    }

}
