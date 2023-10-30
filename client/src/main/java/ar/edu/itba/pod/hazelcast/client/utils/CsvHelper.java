package ar.edu.itba.pod.hazelcast.client.utils;

import ar.edu.itba.pod.hazelcast.api.models.dto.Dto;
import ar.edu.itba.pod.hazelcast.client.exceptions.IOClientFileError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.function.Consumer;

public class CsvHelper {

    private static final Logger logger = LoggerFactory.getLogger(CsvHelper.class);

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

    public static void readData(String file, Consumer<String[]> consumer) {
        CsvFileIterator fileIterator = new CsvFileIterator(file);
        while (fileIterator.hasNext()) {
            String[] fields = fileIterator.next();
            if (fields.length == fileIterator.getColumns()) {
                consumer.accept(fields);
            } else {
                logger.error("Invalid line format, expected {} fileds but got {}", fileIterator.getColumns(), fields.length);
            }
        }
        fileIterator.close();
    }
}
