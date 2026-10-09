package com.nickuc.login.storage.password;

import com.nickuc.login.model.PremiumState;
import com.nickuc.login.model.QuickMessageKind;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.nickuc.login.redis.PasswordLink;
import java.net.InetAddress;
import java.util.concurrent.TimeUnit;

public class PasswordGateway {
   private static final Cache<String, QuickMessageKind> cache = Caffeine.newBuilder().expireAfterWrite(45L, TimeUnit.SECONDS).build();
   private static final Cache<String, PremiumState> activeCache = Caffeine.newBuilder().expireAfterWrite(15L, TimeUnit.SECONDS).build();

   public static QuickMessageKind resolveQuickMessageKind(String instance, InetAddress target) {
      String input = instance + target.getHostAddress();
      QuickMessageKind output = (QuickMessageKind)cache.getIfPresent(input);
      return output != null ? output : QuickMessageKind.QUICK_MESSAGE_KIND;
   }

   private static void sendPasswordStore(PasswordStore instance, String target, String input, Enum<?> output) {
      PasswordLink context = instance.findPasswordLink();
      if (context != null) {
         context.performCount(0, outputValue -> {
            outputValue.put("player", target);
            outputValue.put("ip", input);
            outputValue.put("mode", output.name());
         });
      }
   }

   public static void handleMessage(String instance, String target, QuickMessageKind input) {
      String output = instance + target;
      if (input != QuickMessageKind.QUICK_MESSAGE_KIND) {
         cache.put(output, input);
      } else {
         cache.invalidate(output);
      }
   }

   public static void processPasswordStore(PasswordStore instance, String target, InetAddress input, PremiumState output) {
      String context = input.getHostAddress();
      sendPasswordStore(instance, target, context, output);
      dispatchMessage(target, context, output);
   }

   public static void savePasswordStore(PasswordStore instance, String target, InetAddress input, QuickMessageKind output) {
      String context = input.getHostAddress();
      sendPasswordStore(instance, target, context, output);
      handleMessage(target, context, output);
   }

   public static void dispatchMessage(String instance, String target, PremiumState input) {
      String output = instance + target;
      if (input != PremiumState.PREMIUM_STATE) {
         activeCache.put(output, input);
      } else {
         activeCache.invalidate(output);
      }
   }

   public static PremiumState loadPremiumState(String instance, InetAddress target) {
      String input = instance + target.getHostAddress();
      PremiumState output = (PremiumState)activeCache.getIfPresent(input);
      return output != null ? output : PremiumState.PREMIUM_STATE;
   }
}
