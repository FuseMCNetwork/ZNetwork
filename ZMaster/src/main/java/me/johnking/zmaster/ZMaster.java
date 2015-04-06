package me.johnking.zmaster;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.johnking.zmaster.command.CommandHandler;
import me.johnking.zmaster.command.CommandLineReader;
import me.johnking.zmaster.mysql.MySQL;
import me.johnking.zmaster.mysql.MySQLData;
import me.johnking.zmaster.server.DynamicServerType;
import me.johnking.zmaster.server.Server;
import me.johnking.zmaster.server.StaticServer;
import com.xxmicloxx.znetworklib.packet.core.GeneralRequest;
import com.xxmicloxx.znetworkplugin.ResultHandler;
import com.xxmicloxx.znetworkplugin.ZNativePlugin;
import com.xxmicloxx.znetworkplugin.ZNetworkPlugin;
import jline.console.ConsoleReader;

import java.io.File;
import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Marco on 15.08.2014.
 */
public class ZMaster implements ZNativePlugin {

    private static Logger logger;

    private static ZMaster instance;
    private ConsoleReader reader;
    private ShutdownThread shudownThread;
    private String name;
    private Gson gson;
    private MySQL mySQL;
    private Config config;
    private ServerQueue queue;
    private String parameters;
    private CommandLineReader commandReader;
    private CommandHandler handler;

    public static void main(String[] args){
        instance = new ZMaster();
        instance.start();
    }

    public static Logger getLogger(){
        return logger;
    }

    public void start() {
        new ZNetworkPlugin(this);
        ZNetworkPlugin.getInstance().onEnable();
        try {
            this.reader = new ConsoleReader(System.in, System.out);
        } catch (IOException e){
            System.out.println("Could not load console reader!");
            System.exit(2);
        }
        this.handler = new CommandHandler(this.reader);
        this.commandReader = new CommandLineReader(this.handler, this.reader);
        logger = createLogger();

        name = ZNetworkPlugin.getInstance().getConnectionName();
        if(name == null || name.equals("")){
            logger.log(Level.SEVERE, "Could not load my name!");
            System.exit(1);
        }
        gson = new GsonBuilder().setPrettyPrinting().create();
        logger.log(Level.INFO, "Loading settings from database!");
        loadMySQL();
        this.config = new Config();
        this.parameters = config.getParameters();
        this.queue = new ServerQueue(config.getQueueSize());
        logger.log(Level.INFO, "Loading server types!");
        DynamicServerType.loadServerTypes(config);
        StaticServer.loadServers();
        DynamicServerHandler.init();

        this.shudownThread = new ShutdownThread();
        Runtime.getRuntime().addShutdownHook(this.shudownThread);
        logger.log(Level.INFO, "Done!");
    }

    private void loadMySQL(){
        File file = new File("mysql.json");
        String input = FileHelper.stringFromFile(file);
        MySQLData data = gson.fromJson(input, MySQLData.class);
        mySQL = new MySQL(data);
    }

    public String getName(){
        return name;
    }

    public String getParameters(){
        return parameters;
    }

    public ServerQueue getQueue(){
        return queue;
    }

    public static ZMaster getInstance(){
        return instance;
    }

    public static MySQL getNetworkConnection(){
        return instance.mySQL;
    }

    public void stop(){
        ZMaster.getLogger().log(Level.INFO, "Stopping servers ...");
        Server.stopServers();
        int i = 30;
        while(!Server.allServerDown() && i >= 0){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            i--;
        }
        if(i == -1){
            ZMaster.getLogger().log(Level.WARNING, "Killing servers ...");
            Server.killServers();
        }
        ZMaster.getLogger().log(Level.INFO, "Disabling network-connection ... ");
        ZNetworkPlugin.getInstance().onDisable();
        ZMaster.getLogger().log(Level.INFO, "Done! Thank you and goodbye!");
    }

    @Override
    public void shutdown() {
        Runtime.getRuntime().removeShutdownHook(this.shudownThread);
        try {
            stop();
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            try {
                this.reader.getTerminal().restore();
            } catch (Exception e){
                e.printStackTrace();
            }
            System.exit(0);
        }
    }

    private static Logger createLogger(){
        Logger logger = Logger.getLogger("ZMaster");
        ConsoleHandler handler = new ConsoleHandler();
        handler.setFormatter(new LogFormatter());
        logger.addHandler(handler);
        logger.setUseParentHandlers(false);
        return logger;
    }

    @Override
    public void handleRequest(GeneralRequest request, ResultHandler handler) {
        // TODO
    }

    public ConsoleReader getReader(){
        return reader;
    }
}
