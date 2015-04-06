package me.johnking.zmaster;

import java.text.SimpleDateFormat;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * Created by Marco on 17.08.2014.
 */
public class LogFormatter extends Formatter {

    private SimpleDateFormat date = new SimpleDateFormat("HH:mm:ss");

    public String format(LogRecord logRecord) {
        StringBuilder buffer = new StringBuilder();
        buffer.append("[").append(date.format(Long.valueOf(logRecord.getMillis())));
        buffer.append(" ").append(logRecord.getLevel()).append("]: ");
        buffer.append(logRecord.getMessage()).append('\n');
        return buffer.toString( );
    }
}
