package de.sofd.junit.concurrent;

/**
 *
 * @author olaf
 */
public class Message {

    private final long time;
    private final String message;

    public Message(long time, String message) {
        this.time = time;
        this.message = message;
    }

    public long getTime() {
        return time;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "" + time + " ticks: " + message;
    }

}
