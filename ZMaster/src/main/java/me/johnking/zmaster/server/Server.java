package me.johnking.zmaster.server;

import me.johnking.zmaster.CommandBuilder;
import me.johnking.zmaster.ZMaster;
import me.johnking.zmaster.mysql.MySQLHelper;
import com.xxmicloxx.znetworklib.packet.ext.ServerStatusChangeEvent;
import com.xxmicloxx.znetworkplugin.ZNetworkPlugin;

import java.io.*;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

/**
 * Created by Marco on 15.08.2014.
 */
public abstract class Server {

    private static final Set<Server> servers = Collections.newSetFromMap(new ConcurrentHashMap<Server, Boolean>());

    protected String gamemode;
    protected String serverId;
    protected String address;
    protected int port;
    protected int heap;
    protected int maxUsers;

    private Process process;
    private ServerListener serverListener;
    private boolean running;
    private boolean stop;
    private boolean readyToJoin;

    private LinkedList<String> log;

    public Server(String gamemode, String serverId, String address, int port, int heap, int maxUsers) {
        this.gamemode = gamemode;
        this.serverId = serverId;
        this.address = address;
        this.port = port;
        this.heap = heap;
        this.maxUsers = maxUsers;

        log = new LinkedList<>();
        servers.add(this);
    }

    public static void stopServers(){
        for (DynamicServerType type : DynamicServerType.getTypes()) {
            type.setStartServer(false);
        }
        ZMaster.getInstance().getQueue().clearQueue();
        for(Server server : servers){
            if(server.running){
                server.stop();
            }
        }
    }

    public static void killServers(){
        for(Server server: servers){
            if(server.running){
                server.kill();
            }
        }
    }

    public static boolean allServerDown(){
        for(Server server: servers){
            if(server.running){
                return false;
            }
        }
        return true;
    }

    public void logLine(String line){
        if(line.endsWith("For help, type \"help\" or \"?\"")){
            readyToJoin = true;
            ////Call Event////
            ZNetworkPlugin.getInstance().sendEvent("minecraft_server_loaded", new ServerStatusChangeEvent(serverId, gamemode, address, port));
            //////////////////
            ////Write to SQL////
            MySQLHelper.loadedServer(this);
            ////////////////////
        }
    }

    public void sendCommand(String line){
        if(!running){
            ZMaster.getLogger().log(Level.WARNING, "Error: server " + serverId + " was not running!");
            return;
        }
        OutputStream os = process.getOutputStream();
        OutputStreamWriter osw = new OutputStreamWriter(os);
        BufferedWriter bw = new BufferedWriter(osw);
        try {
            bw.write(line);
            bw.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bw.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void processStop(int exit){
        ////Call Event////
        ZNetworkPlugin.getInstance().sendEvent("minecraft_server_stopped", new ServerStatusChangeEvent(serverId, gamemode, address, port));
        //////////////////
        ////Write to SQL////
        MySQLHelper.stopServer(this);
        ////////////////////
        ZMaster.getLogger().log(Level.INFO, "Server with name " + serverId + " stopped with exit code " + exit + ".");
        running = false;
        readyToJoin = false;
        serverListener = null;
        process = null;
        if (!shouldStop()) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignored) {
            }
            ZMaster.getLogger().log(Level.INFO, "Server put in starting queue.");
            ZMaster.getInstance().getQueue().queue(this);
        }
        stop = false;
    }

    public void start(){
        if (running) {
            ZMaster.getLogger().log(Level.WARNING, "Error: server " + serverId + " is already running!");
            return;
        }
        CommandBuilder cb = new CommandBuilder();
        cb.appendPart(System.getProperty("java.home") + File.separator + "bin" + File.separator + "java");
        cb.append(ZMaster.getInstance().getParameters().replaceFirst("\\$\\{heap\\}", String.valueOf(heap)));
        cb.append("-Dnetwork.name=" + serverId);
        if (System.getProperty("destinationPort") != null) {
            cb.append("-DdestinationPort=" + System.getProperty("destinationPort"));
        }
        cb.append("-jar", "server.jar");
        cb.append("--log-strip-color", "--nojline");
        cb.append("--host", address);
        cb.append("--port", String.valueOf(port));
        cb.append("--size", String.valueOf(maxUsers));
        cb.append("--world-dir", "worlds");
        cb.append("--online-mode", "false");

        if (isDynamic()) cb.append("--world", "WGameLobby");

        ProcessBuilder pb = cb.toBuilder();
        pb.directory(new File("templates/" + gamemode + "/"));
        try {
            process = pb.start();
            running = true;
            ////Call Event////
            ZNetworkPlugin.getInstance().sendEvent("minecraft_server_started", new ServerStatusChangeEvent(serverId, gamemode, address, port));
            //////////////////
            ////Write to SQL////
            MySQLHelper.startServer(this);
            ////////////////////
            ZMaster.getLogger().log(Level.INFO, "Server with name " + serverId + " was started.");
        } catch (IOException e) {
            ZMaster.getLogger().log(Level.WARNING, "Failed to start server " + serverId + "!");
            e.printStackTrace();
            return;
        }
        (serverListener = new ServerListener(this)).start();
    }

    public void stop(){
        if (!running) {
            ZMaster.getLogger().log(Level.WARNING, "Error: server " + serverId + " was not running!");
            return;
        }
        stop = true;
        sendCommand("stop");
        ZMaster.getLogger().log(Level.INFO, "Sent stop to " + serverId + ".");
    }

    public void kill(){
        if(!running){
            return;
        }
        stop = true;
        process.destroy();
        ZMaster.getLogger().log(Level.INFO, "Killed " + serverId + "!");
    }

    public static Server getServer(String serverId){
        for(Server server: servers){
            if(server.serverId.equals(serverId)){
                return server;
            }
        }
        return null;
    }

    public static Set<Server> getServers(){
        return servers;
    }

    public boolean shouldStop(){
        return (this instanceof DynamicServer) || stop;
    }

    public boolean isRunning(){
        return running;
    }

    public boolean isDynamic() {
        return this instanceof DynamicServer;
    }

    public String getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }

    public int getMaxUsers() {
        return maxUsers;
    }

    public Process getServerProcess(){
        return process;
    }

    public String getType() {
        return gamemode;
    }

    public String getServerId(){
        return serverId;
    }

    public LinkedList<String> getLog(){
        return log;
    }

    public boolean isReadyToJoin(){
        return readyToJoin;
    }
}
