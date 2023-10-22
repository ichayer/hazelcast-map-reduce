package ar.edu.itba.pod.hazelcast.client.exceptions;

public class IOClientFileError extends ClientException {
    public IOClientFileError(String message) {
        super(message);
    }

    public IOClientFileError(String message, Throwable cause) {
        super(message, cause);
    }
}