package com.xxmicloxx.zbungeeplugin;

import com.xxmicloxx.znetworklib.packet.core.GeneralRequest;
import com.xxmicloxx.znetworkplugin.ResultHandler;
import com.xxmicloxx.znetworkplugin.ZNativePlugin;
import com.xxmicloxx.znetworkplugin.ZNetworkPlugin;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;

/**
 * Created by ml on 15.08.14.
 */
public class ZBungeePlugin extends Plugin implements ZNativePlugin {
    @Override
    public void onEnable() {
        new ZNetworkPlugin(this);
        ZNetworkPlugin.getInstance().onEnable();
    }

    @Override
    public void onDisable() {
        ZNetworkPlugin.getInstance().onDisable();
    }

    @Override
    public void shutdown() {
        ProxyServer.getInstance().stop();
    }

    @Override
    public void handleRequest(GeneralRequest request, ResultHandler handler) {
        RequestReceivedEvent event = new RequestReceivedEvent(request.getSender(), request.getRequest());
        ProxyServer.getInstance().getPluginManager().callEvent(event);
        handler.handle(event.getResult());
    }
}
