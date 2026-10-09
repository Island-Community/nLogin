package com.nickuc.login.tasks;

import com.nickuc.login.config.PasswordHashContainer;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPooled;
import redis.clients.jedis.JedisPubSub;
import redis.clients.jedis.UnifiedJedis;
import com.nickuc.login.security.hashing.PasswordHashHasher;
import java.io.Closeable;
import java.util.function.Consumer;


public class RedisTicker extends JedisPubSub implements Closeable, Runnable {
   private final Consumer<String> consumer;
   private final String name;
   private boolean enabled;
   private final PasswordHashHasher passwordHashHasher;

   private boolean resolveState() {
      UnifiedJedis target = PasswordHashHasher.handleUnifiedJedis(this.passwordHashHasher);
      if (target instanceof JedisPooled) {
         return !((JedisPooled)target).getPool().isClosed();
      } else if (target instanceof JedisCluster) {
         return !((JedisCluster)target).getClusterNodes().isEmpty();
      } else {
         throw new RuntimeException("Unknown jedis type: " + target.getClass().getName());
      }
   }

   public void updateTask() {
      byte target = 1;

      while (!this.enabled && !Thread.interrupted() && this.resolveState()) {
         try {
            if (target != 0) {
               target = 0;
            } else {
               PasswordHashContainer.processMessage("[" + this.name + "] Redis pubsub connection re-established");
            }

            PasswordHashHasher.handleUnifiedJedis(this.passwordHashHasher).subscribe(this, new String[]{this.name});
         } catch (Exception value) {
            if (this.enabled) {
               return;
            }

            PasswordHashContainer.processMessage("[" + this.name + "] Redis pubsub connection dropped, trying to re-open the connection", value);

            try {
               this.unsubscribe();
            } catch (Exception data) {
            }

            try {
               Thread.sleep(5000L);
            } catch (InterruptedException context) {
               Thread.currentThread().interrupt();
            }
         }
      }
   }

   @Override
   public void close() {
      this.enabled = true;

      try {
         this.unsubscribe();
      } catch (Exception input) {
         PasswordHashContainer.processMessage("[" + this.name + "] Redis connection failed to close", input);
      }
   }

   private RedisTicker(PasswordHashHasher target, String input, Consumer<String> output) {
      this.passwordHashHasher = target;
      this.name = input;
      this.consumer = output;
   }

   public void dispatchMessage(String target, String input) {
      if (target.equals(this.name)) {
         this.consumer.accept(input);
      }
   }
}
