package no.hauglum.flightlog.exception;

public class FatalException extends RuntimeException {
    public FatalException(String message, Throwable e) {
        super(message, e);
    }

    public FatalException(String s) {
        super(s);
    }
}
