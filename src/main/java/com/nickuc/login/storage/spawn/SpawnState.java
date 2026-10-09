package com.nickuc.login.storage.spawn;

import com.nickuc.login.auth.login.BusyLoginProcessor;
import com.nickuc.login.auth.login.SecureLoginGate;
import com.nickuc.login.model.NoticeKind;
import com.nickuc.login.model.UpstreamSpawnState;
import com.nickuc.login.platform.listener.InternalListenerContract;
import java.util.Arrays;
import java.util.Collections;

public enum SpawnState implements InternalListenerContract {
   SPAWN_STATE(BusyLoginProcessor.handleBusyLoginProcessor("language-file", "languageFile"), "messages_en.yml", true),
   ACTIVE_SPAWNSTATE(
      BusyLoginProcessor.handleBusyLoginProcessor("database.type", "database.database-type", "Database.database-type", "Database.Tipo"), "SQLite"
   ),
   PENDING_SPAWNSTATE(BusyLoginProcessor.handleBusyLoginProcessor("database.SQLITE.database-filename", "Database.SQLITE.database-filename"), "accounts.db"),
   CURRENT_SPAWNSTATE(BusyLoginProcessor.handleBusyLoginProcessor("database.table.account.table-name"), "nlogin"),
   PRIMARY_SPAWNSTATE(BusyLoginProcessor.handleBusyLoginProcessor("database.table.account.columns.ai"), "ai"),
   MAIN_SPAWNSTATE(BusyLoginProcessor.handleBusyLoginProcessor("database.table.account.columns.last-name"), "last_name"),
   LOCAL_SPAWNSTATE(BusyLoginProcessor.handleBusyLoginProcessor("database.table.account.columns.unique-id"), "unique_id"),
   REMOTE_SPAWNSTATE(BusyLoginProcessor.handleBusyLoginProcessor("database.table.account.columns.mojang-id"), "mojang_id"),
   CACHED_SPAWNSTATE(BusyLoginProcessor.handleBusyLoginProcessor("database.table.account.columns.bedrock-id"), "bedrock_id"),
   STORED_SPAWNSTATE(BusyLoginProcessor.handleBusyLoginProcessor("database.table.account.columns.password"), "password"),
   VERIFIED_SPAWNSTATE(
      BusyLoginProcessor.handleBusyLoginProcessor("database.table.account.columns.last-ip", "database.table.account.columns.last-address"), "last_ip"
   ),
   AUTHENTICATED_SPAWNSTATE(
      BusyLoginProcessor.handleBusyLoginProcessor("database.table.account.columns.last-seen", "database.table.account.columns.last-login"), "last_seen"
   ),
   SHARED_SPAWNSTATE(BusyLoginProcessor.handleBusyLoginProcessor("database.table.account.columns.creation-date"), "creation_date"),
   PRIVATE_SPAWNSTATE(BusyLoginProcessor.handleBusyLoginProcessor("database.table.account.columns.email"), "email"),
   INTERNAL_SPAWNSTATE(BusyLoginProcessor.handleBusyLoginProcessor("database.table.account.columns.discord"), "discord"),
   UPSTREAM_SPAWNSTATE(BusyLoginProcessor.handleBusyLoginProcessor("database.table.account.columns.settings"), "settings"),
   INCOMING_SPAWNSTATE(BusyLoginProcessor.handleBusyLoginProcessor("database.table.data.table-name"), "nlogin_data"),
   OUTGOING_SPAWNSTATE(BusyLoginProcessor.handleBusyLoginProcessor("database.table.data.columns.id"), "id"),
   SECONDARY_SPAWNSTATE(BusyLoginProcessor.handleBusyLoginProcessor("database.table.data.columns.key"), "key"),
   DIRECT_SPAWNSTATE(BusyLoginProcessor.handleBusyLoginProcessor("database.table.data.columns.value"), "value"),
   LINKED_SPAWNSTATE(BusyLoginProcessor.handleBusyLoginProcessor("limbo.delay", "limbo.hide-player-stats-delay"), 0, true),
   ROOT_SPAWNSTATE(BusyLoginProcessor.handleBusyLoginProcessor("limbo.hide-player-stats"), true, true),
   TOP_SPAWNSTATE(
      BusyLoginProcessor.handleBusyLoginProcessor("limbo.hide-player-inventory", "limbo.hide-inventory", "limbo.inventory.hide-inventory"), true, true
   ),
   FAST_SPAWNSTATE(BusyLoginProcessor.handleBusyLoginProcessor("limbo.hide-unauthenticated-players", "limbo.hide-players-before-login"), true, true),
   SAFE_SPAWNSTATE(BusyLoginProcessor.handleBusyLoginProcessor("limbo.block-player-movement", "limbo.block-player-walk"), true, true),
   SECURE_SPAWNSTATE(BusyLoginProcessor.handleBusyLoginProcessor("limbo.use-blindness-effect", "limbo.blindness-effect"), false, true),
   OPEN_SPAWNSTATE(BusyLoginProcessor.handleBusyLoginProcessor("limbo.highest-block-location", "teleport.safe-location"), false, true),
   READY_SPAWNSTATE(
      BusyLoginProcessor.handleBusyLoginProcessor("limbo.unrestricted.nicknames", "advanced.unrestricted.unrestricted-names"), Collections.emptyList(), true
   ),
   LIVE_SPAWNSTATE(
      BusyLoginProcessor.handleBusyLoginProcessor("limbo.unrestricted.inventories", "advanced.unrestricted.unrestricted-inventories"),
      Collections.emptyList(),
      true
   ),
   ACTIVE_PENDING_SPAWNSTATE(BusyLoginProcessor.handleBusyLoginProcessor("security.auth-timeout", "security.time-to-login"), 90),
   ACTIVE_CURRENT_SPAWNSTATE(BusyLoginProcessor.handleBusyLoginProcessor("security.force-console-usage", "security.disable-high-risk-commands"), true),
   ACTIVE_PRIMARY_SPAWNSTATE(
      BusyLoginProcessor.handleBusyLoginProcessor("security.nickname.validation-regex", "security.nickname-validation-regex", "security.nickname-regex"),
      "([a-zA-Z0-9_]{3,16})"
   ),
   ACTIVE_MAIN_SPAWNSTATE(BusyLoginProcessor.handleBusyLoginProcessor("security.nickname.kick-unregistered.enable"), false),
   ACTIVE_LOCAL_SPAWNSTATE(BusyLoginProcessor.handleBusyLoginProcessor("security.nickname.kick-unregistered.bypass.premium"), false),
   ACTIVE_REMOTE_SPAWNSTATE(BusyLoginProcessor.handleBusyLoginProcessor("security.nickname.kick-unregistered.bypass.bedrock"), false),
   ACTIVE_CACHED_SPAWNSTATE(BusyLoginProcessor.handleBusyLoginProcessor("security.nickname.kick-unregistered.bypass.ips"), Collections.emptyList()),
   ACTIVE_STORED_SPAWNSTATE(
      BusyLoginProcessor.handleBusyLoginProcessor("security.ip.bypass-online-check-with-same-ip", "security.bypass-online-check-with-same-address"), true
   ),
   ACTIVE_VERIFIED_SPAWNSTATE(
      BusyLoginProcessor.handleBusyLoginProcessor(
         "security.ip.limit.enable", "security.ip-limit.enable", "security.address-limiter.enable", "security.address-limiter.enabled"
      ),
      true
   ),
   ACTIVE_AUTHENTICATED_SPAWNSTATE(
      BusyLoginProcessor.handleBusyLoginProcessor("security.ip.limit.max", "security.ip-limit.limit", "security.address-limiter.limit"), 3
   ),
   ACTIVE_SHARED_SPAWNSTATE(BusyLoginProcessor.handleBusyLoginProcessor("security.ip.limit.prevent-login", "security.ip-limit.prevent-login"), false),
   ACTIVE_PRIVATE_SPAWNSTATE(BusyLoginProcessor.handleBusyLoginProcessor("security.ip.limit.bypass.registered", "security.ip-limit.bypass.registered"), true),
   ACTIVE_INTERNAL_SPAWNSTATE(BusyLoginProcessor.handleBusyLoginProcessor("security.ip.limit.bypass.premium", "security.ip-limit.bypass.premium"), true),
   ACTIVE_UPSTREAM_SPAWNSTATE(BusyLoginProcessor.handleBusyLoginProcessor("security.ip.limit.bypass.bedrock", "security.ip-limit.bypass.bedrock"), true),
   ACTIVE_INCOMING_SPAWNSTATE(
      BusyLoginProcessor.handleBusyLoginProcessor("security.ip.limit.bypass.ips", "security.ip-limit.bypass.ips", "security.address-limiter.bypass"),
      Arrays.asList("127.0.0.1", "localhost")
   ),
   ACTIVE_OUTGOING_SPAWNSTATE(BusyLoginProcessor.handleBusyLoginProcessor("security.passwords.small", "passwords.small"), 5),
   ACTIVE_SECONDARY_SPAWNSTATE(BusyLoginProcessor.handleBusyLoginProcessor("security.passwords.large", "passwords.large"), 32),
   ACTIVE_DIRECT_SPAWNSTATE(BusyLoginProcessor.handleBusyLoginProcessor("security.passwords.secure.enable", "passwords.secure.enable"), false),
   ACTIVE_LINKED_SPAWNSTATE(BusyLoginProcessor.handleBusyLoginProcessor("security.passwords.secure.enforce", "passwords.secure.enforce"), false),
   ACTIVE_ROOT_SPAWNSTATE(
      BusyLoginProcessor.handleBusyLoginProcessor("security.passwords.secure.secure-regex", "passwords.secure.secure-regex"),
      "(?=\\S*\\d)(?=\\S*[A-Z])(?=\\S*[a-z])(?=\\S*[!@#$%^&*?])\\S*$"
   ),
   ACTIVE_TOP_SPAWNSTATE(BusyLoginProcessor.handleBusyLoginProcessor("security.bruteforce.max-auth-tries", "passwords.bruteforce.max-login-tries"), 1),
   ACTIVE_FAST_SPAWNSTATE(BusyLoginProcessor.handleBusyLoginProcessor("security.bruteforce.punish.enable", "passwords.bruteforce.auto-punish"), true),
   ACTIVE_SAFE_SPAWNSTATE(BusyLoginProcessor.handleBusyLoginProcessor("security.bruteforce.punish.duration", "passwords.bruteforce.punishment-duration"), 15),
   ACTIVE_SECURE_SPAWNSTATE(
      BusyLoginProcessor.handleBusyLoginProcessor("security.hashing.algorithm", "passwords.hashing.algorithm"),
      UpstreamSpawnState.LOCAL_UPSTREAMSPAWNSTATE.name()
   ),
   ACTIVE_OPEN_SPAWNSTATE(BusyLoginProcessor.handleBusyLoginProcessor("security.hashing.bcrypt.rounds", "passwords.hashing.bcrypt.rounds"), 10),
   ACTIVE_READY_SPAWNSTATE(BusyLoginProcessor.handleBusyLoginProcessor("security.hashing.pbkdf2.iterations", "passwords.hashing.pbkdf2.iterations"), 10000),
   ACTIVE_LIVE_SPAWNSTATE(BusyLoginProcessor.handleBusyLoginProcessor("security.hashing.pbkdf2.algorithm", "passwords.hashing.pbkdf2.algorithm"), "HmacSHA512"),
   PENDING_ACTIVE_SPAWNSTATE(BusyLoginProcessor.handleBusyLoginProcessor("security.hashing.argon2.iterations", "passwords.hashing.argon2.iterations"), 10),
   PENDING_CURRENT_SPAWNSTATE(BusyLoginProcessor.handleBusyLoginProcessor("security.hashing.argon2.memory", "passwords.hashing.argon2.memory"), 64),
   PENDING_PRIMARY_SPAWNSTATE(BusyLoginProcessor.handleBusyLoginProcessor("security.hashing.argon2.parallelism", "passwords.hashing.argon2.parallelism"), 1),
   PENDING_MAIN_SPAWNSTATE(BusyLoginProcessor.handleBusyLoginProcessor("ui.use-title-bar"), true),
   PENDING_LOCAL_SPAWNSTATE(BusyLoginProcessor.handleBusyLoginProcessor("ui.use-action-bar"), true),
   PENDING_REMOTE_SPAWNSTATE(BusyLoginProcessor.handleBusyLoginProcessor("ui.action-bar-timer", "ui.actionbar-counter"), true),
   PENDING_CACHED_SPAWNSTATE(BusyLoginProcessor.handleBusyLoginProcessor("ui.show-dialogs"), NoticeKind.NOTICE_KIND.name()),
   PENDING_STORED_SPAWNSTATE(BusyLoginProcessor.handleBusyLoginProcessor("ui.use-sounds"), true),
   PENDING_VERIFIED_SPAWNSTATE(BusyLoginProcessor.handleBusyLoginProcessor("ui.remove-chat-messages", "join.clean-chat-on-join"), true),
   PENDING_AUTHENTICATED_SPAWNSTATE(BusyLoginProcessor.handleBusyLoginProcessor("ui.remove-join-message", "join.remove-join-message"), false, true),
   PENDING_SHARED_SPAWNSTATE(BusyLoginProcessor.handleBusyLoginProcessor("ui.language-by-client", "advanced.client.language-by-client"), false),
   PENDING_PRIVATE_SPAWNSTATE(
      BusyLoginProcessor.handleBusyLoginProcessor("commands.after-register", "advanced.client.commands-after-register"), Collections.emptyList()
   ),
   PENDING_INTERNAL_SPAWNSTATE(
      BusyLoginProcessor.handleBusyLoginProcessor("commands.after-login", "advanced.client.commands-after-login"), Collections.emptyList()
   ),
   PENDING_UPSTREAM_SPAWNSTATE(
      BusyLoginProcessor.handleBusyLoginProcessor("commands.allowed-commands", "advanced.client.allowed-commands"),
      Collections.singletonList("/loginstaff"),
      true
   );

   public static final SecureLoginGate secureLoginGate = new SecureLoginGate("config", values().length);
   public final BusyLoginProcessor busyLoginProcessor;
   private final Object object;
   private final boolean enabled;

   @Override
   public BusyLoginProcessor retrieveBusyLoginProcessor() {
      return this.busyLoginProcessor;
   }

   @Override
   public Object getObject() {
      return this.object;
   }

   SpawnState(BusyLoginProcessor output, Object context) {
      this(output, context, false);
   }

   SpawnState(BusyLoginProcessor output, Object context, boolean data) {
      if (output.fetchNames().length == 0) {
         throw new IllegalArgumentException("Keys cannot be empty! " + this);
      }

      this.busyLoginProcessor = output;
      this.object = context;
      this.enabled = data;
   }

   @Override
   public int fetchCount() {
      return this.ordinal();
   }

   @Override
   public SecureLoginGate loadSecureLoginGate() {
      return secureLoginGate;
   }
}
