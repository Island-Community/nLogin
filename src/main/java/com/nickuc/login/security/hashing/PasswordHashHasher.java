package com.nickuc.login.security.hashing;

import redis.clients.jedis.DefaultJedisClientConfig;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisClientConfig;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPooled;
import redis.clients.jedis.UnifiedJedis;
import com.nickuc.login.platform.session.IndirectSessionHandler;
import com.nickuc.login.tasks.RedisTicker;
import java.io.Closeable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;


public class PasswordHashHasher implements Closeable {
   private boolean enabled;
   private final UnifiedJedis unifiedJedis;
   private final IndirectSessionHandler<?> indirectSessionHandler;
   private final Map<String, RedisTicker> sessions = new HashMap<>();

   public void updateMessage(String target, Consumer<String> input) {
      if (this.enabled) {
         throw new IllegalStateException("This Redis instance is closed!");
      }

      synchronized (this.sessions) {
         RedisTicker context = new RedisTicker(this, target, input, null);
         this.sessions.put(target, context);
         this.indirectSessionHandler.processLinkedSessionHandler(true).buildStrictCommandHandler(context);
      }
   }

   public static PasswordHashHasher resolvePasswordHashHasher(IndirectSessionHandler<?> instance, List<String> target, String input, String output, boolean context) {
      Set data = target.stream().map(PasswordHashHasher::createHostAndPort).collect(Collectors.toSet());
      return new PasswordHashHasher(instance, new JedisCluster(data, loadJedisClientConfig(input, output, context)));
   }

   private PasswordHashHasher(IndirectSessionHandler<?> target, UnifiedJedis input) {
      this.indirectSessionHandler = target;
      this.unifiedJedis = input;
   }

   public void performMessage(String target, String input) {
      this.unifiedJedis.publish(target, input);
   }

   private static JedisClientConfig loadJedisClientConfig(String instance, String target, boolean input) {
      return DefaultJedisClientConfig.builder().user(instance).password(target).ssl(input).timeoutMillis(2000).build();
   }

   public void saveMessage(String target) {
      if (this.enabled) {
         throw new IllegalStateException("This Redis instance is closed!");
      }

      synchronized (this.sessions) {
         RedisTicker output = this.sessions.remove(target);
         if (output != null) {
            output.close();
         }
      }
   }

   @Override
   public void close() {
      this.enabled = true;
      synchronized (this.sessions) {
         this.sessions.values().forEach(RedisTicker::close);
      }

      this.unifiedJedis.close();
   }

   private static HostAndPort createHostAndPort(String instance) {
      String[] target = instance.split(":");
      String input = target[0];
      int output = target.length > 1 ? Integer.parseInt(target[1]) : 6379;
      return new HostAndPort(input, output);
   }

   public static PasswordHashHasher loadPasswordHashHasher(IndirectSessionHandler<?> instance, String target, String input, String output, boolean context) {
      return new PasswordHashHasher(instance, new JedisPooled(createHostAndPort(target), loadJedisClientConfig(input, output, context)));
   }
}
