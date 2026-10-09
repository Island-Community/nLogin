package com.nickuc.login.premium;

import com.nickuc.login.account.InternalLoginOption;
import com.nickuc.login.account.QuickPremiumOption;
import com.nickuc.login.account.StrictPremiumOption;
import com.nickuc.login.command.PasswordHashCommand;
import com.nickuc.login.config.PasswordHashLoader;
import com.nickuc.login.platform.account.IncomingAccountHandler;
import com.nickuc.login.platform.server.NestedServerAdapter;
import com.nickuc.login.platform.proxy.SilentProxyState;
import com.nickuc.login.storage.password.PasswordStore;
import com.nickuc.login.storage.spawn.SpawnState;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nullable;


public class IndirectPasswordHashVerifier implements IncomingAccountHandler {
   private final Set<String> players;
   private final EnumMap<InternalLoginOption, NestedServerAdapter> sessions = new EnumMap<>(InternalLoginOption.class);
   private final Set<String> activePlayers;
   private final Map<String, InternalLoginOption> activeSessions = new ConcurrentHashMap<>();
   private final PasswordStore passwordStore;

   public Set<String> resolveSet() {
      return this.activePlayers;
   }

   public void performTask() {
      this.dispatchTask();

      for (InternalLoginOption context : InternalLoginOption.values()) {
         this.saveInternalLoginOption(context);
      }
   }

   public void dispatchTask() {
      this.activePlayers.clear();
      this.players.clear();
      synchronized (this.sessions) {
         this.sessions.forEach((instance, target) -> target.saveTask());
      }
   }

   @Override
   public void processPasswordStore(PasswordStore target, boolean input) {
      if (target.findIncomingLoginGate().retrieveSilentProxyState() != SilentProxyState.SILENT_PROXY_STATE) {
         this.performTask();
      }
   }

   private void saveInternalLoginOption(InternalLoginOption target) {
      PasswordHashLoader input = this.passwordStore.a();
      String output = target.findMessage();
      PasswordHashCommand context = target.fetchPasswordHashCommand();
      boolean data = target.resolveState();
      String value = "commands." + output;
      byte result = 1;
      String response = null;
      List request;
      String source;
      if (target != InternalLoginOption.INTERNAL_LOGIN_OPTION) {
         result = input.a(value + ".enable", input.a(value + ".enabled", true));
         request = input.processCollection(value + ".commands");
         response = input.b(value + ".permission");
         source = input.a(value + ".description", "Failed to load the command description");
         if (result != 0) {
            switch (output) {
               case "discord":
                  if (!StrictPremiumOption.STRICT_PREMIUM_OPTION.retrieveState()) {
                     result = 0;
                  }
                  break;
               case "email":
                  if (!StrictPremiumOption.VERIFIED_STRICTPREMIUMOPTION.retrieveState()) {
                     result = 0;
                  }
                  break;
               case "offline":
               case "premium":
                  if (QuickPremiumOption.CURRENT_QUICKPREMIUMOPTION.retrieveState()) {
                     result = 0;
                  }
               case "recover":
                  if (!QuickPremiumOption.PRIVATE_QUICKPREMIUMOPTION.retrieveState()) {
                     result = 0;
                  }
            }
         }
      } else {
         request = Collections.singletonList("nlogin");
         source = "nLogin primary command";
      }

      if (result != 0) {
         if (request.isEmpty()) {
            request = Collections.singletonList(output);
         } else if (request.size() > 1) {
            context.loadPasswordHashCommand(new ArrayList<>(request).subList(1, request.size()));
         }

         if (response != null) {
            context.executeMessage(response);
         }

         context.computePasswordHashCommand(source);
         context.buildPasswordHashCommand((String)request.get(0));
         String content = '/' + this.passwordStore.retrieveMessage().toLowerCase(Locale.ENGLISH) + ':';
         this.activePlayers.add(content);
         request.stream().map(instance -> instance.toLowerCase(Locale.ENGLISH)).forEach(contextValue -> {
            if (data) {
               this.players.add('/' + contextValue);
               this.players.add(content + contextValue);
            }

            this.activePlayers.add('/' + contextValue);
            this.activeSessions.put(contextValue, target);
         });
         synchronized (this.sessions) {
            this.sessions.put(target, context.handleNestedServerAdapter(this.passwordStore));
         }
      }
   }

   @Nullable
   public InternalLoginOption computeInternalLoginOption(String target) {
      return this.activeSessions.get(target);
   }

   public IndirectPasswordHashVerifier(PasswordStore target) {
      this.activePlayers = ConcurrentHashMap.newKeySet();
      this.players = ConcurrentHashMap.newKeySet();
      this.passwordStore = target;
   }

   public boolean validateState(String target) {
      if (this.players.stream().anyMatch(target::equals)) {
         return false;
      }

      List input = SpawnState.PENDING_UPSTREAM_SPAWNSTATE.a(new Object[0]);
      return input.stream().noneMatch(targetValue -> !targetValue.isEmpty() && (targetValue.equals("*") || target.equals(targetValue)));
   }
}
