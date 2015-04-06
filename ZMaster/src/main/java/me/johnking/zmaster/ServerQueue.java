package me.johnking.zmaster;

import me.johnking.zmaster.server.DynamicServer;
import me.johnking.zmaster.server.DynamicServerType;
import me.johnking.zmaster.server.Server;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;

/**
 * Created by Marco on 15.08.2014.
 */
public class ServerQueue {

    private ReentrantLock serversLock = new ReentrantLock();
    private Condition serversUpdate = serversLock.newCondition();
    private LinkedList<Server> servers;
    private volatile boolean running = true;

    public ServerQueue(int queueSize){
        servers = new LinkedList<>();
        for (int i = 0; i < queueSize; i++) {
            new Handler(this);
        }
    }

    public void clearQueue(){
        serversLock.lock();
        for(Iterator<Server> iterator = servers.iterator(); iterator.hasNext(); ){
            Server server = iterator.next();
            if(server instanceof DynamicServer){
                DynamicServer dynamicServer = (DynamicServer) server;
                dynamicServer.getDynamicType().addToCache(dynamicServer);
            }
            iterator.remove();
        }
        serversLock.unlock();
    }

    public void removeDynamicServers(DynamicServerType type) {
        serversLock.lock();
        for (Iterator<Server> iterator = servers.iterator(); iterator.hasNext(); ) {
            Server server = iterator.next();
            if (server instanceof DynamicServer) {
                DynamicServer ds = (DynamicServer) server;
                if (ds.getDynamicType() == type) {
                    iterator.remove();
                    type.addToCache(ds);
                }
            }
        }
        serversLock.unlock();
    }

    public boolean isQueued(Server server) {
        serversLock.lock();
        boolean queued = servers.contains(server);
        serversLock.unlock();
        return queued;
    }

    public boolean removeFromQueue(Server server) {
        // returns true if this server was removed, false otherwise.
        serversLock.lock();
        boolean wasQueued = servers.remove(server);
        serversLock.unlock();
        if (wasQueued && server.isDynamic()) {
            DynamicServer ds = (DynamicServer) server;
            ds.getDynamicType().addToCache(ds);
        }
        return wasQueued;
    }

    public void queue(Server server) {
        if(server == null){
            return;
        }
        serversLock.lock();
        servers.add(server);
        serversUpdate.signal();
        serversLock.unlock();
    }

    private static class Handler extends Thread {

        private ServerQueue queue;

        public Handler(ServerQueue queue){
            this.queue = queue;
            setDaemon(true);
            setName("Queue Handler");
            start();
        }

        @Override
        public void run(){
            while (queue.running) {
                queue.serversLock.lock();

                while (queue.servers.size() == 0) {
                    try {
                        queue.serversUpdate.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                Server server = queue.servers.remove(0);
                queue.serversLock.unlock();

                server.start();
                int time = 30;
                while (!server.isReadyToJoin() && time >= 0){
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    time--;
                }
                if(time == -1){
                    ZMaster.getLogger().log(Level.WARNING, "Couldn't start queued server " + server.getServerId() + " ... killing and going on!");
                    if(server.isRunning()){
                        server.kill();
                    }
                }
            }
        }
    }
}
