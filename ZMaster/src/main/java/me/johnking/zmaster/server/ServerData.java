package me.johnking.zmaster.server;

/**
 * Created by Marco on 15.08.2014.
 */
public class ServerData {

    private int port;
    private String serverId;

    public ServerData(int port, String serverId){
        this.port = port;
        this.serverId = serverId;
    }

    public int getPort(){
        return port;
    }

    public String getServerId(){
        return serverId;
    }
}
