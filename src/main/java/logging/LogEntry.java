package logging;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * LogEntry is a single entry in the log kept by the singleton Logger.
 */
public class LogEntry extends Object {

    String callerName;
    Object result;
    Map<String, String> additionalInfo;

    /**
     * @param callerName human-readable name (usually in the format ClassName.MethodName) where the LogEntry
     * is logged from
     * @param result a single string representing the result of the operation that is being logged
     * @param additionalInfo a map containing any additional information that may help to explain the result,
     * the keys should be variable names and the values should be their values.
     * For convenience, the values are allowed to be any Objects that get converted to
     * Strings in this constructor using:
     *
     * additionalInfo
     * .entrySet()
     * .stream()
     * .collect(Collectors.toMap(
     * Map.Entry::getKey,
     * entry -> String.valueOf(entry.getValue()))
     * );
     *
     */

    LogEntry(String callerName, Object result, Map<String, Object> additionalInfo) {
        this.callerName = callerName;
        this.result = result;
        this.additionalInfo = additionalInfo
                .entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> String.valueOf(entry.getValue()))
                );;
    }


    public String getResult() {
        return this.result.toString();
    }

    /**
     * @Overrides: toString in class Object
     */
    public String toString() {
        return "Log{"+
                "callerName:" + this.callerName + "\n"+
                "result:" + this.result +"\n"+
                "additionalInfo:" + this.additionalInfo +"\n";
    }
}
