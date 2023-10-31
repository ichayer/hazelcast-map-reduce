package ar.edu.itba.pod.hazelcast.client.utils;

import ar.edu.itba.pod.hazelcast.api.models.dto.Dto;
import ar.edu.itba.pod.hazelcast.client.exceptions.IOClientFileError;

import java.io.*;
import java.util.Collection;
import java.util.function.Consumer;

public class CsvHelper {

    public static <T extends Dto> void printData(String filename, String header, Collection<T> collection) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            writer.write(header);
            writer.newLine();
            collection.forEach((t) -> {
                try {
                    writer.write(t.toCsv());
                    writer.newLine();
                } catch (IOException e) {
                    throw new IOClientFileError("Error while writing output data to file " + filename, e);
                }
            });
        } catch (IOException e) {
            throw new IOClientFileError("Error while writing output data to file " + filename, e);
        }
    }

    public static void readDataParallel(String filename, Consumer<String> lineConsumer) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            reader.lines().skip(1).parallel().forEach(lineConsumer);
        } catch (IOException e) {
            throw new IOClientFileError(e.getMessage(), e.getCause());
        }
    }
}
