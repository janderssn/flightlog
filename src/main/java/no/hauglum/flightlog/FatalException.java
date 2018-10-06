package no.hauglum.flightlog;

public class FatalException extends RuntimeException {
    public FatalException(String message, Throwable e) {
        super(message, e);
    }
}
