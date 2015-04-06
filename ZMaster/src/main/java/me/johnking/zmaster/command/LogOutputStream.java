package me.johnking.zmaster.command;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Created by Marco on 19.08.2014.
 */
public class LogOutputStream extends ByteArrayOutputStream {

    private static final String separator = System.getProperty("line.separator");
    private OutputController queue;

    public LogOutputStream(OutputController queue){
        this.queue = queue;
    }

    @Override
    public void flush() throws IOException {
        synchronized (this) {
            super.flush();
            String record = this.toString();
            super.reset();

            if ((record.length() > 0) && (!record.equals(separator))) {
                queue.queue(record);
            }
        }
    }
}
