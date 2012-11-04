package de.sofd.junit.concurrent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import static org.junit.Assert.*;

/**
 *
 * @author olaf
 */
public class MessageLog {

    private Collection<Message> currMessages = Collections.synchronizedCollection(new ArrayList<Message>());
    private final Clock clock;

    public MessageLog() {
        this(Clock.getDefaultClock());
    }

    public MessageLog(Clock clock) {
        this.clock = clock;
    }

    public void writeMessage(String msg) {
        long time = clock.getCurrentTime();
        currMessages.add(new Message(time, msg));
        System.err.println(time + " ticks: " + msg);
    }

    public void assertMessagesEqual(Message[] messages) {
        assertArrayEquals(messages, currMessages.toArray());
    }

    public void clear() {
        currMessages.clear();
    }

    public Clock getClock() {
        return clock;
    }

    private static MessageLog defaultLog = new MessageLog();

    public static void writeLogMessage(String msg) {
        defaultLog.writeMessage(msg);
    }

    public static void assertLogMessagesEqual(Message... messages) {
        defaultLog.assertMessagesEqual(messages);
    }

    public static void clearMessageLog() {
        defaultLog.clear();
    }

    public static MessageLog getDefaultMessageLog() {
        return defaultLog;
    }

}
