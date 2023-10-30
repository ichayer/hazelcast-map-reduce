package ar.edu.itba.pod.hazelcast.client.interfaces;

import ar.edu.itba.pod.hazelcast.api.models.dto.Dto;
import ar.edu.itba.pod.hazelcast.client.utils.Arguments;
import com.hazelcast.core.HazelcastInstance;

import java.util.Collection;

public interface Strategy {
    void loadData(Arguments args, HazelcastInstance hz);

    Collection<? extends Dto> runClient(Arguments arguments, HazelcastInstance hz);
}
