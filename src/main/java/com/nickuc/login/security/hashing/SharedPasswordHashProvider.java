package com.nickuc.login.security.hashing;

import com.nickuc.login.auth.login.LoginProcessor;
import com.nickuc.login.discord.PasswordHashBridge;
import com.nickuc.login.model.InternalSpawnState;
import com.nickuc.login.platform.sender.TightSenderAdapter;
import com.nickuc.login.spawn.LoginLocator;
import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class SharedPasswordHashProvider {
   public static final String name = "com.nickuc.%s.lib.%s";
   private final Method method;
   public static final TightSenderAdapter[] values = new TightSenderAdapter[]{
      InternalSpawnState.INTERNAL_SPAWN_STATE, InternalSpawnState.ACTIVE_INTERNALSPAWNSTATE, InternalSpawnState.PENDING_INTERNALSPAWNSTATE
   };
   private final Constructor<?> constructor;
   private final PasswordHashBridge passwordHashBridge;

   public SharedPasswordHashProvider(PasswordHashBridge target) {
      try {
         this.passwordHashBridge = target;
         target.verifyState(values);
         LoginProcessor input = target.computeLoginProcessor(values);
         Class output = input.loadClass("me.lucko.jarrelocator.JarRelocator");
         this.constructor = output.getDeclaredConstructor(File.class, File.class, Map.class);
         this.constructor.setAccessible(true);
         this.method = output.getDeclaredMethod("run");
         this.method.setAccessible(true);
      } catch (Exception context) {
         throw new RuntimeException(context);
      }
   }

   public void saveFile(File target, File input, TightSenderAdapter output) {
      HashMap context = new HashMap();

      for (LoginLocator value : output.loadCollection()) {
         String result = value.loadMessage();
         String request = value.findMessage() + ".";
         String response = String.format("com.nickuc.%s.lib.%s", this.passwordHashBridge.fetchMessage(), result);
         if (!result.isEmpty()) {
            response = response + ".";
         }

         context.put(request, response);
      }

      Object source = this.constructor.newInstance(target, input, context);
      this.method.invoke(source);
   }
}
