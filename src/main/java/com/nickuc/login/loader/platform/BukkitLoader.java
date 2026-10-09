package com.nickuc.login.loader.platform;

import com.nickuc.login.loader.LoaderBootstrap;
import com.nickuc.login.loader.MemClassLoader;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class BukkitLoader
extends JavaPlugin {
    private final LoaderBootstrap plugin;

    public BukkitLoader(String bootstrapClass) {
        MemClassLoader loader = new MemClassLoader(((Object)((Object)this)).getClass().getClassLoader());
        this.plugin = loader.createLoader(bootstrapClass, BukkitLoader.class, this);
    }

    public void onLoad() {
        this.plugin.load();
    }

    public void onEnable() {
        this.plugin.enable();
    }

    public void onDisable() {
        this.plugin.disable();
    }

    public abstract String getVersion();
}

