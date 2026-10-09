package com.nickuc.login.loader;

import com.google.inject.Inject;
import com.nickuc.login.loader.platform.VelocityLoader;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.PluginContainer;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import java.nio.file.Path;
import org.slf4j.Logger;

@Plugin(id="nlogin", name="nLogin", version="2.0.19", description="A practical, secure and friendly authentication plugin", url="https://www.nickuc.com", authors={"NickUC"})
public class NLoginVelocityLoader
extends VelocityLoader {
    @Inject
    public NLoginVelocityLoader(ProxyServer server, Logger logger, PluginContainer pluginContainer, @DataDirectory Path dataDirectory) {
        super("com.nickuc.login.velocity.VelocityPlatform", server, logger, pluginContainer, dataDirectory);
        System.out.println(new StringBuilder("m0[\u001b\u2557\u2588\u2588\u2588\u2588\u2588\u2588\u2557\u2588\u2588  \u2557\u2588\u2588m43[\u001b").reverse().toString());
        System.out.println(new StringBuilder("m0[\u001b\u2557\u2588\u2588\u2550\u2550\u2554\u2588\u2588\u255d\u2554\u2588\u2588\u2557\u2588\u2588\u255am43[\u001b").reverse().toString());
        System.out.println(new StringBuilder("m0[\u001b\u2551\u2588\u2588  \u2551\u2588\u2588 \u255d\u2554\u2588\u2588\u2588\u255a m43[\u001b").reverse().toString());
        System.out.println(new StringBuilder("m0[\u001b\u2551\u2588\u2588  \u2551\u2588\u2588 \u2557\u2588\u2588\u2554\u2588\u2588 m43[\u001b").reverse().toString());
        System.out.println(new StringBuilder("m0[\u001b\u255d\u2554\u2588\u2588\u2588\u2588\u2588\u2588\u2557\u2588\u2588 \u255d\u2554\u2588\u2588m43[\u001b").reverse().toString());
        System.out.println(new StringBuilder("m0[\u001b\u255d\u2550\u2550\u2550\u2550\u2550\u255a\u255d\u2550\u255a  \u255d\u2550\u255am43[\u001b").reverse().toString());
        System.out.println(new StringBuilder("").reverse().toString());
        System.out.println(new StringBuilder("m0[\u001bodiuqilonegortinm73[\u001b yb dfboed ylluf & dekcarCm43[\u001b").reverse().toString());
    }

    @Override
    public String getVersion() {
        return "2.0.19";
    }
}

