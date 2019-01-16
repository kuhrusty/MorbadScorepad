package com.kuhrusty.morbadscorepad.model;

/**
 * Thrown when deserializing some objects; indicates that some problem in the
 * data itself prevented that from succeeding.
 *
 * <p>In DeckState, this can happen if we save the deck state to file, then a
 * new version of the deck is installed with different cards/IDs, and then we
 * try to load our state from the old file.</p>
 */
public class BadDataException extends RuntimeException {
    public BadDataException() {
    }
    public BadDataException(String msg) {
        super(msg);
    }
    public BadDataException(String msg, Throwable src) {
        super(msg, src);
    }
}
