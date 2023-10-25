package ar.edu.itba.pod.hazelcast.client.utils;

import ar.edu.itba.pod.hazelcast.client.exceptions.IllegalClientArgumentException;
import ar.edu.itba.pod.hazelcast.client.interfaces.Strategy;
import ar.edu.itba.pod.hazelcast.client.interfaces.StrategyMapper;

import java.util.Map;
import java.util.function.Supplier;

public class StrategyMapperImpl implements StrategyMapper {

    public static final String DEFAULT_STRATEGY = "DEFAULT";
    private final Map<String, Supplier<Strategy>> strategyMapper;

    public StrategyMapperImpl(Map<String, Supplier<Strategy>> strategyMapper) {
        this.strategyMapper = strategyMapper;
    }

    private static Strategy InvalidAction() {
        throw new IllegalClientArgumentException("The provided strategy is not valid");
    }

    @Override
    public Strategy getStrategy(String strategy) {
        return strategyMapper.getOrDefault(strategy, StrategyMapperImpl::InvalidAction).get();
    }
}