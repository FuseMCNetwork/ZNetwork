package me.johnking.zmaster.command;

import jline.console.ConsoleReader;
import jline.console.completer.*;

import java.io.IOException;
import java.io.PrintStream;
import java.util.LinkedList;

/**
 * Created by Marco on 15.08.2014.
 */
public class CommandLineReader extends Thread{

    private CommandHandler handler;
    private ConsoleReader reader;

    public CommandLineReader(CommandHandler handler, ConsoleReader reader){
        super("ConsoleReader");
        this.handler = handler;
        try {
            this.reader = reader;
            this.reader.setExpandEvents(false);
            this.reader.setPrompt(">");

            System.setOut(new PrintStream(new LogOutputStream(new OutputController(System.out, this.reader)), true));
            System.setErr(new PrintStream(new LogOutputStream(new OutputController(System.err, this.reader)), true));

            LinkedList<Completer> completers = new LinkedList<Completer>();
            completers.add(new AggregateCompleter(
                new ArgumentCompleter(new StringsCompleter("dynamic"), new StringsCompleter("enable", "disable", "stopall", "killall", "list"), new NullCompleter()),
                new ArgumentCompleter(new StringsCompleter("static"), new StringsCompleter("start", "stop", "queue", "unqueue", "list", "kill")),
                new StringsCompleter("exit", "send", "clear", "log", "list")
            ));
            for(Completer completer: completers){
                this.reader.addCompleter(completer);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        setDaemon(true);
        start();
    }

    @Override
    public void run(){
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                try {
                    if(line.equals("")){
                        continue;
                    }
                    String[] parts = line.split(" ");
                    String[] args = new String[parts.length - 1];
                    for (int i = 1; i < parts.length; i++) {
                        args[i - 1] = parts[i];
                    }
                    handler.handleCommand(parts[0], args);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
