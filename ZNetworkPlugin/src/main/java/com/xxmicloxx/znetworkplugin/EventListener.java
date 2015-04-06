package com.xxmicloxx.znetworkplugin;

import com.xxmicloxx.znetworklib.codec.NetworkEvent;

/**
 * Created by ml on 15.08.14.
 */
public interface EventListener {
    public void onEventReceived(String event, String sender, NetworkEvent data);
}
