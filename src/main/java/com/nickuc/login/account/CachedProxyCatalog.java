package com.nickuc.login.account;

import com.nickuc.login.auth.login.OpenLoginService;
import com.nickuc.login.auth.login.PendingLoginService;
import com.nickuc.login.auth.login.PrivateLoginService;
import com.nickuc.login.auth.login.SharedLoginService;
import com.nickuc.login.auth.login.UpstreamLoginService;
import com.nickuc.login.auth.login.VerifiedLoginService;
import com.nickuc.login.config.SafeSettingsRegistry;
import com.nickuc.login.discord.DiscordBridge;
import com.nickuc.login.platform.player.LoudPlayerContract;
import com.nickuc.login.platform.packet.SilentPacketAdapter;
import com.nickuc.login.premium.DiscordLookup;
import com.nickuc.login.premium.SessionLinker;
import com.nickuc.login.storage.bungee.BungeeDao;
import com.nickuc.login.storage.session.BusySessionTable;
import com.nickuc.login.storage.session.CachedSessionCollection;
import com.nickuc.login.storage.session.CachedSessionStore;
import com.nickuc.login.storage.update.CachedUpdateStore;
import com.nickuc.login.storage.session.DeadSessionCollection;
import com.nickuc.login.storage.session.DeadSessionDao;
import com.nickuc.login.storage.session.DeadSessionRepository;
import com.nickuc.login.storage.session.FastSessionSource;
import com.nickuc.login.storage.session.IncomingSessionCollection;
import com.nickuc.login.storage.session.InternalSessionRepository;
import com.nickuc.login.storage.session.LocalSessionGateway;
import com.nickuc.login.storage.session.ParentSessionDao;
import com.nickuc.login.storage.update.PendingUpdateStore;
import com.nickuc.login.storage.session.PrimarySessionDao;
import com.nickuc.login.storage.session.ReadySessionTable;
import com.nickuc.login.storage.session.SecondarySessionStore;
import com.nickuc.login.storage.session.SessionArchive;
import com.nickuc.login.storage.session.SessionCollection;
import com.nickuc.login.storage.session.SessionDao;
import com.nickuc.login.storage.session.SessionGateway;
import com.nickuc.login.storage.session.SessionRepository;
import com.nickuc.login.storage.session.SessionSource;
import com.nickuc.login.storage.session.SessionStore;
import com.nickuc.login.storage.spawn.SpawnDao;
import com.nickuc.login.storage.update.UpdateStore;
import com.nickuc.login.storage.session.VerifiedSessionCollection;

public enum CachedProxyCatalog {
   CACHED_PROXY_CATALOG(SessionDao.class),
   ACTIVE_CACHEDPROXYCATALOG(SessionStore.class),
   PENDING_CACHEDPROXYCATALOG(VerifiedLoginService.class),
   CURRENT_CACHEDPROXYCATALOG(UpstreamLoginService.class),
   PRIMARY_CACHEDPROXYCATALOG(PrivateLoginService.class),
   MAIN_CACHEDPROXYCATALOG(SharedLoginService.class),
   LOCAL_CACHEDPROXYCATALOG(DeadSessionDao.class),
   REMOTE_CACHEDPROXYCATALOG(CachedSessionStore.class),
   CACHED_CACHEDPROXYCATALOG(CachedSessionCollection.class),
   STORED_CACHEDPROXYCATALOG(SessionSource.class),
   VERIFIED_CACHEDPROXYCATALOG(DiscordLookup.class),
   AUTHENTICATED_CACHEDPROXYCATALOG(DiscordBridge.class),
   SHARED_CACHEDPROXYCATALOG(UpdateStore.class),
   PRIVATE_CACHEDPROXYCATALOG(CachedUpdateStore.class),
   INTERNAL_CACHEDPROXYCATALOG(DeadSessionRepository.class),
   UPSTREAM_CACHEDPROXYCATALOG(ReadySessionTable.class),
   INCOMING_CACHEDPROXYCATALOG(InternalSessionRepository.class),
   OUTGOING_CACHEDPROXYCATALOG(BungeeDao.class),
   SECONDARY_CACHEDPROXYCATALOG(LocalSessionGateway.class),
   DIRECT_CACHEDPROXYCATALOG(SecondarySessionStore.class),
   LINKED_CACHEDPROXYCATALOG(PrimarySessionDao.class),
   ROOT_CACHEDPROXYCATALOG(IncomingSessionCollection.class),
   TOP_CACHEDPROXYCATALOG(DeadSessionCollection.class),
   FAST_CACHEDPROXYCATALOG(SessionRepository.class),
   SAFE_CACHEDPROXYCATALOG(BusySessionTable.class),
   SECURE_CACHEDPROXYCATALOG(SessionCollection.class),
   OPEN_CACHEDPROXYCATALOG(SessionArchive.class),
   READY_CACHEDPROXYCATALOG(ParentSessionDao.class),
   LIVE_CACHEDPROXYCATALOG(SpawnDao.class),
   ACTIVE_PENDING_CACHEDPROXYCATALOG(SafeSettingsRegistry.class),
   ACTIVE_CURRENT_CACHEDPROXYCATALOG(FastSessionSource.class),
   ACTIVE_PRIMARY_CACHEDPROXYCATALOG(PendingUpdateStore.class),
   ACTIVE_MAIN_CACHEDPROXYCATALOG(VerifiedSessionCollection.class),
   ACTIVE_LOCAL_CACHEDPROXYCATALOG(OpenLoginService.class),
   ACTIVE_REMOTE_CACHEDPROXYCATALOG(PendingLoginService.class),
   ACTIVE_CACHED_CACHEDPROXYCATALOG(SessionLinker.class),
   ACTIVE_STORED_CACHEDPROXYCATALOG(SessionGateway.class);

   private final LoudPlayerContract loudPlayerContract;

   CachedProxyCatalog(Class<? extends LoudPlayerContract> output) {
      try {
         this.loudPlayerContract = (LoudPlayerContract)output.getConstructor(CachedProxyCatalog.class).newInstance(this);
      } catch (ReflectiveOperationException data) {
         throw new RuntimeException(data);
      }
   }

   public int findCount() {
      return this.ordinal();
   }

   public LoudPlayerContract loadLoudPlayerContract() {
      return this.loudPlayerContract;
   }

   public static CachedProxyCatalog handleCachedProxyCatalog(int instance, boolean target) {
      CachedProxyCatalog[] input = loadValues(target);
      if (instance >= 0 && instance < input.length) {
         return input[instance];
      } else {
         throw new IllegalArgumentException("Invalid notification ID! " + instance);
      }
   }

   private static CachedProxyCatalog[] loadValues(boolean instance) {
      if (instance) {
         CachedProxyCatalog[] target = values();
         CachedProxyCatalog[] input = new CachedProxyCatalog[target.length];
         int output = 0;

         for (CachedProxyCatalog result : target) {
            if (result.loudPlayerContract instanceof SilentPacketAdapter) {
               input[output++] = result;
            }
         }

         for (CachedProxyCatalog entry : target) {
            if (!(entry.loudPlayerContract instanceof SilentPacketAdapter)) {
               input[output++] = entry;
            }
         }

         return input;
      } else {
         return values();
      }
   }

   public static int size() {
      return values().length;
   }
}
