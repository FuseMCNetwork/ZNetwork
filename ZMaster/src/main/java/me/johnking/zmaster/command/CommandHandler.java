package me.johnking.zmaster.command;

import me.johnking.zmaster.DynamicServerHandler;
import me.johnking.zmaster.ZMaster;
import me.johnking.zmaster.server.DynamicServerType;
import me.johnking.zmaster.server.Server;
import com.xxmicloxx.znetworkplugin.ZNetworkPlugin;
import jline.console.ConsoleReader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;

/**
 * Created by Marco on 15.08.2014.
 */
public class CommandHandler {

    private ConsoleReader reader;

    public CommandHandler(ConsoleReader reader){
        this.reader = reader;
    }

    public void handleCommand(String command, String[] args){
        if(command.equalsIgnoreCase("dynamic")){
            if(args.length > 0){
                String sub = args[0];
                if(sub.equalsIgnoreCase("list")){
                    for(DynamicServerType type : DynamicServerType.getTypes()){
                        String line = type.getPrefix() + " (" + type.getGamemode() + ")" + (type.shouldStartServers() ? " (E): " : ": ") + type.getRunningAndQueuedServerCount();
                        ZMaster.getLogger().log(Level.INFO, "\t" + line);
                    }
                } else if (args.length > 1) {
                    DynamicServerType type = DynamicServerType.getTypeByPrefix(args[1]);
                    if(type == null){
                        ZMaster.getLogger().log(Level.INFO, "No server-type found " + args[1]);
                        return;
                    }
                    if(sub.equalsIgnoreCase("enable")){
                        type.setStartServer(true);
                        DynamicServerHandler.checkServerTypes();
                        ZMaster.getLogger().log(Level.INFO, "Started Servers " + args[1]);
                    } else if (sub.equalsIgnoreCase("disable")){
                        type.setStartServer(false);
                        ZMaster.getInstance().getQueue().removeDynamicServers(type);
                        ZMaster.getLogger().log(Level.INFO, "Disabled Servers " + args[1]);
                    } else if (sub.equalsIgnoreCase("stopall")){
                        type.setStartServer(false);
                        ZMaster.getInstance().getQueue().removeDynamicServers(type);
                        type.stopServers();
                        ZMaster.getLogger().log(Level.INFO, "Stopped all Server with Type: " + args[1]);
                    } else if (sub.equalsIgnoreCase("killall")){
                        type.setStartServer(false);
                        ZMaster.getInstance().getQueue().removeDynamicServers(type);
                        type.killServers();
                        ZMaster.getLogger().log(Level.INFO, "Killed all Server with Type: " + args[1]);
                    }
                }
            }
        } else if (command.equalsIgnoreCase("static")){
            if(args.length > 0){
                String sub = args[0];
                if(sub.equalsIgnoreCase("list")){
                    ArrayList<Server> servers = new ArrayList<>();
                    for(Server server: Server.getServers()){
                        if(!server.isDynamic()){
                            servers.add(server);
                        }
                    }
                    for(Server server: servers){
                        String line = server.getServerId() + (server.isRunning() ? "(R)" : "");
                        ZMaster.getLogger().log(Level.INFO, "\t" + line);
                    }
                } else if (args.length > 1){
                    Server server = Server.getServer(args[1]);
                    if(server == null || server.isDynamic()){
                        ZMaster.getLogger().log(Level.INFO, "static cmd <static-server>");
                        return;
                    }
                    if(sub.equalsIgnoreCase("queue")) {
                        if (server.isRunning() || ZMaster.getInstance().getQueue().isQueued(server)) {
                            ZMaster.getLogger().log(Level.INFO, "Server " + server.getServerId() + " is already running!");
                            return;
                        }
                        ZMaster.getInstance().getQueue().queue(server);
                        ZMaster.getLogger().log(Level.INFO, "Queued server " + server.getServerId());
                    } else if (sub.equalsIgnoreCase("unqueue")) {
                        if (!ZMaster.getInstance().getQueue().removeFromQueue(server)) {
                            // was not queued
                            ZMaster.getLogger().log(Level.INFO, "Server " + server.getServerId() + " was not queued.");
                            return;
                        }
                        ZMaster.getLogger().log(Level.INFO, "Unqueued server " + server.getServerId());
                    } else if (sub.equalsIgnoreCase("start")) {
                        if (server.isRunning()) {
                            ZMaster.getLogger().log(Level.INFO, "Server " + server.getServerId() + " is already running!");
                            return;
                        }
                        ZMaster.getInstance().getQueue().removeFromQueue(server);
                        server.start();
                        ZMaster.getLogger().log(Level.INFO, "Started server " + server.getServerId());
                    } else if (sub.equalsIgnoreCase("stop")){
                        if(!server.isRunning()){
                            ZMaster.getLogger().log(Level.INFO, "Server " + server.getServerId() + " is not running!");
                            return;
                        }
                        server.stop();
                        ZMaster.getLogger().log(Level.INFO, "Stopped server " + server.getServerId());
                    } else if (sub.equalsIgnoreCase("kill")){
                        if(!server.isRunning()){
                            ZMaster.getLogger().log(Level.INFO, "Server " + server.getServerId() + " is not running!");
                            return;
                        }
                        server.kill();
                        ZMaster.getLogger().log(Level.INFO, "Killed server " + server.getServerId());
                    }
                }
            }
        } else if (command.equalsIgnoreCase("log")){
            if(args.length > 0){
                Server server = Server.getServer(args[0]);
                if(server == null){
                    ZMaster.getLogger().log(Level.INFO, "No such server found!");
                    return;
                }
                ZMaster.getLogger().log(Level.INFO, "-------------------------------------");
                synchronized (server.getLog()){
                    for(String line : server.getLog()){
                        ZMaster.getLogger().log(Level.INFO, ">>" + line);
                    }
                }
                ZMaster.getLogger().log(Level.INFO, "-------------------------------------");
            } else {
                ZMaster.getLogger().log(Level.INFO, "Command: log <server>");
            }
        } else if (command.equalsIgnoreCase("send")) {
            if(args.length > 1) {
                Server server = Server.getServer(args[0]);
                if(server == null){
                    ZMaster.getLogger().log(Level.INFO, "No such server found!");
                    return;
                }
                StringBuilder sb = new StringBuilder();
                for(int i = 1; i < args.length; i++){
                    if(i != 1){
                        sb.append(" ");
                    }
                    sb.append(args[i]);
                }
                server.sendCommand(sb.toString());
            }
        } else if (command.equalsIgnoreCase("clear")) {
            try {
                reader.clearScreen();
            } catch (IOException e){
                e.printStackTrace();
            }
        } else if (command.equalsIgnoreCase("list")) {
            ArrayList<Server> servers = new ArrayList<>();
            for(Server server: Server.getServers()){
                if(!server.isDynamic()){
                    servers.add(server);
                }
            }
            ZMaster.getLogger().log(Level.INFO, "---------- Static - Servers -----------");
            for(Server server: servers){
                String line = server.getServerId() + (server.isRunning() ? "(R)" : "");
                ZMaster.getLogger().log(Level.INFO, "\t" + line);
            }
            ZMaster.getLogger().log(Level.INFO, "---------- Dynamic - Servers ----------");
            for(DynamicServerType type : DynamicServerType.getTypes()){
                String line = type.getPrefix() + " (" + type.getGamemode() + ")" + (type.shouldStartServers() ? " (E): " : ": ") + type.getRunningAndQueuedServerCount();
                ZMaster.getLogger().log(Level.INFO, "\t" + line);
            }
            ZMaster.getLogger().log(Level.INFO, "---------- ----------------- ----------");
        } else if (command.equalsIgnoreCase("exit")) {
            ZNetworkPlugin.getInstance().shutdown();
        }
    }
}
