package com.xxmicloxx.znetworkplugin;

import com.xxmicloxx.znetworklib.packet.core.GeneralRequest;

/**
 * Created by ml on 15.08.14.
 */
public interface ZNativePlugin {
    void shutdown();

    void handleRequest(GeneralRequest request, ResultHandler handler);
}
