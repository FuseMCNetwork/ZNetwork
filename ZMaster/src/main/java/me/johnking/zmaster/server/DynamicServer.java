package me.johnking.zmaster.server;

import me.johnking.zmaster.DynamicServerHandler;

/**
 * Created by Marco on 15.08.2014.
 */
public class DynamicServer extends Server {

    private DynamicServerType type;
    private boolean inGame;

    protected DynamicServer(DynamicServerType type, ServerData data){
        super(type.getGamemode(), data.getServerId(), type.getAddress(), data.getPort(), type.getHeap(), type.getMaxPlayers());
        this.type = type;
    }

    public DynamicServerType getDynamicType() {
        return type;
    }

    @Override
    public void processStop(int exit){
        super.processStop(exit);

        this.inGame = false;
        type.addToCache(this);
        DynamicServerHandler.checkServerTypes();
    }

    public boolean isInGame(){
        return inGame;
    }

    public void setInGame(){
        if(isRunning()){
            inGame = true;
        }
    }
}
