package com.nickuc.login.storage.password;

import com.nickuc.login.account.MessageOption;
import com.nickuc.login.auth.login.DeadLoginFlow;
import com.nickuc.login.auth.login.LiveLoginCheckpoint;
import com.nickuc.login.auth.login.LoginBarrier;
import com.nickuc.login.auth.locale.OpenLocaleBarrier;
import com.nickuc.login.auth.login.PrimaryLoginHandler;
import com.nickuc.login.config.PasswordHashContainer;
import com.nickuc.login.model.LenientPremiumOption;
import com.nickuc.login.platform.sender.OutgoingSenderAdapter;
import com.nickuc.login.platform.listener.SharedListenerContract;
import com.nickuc.login.platform.proxy.SilentProxyState;
import com.nickuc.login.platform.command.StrictCommandHandler;
import com.nickuc.login.platform.server.VerifiedServerAdapter;
import com.nickuc.login.premium.IndirectPasswordResolver;
import com.nickuc.login.premium.SpawnLookup;
import com.nickuc.login.session.LimboRegistry;
import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import javax.annotation.Nullable;


public abstract class PasswordHashRepository {
   public long timestamp;
   private LiveLoginCheckpoint liveLoginCheckpoint;
   public long activeTimestamp;
   private static final Set<PasswordHashRepository> players = new HashSet<>();
   public final MessageOption messageOption;
   public LoginBarrier loginBarrier;
   private final boolean enabled;
   private static volatile Thread thread;
   private static double ratio = Double.longBitsToDouble(4636737291354636288L);
   public SharedListenerContract sharedListenerContract;
   public static final String name = "config.yml";
   public final PasswordStore passwordStore;
   public long pendingTimestamp;
   private StrictCommandHandler strictCommandHandler;

   public PasswordHashRepository(PasswordStore target, MessageOption input) {
      this(target, input, true);
   }

   public void executeMessage(String target, String input, String output) {
      PasswordHashContainer.performMessage(
         "[%s] Unsupported hashing algorithm! hash = %s,%splayer name = %s",
         this.messageOption.getName(),
         input,
         output != null ? "algo = " + output + ", " : "",
         target
      );
   }

   public boolean isAvailable() {
      return this.messageOption.resolveState() || this.retrieveFile().exists();
   }

   public void updateMessage(String target, String input, @Nullable String output, @Nullable UUID context, @Nullable UUID data, @Nullable Consumer<SpawnLookup> value) {
      this.updateMessage(target, input, output, context, data, true, value);
   }

   public void processMessage(String target) {
      PrimaryLoginHandler input = this.sharedListenerContract.buildPrimaryLoginHandler("SELECT COUNT(*) FROM `" + target + "`");

      try {
         ResultSet output = input.resolveObject();
         if (output.next()) {
            this.timestamp = output.getInt(1);
         }
      } catch (Throwable value) {
         if (input != null) {
            try {
               input.close();
            } catch (Throwable data) {
               value.addSuppressed(data);
            }
         }

         throw value;
      }

      if (input != null) {
         input.close();
      }
   }

   private void updateMessage(
      String target, String input, @Nullable String output, @Nullable UUID context, @Nullable UUID data, boolean value, @Nullable Consumer<SpawnLookup> result
   ) {
      if (target == null) {
         throw new IllegalArgumentException("Player name cannot be null!");
      }

      if (target.isEmpty()) {
         throw new IllegalArgumentException("Player name cannot be empty!");
      }

      if (value || result != null || input != null && !input.isEmpty()) {
         IndirectPasswordResolver request = this.passwordStore.findIndirectPasswordResolver();
         SpawnLookup response = request.computeSpawnLookup(target, data, null, value);
         if (response == null) {
            throw new RuntimeException("Unable to load the " + target + "'s account.");
         }

         if (!response.fetchState()) {
            if (response.getUniqueId() == null) {
               if (context == null) {
                  context = DeadLoginFlow.computeUniqueId(target);
               }

               response.handleUniqueId(context);
            }

            response.handleMessage(target, input, null, output, input != null && !MessageOption.checkState(this.messageOption));
            if (data != null) {
               response.processUniqueId(data);
            } else if (value) {
               response.performTask();
            } else {
               response.saveTask();
            }

            if (result != null) {
               result.accept(response);
            }

            if (request.checkState(this.loginBarrier, response)) {
               this.activeTimestamp++;
            }
         }
      } else {
         throw new IllegalArgumentException("Password cannot be null or empty!");
      }
   }

   public void saveOutgoingSenderAdapter(OutgoingSenderAdapter target, boolean input) {
      LenientPremiumOption output = target instanceof VerifiedServerAdapter
         ? this.passwordStore.loadLimboRegistry().loadLimboCoordinator((VerifiedServerAdapter)target).fetchLenientPremiumOption()
         : CachedSettingsGateway.loadLenientPremiumOption();
      int context = output != LenientPremiumOption.LENIENT_PREMIUM_OPTION && output != LenientPremiumOption.INCOMING_LENIENTPREMIUMOPTION ? 0 : 1;
      if (thread != null) {
         if (target != null) {
            CachedSettingsGateway.handleOutgoingSenderAdapter(
               target,
               context != 0
                  ? "§cDesculpe, mas já existe um processo de conversão em andamento."
                  : "§cSorry, but there is already a conversion process in progress."
            );
         }
      } else if (!this.isAvailable()) {
         if (target != null) {
            CachedSettingsGateway.handleOutgoingSenderAdapter(
               target, context != 0 ? "§cOs arquivos para a conversão não foram encontrados." : "§cThe files for the conversion have not been found."
            );
         }
      } else if (MessageOption.handleMessage(this.messageOption) != null
         && this.messageOption != MessageOption.PRIMARY_MESSAGEOPTION
         && this.passwordStore.b().canState(MessageOption.handleMessage(this.messageOption))) {
         if (target != null) {
            CachedSettingsGateway.handleOutgoingSenderAdapter(
               target,
               context != 0
                  ? "§cPor favor, remova o plugin " + MessageOption.handleMessage(this.messageOption) + " antes de iniciar a conversão."
                  : "§cPlease remove the " + MessageOption.handleMessage(this.messageOption) + " plugin before starting the conversion."
            );
         }
      } else {
         try {
            SharedListenerContract data = this.passwordStore.fetchLocalSettingsRepository().loadSharedListenerContract();
            this.loginBarrier = new LoginBarrier(data, 64);
         } catch (SQLException request) {
            PasswordHashContainer.handleMessage("Unable to init fast database insert", request);
            if (target != null) {
               CachedSettingsGateway.handleOutgoingSenderAdapter(
                  target,
                  context != 0 ? "§cOps, parece que algum erro ocorreu durante a conversão." : "§cOops, it looks like an error occurred during the conversion."
               );
            }

            return;
         }

         this.liveLoginCheckpoint = new LiveLoginCheckpoint();
         if (context != 0) {
            PasswordHashContainer.performMessage("Inicializando a conversão do " + this.messageOption.getName() + "...");
            PasswordHashContainer.performMessage("Por favor, não desligue seu servidor.");
         } else {
            PasswordHashContainer.performMessage("Initializing the " + this.messageOption.getName() + " conversion");
            PasswordHashContainer.performMessage("Please do not shut down your server.");
         }

         thread = new Thread(
            () -> {
               try {
                  BackupDao.createFile(this.passwordStore, null, context);
                  this.handleOutgoingSenderAdapter(target);
               } catch (Exception payload) {
                  PasswordHashContainer.handleMessage("Conversion error detected, converter " + this.messageOption.getName(), payload);
                  if (target != null) {
                     CachedSettingsGateway.handleOutgoingSenderAdapter(
                        target,
                        context ? "§cOps, parece que algum erro ocorreu durante a conversão." : "§cOops, it looks like an error occurred during the conversion."
                     );
                  }
               } finally {
                  if (this.sharedListenerContract != null) {
                     try {
                        this.sharedListenerContract.executeTask();
                     } catch (SQLException content) {
                        PasswordHashContainer.handleMessage("Unable to close " + this.messageOption.getName() + " database after conversion", content);
                     }
                  }

                  if (this.loginBarrier != null) {
                     try {
                        this.loginBarrier.executeTask();
                     } catch (SQLException element) {
                        PasswordHashContainer.handleMessage("Unable to close nLogin database after conversion", element);
                     }
                  }

                  thread = null;
                  this.strictCommandHandler.performTask();
               }
            },
            "nLogin " + this.messageOption.getName() + " Converter"
         );
         thread.start();
         String response = "§6%s §3➜ §6%s §3▪ §f%s §7(%s§7) §3▪ §f%s users.";
         this.strictCommandHandler = this.passwordStore
            .processLinkedSessionHandler(true)
            .computeStrictCommandHandler(
               () -> {
                  if (thread != null && !thread.isInterrupted() && this.timestamp != 0L) {
                     double targetValue = this.pendingTimestamp * ratio / this.timestamp;
                     this.handleMessage(
                        String.format(
                           "§6%s §3➜ §6%s §3▪ §f%s §7(%s§7) §3▪ §f%s users.",
                           MessageOption.resolveMessage(this.messageOption),
                           MessageOption.buildMessage(this.messageOption),
                           OpenLocaleBarrier.resolveMessage(targetValue, 2) + '%',
                           OpenLocaleBarrier.resolveMessage(this.pendingTimestamp, this.timestamp, 25, "▌"),
                           OpenLocaleBarrier.resolveMessage(this.pendingTimestamp)
                        )
                     );
                  }
               },
               1000L,
               100L
            );
         if (input) {
            try {
               thread.join();
            } catch (InterruptedException result) {
               PasswordHashContainer.sendThrowable(result);
            }
         }
      }
   }

   public PasswordHashRepository(PasswordStore target, MessageOption input, boolean output) {
      this.passwordStore = target;
      this.messageOption = input;
      this.enabled = output;
      players.add(this);
   }

   public void updateMessage(String target, String input, @Nullable String output, @Nullable UUID context) {
      this.executeMessage(target, input, output, context, (Consumer<SpawnLookup>)null);
   }

   public static void performTask() {
      if (thread != null) {
         thread.interrupt();
      }
   }

   public void sendOutgoingSenderAdapter(OutgoingSenderAdapter target) {
      String input = OpenLocaleBarrier.resolveMessage(this.activeTimestamp);
      String output = this.liveLoginCheckpoint.fetchMessage();
      if (CachedSettingsGateway.loadState()) {
         PasswordHashContainer.processMessage("§b[Conversão] Os dados do " + this.messageOption.getName() + " foram convertidos com sucesso.");
         PasswordHashContainer.processMessage(
            (
                  this.activeTimestamp == 0L
                     ? "§cNenhum usuário foi convertido"
                     : (this.activeTimestamp == 1L ? "§aUm usuário foi convertido" : "§a" + input + " usuários foram convertidos.")
               )
               + " ~ "
               + output
               + "s"
         );
         this.handleMessage(
            "§6"
               + MessageOption.resolveMessage(this.messageOption)
               + " §3➜ §6"
               + MessageOption.buildMessage(this.messageOption)
               + " §3▪ §aMigração finalizada. §3▪ §f"
               + input
               + " §7"
               + (this.activeTimestamp == 1L ? "usuário foi convertido" : "usuários foram convertidos")
               + "."
         );
      } else {
         PasswordHashContainer.processMessage("§b" + this.messageOption.getName() + " data has been successfully converted.");
         PasswordHashContainer.processMessage(
            (
                  this.activeTimestamp == 0L
                     ? "§cNo users have been converted"
                     : (this.activeTimestamp == 1L ? "§aA user has been converted" : "§a" + input + " users have been converted.")
               )
               + " ~ "
               + output
               + "s"
         );
         this.handleMessage(
            "§6"
               + MessageOption.resolveMessage(this.messageOption)
               + " §3➜ §6"
               + MessageOption.buildMessage(this.messageOption)
               + " §3▪ §aConversion finished. §3▪ §f"
               + input
               + " §7"
               + (this.activeTimestamp == 1L ? "user has been" : "users have been")
               + " converted."
         );
      }
   }

   public static void executePasswordStore(PasswordStore instance) {
      for (MessageOption context : MessageOption.values()) {
         try {
            Constructor data = MessageOption.resolveClass(context).getConstructor(PasswordStore.class);
            data.newInstance(instance);
         } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException value) {
            PasswordHashContainer.handleMessage("Failed to instantiate class for " + context.getName() + " converter", value);
         }
      }
   }

   private void handleMessage(String target) {
      if (SpawnState.PENDING_LOCAL_SPAWNSTATE.ar()) {
         LimboRegistry input = this.passwordStore.loadLimboRegistry();
         this.passwordStore
            .b()
            .fetchCollection()
            .stream()
            .filter(targetValue -> input.canState(targetValue) && targetValue.i("nlogin.admin"))
            .forEach(targetValue -> targetValue.handleMessage(target));
      }
   }

   public boolean findState() {
      return this.enabled;
   }

   public void executeOutgoingSenderAdapter(OutgoingSenderAdapter target) {
      this.saveOutgoingSenderAdapter(target, false);
   }

   public abstract void handleOutgoingSenderAdapter(OutgoingSenderAdapter target);

   public void executeMessage(String target, String input, @Nullable String output, @Nullable UUID context, @Nullable UUID data) {
      this.updateMessage(target, input, output, context, data, null);
   }

   public File retrieveFile() {
      if (this.passwordStore.findIncomingLoginGate().retrieveSilentProxyState() == SilentProxyState.PENDING_SILENTPROXYSTATE) {
         File target = new File(this.passwordStore.resolveFile().getParentFile(), MessageOption.handleMessage(this.messageOption).toLowerCase(Locale.ENGLISH));
         if (target.exists()) {
            return target;
         }
      }

      return new File(this.passwordStore.resolveFile().getParentFile(), MessageOption.handleMessage(this.messageOption));
   }

   public void executeMessage(String target, String input, @Nullable String output, @Nullable UUID context, @Nullable Consumer<SpawnLookup> data) {
      this.updateMessage(target, input, output, context, null, false, data);
   }
}
