package ar.edu.itba.pod.hazelcast.client.utils;


import ar.edu.itba.pod.hazelcast.client.exceptions.IOClientFileError;
import ar.edu.itba.pod.hazelcast.client.exceptions.IllegalClientArgumentException;

import java.io.*;
import java.util.Iterator;

public class CsvFileIterator implements Iterator<String[]>, Closeable {

    private final BufferedReader reader;
    private final long columns;
    private String currentLine;

    public CsvFileIterator(String filename) {
        if (filename == null) {
            throw new IllegalArgumentException("The filename cannot be null");
        }
        try {
            reader = new BufferedReader(new FileReader(filename));
            String header = reader.readLine(); // Skip header
            this.columns = header.chars().filter(ch -> ch == ';').count() + 1;
            currentLine = reader.readLine();
        } catch (FileNotFoundException e) {
            throw new IllegalClientArgumentException("The file " + filename + " was not found", e.getCause());
        } catch (IOException e) {
            throw new IOClientFileError(e.getMessage(), e.getCause());
        }
    }

    public long getColumns() {
        return columns;
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
}