package ar.edu.itba.pod.hazelcast.client.utils;


import ar.edu.itba.pod.hazelcast.api.models.Coordinates;
import ar.edu.itba.pod.hazelcast.api.models.Station;
import ar.edu.itba.pod.hazelcast.client.exceptions.IOClientFileError;
import ar.edu.itba.pod.hazelcast.client.exceptions.IllegalClientArgumentException;
import com.hazelcast.core.IMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Iterator;

public class CsvFileIterator implements Iterator<String[]>, Closeable {

    private final BufferedReader reader;
    private String currentLine;

    private static final String BIKES_CSV = "bikes.csv";
    private static final String STATIONS_CSV = "/stations.csv";

    private static Logger logger = LoggerFactory.getLogger(CsvFileIterator.class);

    public CsvFileIterator(String filename) {
        if (filename == null) {
            throw new IllegalArgumentException("The filename cannot be null");
        }

        try {
            reader = new BufferedReader(new FileReader(filename));
            reader.readLine(); // Skip header
            currentLine = reader.readLine();
        } catch (FileNotFoundException e) {
            throw new IllegalClientArgumentException("The file " + filename + " was not found", e.getCause());
        } catch (IOException e) {
            throw new IOClientFileError(e.getMessage(), e.getCause());
        }
    }

    @Override
    public boolean hasNext() {
        return currentLine != null;
    }

    @Override
    public String[] next() {
        if (!hasNext()) {
            throw new IllegalStateException("No more lines to read");
        }

        String[] fields = currentLine.split(";");

        try {
            currentLine = reader.readLine();
        } catch (IOException e) {
            throw new IOClientFileError(e.getMessage(), e.getCause());
        }

        return fields;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("Remove operation is not supported");
    }

    @Override
    public void close() {
        if (reader != null) {
            try {
                reader.close();
            } catch (IOException e) {
                throw new IOClientFileError(e.getMessage(), e.getCause());
            }
        }
    }

    public static void ParseStationsCsv(String inPath, IMap<Integer, Station> stationMap){
        CsvFileIterator fileIterator = new CsvFileIterator(inPath + STATIONS_CSV);
        while (fileIterator.hasNext()) {
            String[] fields = fileIterator.next();
            if (fields.length == 4) {
                int stationPk = Integer.parseInt(fields[0]);
                double latitude = Double.parseDouble(fields[2]);
                double longitude = Double.parseDouble(fields[3]);
                stationMap.put(stationPk, new Station(stationPk, fields[1], new Coordinates(latitude, longitude)));
            }
            else {
                logger.error(String.format("Invalid line format, expected 4 fileds but got %d \n",fields.length));
            }
        }
        fileIterator.close();
    }
}