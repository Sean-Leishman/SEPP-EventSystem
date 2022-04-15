package logging;

import java.util.*;

/**
 * Logger is a singleton that can be retrieved from anywhere to add log entries. This helps to figure out
 * what is happening in a sequence of events and how the application state changes over time.
 * It keeps a list of LogEntrys and a static instance of a Logger.
 */
public class Logger extends Object {
    /**
     * The Logger() constructor is private, and an instance can only be retrieved via this method.
     * It can either use lazy-loading (so an instance is only created when it is requested for the first time)
     * or eager-loading (an instance is created statically). Either way, subsequent calls to getInstance() always return
     * the same instance
     *
     * @return Logger instance
     */
    private ArrayList<LogEntry> log = new ArrayList<LogEntry>();
    private static Logger instance = null;

    public static Logger getInstance() {
        if (instance == null){
            instance = new Logger();
        }
        return instance;
    }

    /**
     * Create a LogEntry without any additionalInfo (using Collections.emptyMap() as a substitute)
     * and add it to the log
     *
     * @param callerName name of the calling method to log
     * @param result result of the action to log
     */
    public void logAction(String callerName, Object result) {
        LogEntry logEntry = new LogEntry(callerName, result, Collections.emptyMap());
        log.add(logEntry);
    }

    /**
     * Create a LogEntry and add it to the log
     *
     * @param callerName name of the calling method to log
     * @param result result of the action to log
     * @param additionalInfo any additional information about the action to log
     */
    public void logAction(String callerName, Object result, Map<String, Object> additionalInfo) {
        LogEntry logEntry = new LogEntry(callerName, result, additionalInfo);
        log.add(logEntry);
    }


    public List<LogEntry> getLog() {
        return log;
    }


    public void clearLog() {log = new ArrayList<LogEntry>();}

}
