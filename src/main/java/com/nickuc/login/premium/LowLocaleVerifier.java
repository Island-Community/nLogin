package com.nickuc.login.premium;

import com.nickuc.login.account.InternalLoginOption;
import com.nickuc.login.account.LoudProxyState;
import com.nickuc.login.auth.password.PasswordCheckpoint;
import com.nickuc.login.command.LocaleAction;
import com.nickuc.login.command.admin.AccountCommand;
import com.nickuc.login.command.admin.PendingSpawnCompletionCommand;
import com.nickuc.login.command.auth.ParentPasswordHandler;
import com.nickuc.login.command.auth.PasswordAction;
import com.nickuc.login.command.auth.PasswordCommand;
import com.nickuc.login.command.moderation.CachedRootPasswordCommand;
import com.nickuc.login.command.spawn.PurgeUnregisterCommand;
import com.nickuc.login.config.PasswordHashContainer;
import com.nickuc.login.config.SettingsContainer;
import com.nickuc.login.discord.DiscordLinker;
import com.nickuc.login.discord.LoudDiscordNotifier;
import com.nickuc.login.discord.StrictDiscordNotifier;
import com.nickuc.login.notification.NoticeRenderer;
import com.nickuc.login.platform.sender.OutgoingSenderAdapter;
import com.nickuc.login.platform.proxy.QuickProxyState;
import com.nickuc.login.platform.account.SecondaryAccountHandler;
import com.nickuc.login.platform.server.VerifiedServerAdapter;
import com.nickuc.login.session.LimboCoordinator;
import com.nickuc.login.session.LimboRegistry;
import com.nickuc.login.storage.backup.BackupRepository;
import com.nickuc.login.storage.settings.CachedSettingsGateway;
import com.nickuc.login.storage.locale.LocaleCollection;
import com.nickuc.login.storage.locale.LocaleGateway;
import com.nickuc.login.storage.locale.LocaleSource;
import com.nickuc.login.storage.login.LoginSource;
import com.nickuc.login.storage.password.PasswordHashStore;
import com.nickuc.login.storage.password.PasswordStore;
import com.nickuc.login.storage.locale.PendingLocaleCollection;
import com.nickuc.login.storage.settings.SettingsDao;
import com.nickuc.login.storage.spawn.SpawnState;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class LowLocaleVerifier extends LocaleCollection {
   private List<LoginSource> entries = Collections.emptyList();

   private void sendOutgoingSenderAdapter(OutgoingSenderAdapter target) {
      if (!target.hasState("nlogin.command.nlogin") && !target.hasState("nlogin.admin")) {
         CachedSettingsGateway.executeOutgoingSenderAdapter(target, LoudProxyState.ROOT_LOUDPROXYSTATE);
         if (target instanceof VerifiedServerAdapter) {
            CachedSettingsGateway.processOutgoingSenderAdapter(target, QuickProxyState.ACTIVE_PRIMARY_QUICKPROXYSTATE);
         }
      } else {
         CachedSettingsGateway.handleOutgoingSenderAdapter(target, "");
         StringBuilder switchState = new StringBuilder()
            .append(" §eRunning §f")
            .append(this.indirectSessionHandler.retrieveMessage())
            .append(" v")
            .append(this.indirectSessionHandler.getMessage())
            .append(" §b")
            .append(this.indirectSessionHandler.a().getMessage());
         this.indirectSessionHandler.a();
         CachedSettingsGateway.handleOutgoingSenderAdapter(target, switchState.append(false ? " §c(compromised)" : "").toString());
         if (this.indirectSessionHandler.a().getCount() == 9) {
            CachedSettingsGateway.handleOutgoingSenderAdapter(target, " §6⭐ Premium version");
         }

         CachedSettingsGateway.handleOutgoingSenderAdapter(target, "");
         if (this.entries.isEmpty()) {
            CachedSettingsGateway.handleOutgoingSenderAdapter(target, "§cUnable to load the subcommands");
         } else {
            if (target instanceof VerifiedServerAdapter) {
               VerifiedServerAdapter input = (VerifiedServerAdapter)target;
               LimboCoordinator output = this.indirectSessionHandler.loadLimboRegistry().loadLimboCoordinator(input);
               SecondaryAccountHandler context = output.getSecondaryAccountHandler();
               boolean data = this.indirectSessionHandler.loadLimboRegistry().loadLimboCoordinator(input).loadState();

               for (LoginSource result : this.entries) {
                  if (!result.getState()) {
                     String request = result.resolveMessage();
                     if (request == null || input.i(request)) {
                        List response = result.retrieveCollection();
                        String source;
                        if (data) {
                           source = String.format(
                              "§7Requer console: %s\n§7Permissão: §f%s%s",
                              result.fetchState() && SpawnState.ACTIVE_CURRENT_SPAWNSTATE.ar() ? "§a✔" : "§c✗",
                              request == null ? "Este comando não requer uma permissão." : request,
                              !response.isEmpty() ? "\n§7Apelidos: §f" + String.join(", ", response) : ""
                           );
                        } else {
                           source = String.format(
                              "§7Require console: %s\n§7Permission: §f%s%s",
                              result.fetchState() && SpawnState.ACTIVE_CURRENT_SPAWNSTATE.ar() ? "§a✔" : "§c✗",
                              request == null ? "This command does not require a permission." : request,
                              !response.isEmpty() ? "\n§7Aliases: §f" + String.join(", ", response) : ""
                           );
                        }

                        String entry = result.findMessage();
                        context.executeMessage(" §8⋆ §7/nlogin " + entry, source, "nlogin " + entry);
                     }
                  }
               }

               CachedSettingsGateway.processOutgoingSenderAdapter(input, QuickProxyState.LIVE_QUICKPROXYSTATE);
            } else {
               for (LoginSource item : this.entries) {
                  if (!item.getState()) {
                     CachedSettingsGateway.handleOutgoingSenderAdapter(target, " §8⋆ §7/nlogin " + item.findMessage());
                  }
               }
            }

            CachedSettingsGateway.handleOutgoingSenderAdapter(target, "");
         }
      }
   }

   @Override
   public void executeTask() {
      Class[] target = new Class[]{
         PasswordCheckpoint.class,
         PendingSpawnCompletionCommand.class,
         PasswordHashStore.class,
         DiscordLinker.class,
         LoginLinker.class,
         ParentPasswordHandler.class,
         MojangLookup.class,
         LocaleGateway.class,
         AccountCommand.class,
         LocaleAction.class,
         PasswordCommand.class,
         PasswordAction.class,
         CachedRootPasswordCommand.class,
         PasswordResolver.class,
         LoudDiscordNotifier.class,
         BackupRepository.class,
         PendingLocaleCollection.class,
         StrictDiscordNotifier.class,
         SettingsContainer.class,
         SettingsDao.class,
         LocaleSource.class,
         PurgeUnregisterCommand.class,
         NoticeRenderer.class
      };
      ArrayList input = new ArrayList();

      for (Class value : target) {
         try {
            LoginSource result = (LoginSource)value.getConstructor(PasswordStore.class).newInstance(this.indirectSessionHandler);
            input.add(result);
         } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException request) {
            PasswordHashContainer.handleMessage("[Command] Failed to register " + value.getSimpleName(), request);
         }
      }

      input.sort((instance, targetValue) -> instance.findMessage().compareToIgnoreCase(targetValue.findMessage()));
      this.entries = input;
   }

   @Override
   public void executeOutgoingSenderAdapter(OutgoingSenderAdapter target, String input, String[] output) {
      if (output.length == 0) {
         this.sendOutgoingSenderAdapter(target);
      } else if (this.entries.isEmpty()) {
         CachedSettingsGateway.handleOutgoingSenderAdapter(target, "§cUnable to load the subcommands");
      } else {
         LimboRegistry context = this.indirectSessionHandler.loadLimboRegistry();
         boolean data = target instanceof VerifiedServerAdapter;
         String value = output[0].toLowerCase(Locale.ENGLISH);

         for (LoginSource request : this.entries) {
            if (value.equalsIgnoreCase(request.findMessage()) || request.retrieveCollection().contains(value)) {
               if (!(request instanceof NoticeRenderer) && !(request instanceof StrictDiscordNotifier) && data && !context.canState((VerifiedServerAdapter)target)) {
                  return;
               }

               String response = request.resolveMessage();
               if (response != null && !target.hasState("nlogin.admin") && !target.hasState(response)) {
                  CachedSettingsGateway.executeOutgoingSenderAdapter(target, LoudProxyState.ROOT_LOUDPROXYSTATE);
                  if (target instanceof VerifiedServerAdapter) {
                     CachedSettingsGateway.processOutgoingSenderAdapter(target, QuickProxyState.ACTIVE_PRIMARY_QUICKPROXYSTATE);
                  }

                  return;
               } else if (data && request.fetchState() && SpawnState.ACTIVE_CURRENT_SPAWNSTATE.ar()) {
                  if (context.loadLimboCoordinator((VerifiedServerAdapter)target).loadState()) {
                     CachedSettingsGateway.handleOutgoingSenderAdapter(target, "§cDesculpe, mas está operação está restrita para o console.");
                     CachedSettingsGateway.handleOutgoingSenderAdapter(target, "§cOperações com alto risco não podem ser executadas por jogadores.");
                  } else {
                     CachedSettingsGateway.handleOutgoingSenderAdapter(target, "§cSorry, but this operation is restricted to the console.");
                     CachedSettingsGateway.handleOutgoingSenderAdapter(target, "§cHigh-risk operations cannot be performed by players.");
                  }

                  return;
               } else {
                  request.handleOutgoingSenderAdapter(target, output);
                  return;
               }
            }
         }

         if (!data || context.canState((VerifiedServerAdapter)target)) {
            this.sendOutgoingSenderAdapter(target);
         }
      }
   }

   @Override
   public List<String> buildCollection(OutgoingSenderAdapter target, String input, String[] output) {
      if (output.length <= 1) {
         ArrayList response = new ArrayList();
         String source = output.length == 0 ? "" : output[output.length - 1];
         if (!source.isEmpty()) {
            for (LoginSource item : this.entries) {
               String content = item.findMessage();
               if (content.startsWith(source.toLowerCase(Locale.ENGLISH))
                  && (item.resolveMessage() == null || target.hasState("nlogin.admin") || target.hasState(item.resolveMessage()))) {
                  response.add(content);
               }
            }
         } else {
            for (LoginSource element : this.entries) {
               String payload = element.resolveMessage();
               if (payload == null || target.hasState("nlogin.admin") || target.hasState(payload)) {
                  response.add(element.findMessage());
               }
            }
         }

         Collections.sort(response);
         return response;
      } else {
         String context = output[0].toLowerCase(Locale.ENGLISH);
         LimboRegistry data = this.indirectSessionHandler.loadLimboRegistry();

         for (LoginSource result : this.entries) {
            if (context.equals(result.findMessage()) || result.retrieveCollection().contains(context)) {
               if (!(result instanceof NoticeRenderer)
                  && !(result instanceof StrictDiscordNotifier)
                  && target instanceof VerifiedServerAdapter
                  && !data.canState((VerifiedServerAdapter)target)) {
                  return null;
               }

               String request = result.resolveMessage();
               return request != null && !target.hasState("nlogin.admin") && !target.hasState(request)
                  ? null
                  : result.computeCollection(target, context, Arrays.copyOfRange(output, 1, output.length));
            }
         }

         return null;
      }
   }

   public LowLocaleVerifier(InternalLoginOption target) {
      super(target);
      this.getPasswordHashCommand();
   }
}
