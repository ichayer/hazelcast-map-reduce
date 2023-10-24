package ar.edu.itba.pod.hazelcast.client.query1.strategies;

import ar.edu.itba.pod.hazelcast.client.interfaces.Strategy;
import ar.edu.itba.pod.hazelcast.client.interfaces.StrategyMapper;
import ar.edu.itba.pod.hazelcast.client.utils.Arguments;
import com.hazelcast.core.HazelcastInstance;

public class Query1OtraEstrategia implements Strategy {
    @Override
    public void loadData(Arguments args, HazelcastInstance hz) {
        System.out.println("loading data ... ");
    }

    @Override
    public void runClient(Arguments arguments, HazelcastInstance hz) {
        System.out.println("running query ...");
    }
}
