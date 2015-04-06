package com.xxmicloxx.zbungeeplugin;

import com.xxmicloxx.znetworklib.codec.Request;
import com.xxmicloxx.znetworklib.codec.Result;
import net.md_5.bungee.api.plugin.Event;

/**
 * Created with IntelliJ IDEA.
 * User: ml
 * Date: 20.12.13
 * Time: 16:04
 */
public class RequestReceivedEvent extends Event {
    private String sender;
    private Request request;
    private Result result = null;

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

    public Result getResult() {
        return result;
    }

    public void setResult(Result answer) {
        this.result = answer;
    }
}
