package ar.edu.itba.pod.hazelcast.client.interfaces;

import ar.edu.itba.pod.hazelcast.client.utils.Arguments;
import com.hazelcast.core.HazelcastInstance;

public interface Strategy {
    void loadData(Arguments args, HazelcastInstance hz);

    void runClient(Arguments arguments, HazelcastInstance hz);
}
