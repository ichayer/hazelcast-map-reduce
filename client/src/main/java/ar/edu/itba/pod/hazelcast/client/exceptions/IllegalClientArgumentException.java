package ar.edu.itba.pod.hazelcast.client.exceptions;

public class IllegalClientArgumentException extends ClientException{
    public IllegalClientArgumentException(String message) {
        super(message);
    }

    public IllegalClientArgumentException(String message, Throwable cause) {
        super(message, cause);
    }
}
