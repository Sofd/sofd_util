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

    public void writeLogMessage(String msg) {
        long time = clock.getCurrentClockTime();
        currMessages.add(new Message(time, msg));
        System.err.println(time + " ticks: " + msg);
    }

    public void assertLogMessagesEqual(Message[] messages) {
        assertEquals(messages, currMessages);
    }

    public void clearMessageLog() {
        currMessages.clear();
    }

    public Clock getClock() {
        return clock;
    }

    private static MessageLog defaultLog = new MessageLog();

    public static void writeMessage(String msg) {
        defaultLog.writeLogMessage(msg);
    }

    public static void assertMessagesEqual(Message[] messages) {
        defaultLog.assertLogMessagesEqual(messages);
    }

    public static void clearLog() {
        defaultLog.clearMessageLog();
    }

    public static MessageLog getDefaultLog() {
        return defaultLog;
    }

}
