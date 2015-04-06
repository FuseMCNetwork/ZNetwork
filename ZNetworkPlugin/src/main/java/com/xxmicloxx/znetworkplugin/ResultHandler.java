package com.xxmicloxx.znetworkplugin;

import com.xxmicloxx.znetworklib.codec.Packet;
import com.xxmicloxx.znetworklib.codec.Request;
import com.xxmicloxx.znetworklib.codec.Result;

/**
 * Created by ml on 15.08.14.
 */
public interface ResultHandler {
    public void handle(Result result);
}
