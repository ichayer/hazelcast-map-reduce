package ar.edu.itba.pod.hazelcast.client.utils;

import ar.edu.itba.pod.hazelcast.api.models.Dto;
import ar.edu.itba.pod.hazelcast.client.exceptions.IOClientFileError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Collection;
import java.util.function.BiConsumer;

public class CsvHelper {

    private static final Logger logger = LoggerFactory.getLogger(CsvHelper.class);

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

    public static void ReadData(String file, BiConsumer<String[], Integer> consumer){
        CsvFileIterator fileIterator = new CsvFileIterator(file);
        int id = 0;
        while (fileIterator.hasNext()) {
            String[] fields = fileIterator.next();
            if (fields.length == fileIterator.getColumns()) {
                ++id;
                consumer.accept(fields, id);
            }
            else {
                logger.error(String.format("Invalid line format, expected %d fileds but got %d \n", fileIterator.getColumns(), fields.length));
            }
        }
        fileIterator.close();
    }
}
