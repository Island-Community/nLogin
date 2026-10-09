package com.nickuc.login.premium;

import com.nickuc.login.account.IndirectNoticeCatalog;
import com.nickuc.login.account.QuickPremiumOption;
import com.nickuc.login.auth.login.ActiveLoginCheckpoint;
import com.nickuc.login.auth.login.LiveLoginCheckpoint;
import com.nickuc.login.auth.message.MessageProcessor;
import com.nickuc.login.config.PasswordHashContainer;
import com.nickuc.login.model.ProxyState;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.DumperOptions.ScalarStyle;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.Tag;
import com.nickuc.login.platform.listener.InternalListenerContract;
import com.nickuc.login.platform.connection.LenientConnectionContract;
import com.nickuc.login.storage.backup.BackupDao;
import com.nickuc.login.storage.password.PasswordStore;
import com.nickuc.login.storage.spawn.SpawnState;
import java.io.File;
import java.io.FileWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.slf4j.LoggerFactory;

public class ParentPasswordHashVerifier implements LenientConnectionContract {
   public static final ParentPasswordHashVerifier parentPasswordHashVerifier = new ParentPasswordHashVerifier();

   private void processTable(Map<String, InternalListenerContract> target, InternalListenerContract input, String... output) {
      if (output.length == 0) {
         throw new IllegalStateException("Keys cannot be empty!");
      }

      for (String result : output) {
         target.put(result, input);
      }
   }

   @Override
   public boolean validateState(PasswordStore target) {
      return target.a().canState("premium");
   }

   public void processPasswordStore(PasswordStore target) {
      LiveLoginCheckpoint input = new LiveLoginCheckpoint();
      PasswordHashContainer.performMessage("Starting %s converter...", this.retrieveMessage());
      File output = BackupDao.createFile(target, null, false);
      if (output != null) {
         PasswordHashContainer.performMessage("A backup of the previous version was created in %s path", output.getAbsolutePath());
      }

      File context = new File(target.resolveFile(), "premium");
      File data = new File(target.resolveFile(), "2fa");
      if (data.exists() && context.exists()) {
         File value = new File(context, "2fa");
         if (value.exists()) {
            MessageProcessor.validateState(value);
         }

         if (!value.exists()) {
            data.renameTo(value);
         }
      }

      File argument = new File(target.resolveFile(), "config-v10.yml");
      File result = MessageProcessor.buildFile(argument, MessageProcessor.resolveMessage(argument) + "-%d.yml");
      File request = target.a().retrieveFile();
      File response = new File(context, "config.yml");
      if (request.exists()) {
         if (!request.renameTo(result)) {
            throw new IllegalStateException("Unable to move " + request.getAbsolutePath() + " to " + result.getAbsolutePath() + "!");
         }

         Pbkdf2Linker.loadLoudNoticeCatalog(target);
         Map source = this.processTable(false);
         Map entry = this.processTable(true);
         HashMap record = new HashMap();
         Yaml item = ActiveLoginCheckpoint.processYaml(false);
         MappingNode element = ActiveLoginCheckpoint.loadMappingNode(item, result);
         ActiveLoginCheckpoint.handleMappingNode(element, "", (inputValue, outputValue) -> {
            InternalListenerContract contextValue = (InternalListenerContract)source.get(inputValue);
            if (contextValue != null) {
               inputValue = contextValue.retrieveBusyLoginProcessor().fetchNames()[0];
            }

            record.put(inputValue, outputValue);
            return null;
         });
         Yaml content = ActiveLoginCheckpoint.processYaml(true);
         MappingNode payload = ActiveLoginCheckpoint.loadMappingNode(content, request);
         ActiveLoginCheckpoint.handleMappingNode(payload, "", (inputValue, outputValue) -> {
            InternalListenerContract contextValue = (InternalListenerContract)entry.get(inputValue);
            if (contextValue instanceof QuickPremiumOption) {
               return null;
            } else if (contextValue != null) {
               return (Node)record.get(contextValue.retrieveBusyLoginProcessor().fetchNames()[0]);
            } else {
               return !inputValue.startsWith("redis.") && !inputValue.startsWith("commands.") ? null : (Node)record.get(inputValue);
            }
         });
         FileWriter holder = new FileWriter(request);

         try {
            content.serialize(payload, holder);
         } catch (Throwable parameter) {
            try {
               holder.close();
            } catch (Throwable property) {
               parameter.addSuppressed(property);
            }

            throw parameter;
         }

         holder.close();
         if (response.exists()) {
            MappingNode message = ActiveLoginCheckpoint.loadMappingNode(content, response);
            ActiveLoginCheckpoint.handleMappingNode(
               message,
               "",
               (inputValue, outputValue) -> {
                  InternalListenerContract contextValue = (InternalListenerContract)entry.get(inputValue);
                  if (!(contextValue instanceof QuickPremiumOption) && !(contextValue instanceof IndirectNoticeCatalog)) {
                     return null;
                  }

                  Node dataValue = (Node)record.get(contextValue.retrieveBusyLoginProcessor().fetchNames()[0]);
                  if (QuickPremiumOption.ACTIVE_QUICKPREMIUMOPTION.equals(contextValue)) {
                     dataValue = (Node)record.get(
                        IndirectNoticeCatalog.computeBusyLoginProcessor(IndirectNoticeCatalog.CACHED_INDIRECTNOTICECATALOG).fetchNames()[0]
                     );
                     if (!(dataValue instanceof ScalarNode)) {
                        return null;
                     }

                     ProxyState sourceValue = Boolean.parseBoolean(((ScalarNode)dataValue).getValue()) ? ProxyState.PROXY_STATE : ProxyState.PENDING_PROXYSTATE;
                     return new ScalarNode(Tag.STR, sourceValue.name(), dataValue.getStartMark(), dataValue.getEndMark(), ScalarStyle.PLAIN);
                  } else if (QuickPremiumOption.QUICK_PREMIUM_OPTION.equals(contextValue)) {
                     Node valueValue = (Node)record.get(
                        IndirectNoticeCatalog.computeBusyLoginProcessor(IndirectNoticeCatalog.STORED_INDIRECTNOTICECATALOG).fetchNames()[0]
                     );
                     Node resultValue = (Node)record.get(
                        IndirectNoticeCatalog.computeBusyLoginProcessor(IndirectNoticeCatalog.VERIFIED_INDIRECTNOTICECATALOG).fetchNames()[0]
                     );
                     if (valueValue instanceof ScalarNode && resultValue instanceof ScalarNode) {
                        ProxyState requestValue = Boolean.parseBoolean(((ScalarNode)valueValue).getValue())
                           ? ProxyState.ACTIVE_PROXYSTATE
                           : (Boolean.parseBoolean(((ScalarNode)resultValue).getValue()) ? ProxyState.PROXY_STATE : ProxyState.PENDING_PROXYSTATE);
                        return new ScalarNode(Tag.STR, requestValue.name(), valueValue.getStartMark(), valueValue.getEndMark(), ScalarStyle.PLAIN);
                     } else {
                        return null;
                     }
                  } else {
                     return dataValue;
                  }
               }
            );
            FileWriter reference = new FileWriter(response);

            try {
               content.serialize(message, reference);
            } catch (Throwable attribute) {
               try {
                  reference.close();
               } catch (Throwable setting) {
                  attribute.addSuppressed(setting);
               }

               throw attribute;
            }

            reference.close();
         }

         Pbkdf2Linker.loadLoudNoticeCatalog(target);
         if (result.exists() && !result.delete()) {
            result.deleteOnExit();
         }

         PasswordHashContainer.performMessage("Migration %s finished, took %s", this.retrieveMessage(), input.loadMessage(TimeUnit.SECONDS, 2) + "s");
      }
   }

   private Map<String, InternalListenerContract> processTable(boolean target) {
      HashMap input = new HashMap();
      this.processTable(
         input,
         IndirectNoticeCatalog.INDIRECT_NOTICE_CATALOG,
         "database.remote.hostname",
         "database.remote.address",
         "database.MYSQL.connection-address",
         "Database.MYSQL.connection-address",
         "Database.MYSQL.EasyConfig.ENDEREÇO_IP",
         "Database.MYSQL.easy-config.connection-address"
      );
      this.processTable(
         input,
         IndirectNoticeCatalog.ACTIVE_INDIRECTNOTICECATALOG,
         "database.remote.database",
         "database.MYSQL.connection-database",
         "Database.MYSQL.connection-database",
         "Database.MYSQL.EasyConfig.DATABASE",
         "Database.MYSQL.easy-config.connection-database"
      );
      this.processTable(
         input,
         IndirectNoticeCatalog.PENDING_INDIRECTNOTICECATALOG,
         "database.remote.username",
         "database.remote.user",
         "database.MYSQL.connection-user",
         "Database.MYSQL.connection-user",
         "Database.MYSQL.EasyConfig.USUARIO",
         "Database.MYSQL.easy-config.connection-user"
      );
      this.processTable(
         input,
         IndirectNoticeCatalog.CURRENT_INDIRECTNOTICECATALOG,
         "database.remote.password",
         "database.MYSQL.connection-password",
         "Database.MYSQL.connection-password",
         "Database.MYSQL.EasyConfig.SENHA",
         "Database.MYSQL.easy-config.connection-password"
      );
      this.processTable(
         input, IndirectNoticeCatalog.PRIMARY_INDIRECTNOTICECATALOG, "database.pool-settings.maximum-pool-size", "Database.pool-settings.maximum-pool-size"
      );
      this.processTable(input, IndirectNoticeCatalog.MAIN_INDIRECTNOTICECATALOG, "database.pool-settings.minimum-idle", "Database.pool-settings.minimum-idle");
      this.processTable(
         input, IndirectNoticeCatalog.LOCAL_INDIRECTNOTICECATALOG, "database.pool-settings.maximum-lifetime", "Database.pool-settings.maximum-lifetime"
      );
      this.processTable(
         input, IndirectNoticeCatalog.REMOTE_INDIRECTNOTICECATALOG, "database.pool-settings.connection-timeout", "Database.pool-settings.connection-timeout"
      );
      if (target) {
         Arrays.stream(SpawnState.values()).forEach(targetValue -> {
            for (String data : targetValue.busyLoginProcessor.fetchNames()) {
               input.put(data, targetValue);
            }
         });
         Arrays.stream(QuickPremiumOption.values()).forEach(targetValue -> {
            for (String data : targetValue.busyLoginProcessor.fetchNames()) {
               input.put(data, targetValue);
            }
         });
         return input;
      } else {
         this.processTable(input, SpawnState.SPAWN_STATE, "language-file", "languageFile");
         this.processTable(input, SpawnState.ACTIVE_SPAWNSTATE, "database.type", "database.database-type", "Database.database-type", "Database.Tipo");
         this.processTable(input, SpawnState.PENDING_SPAWNSTATE, "database.SQLITE.database-filename", "Database.SQLITE.database-filename");
         this.processTable(input, SpawnState.CURRENT_SPAWNSTATE, "database.table.account.table-name");
         this.processTable(input, SpawnState.PRIMARY_SPAWNSTATE, "database.table.account.columns.ai");
         this.processTable(input, SpawnState.MAIN_SPAWNSTATE, "database.table.account.columns.last-name");
         this.processTable(input, SpawnState.LOCAL_SPAWNSTATE, "database.table.account.columns.unique-id");
         this.processTable(input, SpawnState.REMOTE_SPAWNSTATE, "database.table.account.columns.mojang-id");
         this.processTable(input, SpawnState.CACHED_SPAWNSTATE, "database.table.account.columns.bedrock-id");
         this.processTable(input, SpawnState.STORED_SPAWNSTATE, "database.table.account.columns.password");
         this.processTable(input, SpawnState.VERIFIED_SPAWNSTATE, "database.table.account.columns.last-ip", "database.table.account.columns.last-address");
         this.processTable(input, SpawnState.AUTHENTICATED_SPAWNSTATE, "database.table.account.columns.last-seen", "database.table.account.columns.last-login");
         this.processTable(input, SpawnState.SHARED_SPAWNSTATE, "database.table.account.columns.creation-date");
         this.processTable(input, SpawnState.PRIVATE_SPAWNSTATE, "database.table.account.columns.email");
         this.processTable(input, SpawnState.INTERNAL_SPAWNSTATE, "database.table.account.columns.discord");
         this.processTable(input, SpawnState.UPSTREAM_SPAWNSTATE, "database.table.account.columns.settings");
         this.processTable(input, SpawnState.INCOMING_SPAWNSTATE, "database.table.data.table-name");
         this.processTable(input, SpawnState.OUTGOING_SPAWNSTATE, "database.table.data.columns.id");
         this.processTable(input, SpawnState.SECONDARY_SPAWNSTATE, "database.table.data.columns.key");
         this.processTable(input, SpawnState.DIRECT_SPAWNSTATE, "database.table.data.columns.value");
         this.processTable(input, SpawnState.LINKED_SPAWNSTATE, "limbo.delay", "limbo.hide-player-stats-delay");
         this.processTable(input, SpawnState.ROOT_SPAWNSTATE, "limbo.hide-player-stats");
         this.processTable(input, SpawnState.TOP_SPAWNSTATE, "limbo.hide-player-inventory", "limbo.hide-inventory", "limbo.inventory.hide-inventory");
         this.processTable(input, SpawnState.FAST_SPAWNSTATE, "limbo.hide-unauthenticated-players", "limbo.hide-players-before-login");
         this.processTable(input, SpawnState.SAFE_SPAWNSTATE, "limbo.block-player-movement", "limbo.block-player-walk");
         this.processTable(input, SpawnState.SECURE_SPAWNSTATE, "limbo.use-blindness-effect", "limbo.blindness-effect");
         this.processTable(input, SpawnState.OPEN_SPAWNSTATE, "limbo.highest-block-location", "teleport.safe-location");
         this.processTable(input, SpawnState.READY_SPAWNSTATE, "limbo.unrestricted.nicknames", "advanced.unrestricted.unrestricted-names");
         this.processTable(input, SpawnState.LIVE_SPAWNSTATE, "limbo.unrestricted.inventories", "advanced.unrestricted.unrestricted-inventories");
         this.processTable(input, SpawnState.ACTIVE_PENDING_SPAWNSTATE, "security.auth-timeout", "security.time-to-login");
         this.processTable(input, SpawnState.ACTIVE_CURRENT_SPAWNSTATE, "security.force-console-usage", "security.disable-high-risk-commands");
         this.processTable(input, SpawnState.ACTIVE_PRIMARY_SPAWNSTATE, "security.nickname-validation-regex", "security.nickname-regex");
         this.processTable(
            input, SpawnState.ACTIVE_STORED_SPAWNSTATE, "security.ip.bypass-online-check-with-same-ip", "security.bypass-online-check-with-same-address"
         );
         this.processTable(
            input,
            SpawnState.ACTIVE_VERIFIED_SPAWNSTATE,
            "security.ip.limit.enable",
            "security.ip-limit.enable",
            "security.address-limiter.enable",
            "security.address-limiter.enabled"
         );
         this.processTable(
            input, SpawnState.ACTIVE_AUTHENTICATED_SPAWNSTATE, "security.ip.limit.max", "security.ip-limit.limit", "security.address-limiter.limit"
         );
         this.processTable(input, SpawnState.ACTIVE_SHARED_SPAWNSTATE, "security.ip.limit.prevent-login", "security.ip-limit.prevent-login");
         this.processTable(input, SpawnState.ACTIVE_PRIVATE_SPAWNSTATE, "security.ip.limit.bypass.registered", "security.ip-limit.bypass.registered");
         this.processTable(input, SpawnState.ACTIVE_INTERNAL_SPAWNSTATE, "security.ip.limit.bypass.premium", "security.ip-limit.bypass.premium");
         this.processTable(input, SpawnState.ACTIVE_UPSTREAM_SPAWNSTATE, "security.ip.limit.bypass.bedrock", "security.ip-limit.bypass.bedrock");
         this.processTable(
            input, SpawnState.ACTIVE_INCOMING_SPAWNSTATE, "security.ip.limit.bypass.ips", "security.ip-limit.bypass.ips", "security.address-limiter.bypass"
         );
         this.processTable(input, SpawnState.ACTIVE_OUTGOING_SPAWNSTATE, "security.passwords.small", "passwords.small");
         this.processTable(input, SpawnState.ACTIVE_SECONDARY_SPAWNSTATE, "security.passwords.large", "passwords.large");
         this.processTable(input, SpawnState.ACTIVE_DIRECT_SPAWNSTATE, "security.passwords.secure.enable", "passwords.secure.enable");
         this.processTable(input, SpawnState.ACTIVE_LINKED_SPAWNSTATE, "security.passwords.secure.enforce", "passwords.secure.enforce");
         this.processTable(input, SpawnState.ACTIVE_ROOT_SPAWNSTATE, "security.passwords.secure.secure-regex", "passwords.secure.secure-regex");
         this.processTable(input, SpawnState.ACTIVE_TOP_SPAWNSTATE, "security.bruteforce.max-auth-tries", "passwords.bruteforce.max-login-tries");
         this.processTable(input, SpawnState.ACTIVE_FAST_SPAWNSTATE, "security.bruteforce.punish.enable", "passwords.bruteforce.auto-punish");
         this.processTable(input, SpawnState.ACTIVE_SAFE_SPAWNSTATE, "security.bruteforce.punish.duration", "passwords.bruteforce.punishment-duration");
         this.processTable(input, SpawnState.ACTIVE_SECURE_SPAWNSTATE, "security.hashing.algorithm", "passwords.hashing.algorithm");
         this.processTable(input, SpawnState.ACTIVE_OPEN_SPAWNSTATE, "security.hashing.bcrypt.rounds", "passwords.hashing.bcrypt.rounds");
         this.processTable(input, SpawnState.ACTIVE_READY_SPAWNSTATE, "security.hashing.pbkdf2.iterations", "passwords.hashing.pbkdf2.iterations");
         this.processTable(input, SpawnState.ACTIVE_LIVE_SPAWNSTATE, "security.hashing.pbkdf2.algorithm", "passwords.hashing.pbkdf2.algorithm");
         this.processTable(input, SpawnState.PENDING_ACTIVE_SPAWNSTATE, "security.hashing.argon2.iterations", "passwords.hashing.argon2.iterations");
         this.processTable(input, SpawnState.PENDING_CURRENT_SPAWNSTATE, "security.hashing.argon2.memory", "passwords.hashing.argon2.memory");
         this.processTable(input, SpawnState.PENDING_PRIMARY_SPAWNSTATE, "security.hashing.argon2.parallelism", "passwords.hashing.argon2.parallelism");
         this.processTable(input, SpawnState.PENDING_MAIN_SPAWNSTATE, "ui.use-title-bar");
         this.processTable(input, SpawnState.PENDING_LOCAL_SPAWNSTATE, "ui.use-action-bar");
         this.processTable(input, SpawnState.PENDING_REMOTE_SPAWNSTATE, "ui.action-bar-timer", "ui.actionbar-counter");
         this.processTable(input, SpawnState.PENDING_STORED_SPAWNSTATE, "ui.use-sounds");
         this.processTable(input, SpawnState.PENDING_VERIFIED_SPAWNSTATE, "ui.remove-chat-messages", "join.clean-chat-on-join");
         this.processTable(input, SpawnState.PENDING_AUTHENTICATED_SPAWNSTATE, "ui.remove-join-message", "join.remove-join-message");
         this.processTable(input, SpawnState.PENDING_SHARED_SPAWNSTATE, "ui.language-by-client", "advanced.client.language-by-client");
         this.processTable(input, SpawnState.PENDING_PRIVATE_SPAWNSTATE, "commands.after-register", "advanced.client.commands-after-register");
         this.processTable(input, SpawnState.PENDING_INTERNAL_SPAWNSTATE, "commands.after-login", "advanced.client.commands-after-login");
         this.processTable(input, SpawnState.PENDING_UPSTREAM_SPAWNSTATE, "commands.allowed-commands", "advanced.client.allowed-commands");
         this.processTable(input, IndirectNoticeCatalog.CACHED_INDIRECTNOTICECATALOG, "premium.challenge-if-premium-uuid");
         this.processTable(input, QuickPremiumOption.CURRENT_QUICKPREMIUMOPTION, "premium.username-appender.enable", "premium.username-appender.enabled");
         this.processTable(input, QuickPremiumOption.PRIMARY_QUICKPREMIUMOPTION, "premium.username-appender.premium.username-appendix");
         this.processTable(input, QuickPremiumOption.MAIN_QUICKPREMIUMOPTION, "premium.username-appender.premium.position");
         this.processTable(input, QuickPremiumOption.LOCAL_QUICKPREMIUMOPTION, "premium.username-appender.premium.domains");
         this.processTable(input, QuickPremiumOption.REMOTE_QUICKPREMIUMOPTION, "premium.username-appender.offline.username-appendix");
         this.processTable(input, QuickPremiumOption.CACHED_QUICKPREMIUMOPTION, "premium.username-appender.offline.position");
         this.processTable(input, QuickPremiumOption.STORED_QUICKPREMIUMOPTION, "premium.username-appender.offline.domains");
         this.processTable(input, IndirectNoticeCatalog.STORED_INDIRECTNOTICECATALOG, "premium.legacy.restrict-premium-nicknames");
         this.processTable(input, IndirectNoticeCatalog.VERIFIED_INDIRECTNOTICECATALOG, "premium.legacy.challenge-if-premium-nickname");
         this.processTable(
            input,
            QuickPremiumOption.PENDING_QUICKPREMIUMOPTION,
            "premium.legacy.send-premium-question",
            "premium.legacy.premium-question",
            "premium.autologin.premium.ask-via-notification"
         );
         this.processTable(input, QuickPremiumOption.VERIFIED_QUICKPREMIUMOPTION, "premium.autologin.bedrock.enable", "premium.autologin.bedrock.enabled");
         this.processTable(input, QuickPremiumOption.AUTHENTICATED_QUICKPREMIUMOPTION, "premium.autologin.bedrock.skip-register");
         this.processTable(input, QuickPremiumOption.SHARED_QUICKPREMIUMOPTION, "premium.autologin.bedrock.use-database-uuid");
         this.processTable(input, QuickPremiumOption.PRIVATE_QUICKPREMIUMOPTION, "premium.autologin.premium.enable", "premium.autologin.premium.enabled");
         this.processTable(input, QuickPremiumOption.UPSTREAM_QUICKPREMIUMOPTION, "premium.autologin.premium.skip-register");
         this.processTable(input, QuickPremiumOption.INCOMING_QUICKPREMIUMOPTION, "premium.autologin.session.enable", "premium.autologin.session.enabled");
         this.processTable(input, QuickPremiumOption.OUTGOING_QUICKPREMIUMOPTION, "premium.autologin.session.duration");
         return input;
      }
   }

   @Override
   public String retrieveMessage() {
      return "nLoginV2Converter";
   }

   static {
      LoggerFactory.getLogger(ParentPasswordHashVerifier.class);
   }
}
