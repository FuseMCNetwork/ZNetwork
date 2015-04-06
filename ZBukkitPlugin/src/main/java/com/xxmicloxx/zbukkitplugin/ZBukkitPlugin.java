package com.xxmicloxx.zbukkitplugin;

import com.xxmicloxx.znetworklib.codec.Request;
import com.xxmicloxx.znetworklib.codec.Result;
import com.xxmicloxx.znetworklib.packet.core.GeneralRequest;
import com.xxmicloxx.znetworkplugin.ResultHandler;
import com.xxmicloxx.znetworkplugin.ZNativePlugin;
import com.xxmicloxx.znetworkplugin.ZNetworkPlugin;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

/**
 * Created by ml on 15.08.14.
 */
public class ZBukkitPlugin extends JavaPlugin implements ZNativePlugin {
    private Logger log = Logger.getLogger(ZBukkitPlugin.class.getName());

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
        Bukkit.shutdown();
    }

    @Override
    public void handleRequest(GeneralRequest request, final ResultHandler handler) {
        AsyncRequestReceivedEvent event = new AsyncRequestReceivedEvent(request.getSender(), request.getRequest());
        Bukkit.getPluginManager().callEvent(event);
        Result result = event.getResult();

        if (RequestReceivedEvent.getHandlerList().getRegisteredListeners().length != 0) {
            log.warning("There are synchronous request listeners! BAD!");
            final RequestReceivedEvent event1 = new RequestReceivedEvent(request.getSender(), request.getRequest());
            event1.setResult(result);

            Bukkit.getScheduler().runTask(this, new Runnable() {
                @Override
                public void run() {
                    Bukkit.getPluginManager().callEvent(event1);
                    handler.handle(event1.getResult());
                }
            });
        } else {
            handler.handle(result);
        }
    }
}
