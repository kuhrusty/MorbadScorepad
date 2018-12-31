package com.kuhrusty.morbadscorepad;

/**
 * Thrown when an unfinished bit of code is hit.  Of course you should never
 * see this!
 */
public class NotDoneException extends RuntimeException {
    public NotDoneException() {
        super("NOT_DONE");
    }
    public NotDoneException(String message) {
        super("NOT_DONE " + message);
    }
    public NotDoneException(String message, Throwable throwable) {
        super("NOT_DONE " + message, throwable);
    }
}
