package ar.edu.itba.pod.hazelcast.client.exceptions;

public class QueryException extends RuntimeException {
    public QueryException(String message) {
        super(message);
    }

    public QueryException(String message, Throwable cause) {
        super(message, cause);
    }

    public QueryException(Throwable cause) {super(cause);}
}
