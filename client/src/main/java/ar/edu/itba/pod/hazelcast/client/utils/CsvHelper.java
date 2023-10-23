package ar.edu.itba.pod.hazelcast.client.utils;

import ar.edu.itba.pod.hazelcast.api.models.Dto;
import ar.edu.itba.pod.hazelcast.client.exceptions.IOClientFileError;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;

public class CsvHelper {
    public static <T extends Dto> void printData(String filename, String header, Collection<T> collection){
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            writer.write(header);
            writer.newLine();
            collection.forEach((t) -> {
                try {
                    writer.write(t.toCsv());
                    writer.newLine();
                } catch (IOException e) {
                    throw new IOClientFileError("Error while writing output data to file " + filename,e);
                }
            });
        } catch (IOException e) {
            throw new IOClientFileError("Error while writing output data to file " + filename,e);
        }
    }
}
