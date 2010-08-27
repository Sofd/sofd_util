package de.sofd.junit.concurrent;

/**
 *
 * @author olaf
 */
public class Sleeper implements Runnable {
    private final Clock clock;
    private final MessageLog messageLog;
    private final String name;
    private final long sleepTime;

    public Sleeper(String name, int sleepTime) {
        this(Clock.getDefaultClock(), MessageLog.getDefaultMessageLog(), name, sleepTime);
    }

    public Sleeper(Clock clock, MessageLog messageLog, String name, int sleepTime) {
        this.clock = clock;
        this.messageLog = messageLog;
        this.name = name;
        this.sleepTime = sleepTime;
    }

    @Override
    public void run() {
        messageLog.writeMessage(name+" started");
        clock.sleep(sleepTime);
        messageLog.writeMessage(name+" finished");
    }

}
