package com.nickuc.login.discord;

import com.nickuc.login.auth.message.FastMessageHandler;
import com.nickuc.login.auth.login.IncomingLoginGate;
import com.nickuc.login.auth.login.LiveLoginCheckpoint;
import com.nickuc.login.auth.locale.OpenLocaleBarrier;
import com.nickuc.login.auth.login.SecondaryLoginGate;
import com.nickuc.login.auth.settings.SettingsGate;
import com.nickuc.login.auth.settings.SettingsHandler;
import com.nickuc.login.command.BungeeHandler;
import com.nickuc.login.command.PasswordHashCommand;
import com.nickuc.login.config.PasswordHashContainer;
import com.nickuc.login.config.PasswordHashLoader;
import org.json.JSONArray;
import com.nickuc.login.loader.MemClassLoader;
import com.nickuc.login.platform.session.CachedSessionHandler;
import com.nickuc.login.platform.session.IndirectSessionHandler;
import com.nickuc.login.platform.session.LinkedSessionHandler;
import com.nickuc.login.platform.account.OutgoingAccountHandler;
import com.nickuc.login.platform.account.ParentAccountHandler;
import com.nickuc.login.platform.proxy.SilentProxyState;
import com.nickuc.login.platform.connection.TightConnectionContract;
import com.nickuc.login.premium.LocaleLookup;
import com.nickuc.login.premium.ParentSettingsLookup;
import com.nickuc.login.premium.UpdateLookup;
import com.nickuc.login.security.hashing.PendingPasswordHashHasher;
import com.nickuc.login.security.hashing.PrimaryPasswordHashVerifier;
import com.nickuc.login.security.hashing.VerifiedPasswordHashHasher;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.util.Collection;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import net.md_5.bungee.api.ProxyServer;
import org.bukkit.Bukkit;

public class ParentDiscordNotifier {
   private File dataFile;
   private boolean enabled;
   public final MemClassLoader memClassLoader;
   public final String name;
   private final Object object;
   private LinkedSessionHandler linkedSessionHandler;
   private DiscordNotifier discordNotifier;
   public final String activeName;
   private int count = 1;
   private PasswordHashBridge passwordHashBridge;
   private UpdateLookup updateLookup;
   private PasswordHashLoader passwordHashLoader;
   private TightConnectionContract tightConnectionContract;
   private volatile boolean activeEnabled;
   private ParentAccountHandler parentAccountHandler;
   private final CountDownLatch countDownLatch = new CountDownLatch(1);
   private ParentSettingsLookup parentSettingsLookup;
   private IncomingLoginGate incomingLoginGate;
   private volatile boolean pendingEnabled;
   private volatile boolean currentEnabled;
   private final IndirectSessionHandler<?> indirectSessionHandler;
   private volatile boolean primaryEnabled;
   private LinkedSessionHandler activeLinkedSessionHandler;

   public LinkedSessionHandler processLinkedSessionHandler(boolean target) {
      return target ? this.linkedSessionHandler : this.activeLinkedSessionHandler;
   }

   public MemClassLoader loadMemClassLoader() {
      return this.memClassLoader;
   }

   public void sendTask() {
      if (!this.activeEnabled) {
         this.currentEnabled = false;
         LiveLoginCheckpoint target = new LiveLoginCheckpoint();
         if (this.parentSettingsLookup != null) {
            this.parentSettingsLookup.findCachedLoginBarrier().dispatchTask();
         }

         if (this.activeLinkedSessionHandler != null) {
            try {
               if (!this.activeLinkedSessionHandler.checkState(2, TimeUnit.SECONDS)) {
                  PasswordHashContainer.processMessage("Awaiting active tasks...");
                  if (!this.activeLinkedSessionHandler.checkState(3, TimeUnit.SECONDS)) {
                     Collection input = this.activeLinkedSessionHandler.getCollection();
                     if (!input.isEmpty()) {
                        PasswordHashContainer.performMessage("Forcing active tasks (" + OpenLocaleBarrier.resolveMessage(input.size()) + ") to terminate");
                        input.stream().limit(5L).forEach(instance -> PasswordHashContainer.performMessage(" - " + instance.loadMessage()));
                     }
                  }
               }
            } catch (InterruptedException result) {
               throw new RuntimeException(result);
            }

            this.activeLinkedSessionHandler.dispatchTask();
         }

         PasswordHashContainer.retrieveSet().clear();
         PasswordHashContainer.processMessage("Successful plugin core shutdown (took " + target.fetchMessage() + "s)");

         try {
            this.indirectSessionHandler.resolveCachedSessionHandler().updateTask();
         } catch (Throwable value) {
            PasswordHashContainer.sendThrowable(value);
            PasswordHashContainer.updateMessage("Unable to shutdown plugin (this is probably a bug!)");
            PasswordHashContainer.updateMessage("");
            PasswordHashContainer.updateMessage("Please report to our team:");
            PasswordHashContainer.updateMessage(" Discord: §bwww.nickuc.com/discord");
            PasswordHashContainer.updateMessage(" Email: §fsupport@nickuc.com");
            PasswordHashContainer.updateMessage("");

            try {
               Thread.sleep(15000L);
            } catch (InterruptedException data) {
               PasswordHashContainer.sendThrowable(data);
            }
         }

         try {
            PasswordHashContainer.close();
         } catch (Throwable context) {
            PasswordHashContainer.sendThrowable(context);
            PasswordHashContainer.updateMessage("Unexpected error while closing console logger (this is probably a bug!)");
            PasswordHashContainer.updateMessage("");
            PasswordHashContainer.updateMessage("Please report to our team:");
            PasswordHashContainer.updateMessage(" Discord: §bwww.nickuc.com/discord");
            PasswordHashContainer.updateMessage(" Email: §fsupport@nickuc.com");
            PasswordHashContainer.updateMessage("");
         }
      }
   }

   public TightConnectionContract retrieveTightConnectionContract() {
      if (!this.retrieveState()) {
         throw new IllegalStateException("Plugin messenger is not loaded!");
      } else {
         return this.tightConnectionContract;
      }
   }

   public int resolveCount() {
      return this.count;
   }

   public PasswordHashBridge fetchPasswordHashBridge() {
      return this.passwordHashBridge;
   }

   public ParentAccountHandler retrieveParentAccountHandler() {
      return this.parentAccountHandler;
   }

   public ParentSettingsLookup fetchParentSettingsLookup() {
      return this.parentSettingsLookup;
   }

   public IncomingLoginGate findIncomingLoginGate() {
      return this.incomingLoginGate;
   }

   public void saveOutgoingAccountHandler(OutgoingAccountHandler target, OutgoingAccountHandler... input) {
      target.handleObject(this.indirectSessionHandler);

      for (OutgoingAccountHandler value : input) {
         value.handleObject(this.indirectSessionHandler);
      }
   }

   public boolean retrieveState() {
      return this.tightConnectionContract != null;
   }

   public <T extends DiscordNotifier> T loadDiscordNotifier() {
      if (!this.fetchState()) {
         throw new IllegalStateException("Core is not loaded!");
      } else {
         return (T)this.discordNotifier;
      }
   }

   private void saveTask() {
      String target = this.incomingLoginGate.retrieveSilentProxyState() == SilentProxyState.PENDING_SILENTPROXYSTATE ? "ncore" : "nCore";
      this.dataFile = new File(this.indirectSessionHandler.resolveFile().getParentFile(), target);
      if (!this.dataFile.exists() && !this.dataFile.mkdirs()) {
         throw new RuntimeException("Unable to create " + this.dataFile + " folder");
      }

      File input = new File(this.dataFile, "cache");
      if (!input.exists() && !input.mkdirs()) {
         throw new RuntimeException("Unable to create " + input + " folder");
      }

      File output = new File(this.dataFile, "libraries");
      if (!output.exists() && !output.mkdirs()) {
         throw new RuntimeException("Unable to create " + output + " folder");
      }
   }

   private void updateTask() {
      if (!this.updateLookup.loadLinkedPasswordHashVerifier().resolveState() && this.updateLookup.getCount() == 1) {
         this.enabled = false;
      } else {
         this.enabled = false;
         PrimaryPasswordHashVerifier target = this.parentSettingsLookup.loadPrimaryPasswordHashVerifier();
         byte[] input = target.createPayload("signature-update");
         if (input == null) {
            target.computePrimaryPasswordHashVerifier("signature-update", FastMessageHandler.buildPayload(instance -> instance.handleTime(System.currentTimeMillis())));
            target.sendTask();
         } else {
            DataInputStream output = new DataInputStream(new ByteArrayInputStream(input));
            long context = output.readLong();
            if (System.currentTimeMillis() - context >= 691200L) {
               this.enabled = true;
            }
         }
      }

      JSONArray value = this.updateLookup.loadLinkedPasswordHashVerifier().handleObject("bootstrap_message");
      if (value != null) {
         value.forEach(instance -> PasswordHashContainer.performMessage(String.valueOf(instance)));
      }

      int result = this.updateLookup.loadLinkedPasswordHashVerifier().buildObject("bootstrap_await", 0);
      if (result > 0) {
         Thread.sleep(result);
      }
   }

   public void at() {
      try {
         this.indirectSessionHandler.resolveCachedSessionHandler().dispatchTask();
      } catch (Exception context) {
         this.activeEnabled = true;
         PasswordHashContainer.sendThrowable(context);
         PasswordHashContainer.updateMessage("Unexpected error when preloading the plugin (this is probably a bug!)");
         PasswordHashContainer.updateMessage("");
         PasswordHashContainer.updateMessage("Please report to our team:");
         PasswordHashContainer.updateMessage(" Discord: §bwww.nickuc.com/discord");
         PasswordHashContainer.updateMessage(" Email: §fsupport@nickuc.com");
         PasswordHashContainer.updateMessage("");

         try {
            Thread.sleep(15000L);
         } catch (InterruptedException output) {
            PasswordHashContainer.sendThrowable(output);
         }
      }
   }

   public PasswordHashLoader retrievePasswordHashLoader() {
      return this.passwordHashLoader;
   }

   public Object getObject() {
      return this.object;
   }

   public void processTask() {
      if (!this.activeEnabled) {
         if (this.pendingEnabled) {
            throw new IllegalStateException("The plugin is already loaded!");
         }

         this.pendingEnabled = true;
         this.at();

         try {
            CachedSessionHandler target = this.indirectSessionHandler.resolveCachedSessionHandler();
            this.incomingLoginGate = target.loadIncomingLoginGate();
            if (this.incomingLoginGate.retrieveSilentProxyState() != SilentProxyState.SILENT_PROXY_STATE) {
               this.activeLinkedSessionHandler = this.linkedSessionHandler = target.computeLinkedSessionHandler(true);
            } else {
               this.linkedSessionHandler = target.computeLinkedSessionHandler(true);
               this.activeLinkedSessionHandler = target.computeLinkedSessionHandler(false);
            }

            this.parentAccountHandler = target.resolveParentAccountHandler();
            target.handleTask();
            this.saveTask();
            VerifiedPasswordHashHasher.updateState(new File(this.dataFile, "debug.mode").exists());
            this.passwordHashBridge = new PasswordHashBridge(this.indirectSessionHandler, this.memClassLoader);
            if (!this.passwordHashBridge.hasState(this.indirectSessionHandler, target.findValues())) {
               this.currentEnabled = false;
               this.activeEnabled = true;
               this.parentAccountHandler.executeTask();
               return;
            }

            this.parentSettingsLookup = new ParentSettingsLookup(this.indirectSessionHandler);
            this.sendFile(this.indirectSessionHandler.resolveFile(), this.parentSettingsLookup.loadPrimaryPasswordHashVerifier().isState("debug"));
            this.tightConnectionContract = target.retrieveTightConnectionContract();

            try {
               Class.forName("org.apache.logging.log4j.core.filter.AbstractFilter");
               SettingsGate.as();
            } catch (ClassNotFoundException | NoClassDefFoundError result) {
               switch (this.indirectSessionHandler.findIncomingLoginGate().retrieveSilentProxyState()) {
                  case SILENT_PROXY_STATE:
                     Logger response = Bukkit.getServer().getLogger();
                     SettingsHandler source = new SettingsHandler(response.getFilter());
                     response.setFilter(source);
                     Logger.getLogger("Minecraft").setFilter(source);
                     break;
                  case ACTIVE_SILENTPROXYSTATE:
                     Logger output = ProxyServer.getInstance().getLogger();
                     SettingsHandler context = new SettingsHandler(output.getFilter());
                     output.setFilter(context);
               }
            }

            if (this.updateLookup == null) {
               this.updateLookup = new UpdateLookup(this.indirectSessionHandler, this.memClassLoader);
               new Thread(() -> {
                  synchronized (this) {
                     Throwable input = null;

                     try {
                        this.updateLookup.executeTask();
                     } catch (NullPointerException entry) {
                        StackTraceElement[] contextValue = entry.getStackTrace();
                        if (contextValue.length > 0 && "org.bukkit.plugin.java.JavaPluginLoader".equals(contextValue[0].getClassName())) {
                           this.primaryEnabled = true;
                        } else {
                           input = entry;
                        }
                     } catch (Throwable record) {
                        input = record;
                     } finally {
                        if (input != null) {
                           this.activeEnabled = true;
                           PasswordHashContainer.sendThrowable(input);
                           PasswordHashContainer.updateMessage("Unexpected error when connecting to api.nickuc.com (probably this is a bug!)");
                           PasswordHashContainer.updateMessage("");
                           PasswordHashContainer.updateMessage("Please report to our team:");
                           PasswordHashContainer.updateMessage(" Discord: §bwww.nickuc.com/discord");
                           PasswordHashContainer.updateMessage(" Email: §fsupport@nickuc.com");
                           PasswordHashContainer.updateMessage("");
                        }

                        this.countDownLatch.countDown();
                     }
                  }
               }, this.activeName + " Request API").start();
            }

            try {
               target.processTask();
            } catch (Throwable value) {
               PasswordHashContainer.sendThrowable(value);
               PasswordHashContainer.updateMessage("Unable to load plugin.");
               PasswordHashContainer.updateMessage("");
               PasswordHashContainer.updateMessage("Please report to our team:");
               PasswordHashContainer.updateMessage(" Discord: §bwww.nickuc.com/discord");
               PasswordHashContainer.updateMessage(" Email: §fsupport@nickuc.com");
               PasswordHashContainer.updateMessage("");
            }
         } catch (Exception request) {
            this.activeEnabled = true;
            PasswordHashContainer.sendThrowable(request);
            PasswordHashContainer.updateMessage("Unable to load plugin core.");
            PasswordHashContainer.updateMessage("");
            PasswordHashContainer.updateMessage("Please report to our team:");
            PasswordHashContainer.updateMessage(" Discord: §bwww.nickuc.com/discord");
            PasswordHashContainer.updateMessage(" Email: §fsupport@nickuc.com");
            PasswordHashContainer.updateMessage("");

            try {
               Thread.sleep(15000L);
            } catch (InterruptedException data) {
               PasswordHashContainer.sendThrowable(data);
            }

            if (this.incomingLoginGate.retrieveSilentProxyState() == SilentProxyState.SILENT_PROXY_STATE) {
               this.indirectSessionHandler.executeTask();
            }
         }
      }
   }

   public File fetchFile() {
      return this.dataFile;
   }

   private void sendFile(File target, boolean input) {
      PasswordHashLoader.updateFile(target);

      try {
         this.passwordHashLoader = new PasswordHashLoader("config.yml");
      } catch (Exception context) {
         throw new RuntimeException("Unable to load config.yml (nconfig)", context);
      }

      VerifiedPasswordHashHasher.updateState(new File(this.dataFile, "debug.mode").exists() || input);
   }

   public boolean getState() {
      return this.enabled;
   }

   public ParentDiscordNotifier createParentDiscordNotifier(int target) {
      this.count = target;
      return this;
   }

   public boolean fetchState() {
      return this.discordNotifier != null;
   }

   public void handleTask() {
      if (!this.activeEnabled) {
         if (this.currentEnabled) {
            throw new IllegalStateException("The plugin is already enabled!");
         }

         this.currentEnabled = true;
         CachedSessionHandler target = this.indirectSessionHandler.resolveCachedSessionHandler();
         if (target.verifyState(this.activeName)) {
            if (this.incomingLoginGate.retrieveSilentProxyState() == SilentProxyState.SILENT_PROXY_STATE) {
               this.indirectSessionHandler.executeTask();
            }
         } else {
            try {
               LiveLoginCheckpoint input = new LiveLoginCheckpoint();
               if (!this.pendingEnabled) {
                  this.processTask();
               }

               if (this.countDownLatch.getCount() != 0L && !this.countDownLatch.await(3L, TimeUnit.SECONDS)) {
                  PasswordHashContainer.performMessage("Awaiting remote response from api.nickuc.com...");
                  this.countDownLatch.await();
               }

               if (this.primaryEnabled) {
                  PasswordHashContainer.performMessage("Fetching remote response from api.nickuc.com... (class loader workaround)");
                  this.updateLookup.executeTask();
               }

               this.updateLookup.dispatchLinkedSessionHandler(this.linkedSessionHandler);
               target.sendTask();
               if (this.updateLookup.resolveReadyLoginGate() == null && this.updateLookup.loadLinkedPasswordHashVerifier().resolveState()) {
                  throw new IllegalStateException("No API session started!");
               }

               SecondaryLoginGate.performParentAccountHandler(this.parentAccountHandler);
               BungeeHandler.processIndirectSessionHandler(this.indirectSessionHandler, this.indirectSessionHandler.resolveFile().getParentFile());
               this.savePasswordHashCommand(new LocaleLookup(this.indirectSessionHandler));
               this.saveOutgoingAccountHandler(target.getOutgoingAccountHandler());
               this.saveOutgoingAccountHandler(target.loadOutgoingAccountHandler());
               PasswordHashContainer.processMessage("Successful plugin core start (took " + input.fetchMessage() + "s)");
               this.updateTask();

               try {
                  target.performTask();
               } catch (Throwable data) {
                  PasswordHashContainer.sendThrowable(data);
                  PasswordHashContainer.updateMessage("Unable to start plugin.");
                  PasswordHashContainer.updateMessage("");
                  PasswordHashContainer.updateMessage("Please report to our team:");
                  PasswordHashContainer.updateMessage(" Discord: §bwww.nickuc.com/discord");
                  PasswordHashContainer.updateMessage(" Email: §fsupport@nickuc.com");
                  PasswordHashContainer.updateMessage("");
               }

               this.updateLookup.retrieveLowLoginResolver().updateTask();
            } catch (Exception value) {
               this.activeEnabled = true;
               this.currentEnabled = false;
               PasswordHashContainer.sendThrowable(value);
               PasswordHashContainer.updateMessage("Unexpected error when starting plugin core (this is probably a bug!)");
               PasswordHashContainer.updateMessage("");
               PasswordHashContainer.updateMessage("Please report to our team:");
               PasswordHashContainer.updateMessage(" Discord: §bwww.nickuc.com/discord");
               PasswordHashContainer.updateMessage(" Email: §fsupport@nickuc.com");
               PasswordHashContainer.updateMessage("");

               try {
                  Thread.sleep(15000L);
               } catch (InterruptedException context) {
                  PasswordHashContainer.sendThrowable(context);
               }

               if (this.incomingLoginGate.retrieveSilentProxyState() == SilentProxyState.SILENT_PROXY_STATE) {
                  this.indirectSessionHandler.executeTask();
               }
            }
         }
      }
   }

   @Override
   public String toString() {
      return this.activeName
         + " v"
         + this.indirectSessionHandler.getMessage()
         + (this.updateLookup == null ? "" : " (build " + this.updateLookup.getMessage() + ")");
   }

   public ParentDiscordNotifier(String target, String input, Object output, IndirectSessionHandler<?> context) {
      this.activeName = target;
      this.name = input;
      this.indirectSessionHandler = context;
      this.object = output;
      ClassLoader data = this.getClass().getClassLoader();
      if (!(data instanceof MemClassLoader)) {
         throw new UnsupportedOperationException("Unsupported class loader!");
      }

      this.memClassLoader = (MemClassLoader)data;
      this.enabled = true;
      PendingPasswordHashHasher.processMessage(target, input);
   }

   public UpdateLookup retrieveUpdateLookup() {
      return this.updateLookup;
   }

   public void savePasswordHashCommand(PasswordHashCommand<?> target, PasswordHashCommand<?>... input) {
      IndirectSessionHandler output = this.fetchState() ? this.discordNotifier : this.indirectSessionHandler;
      target.handleNestedServerAdapter(output);

      for (PasswordHashCommand result : input) {
         result.handleNestedServerAdapter(output);
      }
   }

   public void processDiscordNotifier(DiscordNotifier target) {
      if (this.fetchState()) {
         throw new IllegalStateException("Core is already loaded!");
      }

      this.discordNotifier = target;
      target.handleIndirectSessionHandler(this.indirectSessionHandler);
   }
}
