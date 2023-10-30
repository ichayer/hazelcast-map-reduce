package ar.edu.itba.pod.hazelcast.client.utils;

import ar.edu.itba.pod.hazelcast.client.exceptions.IllegalClientArgumentException;

import java.util.Map;
import java.util.function.BiConsumer;

public class Parser {

    private Parser() {
        throw new AssertionError();
    }

    private static final Map<String, BiConsumer<String, Arguments.Builder>> OPTIONS = Map.ofEntries(
            Map.entry("-Daddresses", (argValue, argBuilder) -> argBuilder.addresses(argValue.substring(1, argValue.length() - 1).split(";"))),
            Map.entry("-DinPath", (argValue, argBuilder) -> argBuilder.inPath(argValue)),
            Map.entry("-DoutPath", (argValue, argBuilder) -> argBuilder.outPath(argValue)),
            Map.entry("-DstartDate", (argValue, argBuilder) -> argBuilder.startDate(argValue)),
            Map.entry("-DendDate", (argValue, argBuilder) -> argBuilder.endDate(argValue)),
            Map.entry("-Dn", (argValue, argBuilder) -> argBuilder.limit(Integer.parseInt(argValue))),
            Map.entry("-Dstrategy", (argValue, argBuilder) -> argBuilder.strategy(argValue))
    );

    private static void invalidArgument(String arg, Arguments.Builder argBuilder) {
        throw new IllegalArgumentException("The argument " + arg + " is not valid");
    }

    public static Arguments parse(String[] args) {
        Arguments.Builder arguments = new Arguments.Builder();
        for (String arg : args) {
            String[] parts = arg.split("=");
            if (parts.length != 2) {
                throw new IllegalClientArgumentException("Arguments must have the format -Dargument=value");
            }
            try {
                OPTIONS.getOrDefault(parts[0], Parser::invalidArgument).accept(parts[1], arguments);
            } catch (Exception e) {
                throw new IllegalClientArgumentException(e.getMessage());
            }
        }
        return arguments.build();
    }
}
