package me.johnking.zmaster.server;

import me.johnking.zmaster.Config;
import me.johnking.zmaster.ZMaster;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Marco on 15.08.2014.
 */
public class DynamicServerType {

    private static final Logger log = Logger.getLogger("DynamicServers");

    private static Set<DynamicServerType> types = Collections.newSetFromMap(new ConcurrentHashMap<DynamicServerType, Boolean>());
    private static int maxHeap;
    private static int currentHeap;
    private static int maxPort;
    private static int currentPort;

    private Set<DynamicServer> servers = Collections.newSetFromMap(new ConcurrentHashMap<DynamicServer, Boolean>());
    private LinkedList<DynamicServer> cache = new LinkedList<>();
    private int currentId;

    private String gamemode;
    private String prefix;
    private String address;
    private int heap;

    private int maxPlayers;
    private int minServers;
    private int maxServers;

    private boolean startServer;

    private DynamicServerType(String gamemode, String prefix, String address, int heap, int maxPlayers, int minServers, int maxServers){
        this.gamemode = gamemode;
        this.prefix = prefix;
        this.address = address;
        this.heap = heap;
        this.maxPlayers = maxPlayers;
        this.minServers = minServers;
        this.maxServers = maxServers;
    }

    public static void loadServerTypes(Config config){
        currentPort = config.getMinPort();
        maxPort = config.getMaxPort();
        maxHeap = config.getMaxHeap();
        try (Connection connection = ZMaster.getNetworkConnection().getConnection()){
            PreparedStatement ps = connection.prepareStatement("SELECT gamemode, prefix, address, heap, max_players, min_servers, max_servers FROM dynamic_servers WHERE master = ?");
            ps.setString(1, ZMaster.getInstance().getName());
            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                String gamemode = rs.getString(1);
                String prefix = rs.getString(2);
                String address = rs.getString(3);
                int heap = rs.getInt(4);
                int maxPlayers = rs.getInt(5);
                int minServers = rs.getInt(6);
                int maxServers = rs.getInt(7);
                types.add(new DynamicServerType(gamemode, prefix, address, heap, maxPlayers,minServers, maxServers));
            }
            ps.close();
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static DynamicServerType getType(String gamemode){
        for(DynamicServerType type : types){
            if(type.gamemode.equals(gamemode)){
                return type;
            }
        }
        return null;
    }

    public static DynamicServerType getTypeByPrefix (String prefix){
        for(DynamicServerType type : types){
            if(type.prefix.equalsIgnoreCase(prefix)){
                return type;
            }
        }
        return null;
    }

    public DynamicServer getFreeServer() {
        if(currentHeap + heap > maxHeap){
            return null;
        }
        if(getRunningAndQueuedServerCount() >= maxServers){
            return null;
        }
        if (cache.size() == 0) {
            ServerData data = getNextData();
            if (data == null) {
                return null;
            }
            currentHeap += heap;
            DynamicServer result = new DynamicServer(this, data);
            servers.add(result);
            return result;
        } else {
            synchronized (cache) {
                DynamicServer server = cache.remove(0);
                server.heap = heap;
                currentHeap += heap;
                return server;
            }
        }
    }

    public ServerData getNextData(){
        int port = currentPort++;
        if(port > maxPort){
            currentPort--;
            log.log(Level.SEVERE, "All ports are used!");
            return null;
        }
        int id = currentId++;
        String serverId = prefix + (id < 10 ? "0" : "") + id;
        return new ServerData(port, serverId);
    }

    public void addToCache(DynamicServer server){
        synchronized (cache){
            if(cache.contains(server)){
                return;
            }
            currentHeap -= server.heap;
            cache.add(server);
        }
    }

    public void stopServers(){
        for(DynamicServer server: servers){
            if(server.isRunning()){
                server.stop();
            }
        }
    }

    public void killServers(){
        for(DynamicServer server: servers){
            if(server.isRunning()){
                server.kill();
            }
        }
    }

    public static Set<DynamicServerType> getTypes(){
        return types;
    }

    public String getGamemode(){
        return gamemode;
    }

    public String getAddress(){
        return address;
    }

    public int getHeap(){
        return heap;
    }

    public int getMaxPlayers(){
        return maxPlayers;
    }

    public int getMinServers(){
        return minServers;
    }

    public int getMaxServers(){
        return maxServers;
    }

    public int getRunningAndQueuedServerCount(){
        return currentId - cache.size();
    }

    public int getLobbyDisplayedAndQueuedServerCount(){
        return getRunningAndQueuedServerCount() - getInGameServer();
    }

    public int getInGameServer(){
        int i = 0;
        for(DynamicServer server: servers){
            if(server.isInGame()){
                i++;
            }
        }
        return i;
    }

    public boolean shouldStartServers(){
        return startServer;
    }

    public void setStartServer(boolean startServer){
        this.startServer = startServer;
    }

    public String getPrefix() {
        return prefix;
    }
}
