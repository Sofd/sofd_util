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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((message == null) ? 0 : message.hashCode());
        result = prime * result + (int) (time ^ (time >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Message other = (Message) obj;
        if (message == null) {
            if (other.message != null)
                return false;
        } else if (!message.equals(other.message))
            return false;
        if (time != other.time)
            return false;
        return true;
    }

}
