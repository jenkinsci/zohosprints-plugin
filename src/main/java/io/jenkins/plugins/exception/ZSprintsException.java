package io.jenkins.plugins.exception;

public class ZSprintsException extends RuntimeException {

    public ZSprintsException(String message) {
        super(message);
    }

    public ZSprintsException(String message, Throwable e) {
        super(message, e);
    }

}
