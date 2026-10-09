package com.nickuc.login.premium;

import com.nickuc.login.account.QuickPremiumOption;
import com.nickuc.login.account.SpawnOption;
import com.nickuc.login.auth.login.DeadLoginFlow;
import com.nickuc.login.discord.QuickDiscordHandler;
import com.nickuc.login.model.OutgoingSpawnState;
import com.nickuc.login.model.UpstreamSpawnState;
import com.nickuc.login.platform.account.InternalAccountHandler;
import com.nickuc.login.platform.connection.SecondaryConnectionContract;
import com.nickuc.login.security.hashing.CachedPasswordHashHasher;
import com.nickuc.login.storage.password.PasswordStore;
import com.nickuc.login.storage.spawn.SpawnState;
import java.sql.Timestamp;
import java.util.UUID;
import javax.annotation.Nullable;


public class SpawnLookup {
   public long timestamp;
   public final QuickDiscordHandler quickDiscordHandler;
   public UUID uniqueId;
   public String name;
   public UUID activeUniqueId;
   public Long value;
   public final CachedPasswordHashHasher cachedPasswordHashHasher;
   public long activeTimestamp;
   public String activeName;
   public String pendingName;
   public final Object object = new Object();
   public UUID pendingUniqueId;

   public synchronized boolean fetchState() {
      return this.loadSpawnOption() != SpawnOption.CURRENT_SPAWNOPTION;
   }

   public synchronized void sendTask() {
      this.activeTimestamp = System.currentTimeMillis();
   }

   public synchronized void saveTask() {
      this.cachedPasswordHashHasher.updateMessage("premium.force-notification", true);
   }

   public SpawnLookup(Long target, String input, UUID output, UUID context, UUID data, String value, String result, long request, long source) {
      this.pendingName = "127.0.0.1";
      this.cachedPasswordHashHasher = new CachedPasswordHashHasher(this);
      this.quickDiscordHandler = new QuickDiscordHandler(this);
      this.value = target;
      this.activeName = input;
      this.pendingUniqueId = output;
      this.activeUniqueId = context;
      this.uniqueId = data;
      this.name = value;
      this.pendingName = result;
      this.activeTimestamp = request;
      this.timestamp = source;
   }

   public synchronized boolean findState() {
      return !this.fetchState() || this.cachedPasswordHashHasher.resolveObject("force-register-spawn", false);
   }

   public long loadTime() {
      return this.activeTimestamp;
   }

   public synchronized void processTask() {
      this.cachedPasswordHashHasher.updateMessage("nickname.force-update", true);
   }

   public synchronized boolean getState() {
      if (this.uniqueId != null) {
         return true;
      }

      if (this.pendingUniqueId != null && this.pendingUniqueId.getMostSignificantBits() == 0L) {
         InternalAccountHandler target = PasswordStore.resolvePasswordStore().loadInternalAccountHandler();
         if (target == null) {
            return false;
         }

         SecondaryConnectionContract input = target.retrieveSecondaryConnectionContract();
         return input != null && !input.fetchState();
      } else {
         return false;
      }
   }

   public synchronized void performTask() {
      this.cachedPasswordHashHasher.updateMessage("premium.force-request", true);
   }

   public synchronized boolean fetchStateForState() {
      return this.cachedPasswordHashHasher.resolveObject("nickname.force-update", false);
   }

   public synchronized boolean resolveState() {
      return this.cachedPasswordHashHasher.resolveObject("force-password-update", false);
   }

   public long findTime() {
      return this.timestamp;
   }

   public Long loadLong() {
      return this.value;
   }

   public synchronized void executeMessage(String target, boolean input) {
      this.cachedPasswordHashHasher.updateMessage("force-invalid-session", true);
      this.name = input ? Pbkdf2Linker.loadUpstreamSpawnState().computeMessage(target) : target;
   }

   public synchronized void performTaskForValue() {
      this.cachedPasswordHashHasher.updateMessage("force-password-update", true);
   }

   public synchronized boolean loadState() {
      return !this.fetchState() || this.cachedPasswordHashHasher.resolveObject("force-register-commands", false);
   }

   public synchronized void handleTask() {
      this.cachedPasswordHashHasher.performMessage("nickname.force-update");
   }

   public String loadMessage(boolean target, String input) {
      String output;
      if (target) {
         output = "UPDATE `"
            + SpawnState.CURRENT_SPAWNSTATE.a(new Object[0])
            + "` SET `"
            + OutgoingSpawnState.ACTIVE_OUTGOINGSPAWNSTATE.getName()
            + "` = '"
            + this.activeName
            + "', `"
            + OutgoingSpawnState.PENDING_OUTGOINGSPAWNSTATE.getName()
            + "` = "
            + DeadLoginFlow.processMessage(this.pendingUniqueId)
            + ",  `"
            + OutgoingSpawnState.CURRENT_OUTGOINGSPAWNSTATE.getName()
            + "` = "
            + DeadLoginFlow.processMessage(this.activeUniqueId)
            + ",  `"
            + OutgoingSpawnState.PRIMARY_OUTGOINGSPAWNSTATE.getName()
            + "` = "
            + DeadLoginFlow.processMessage(this.uniqueId)
            + ",  `"
            + OutgoingSpawnState.MAIN_OUTGOINGSPAWNSTATE.getName()
            + "` = "
            + (this.name == null ? null : "'" + this.name + "'")
            + ", `"
            + OutgoingSpawnState.LOCAL_OUTGOINGSPAWNSTATE.getName()
            + "` = "
            + (this.pendingName == null ? null : "'" + this.pendingName + "'")
            + ", `"
            + OutgoingSpawnState.CACHED_OUTGOINGSPAWNSTATE.getName()
            + "` = "
            + new Timestamp(this.timestamp)
            + ", `"
            + OutgoingSpawnState.REMOTE_OUTGOINGSPAWNSTATE.getName()
            + "` = "
            + new Timestamp(this.activeTimestamp)
            + ", `"
            + OutgoingSpawnState.STORED_OUTGOINGSPAWNSTATE.getName()
            + "` = "
            + (this.quickDiscordHandler.activeName == null ? null : "'" + this.quickDiscordHandler.activeName + "'")
            + ", `"
            + OutgoingSpawnState.VERIFIED_OUTGOINGSPAWNSTATE.getName()
            + "` = "
            + (this.quickDiscordHandler.name == null ? null : "'" + this.quickDiscordHandler.name + "'")
            + ", `"
            + OutgoingSpawnState.AUTHENTICATED_OUTGOINGSPAWNSTATE.getName()
            + "` = "
            + (input == null ? null : "'" + input + "'")
            + " WHERE `"
            + OutgoingSpawnState.OUTGOING_SPAWN_STATE.getName()
            + "` = "
            + this.value;
      } else {
         output = "INSERT INTO `"
            + SpawnState.CURRENT_SPAWNSTATE.a(new Object[0])
            + "` (`"
            + OutgoingSpawnState.ACTIVE_OUTGOINGSPAWNSTATE.getName()
            + "`, `"
            + OutgoingSpawnState.PENDING_OUTGOINGSPAWNSTATE.getName()
            + "`, `"
            + OutgoingSpawnState.CURRENT_OUTGOINGSPAWNSTATE.getName()
            + "`, `"
            + OutgoingSpawnState.PRIMARY_OUTGOINGSPAWNSTATE.getName()
            + "`, `"
            + OutgoingSpawnState.MAIN_OUTGOINGSPAWNSTATE.getName()
            + "`, `"
            + OutgoingSpawnState.LOCAL_OUTGOINGSPAWNSTATE.getName()
            + "`, `"
            + OutgoingSpawnState.CACHED_OUTGOINGSPAWNSTATE.getName()
            + "`, `"
            + OutgoingSpawnState.REMOTE_OUTGOINGSPAWNSTATE.getName()
            + "`, `"
            + OutgoingSpawnState.STORED_OUTGOINGSPAWNSTATE.getName()
            + "`, `"
            + OutgoingSpawnState.VERIFIED_OUTGOINGSPAWNSTATE.getName()
            + "`, `"
            + OutgoingSpawnState.AUTHENTICATED_OUTGOINGSPAWNSTATE.getName()
            + "`) VALUES ('"
            + this.activeName
            + "', "
            + DeadLoginFlow.processMessage(this.pendingUniqueId)
            + ", "
            + DeadLoginFlow.processMessage(this.activeUniqueId)
            + ", "
            + DeadLoginFlow.processMessage(this.uniqueId)
            + ", "
            + (this.name == null ? null : "'" + this.name + "'")
            + ", '"
            + this.pendingName
            + "', '"
            + new Timestamp(this.timestamp)
            + "', '"
            + new Timestamp(this.activeTimestamp)
            + "', "
            + (this.quickDiscordHandler.activeName == null ? null : "'" + this.quickDiscordHandler.activeName + "'")
            + ", "
            + (this.quickDiscordHandler.name == null ? null : "'" + this.quickDiscordHandler.name + "'")
            + ", "
            + (input == null ? null : "'" + input + "'")
            + ")";
      }

      return output;
   }

   public synchronized void dispatchTask() {
      this.cachedPasswordHashHasher.performMessage("premium.force-request");
   }

   public UUID getMojangId() {
      return this.activeUniqueId;
   }

   public UUID getUniqueId() {
      return this.pendingUniqueId;
   }

   public void executeLong(Long target, Long input) {
      if (target != null) {
         this.timestamp = target;
      }

      if (input != null) {
         this.activeTimestamp = input;
      }
   }

   public synchronized void handleUniqueId(UUID target) {
      this.pendingUniqueId = target;
   }

   public synchronized void executeUniqueId(UUID target) {
      if (target != null && target.version() != 0) {
         throw new IllegalArgumentException(
            "The Bedrock ID provided is not valid! expected version = 0, received version = "
               + target.version()
               + ", username = "
               + this.activeName
               + ", uuid = "
               + target
         );
      }

      this.uniqueId = target;
   }

   public synchronized boolean fetchStateAndState() {
      return this.activeUniqueId != null || this.cachedPasswordHashHasher.resolveObject("premium.force-request", false);
   }

   public synchronized boolean resolveStateForState() {
      return this.value != null;
   }

   public String resolveMessage() {
      return this.name;
   }

   public synchronized SpawnOption computeSpawnOption(boolean target) {
      if (target ? this.activeUniqueId == null : !this.fetchStateAndState()) {
         if (target ? this.uniqueId == null : !this.getState()) {
            return this.retrieveState() ? SpawnOption.ACTIVE_SPAWNOPTION : SpawnOption.CURRENT_SPAWNOPTION;
         } else {
            return SpawnOption.PENDING_SPAWNOPTION;
         }
      } else {
         return SpawnOption.SPAWN_OPTION;
      }
   }

   public SpawnLookup() {
      this.pendingName = "127.0.0.1";
      this.cachedPasswordHashHasher = new CachedPasswordHashHasher(this);
      this.quickDiscordHandler = new QuickDiscordHandler(this);
   }

   public synchronized boolean retrieveState() {
      return this.name != null;
   }

   public QuickDiscordHandler fetchQuickDiscordHandler() {
      return this.quickDiscordHandler;
   }

   @Nullable
   public synchronized UpstreamSpawnState findUpstreamSpawnState() {
      return UpstreamSpawnState.createUpstreamSpawnState(this.name);
   }

   public UUID getBedrockId() {
      return this.uniqueId;
   }

   public synchronized String loadMessage(String target) {
      return this.activeName != null ? this.activeName : target;
   }

   public synchronized boolean retrieveStateForState() {
      return this.cachedPasswordHashHasher.resolveObject("premium.force-notification", false);
   }

   public synchronized void executeTask() {
      this.cachedPasswordHashHasher.performMessage("force-password-update");
   }

   public static SpawnLookup handleSpawnLookup(String instance) {
      SpawnLookup target = new SpawnLookup();
      target.activeName = instance;
      return target;
   }

   public String findMessage() {
      return this.pendingName;
   }

   public void updateTask() {
      this.name = null;
      this.pendingName = null;
      if (!this.fetchStateAndState() || this.pendingUniqueId != null && !this.pendingUniqueId.equals(this.activeUniqueId)) {
         this.activeUniqueId = null;
      }

      this.activeTimestamp = 0L;
      this.timestamp = 0L;
      this.cachedPasswordHashHasher.sessions.clear();
      this.cachedPasswordHashHasher.enabled = true;
      this.quickDiscordHandler.saveTask();
   }

   public synchronized void processUniqueId(UUID target) {
      if (target != null && target.version() != 4) {
         throw new IllegalArgumentException(
            "The Mojang ID provided is not valid! expected version = 4, received version = "
               + target.version()
               + ", username = "
               + this.activeName
               + ", uuid = "
               + target
         );
      }

      this.activeUniqueId = target;
   }

   public synchronized SpawnOption loadSpawnOption() {
      return this.computeSpawnOption(false);
   }

   public synchronized boolean findStateForState() {
      if (this.retrieveState()) {
         return false;
      }

      switch (this.loadSpawnOption()) {
         case SPAWN_OPTION:
            return !QuickPremiumOption.PRIVATE_QUICKPREMIUMOPTION.retrieveState() || !QuickPremiumOption.UPSTREAM_QUICKPREMIUMOPTION.retrieveState();
         case PENDING_SPAWNOPTION:
            return !QuickPremiumOption.VERIFIED_QUICKPREMIUMOPTION.retrieveState() || !QuickPremiumOption.AUTHENTICATED_QUICKPREMIUMOPTION.retrieveState();
         default:
            return true;
      }
   }

   public synchronized String fetchMessage() {
      if (this.activeName != null && QuickPremiumOption.CURRENT_QUICKPREMIUMOPTION.retrieveState()) {
         switch (this.loadSpawnOption()) {
            case SPAWN_OPTION:
               return Pbkdf2Linker.handleMessage(this.activeName, true);
            case ACTIVE_SPAWNOPTION:
            case CURRENT_SPAWNOPTION:
               return Pbkdf2Linker.handleMessage(this.activeName, false);
            case PENDING_SPAWNOPTION:
            default:
               return this.activeName;
         }
      } else {
         return this.activeName;
      }
   }

   public synchronized void handleMessage(String target, String input, @Nullable String output, String context, boolean data) {
      if (target != null && !target.isEmpty()) {
         if (!data || input != null && !input.isEmpty()) {
            this.activeName = target;
            if (context != null) {
               this.pendingName = context;
            } else if (this.pendingName == null) {
               this.pendingName = "127.0.0.1";
            }

            if (this.pendingUniqueId == null && !this.getState()) {
               this.handleUniqueId(DeadLoginFlow.computeUniqueId(target));
            }

            this.cachedPasswordHashHasher.performMessage("force-register-spawn");
            this.cachedPasswordHashHasher.performMessage("force-register-commands");
            long value = System.currentTimeMillis();
            this.timestamp = value;
            this.activeTimestamp = value;
            this.executeMessage(output != null ? output : input, data && output == null);
         } else {
            throw new IllegalArgumentException("Password cannot be null or empty!");
         }
      } else {
         throw new IllegalArgumentException("Real name cannot be null or empty!");
      }
   }

   public CachedPasswordHashHasher resolveCachedPasswordHashHasher() {
      return this.cachedPasswordHashHasher;
   }

   public synchronized void dispatchTaskForValue() {
      this.cachedPasswordHashHasher.performMessage("premium.force-notification");
   }

   public String retrieveMessage() {
      return this.activeName;
   }
}
