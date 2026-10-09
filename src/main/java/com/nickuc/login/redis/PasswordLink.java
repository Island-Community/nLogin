package com.nickuc.login.redis;

import com.nickuc.login.config.PasswordHashLoader;
import org.json.JSONObject;
import com.nickuc.login.security.hashing.PasswordHashHasher;
import com.nickuc.login.storage.password.PasswordStore;
import com.nickuc.login.storage.redis.RedisSource;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import javax.annotation.Nullable;

public class PasswordLink {
   public static final String name = "nlogin:sync";
   public static final int count = 0;
   public static final int activeCount = 1;
   private final PasswordHashHasher passwordHashHasher;

   private PasswordLink(PasswordStore target, PasswordHashHasher input) {
      this.passwordHashHasher = input;
      input.updateMessage("nlogin:sync", new RedisSource(target, null));
   }

   @Nullable
   public static PasswordLink loadPasswordLink(PasswordStore instance) {
      PasswordHashLoader target = instance.a();
      if (!target.d("redis.enable") && !target.d("redis.enabled")) {
         return null;
      }

      Object input = target.d("redis.hostname");
      if (input == null) {
         return null;
      }

      List output;
      if (input instanceof String) {
         output = Collections.singletonList(input.toString());
      } else {
         if (!(input instanceof List)) {
            throw new IllegalArgumentException("Invalid Redis hostname option! " + input);
         }

         List context = (List)input;
         output = context.stream().map(Object::toString).collect(Collectors.toList());
      }

      if (output.isEmpty()) {
         throw new IllegalArgumentException("Redis hostname cannot be empty!");
      }

      String request = target.b("redis.username");
      String data = target.b("redis.password");
      boolean value = target.d("redis.ssl");
      if (request != null && request.isEmpty()) {
         request = null;
      }

      if (data != null && data.isEmpty()) {
         data = null;
      }

      PasswordHashHasher result = output.size() > 1
         ? PasswordHashHasher.resolvePasswordHashHasher(instance, output, request, data, value)
         : PasswordHashHasher.loadPasswordHashHasher(instance, (String)output.get(0), request, data, value);
      return new PasswordLink(instance, result);
   }

   public void executeTask() {
      this.passwordHashHasher.close();
   }

   public void performCount(int target, Consumer<JSONObject> input) {
      JSONObject output = new JSONObject();
      output.put("id", target);
      JSONObject context = new JSONObject();
      input.accept(context);
      output.put("data", context);
      this.passwordHashHasher.performMessage("nlogin:sync", output.toString());
   }
}
