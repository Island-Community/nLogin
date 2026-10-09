package com.nickuc.login.listener.bukkit;

import com.nickuc.login.auth.login.ReadyLoginHandler;
import com.nickuc.login.discord.DiscordNotifier;
import com.nickuc.login.discord.ParentDiscordNotifier;
import com.nickuc.login.loader.LoaderBootstrap;
import com.nickuc.login.loader.platform.BukkitLoader;
import com.nickuc.login.platform.session.CachedSessionHandler;
import com.nickuc.login.platform.command.CommandHandler;
import com.nickuc.login.platform.session.IndirectSessionHandler;
import com.nickuc.login.platform.session.LinkedSessionHandler;
import com.nickuc.login.platform.sender.SenderAdapter;
import com.nickuc.login.platform.sender.TightSenderAdapter;
import java.io.File;
import org.bukkit.Server;

public abstract class DiscordGuard implements LoaderBootstrap, CommandHandler, IndirectSessionHandler<BukkitLoader> {
   private final SecondarySettingsGuard secondarySettingsGuard;
   private final BukkitLoader bukkitLoader;
   public final ParentDiscordNotifier parentDiscordNotifier;
   private final SenderAdapter senderAdapter;

   public void enable() {
      this.parentDiscordNotifier.handleTask();
   }

   public BukkitLoader retrieveBukkitLoader() {
      return this.bukkitLoader;
   }

   @Override
   public void executeTask() {
      if (this.resolveState()) {
         this.bukkitLoader.getServer().getPluginManager().disablePlugin(this.bukkitLoader);
      }
   }

   @Override
   public Object buildObject(int target) {
      return target == 1 ? this.getServer().getPort() : null;
   }

   @Override
   public ParentDiscordNotifier getParentDiscordNotifier() {
      return this.parentDiscordNotifier;
   }

   public void load() {
      this.parentDiscordNotifier.processTask();
   }

   @Override
   public LinkedSessionHandler processLinkedSessionHandler(boolean target) {
      return this.parentDiscordNotifier.processLinkedSessionHandler(target);
   }

   public void performTask() {
      if (this.parentDiscordNotifier.fetchState()) {
         this.parentDiscordNotifier.<DiscordNotifier>loadDiscordNotifier().performTask();
      }
   }

   public MessageListener findMessageListener() {
      return (MessageListener)this.parentDiscordNotifier.retrieveTightConnectionContract();
   }

   @Override
   public CachedSessionHandler resolveCachedSessionHandler() {
      return this.secondarySettingsGuard;
   }

   public void updateTask() {
      if (this.parentDiscordNotifier.fetchState()) {
         this.parentDiscordNotifier.<DiscordNotifier>loadDiscordNotifier().updateTask();
      }
   }

   @Override
   public String getMessage() {
      return this.parentDiscordNotifier.name;
   }

   @Override
   public String retrieveMessage() {
      return this.parentDiscordNotifier.activeName;
   }

   @Override
   public File resolveFile() {
      return this.bukkitLoader.getDataFolder();
   }

   @Override
   public String toString() {
      return this.parentDiscordNotifier.toString();
   }

   @Override
   public boolean resolveState() {
      return this.bukkitLoader.isEnabled();
   }

   @Override
   public SenderAdapter fetchSenderAdapter() {
      return this.senderAdapter;
   }

   public Server getServer() {
      return this.bukkitLoader.getServer();
   }

   public DiscordGuard(BukkitLoader target, String input, Object output) {
      this.bukkitLoader = target;
      this.secondarySettingsGuard = new SecondarySettingsGuard(this);
      this.parentDiscordNotifier = new ParentDiscordNotifier(input, target.getVersion(), output, this);
      this.senderAdapter = new ReadyLoginHandler(target.getLogger());
   }

   public abstract TightSenderAdapter[] findValues();

   public void processTask() {
      if (this.parentDiscordNotifier.fetchState()) {
         this.parentDiscordNotifier.<DiscordNotifier>loadDiscordNotifier().processTask();
      }
   }

   public void disable() {
      this.parentDiscordNotifier.sendTask();
   }

   public void dispatchTask() {
      if (this.parentDiscordNotifier.fetchState()) {
         this.parentDiscordNotifier.<DiscordNotifier>loadDiscordNotifier().dispatchTask();
      }
   }
}
