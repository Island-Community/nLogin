package com.nickuc.login.storage.password;

import com.nickuc.login.api.nLoginAPIHolder;
import com.nickuc.login.api.enums.event.EventEnum;
import com.nickuc.login.api.event.internal.EventPlayer;
import com.nickuc.login.api.event.internal.LockableEvent;
import com.nickuc.login.auth.login.DeadLoginFlow;
import com.nickuc.login.auth.locale.LocalLocaleFlow;
import com.nickuc.login.auth.locale.LocaleFlow;
import com.nickuc.login.auth.login.SafeLoginBarrier;
import com.nickuc.login.command.BungeeHandler;
import com.nickuc.login.config.PasswordHashContainer;
import com.nickuc.login.discord.DiscordNotifier;
import org.json.JSONArray;
import org.json.JSONObject;
import com.nickuc.login.platform.account.InternalAccountHandler;
import com.nickuc.login.platform.connection.LenientConnectionContract;
import com.nickuc.login.platform.listener.ListenerContract;
import com.nickuc.login.platform.sender.SecondarySenderAdapter;
import com.nickuc.login.platform.server.ServerAdapter;
import com.nickuc.login.platform.proxy.SilentProxyState;
import com.nickuc.login.platform.server.VerifiedServerAdapter;
import com.nickuc.login.premium.DiscordVerifier;
import com.nickuc.login.premium.FloodgateResolver;
import com.nickuc.login.premium.IndirectPasswordHashVerifier;
import com.nickuc.login.premium.IndirectPasswordResolver;
import com.nickuc.login.premium.LinkedPasswordHashVerifier;
import com.nickuc.login.premium.ParentPasswordHashVerifier;
import com.nickuc.login.premium.ParentSettingsLookup;
import com.nickuc.login.premium.Pbkdf2Linker;
import com.nickuc.login.premium.SettingsLinker;
import com.nickuc.login.protocol.RootMessageHandler;
import com.nickuc.login.redis.PasswordLink;
import com.nickuc.login.security.hashing.PrimaryPasswordHashVerifier;
import com.nickuc.login.session.LimboRegistry;
import com.nickuc.login.spawn.LoginKind;
import com.nickuc.login.spawn.VerifiedNoticeKind;
import com.nickuc.login.updater.AuthenticatedNoticeKind;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.TimeUnit;


public class PasswordStore extends DiscordNotifier {
   private LimboRegistry limboRegistry;
   private static PasswordStore passwordStore;
   private PasswordLink passwordLink;
   private IndirectPasswordResolver indirectPasswordResolver;
   private FloodgateResolver floodgateResolver;
   private LocalSettingsRepository localSettingsRepository;
   private boolean enabled = true;
   private RootMessageHandler rootMessageHandler;
   private InternalAccountHandler internalAccountHandler;
   private final ServerAdapter serverAdapter;
   private final boolean activeEnabled;
   private final ListenerContract listenerContract;
   private IndirectPasswordHashVerifier indirectPasswordHashVerifier;
   private DiscordVerifier discordVerifier;
   private SettingsLinker settingsLinker;

   public PasswordStore(ServerAdapter target, ListenerContract input, boolean output) {
      this.serverAdapter = target;
      this.listenerContract = input;
      this.activeEnabled = output;
   }

   public boolean getState() {
      return this.activeEnabled;
   }

   public ServerAdapter findServerAdapter() {
      return this.serverAdapter;
   }

   public SettingsLinker findSettingsLinker() {
      return this.settingsLinker;
   }

   public RootMessageHandler resolveRootMessageHandler() {
      return this.rootMessageHandler;
   }

   private RootMessageHandler findRootMessageHandler() {
      VerifiedNoticeKind[] target;
      switch (this.findIncomingLoginGate().retrieveSilentProxyState()) {
         case PENDING_SILENTPROXYSTATE:
            target = new VerifiedNoticeKind[]{VerifiedNoticeKind.SHARED_VERIFIEDNOTICEKIND};
            break;
         case SILENT_PROXY_STATE:
            target = new VerifiedNoticeKind[]{
               VerifiedNoticeKind.AUTHENTICATED_VERIFIEDNOTICEKIND,
               VerifiedNoticeKind.INTERNAL_VERIFIEDNOTICEKIND,
               VerifiedNoticeKind.UPSTREAM_VERIFIEDNOTICEKIND
            };
            break;
         case ACTIVE_SILENTPROXYSTATE:
            target = new VerifiedNoticeKind[]{
               VerifiedNoticeKind.PRIVATE_VERIFIEDNOTICEKIND, VerifiedNoticeKind.INTERNAL_VERIFIEDNOTICEKIND, VerifiedNoticeKind.UPSTREAM_VERIFIEDNOTICEKIND
            };
            break;
         default:
            throw new IllegalStateException("Platform " + this.findIncomingLoginGate().retrieveSilentProxyState() + " not implemented!");
      }

      PasswordHashContainer.processMessage("Loading " + target[0].fetchMessage() + " " + target[0].getVersion() + "...");
      if (!this.indirectSessionHandler.fetchPasswordHashBridge().verifyState(target)) {
         throw new IllegalStateException("Unable to load " + Arrays.toString(target) + " dependencies!");
      } else {
         return new RootMessageHandler(this);
      }
   }

   @Override
   public void performTask() {
      if (!this.activeEnabled) {
         this.processLinkedSessionHandler(this.indirectSessionHandler.findIncomingLoginGate().retrieveSilentProxyState() != SilentProxyState.SILENT_PROXY_STATE)
            .loadStrictCommandHandler(() -> {
               if (this.indirectSessionHandler.findIncomingLoginGate().retrieveSilentProxyState() != SilentProxyState.SILENT_PROXY_STATE) {
                  try {
                     Thread.sleep(30000L);
                  } catch (InterruptedException input) {
                     return;
                  }
               }

               if (!this.getStateForState()) {
                  if (this.localSettingsRepository.loadState()) {
                     if (CachedSettingsGateway.loadState()) {
                        PasswordHashContainer.handleMessage("");
                        PasswordHashContainer.handleMessage(" §aMuito obrigado por escolher o nLogin! :)");
                        PasswordHashContainer.handleMessage("");
                        PasswordHashContainer.handleMessage(" §6Para sua segurança, leia a mensagem abaixo:");
                        PasswordHashContainer.handleMessage("  §cNunca baixe o nLogin de fontes desconhecidas.");
                        PasswordHashContainer.handleMessage("  §cUma versão modificada pode conter softwares maliciosos.");
                        PasswordHashContainer.handleMessage("");
                        PasswordHashContainer.handleMessage(" §6Sites confiáveis:");
                        PasswordHashContainer.handleMessage("  §7Website: §fnickuc.com");
                        PasswordHashContainer.handleMessage("  §7GitHub: §fgithub.com/nickuc-com");
                        PasswordHashContainer.handleMessage("  §7Instalador do OpeNLogin (do §fSpigot §7ou §fGitHub§7)");
                        PasswordHashContainer.handleMessage("");
                        PasswordHashContainer.handleMessage(" §6Suporte oficial:");
                        PasswordHashContainer.handleMessage("  §7Documentação: §fdocs.nickuc.com/nlogin/");
                        PasswordHashContainer.handleMessage("  §7Discord: §fnickuc.com/discord");
                        PasswordHashContainer.handleMessage("  §7Email: §fsupport@nickuc.com");
                        PasswordHashContainer.handleMessage("");
                     } else {
                        PasswordHashContainer.handleMessage("");
                        PasswordHashContainer.handleMessage(" §aThank you so much for choosing nLogin! :)");
                        PasswordHashContainer.handleMessage("");
                        PasswordHashContainer.handleMessage(" §6For your safety, please read the message below:");
                        PasswordHashContainer.handleMessage("  §cNever download nLogin from unknown sources.");
                        PasswordHashContainer.handleMessage("  §cA modified version may contain malicious software.");
                        PasswordHashContainer.handleMessage("");
                        PasswordHashContainer.handleMessage(" §6Trusted sources:");
                        PasswordHashContainer.handleMessage("  §7Website: §fnickuc.com");
                        PasswordHashContainer.handleMessage("  §7GitHub: §fgithub.com/nickuc-com");
                        PasswordHashContainer.handleMessage("  §7OpeNLogin installer (from §fSpigot §7or §fGitHub§7)");
                        PasswordHashContainer.handleMessage("");
                        PasswordHashContainer.handleMessage(" §6Official support:");
                        PasswordHashContainer.handleMessage("  §7Documentation: §fdocs.nickuc.com/nlogin/");
                        PasswordHashContainer.handleMessage("  §7Discord: §fnickuc.com/discord");
                        PasswordHashContainer.handleMessage("  §7Email: §fsupport@nickuc.com");
                        if (CachedSettingsGateway.resolveState()) {
                           PasswordHashContainer.handleMessage("  §7VK community: §fnickuc.com/vk");
                        }

                        PasswordHashContainer.handleMessage("");
                     }
                  }
               }
            }, 5L, TimeUnit.SECONDS);
      }
   }

   public IndirectPasswordResolver findIndirectPasswordResolver() {
      return this.indirectPasswordResolver;
   }

   public boolean findState() {
      return this.enabled;
   }

   public static PasswordStore resolvePasswordStore() {
      return passwordStore;
   }

   public LimboRegistry loadLimboRegistry() {
      return this.limboRegistry;
   }

   public boolean callEvent(Object target) {
      return target instanceof LockableEvent ? ((LockableEvent)target).callEvt() : this.listenerContract.callEvent(target);
   }

   public FloodgateResolver getFloodgateResolver() {
      return this.floodgateResolver;
   }

   public PasswordLink findPasswordLink() {
      return this.passwordLink;
   }

   public boolean loadState() {
      return this.findObject() instanceof SecondarySenderAdapter;
   }

   public IndirectPasswordHashVerifier findIndirectPasswordHashVerifier() {
      return this.indirectPasswordHashVerifier;
   }

   public InternalAccountHandler loadInternalAccountHandler() {
      return this.internalAccountHandler;
   }

   private boolean getStateForState() {
      String target = CachedSettingsGateway.loadState() ? "pt" : "en";
      LinkedPasswordHashVerifier input = this.indirectSessionHandler.retrieveUpdateLookup().loadLinkedPasswordHashVerifier();
      String output = input.handleObject("notify." + target);
      if (output != null && !output.isEmpty()) {
         if (!input.buildObject("notify." + target + ".console", true)) {
            return false;
         }

         String context = input.buildObject("notify." + target, "");
         String[] data = LocalLocaleFlow.loadMessage(SafeLoginBarrier.computeMessage(context).replace("\\n", "\n")).split("\n");
         Arrays.stream(data).forEach(instance -> PasswordHashContainer.handleMessage(instance));
         return true;
      } else {
         return false;
      }
   }

   public void performTaskForValue() {
      passwordStore = this;

      try {
         nLoginAPIHolder.init(this.serverAdapter.loadNLoginAPI());
         EventEnum.loadEvents(
            this.listenerContract.getPlayerClass(),
            this.findIncomingLoginGate().retrieveSilentProxyState().name().toLowerCase(Locale.ENGLISH),
            this.findIncomingLoginGate().retrieveSilentProxyState().loadState()
         );
         LoginKind.load();
         if (!this.activeEnabled) {
            this.saveTask();
         }

         AuthenticatedNoticeKind target = this.a().loadLinkedPasswordHashVerifier().fetchAuthenticatedNoticeKind();
         String input = "§9";
         PasswordHashContainer.handleMessage(input + "          __             _       ");
         PasswordHashContainer.handleMessage(input + "  _ __   / /  ___   __ _(_)_ __  ");
         PasswordHashContainer.handleMessage(input + " | '_ \\ / /  / _ \\ / _` | | '_ \\ ");
         PasswordHashContainer.handleMessage(input + " | | | / /__| (_) | (_| | | | | |");
         PasswordHashContainer.handleMessage(input + " |_| |_\\____/\\___/ \\__, |_|_| |_|");
         PasswordHashContainer.handleMessage(input + "                  |___/            ");
         PasswordHashContainer.handleMessage(
            input + " By: www.nickuc.com - V " + this.getMessage() + (target == AuthenticatedNoticeKind.PENDING_AUTHENTICATEDNOTICEKIND ? " §3" + target : "")
         );
         PasswordHashContainer.handleMessage(input + " ");
         this.dispatchTask();
         this.limboRegistry = new LimboRegistry(this);
         this.floodgateResolver = this.serverAdapter.fetchFloodgateResolver();
         this.indirectPasswordResolver = new IndirectPasswordResolver(this);
         this.settingsLinker = this.serverAdapter.resolveSettingsLinker();
         this.internalAccountHandler = this.serverAdapter.getInternalAccountHandler();
         if (!this.activeEnabled) {
            this.indirectPasswordHashVerifier = new IndirectPasswordHashVerifier(this);
            this.discordVerifier = new DiscordVerifier();
         }

         if (!this.serverAdapter.retrieveState()) {
            if (CachedSettingsGateway.loadState()) {
               PasswordHashContainer.updateMessage("");
               PasswordHashContainer.updateMessage("Não foi possível carregar a configuração do nLogin.");
               PasswordHashContainer.updateMessage("Você pode tentar redefini-la apagando o arquivo \"nLogin/config.yml\".");
               PasswordHashContainer.updateMessage("");
               PasswordHashContainer.updateMessage("Por segurança, o servidor será desligado nos próximos 30 segundos.");
               PasswordHashContainer.updateMessage("");
            } else {
               PasswordHashContainer.updateMessage("");
               PasswordHashContainer.updateMessage("The nLogin configuration could not be loaded.");
               PasswordHashContainer.updateMessage("You can try to reset it by deleting the \"nLogin/config.yml\" file.");
               PasswordHashContainer.updateMessage("");
               PasswordHashContainer.updateMessage("For safety, the server will shut down in the next 30 seconds.");
               PasswordHashContainer.updateMessage("");
            }

            try {
               Thread.sleep(30000L);
            } catch (InterruptedException request) {
               PasswordHashContainer.sendThrowable(request);
            }

            this.indirectSessionHandler.retrieveParentAccountHandler().executeTask();
            return;
         }

         if (!this.activeEnabled) {
            try {
               if (!ParentPasswordHashVerifier.parentPasswordHashVerifier.b(this)) {
                  LenientConnectionContract.handleTask();
               }

               this.localSettingsRepository = new LocalSettingsRepository(this, Pbkdf2Linker.getDirectNoticeCatalog());
               this.localSettingsRepository.sendTask();
               LenientConnectionContract.processPasswordStore(this);
               this.localSettingsRepository.updateTask();
               if (this.loadState()) {
                  SecondarySenderAdapter output = this.findObject();
                  this.localSettingsRepository.executePasswordHashAdapter(output.getPasswordHashAdapter());
               }
            } catch (Exception source) {
               PasswordHashContainer.sendThrowable(source);
               if (CachedSettingsGateway.loadState()) {
                  PasswordHashContainer.updateMessage("");
                  PasswordHashContainer.updateMessage("Não foi possível inicializar o banco de dados.");
                  PasswordHashContainer.updateMessage("Por favor, certifique de estar inserindo as credenciais de forma correta.");
                  PasswordHashContainer.updateMessage("");
                  PasswordHashContainer.updateMessage("Tutorial para instalação do MySQL: §bdocs.nickuc.com/nlogin/mysql");
                  PasswordHashContainer.updateMessage("Servidor irá desligar em 30 segundos");
                  PasswordHashContainer.updateMessage("");
               } else {
                  PasswordHashContainer.updateMessage("");
                  PasswordHashContainer.updateMessage("The database could not be initialized.");
                  PasswordHashContainer.updateMessage("Please make sure you are entering your credentials correctly.");
                  PasswordHashContainer.updateMessage("");
                  PasswordHashContainer.updateMessage("Tutorial for installing MySQL: §bdocs.nickuc.com/nlogin/mysql");
                  PasswordHashContainer.updateMessage("Server will shut down in 30 seconds");
                  PasswordHashContainer.updateMessage("");
               }

               try {
                  Thread.sleep(30000L);
               } catch (InterruptedException result) {
                  PasswordHashContainer.sendThrowable(result);
               }

               this.indirectSessionHandler.retrieveParentAccountHandler().executeTask();
               return;
            }

            try {
               this.passwordLink = PasswordLink.loadPasswordLink(this);
            } catch (Exception response) {
               PasswordHashContainer.sendThrowable(response);
               if (CachedSettingsGateway.loadState()) {
                  PasswordHashContainer.updateMessage("");
                  PasswordHashContainer.updateMessage("Não foi possível inicializar o Redis.");
                  PasswordHashContainer.updateMessage("Por favor, certifique de estar inserindo as credenciais de forma correta.");
                  PasswordHashContainer.updateMessage("");
                  PasswordHashContainer.updateMessage("Server will shut down in 30 seconds");
                  PasswordHashContainer.updateMessage("");
               } else {
                  PasswordHashContainer.updateMessage("");
                  PasswordHashContainer.updateMessage("The Redis could not be initialized.");
                  PasswordHashContainer.updateMessage("Please make sure you are entering your credentials correctly.");
                  PasswordHashContainer.updateMessage("");
                  PasswordHashContainer.updateMessage("Server will shut down in 30 seconds");
                  PasswordHashContainer.updateMessage("");
               }

               try {
                  Thread.sleep(30000L);
               } catch (InterruptedException value) {
                  PasswordHashContainer.sendThrowable(value);
               }

               this.indirectSessionHandler.retrieveParentAccountHandler().executeTask();
               return;
            }
         }

         this.serverAdapter.performTask();
         this.rootMessageHandler = this.findRootMessageHandler();
         if (this.internalAccountHandler != null) {
            this.internalAccountHandler.executeTask();
            if (!this.indirectSessionHandler.resolveState()) {
               return;
            }
         }

         this.performTask();
         this.enabled = false;
      } catch (Throwable entry) {
         if (entry instanceof IllegalStateException
            && entry.getMessage() != null
            && entry.getMessage().startsWith("Incompatibility detected with PacketEvents:")) {
            return;
         }

         PasswordHashContainer.sendThrowable(entry);
         PasswordHashContainer.updateMessage("");
         PasswordHashContainer.updateMessage("A critical error was detected and nLogin could not be started.");
         PasswordHashContainer.updateMessage("For safety, the server will shut down in the next 30 seconds.");
         PasswordHashContainer.updateMessage("");
         PasswordHashContainer.updateMessage("Please contact our team:");
         PasswordHashContainer.updateMessage(" Discord: §bwww.nickuc.com/discord");
         PasswordHashContainer.updateMessage(" Email: §fsupport@nickuc.com");
         if (CachedSettingsGateway.resolveState()) {
            PasswordHashContainer.updateMessage(" VK: §fwww.nickuc.com/vk");
         }

         PasswordHashContainer.updateMessage("");

         try {
            Thread.sleep(30000L);
         } catch (InterruptedException data) {
            PasswordHashContainer.sendThrowable(data);
         }

         this.b().executeTask();
      }
   }

   private void saveTask() {
      LocaleFlow target = new LocaleFlow();
      File input = new File(this.resolveFile(), "logs");
      input.mkdirs();

      try {
         BungeeHandler.handleIndirectSessionHandler(this, input);
      } catch (IOException request) {
         PasswordHashContainer.handleMessage("Unable to detect and clean old nLogin logs.", request);
      }

      String output = target.findMessage() + "-" + target.resolveMessage() + "-" + target.retrieveMessage();
      File context = new File(input, output + "-1.log");
      int data = 1;

      while (context.exists()) {
         context = new File(input, output + "-" + ++data + ".log");
      }

      try {
         PasswordHashContainer.handleFile(context);
      } catch (IOException result) {
         PasswordHashContainer.handleMessage("Unable to configure logs in " + context.getAbsolutePath() + " path", result);
      }
   }

   public <T> T loadObject(EventEnum target, Object... input) {
      VerifiedServerAdapter output = null;

      for (int context = 0; context < input.length; context++) {
         Object data = input[context];
         if (data instanceof VerifiedServerAdapter) {
            output = (VerifiedServerAdapter)data;
            input[context] = output.findObject();
         }
      }

      if (target.isForwardEvent() && this.loadState() && output != null) {
         JSONArray element = new JSONArray();
         Class[] content = target.getRewrittenClasses();

         for (int value = 0; value < input.length; value++) {
            JSONObject result = new JSONObject();
            Object request = input[value];
            if (request != null) {
               Class response = content[value];
               if (EventPlayer.class.isAssignableFrom(response)) {
                  request = output;
               } else if (String[].class.isAssignableFrom(response)) {
                  JSONArray source = new JSONArray();
                  String[] entry = (String[])request;

                  for (int record = 0; record < entry.length; record++) {
                     source.put(record, entry[record]);
                  }

                  request = source;
               } else if (UUID.class.isAssignableFrom(response)) {
                  UUID holder = (UUID)request;
                  request = DeadLoginFlow.processMessage(holder);
               } else if (Enum.class.isAssignableFrom(response)) {
                  Enum reference = (Enum)request;
                  request = reference.name();
               }

               result.put("value", request);
            }

            element.put(value, result);
         }

         SecondarySenderAdapter payload = this.findObject();
         payload.getPasswordHashAdapter().updateVerifiedServerAdapter(output, 7, "event", target.name(), "arguments", element);
      }

      try {
         return (T)target.createEvent(input);
      } catch (InvocationTargetException | InstantiationException | IllegalAccessException item) {
         throw new RuntimeException(item);
      }
   }

   public LocalSettingsRepository fetchLocalSettingsRepository() {
      return this.localSettingsRepository;
   }

   @Override
   public void dispatchTask() {
      ParentSettingsLookup target = this.a();
      PrimaryPasswordHashVerifier input = target.loadPrimaryPasswordHashVerifier();
      byte[] output = input.createPayload("autoUpdate");
      if (output != null && output.length != 1) {
         input.createPrimaryPasswordHashVerifier("autoUpdate", Boolean.parseBoolean(input.loadMessage("autoUpdate")));
         input.sendTask();
      }

      if (!this.activeEnabled && !input.hasState("autoUpdate", true)) {
         target.findCachedLoginBarrier().dispatchState(false);
      }
   }

   public DiscordVerifier resolveDiscordVerifier() {
      return this.discordVerifier;
   }

   public ListenerContract loadListenerContract() {
      return this.listenerContract;
   }

   public boolean verifyState(EventEnum target, Object... input) {
      return this.callEvent(this.loadObject(target, input));
   }

   @Override
   public void updateTask() {
      try {
         this.serverAdapter.executeTask();
         if (this.limboRegistry != null) {
            this.limboRegistry.executeTask();
         }

         if (this.discordVerifier != null) {
            this.discordVerifier.savePasswordStore(this);
         }

         if (this.internalAccountHandler != null) {
            this.internalAccountHandler.processTask();
         }

         if (this.localSettingsRepository != null) {
            this.localSettingsRepository.executeTask();
         }

         if (this.passwordLink != null) {
            this.passwordLink.executeTask();
         }
      } catch (Throwable input) {
         PasswordHashContainer.handleMessage("nLogin shutdown error", input);
      }
   }
}
