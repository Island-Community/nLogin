package com.nickuc.login.session;

import com.nickuc.login.model.LenientMessageKind;
import com.nickuc.login.model.LenientPremiumOption;
import com.nickuc.login.model.TightPlatformCatalog;
import com.nickuc.login.limbo.LimboStore;
import com.nickuc.login.platform.sender.IncomingSenderAdapter;
import com.nickuc.login.platform.packet.LoudPacketAdapter;
import com.nickuc.login.platform.account.SecondaryAccountHandler;
import com.nickuc.login.platform.server.VerifiedServerAdapter;
import com.nickuc.login.premium.SpawnLookup;
import com.nickuc.login.storage.settings.CachedSettingsGateway;
import com.nickuc.login.storage.password.PasswordStore;
import com.nickuc.login.storage.spawn.SpawnState;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import javax.annotation.Nullable;


public class LimboCoordinator implements IncomingSenderAdapter<LenientMessageKind> {
   public static final int count = 1;
   public static final int activeCount = -2;
   public static final int pendingCount = -1;
   private final Map<String, Object> sessions = new ConcurrentHashMap<>();
   public static final int currentCount = 0;
   private final VerifiedServerAdapter verifiedServerAdapter;
   public final AtomicInteger atomicInteger;
   public final Object object = new Object();

   public SpawnLookup loadSpawnLookup() {
      SpawnLookup target = (SpawnLookup)this.loadObject(LenientMessageKind.LENIENT_MESSAGE_KIND);
      if (target != null) {
         return target;
      } else {
         throw new IllegalStateException("Account is not loaded for " + this.verifiedServerAdapter.getName() + "!");
      }
   }

   @Nullable
   public Object createObject(LenientMessageKind target) {
      return this.sessions.get(target.findMessage());
   }

   @Nullable
   public Integer findInteger() {
      Integer target = (Integer)this.loadObject(LenientMessageKind.SECURE_LENIENTMESSAGEKIND);
      if (target != null) {
         int input = this.a(LenientMessageKind.OPEN_LENIENTMESSAGEKIND, SpawnState.ACTIVE_PENDING_SPAWNSTATE.r());
         return input - target;
      } else {
         return null;
      }
   }

   public LimboCoordinator(VerifiedServerAdapter target, InetSocketAddress input) {
      this.atomicInteger = new AtomicInteger(0);
      this.verifiedServerAdapter = target;
      this.updateLenientMessageKind(LenientMessageKind.CURRENT_LENIENTMESSAGEKIND, input);
   }

   public TightPlatformCatalog loadTightPlatformCatalog() {
      return (TightPlatformCatalog)this.a(LenientMessageKind.UPSTREAM_LENIENTMESSAGEKIND, TightPlatformCatalog.TIGHT_PLATFORM_CATALOG);
   }

   public LimboStore resolveLimboStore() {
      LimboStore target = (LimboStore)this.loadObject(LenientMessageKind.LOCAL_LENIENTMESSAGEKIND);
      if (target != null) {
         return target;
      } else {
         throw new IllegalStateException("Player limbo is not loaded for " + this.verifiedServerAdapter.getName() + "!");
      }
   }

   public void updateLenientMessageKind(LenientMessageKind target, Object input) {
      this.sessions.put(target.findMessage(), input);
   }

   public <T> T resolveObject(LenientMessageKind target, Function<String, T> input) {
      return (T)this.sessions.computeIfAbsent(target.findMessage(), input);
   }

   @Nullable
   public <T> T loadObject(LenientMessageKind target) {
      return (T)this.sessions.remove(target.findMessage());
   }

   public VerifiedServerAdapter fetchVerifiedServerAdapter() {
      return this.verifiedServerAdapter;
   }

   public SecondaryAccountHandler getSecondaryAccountHandler() {
      return this.resolveObject(
         LenientMessageKind.SHARED_LENIENTMESSAGEKIND,
         target -> SecondaryAccountHandler.processSecondaryAccountHandler(PasswordStore.resolvePasswordStore(), this.verifiedServerAdapter, this)
      );
   }

   public LenientPremiumOption fetchLenientPremiumOption() {
      LenientPremiumOption target = CachedSettingsGateway.loadLenientPremiumOption();
      return SpawnState.PENDING_SHARED_SPAWNSTATE.ar() ? (LenientPremiumOption)this.a(LenientMessageKind.CACHED_LENIENTMESSAGEKIND, target) : target;
   }

   public void saveTask() {
      if (this.isState(LenientMessageKind.SECURE_LENIENTMESSAGEKIND)) {
         this.updateLenientMessageKind(LenientMessageKind.SECURE_LENIENTMESSAGEKIND, this.a(LenientMessageKind.SECURE_LENIENTMESSAGEKIND, -1) + 1);
      }
   }

   public boolean isState(LenientMessageKind target) {
      return this.sessions.containsKey(target.findMessage());
   }

   public LoudPacketAdapter findLoudPacketAdapter() {
      LoudPacketAdapter target = (LoudPacketAdapter)this.loadObject(LenientMessageKind.MAIN_LENIENTMESSAGEKIND);
      if (target != null) {
         return target;
      } else {
         throw new IllegalStateException("Connection data is not loaded for " + this.verifiedServerAdapter.getName() + "!");
      }
   }

   @Override
   public String toString() {
      return "PlayerSession(map="
         + this.sessions
         + ", player="
         + this.fetchVerifiedServerAdapter()
         + ", lock="
         + this.object
         + ", state="
         + this.atomicInteger
         + ")";
   }

   @Nullable
   public LenientPremiumOption retrieveLenientPremiumOption() {
      return (LenientPremiumOption)this.loadObject(LenientMessageKind.CACHED_LENIENTMESSAGEKIND);
   }

   public void executeTightPlatformCatalog(TightPlatformCatalog target, @Nullable TightPlatformCatalog input) {
      synchronized (this.object) {
         if (input != null) {
            TightPlatformCatalog context = this.loadTightPlatformCatalog();
            if (input != context) {
               throw new IllegalStateException(
                  "Unexpected auth state while finishing authentication! player = "
                     + this.verifiedServerAdapter.getName()
                     + ", current = "
                     + context
                     + ", expected = "
                     + input
               );
            }
         }

         this.updateLenientMessageKind(LenientMessageKind.UPSTREAM_LENIENTMESSAGEKIND, target);
      }
   }

   public boolean loadState() {
      LenientPremiumOption target = this.fetchLenientPremiumOption();
      return target == LenientPremiumOption.LENIENT_PREMIUM_OPTION || target == LenientPremiumOption.INCOMING_LENIENTPREMIUMOPTION;
   }

   public String loadMessage() {
      String target = (String)this.loadObject(LenientMessageKind.ACTIVE_LENIENTMESSAGEKIND);
      if (target != null) {
         return target;
      } else {
         throw new IllegalStateException("Real name is not loaded for " + this.verifiedServerAdapter.getName() + "!");
      }
   }
}
