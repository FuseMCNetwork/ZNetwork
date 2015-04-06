package me.johnking.zmaster.command;

import jline.console.ConsoleReader;

import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Marco on 19.08.2014.
 */
public class OutputController {

    private final BlockingQueue<String> queue;
    private final OutputStream outputStream;
    private final ConsoleReader reader;
    private final TerminalConsoleWriterThread logThread;

    public OutputController(OutputStream outputStream, ConsoleReader reader) {
        this.queue = new LinkedBlockingQueue<>(250);
        this.outputStream = outputStream;
        this.reader = reader;

        this.logThread = new TerminalConsoleWriterThread(this);
    }

    public void queue(String string) {
        if (queue.size() >= 250) {
            queue.clear();
        }
        queue.add(string);
    }

    public String getNextLog() {
        try {
            return queue.take();
        } catch (InterruptedException ignored) {
        }
        return null;
    }

    public OutputStream getOutputStream(){
        return outputStream;
    }

    public ConsoleReader getReader(){
        return reader;
    }

    public TerminalConsoleWriterThread getLogThread(){
        return logThread;
    }

    private static class TerminalConsoleWriterThread extends Thread {

        private final OutputController outputController;

        public TerminalConsoleWriterThread(OutputController outputController) {
            this.outputController = outputController;
            this.setDaemon(true);
            this.start();
        }

        public void run() {
            while (true) {
                String input = outputController.getNextLog();
                if (input == null) {
                    continue;
                }
                String[] messages = input.split("\n");
                for (String message : messages) {
                    try {
                        outputController.getReader().print(ConsoleReader.RESET_LINE + "");
                        outputController.getReader().flush();
                        outputController.getOutputStream().write((message + '\n').getBytes());
                        outputController.getOutputStream().flush();

                        try {
                            outputController.getReader().drawLine();
                        } catch (Throwable ex) {
                            outputController.getReader().getCursorBuffer().clear();
                        }
                        outputController.getReader().flush();
                    } catch (IOException ex) {
                        Logger.getLogger(TerminalConsoleWriterThread.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
    }
}
