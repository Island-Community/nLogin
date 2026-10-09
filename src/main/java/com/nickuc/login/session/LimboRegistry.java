package com.nickuc.login.session;

import com.nickuc.login.auth.login.DeadLoginFlow;
import com.nickuc.login.model.LenientMessageKind;
import com.nickuc.login.model.TightPlatformCatalog;
import com.nickuc.login.limbo.LimboStore;
import com.nickuc.login.platform.packet.LoudPacketAdapter;
import com.nickuc.login.platform.proxy.SilentProxyState;
import com.nickuc.login.platform.command.StrictCommandHandler;
import com.nickuc.login.platform.server.VerifiedServerAdapter;
import com.nickuc.login.premium.SpawnLookup;
import com.nickuc.login.storage.password.PasswordStore;
import com.nickuc.login.storage.spawn.SpawnState;
import java.io.File;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nullable;


public class LimboRegistry {
   private final PasswordStore passwordStore;
   private final StrictCommandHandler strictCommandHandler;
   private final Map<VerifiedServerAdapter, LimboCoordinator> sessions = new ConcurrentHashMap<>();
   private final Map<Integer, LimboCoordinator> activeSessions = new ConcurrentHashMap<>();

   public LimboCoordinator loadLimboCoordinator(VerifiedServerAdapter target) {
      String input = target.getName();
      if (this.canState(input, target.getUniqueId())) {
         throw new IllegalStateException("Unable to create a cache session for an unrestricted user! " + input);
      } else {
         LimboCoordinator output = this.buildLimboCoordinator(target);
         if (output == null) {
            throw new IllegalStateException("Player session not set for " + input + "!");
         } else {
            return output;
         }
      }
   }

   public void saveVerifiedServerAdapter(VerifiedServerAdapter target) {
      LimboCoordinator input = this.buildLimboCoordinator(target);
      if (input != null) {
         input.atomicInteger.set(-1);
      }
   }

   public boolean canState(VerifiedServerAdapter target) {
      if (this.canState(target.getName(), target.getUniqueId())) {
         return true;
      }

      LimboCoordinator input = this.buildLimboCoordinator(target);
      return input == null ? false : input.loadTightPlatformCatalog().isState(TightPlatformCatalog.LOCAL_TIGHTPLATFORMCATALOG);
   }

   public LimboCoordinator loadLimboCoordinator(
      VerifiedServerAdapter target, @Nullable SpawnLookup input, String output, InetSocketAddress context, boolean data, boolean value, LoudPacketAdapter result
   ) {
      LimboCoordinator request = this.sessions.computeIfAbsent(target, targetValue -> new LimboCoordinator(targetValue, context));
      int response = request.atomicInteger.getAndSet(1);
      switch (response) {
         case -2:
            request.atomicInteger.set(response);
            throw new IllegalStateException("Player session is inactive!");
         case -1:
            request.atomicInteger.set(response);
            throw new IllegalStateException("Player session is invalid!");
         case 0:
         default:
            this.activeSessions.put(Objects.hash(target.getName(), target.getUniqueId(), request.d(LenientMessageKind.CURRENT_LENIENTMESSAGEKIND)), request);
            if (input != null) {
               request.updateLenientMessageKind(LenientMessageKind.LENIENT_MESSAGE_KIND, input);
            }

            request.updateLenientMessageKind(LenientMessageKind.ACTIVE_LENIENTMESSAGEKIND, output);
            request.updateLenientMessageKind(LenientMessageKind.PENDING_LENIENTMESSAGEKIND, data);
            request.updateLenientMessageKind(LenientMessageKind.PRIMARY_LENIENTMESSAGEKIND, value);
            request.updateLenientMessageKind(LenientMessageKind.MAIN_LENIENTMESSAGEKIND, result);
            request.updateLenientMessageKind(LenientMessageKind.UPSTREAM_LENIENTMESSAGEKIND, TightPlatformCatalog.TIGHT_PLATFORM_CATALOG);
            if (this.passwordStore.findIncomingLoginGate().retrieveSilentProxyState() == SilentProxyState.SILENT_PROXY_STATE) {
               BusyLimboStore source = (BusyLimboStore)this.passwordStore.findSettingsLinker();
               String entry = DeadLoginFlow.processMessage(target.getUniqueId()) + ".limbo";
               File record = new File(source.loadSpawnSession().fetchFile(), entry);
               LimboStore item = new LimboStore(target.findObject(), record);
               request.updateLenientMessageKind(LenientMessageKind.LOCAL_LENIENTMESSAGEKIND, item);
            }

            return request;
         case 1:
            throw new IllegalStateException("Player session already active!");
      }
   }

   public boolean canState(String target, @Nullable UUID input) {
      int output = target.length();
      return output > 1 && target.charAt(0) == '[' && target.charAt(output - 1) == ']'
         || SpawnState.READY_SPAWNSTATE
            .a(new Object[0])
            .stream()
            .anyMatch(inputValue -> inputValue.equalsIgnoreCase(target) || input != null && inputValue.equalsIgnoreCase(input.toString()));
   }

   @Nullable
   public LimboCoordinator buildLimboCoordinator(VerifiedServerAdapter target) {
      LimboCoordinator input = this.sessions.get(target);
      if (input == null) {
         InetSocketAddress output = target.fetchInetSocketAddress();
         if (output != null) {
            input = this.resolveLimboCoordinator(target.getName(), target.getUniqueId(), output.getAddress());
            if (input != null && input.atomicInteger.get() == 1) {
               this.sessions.put(target, input);
            }
         }
      }

      return input;
   }

   public LimboRegistry(PasswordStore target) {
      this.passwordStore = target;
      this.strictCommandHandler = target.processLinkedSessionHandler(true).processStrictCommandHandler(() -> {
         if (!this.sessions.isEmpty()) {
            this.sessions.values().removeIf(targetValue -> {
               if (targetValue.atomicInteger.get() == 1) {
                  return false;
               }

               int input = targetValue.atomicInteger.getAndSet(-2);
               switch (input) {
                  case -2:
                     VerifiedServerAdapter output = targetValue.fetchVerifiedServerAdapter();
                     this.activeSessions.remove(Objects.hash(output.getName(), output.getUniqueId(), targetValue.d(LenientMessageKind.CURRENT_LENIENTMESSAGEKIND)));
                     return true;
                  case 1:
                     targetValue.atomicInteger.set(1);
                     return false;
                  default:
                     return false;
               }
            });
         }
      }, 0L, 15L, TimeUnit.SECONDS);
   }

   @Nullable
   public LimboCoordinator resolveLimboCoordinator(String target, UUID input, InetAddress output) {
      return this.activeSessions.get(Objects.hash(target, input, output.getHostAddress()));
   }

   public LimboRegistry(PasswordStore target, StrictCommandHandler input) {
      this.passwordStore = target;
      this.strictCommandHandler = input;
   }

   public void executeTask() {
      this.strictCommandHandler.performTask();
   }
}
