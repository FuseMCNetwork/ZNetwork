package com.xxmicloxx.zbukkitplugin;

import com.xxmicloxx.znetworklib.codec.Request;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Created with IntelliJ IDEA.
 * User: ml
 * Date: 20.12.13
 * Time: 16:04
 */
public class RequestReceivedEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private String sender;
    private Request request;
    private com.xxmicloxx.znetworklib.codec.Result result = null;

    public RequestReceivedEvent(String sender, Request request) {
        this.sender = sender;
        this.request = request;
    }

    public Request getRequest() {
        return request;
    }

    public String getSender() {
        return sender;
    }

    public com.xxmicloxx.znetworklib.codec.Result getResult() {
        return result;
    }

    public void setResult(com.xxmicloxx.znetworklib.codec.Result answer) {
        this.result = answer;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
