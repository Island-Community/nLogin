package com.nickuc.login.model;

import com.nickuc.login.auth.login.LiveLoginCheckpoint;
import com.nickuc.login.auth.login.PrivateLoginGate;
import com.nickuc.login.auth.login.RemoteLoginBarrier;
import com.nickuc.login.config.PasswordHashContainer;
import com.nickuc.login.discord.PasswordHashBridge;
import com.nickuc.login.platform.session.IndirectSessionHandler;
import com.nickuc.login.platform.sender.TightSenderAdapter;
import com.nickuc.login.security.hashing.PendingPasswordHashHasher;
import com.nickuc.login.security.hashing.SharedPasswordHashProvider;
import com.nickuc.login.security.hashing.VerifiedPasswordHashHasher;
import com.nickuc.login.spawn.LoginLocator;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;


public enum InternalSpawnState implements TightSenderAdapter {
   INTERNAL_SPAWN_STATE("org.ow2.asm", "asm", "9.8"),
   ACTIVE_INTERNALSPAWNSTATE("org.ow2.asm", "asm-commons", "9.8"),
   PENDING_INTERNALSPAWNSTATE("me.lucko", "jar-relocator", "1.8"),
   CURRENT_INTERNALSPAWNSTATE("org{}json", "json", "20250517", "JSONObject", LoginLocator.resolveLoginLocator("json", "org{}json")),
   PRIMARY_INTERNALSPAWNSTATE("net{}lingala{}zip4j", "zip4j", "2.11.5", "ZipFile", LoginLocator.resolveLoginLocator("zip4j", "net{}lingala{}zip4j")),
   MAIN_INTERNALSPAWNSTATE("org{}yaml", "snakeyaml", "2.4", "Yaml", LoginLocator.resolveLoginLocator("snakeyaml", "org{}yaml{}snakeyaml")),
   LOCAL_INTERNALSPAWNSTATE("org{}bouncycastle", "bcprov-lts8on", "2.73.10", LoginLocator.resolveLoginLocator("bouncycastle", "org{}bouncycastle")),
   REMOTE_INTERNALSPAWNSTATE("org{}bouncycastle", "bcutil-lts8on", "2.73.10", LoginLocator.resolveLoginLocator("bouncycastle", "org{}bouncycastle")),
   CACHED_INTERNALSPAWNSTATE("org{}bouncycastle", "bcpg-lts8on", "2.73.10", LoginLocator.resolveLoginLocator("bouncycastle", "org{}bouncycastle")),
   STORED_INTERNALSPAWNSTATE("com{}google{}guava", "guava", "33.4.8-jre", "common.cache.AbstractCache", LoginLocator.resolveLoginLocator("guava", "com.google")),
   VERIFIED_INTERNALSPAWNSTATE(
      "com{}google{}guava",
      "failureaccess",
      "1.0.3",
      "common.util.concurrent.internal.InternalFutures",
      LoginLocator.resolveLoginLocator("guava", "com.google")
   ),
   AUTHENTICATED_INTERNALSPAWNSTATE(
      "com{}github{}ben-manes{}caffeine",
      "caffeine",
      "2.9.3",
      "cache.Caffeine",
      LoginLocator.resolveLoginLocator("caffeine", "com{}github{}benmanes{}caffeine")
   ),
   SHARED_INTERNALSPAWNSTATE(
      "com{}github{}ben-manes{}caffeine",
      "caffeine",
      "3.2.2",
      "cache.Caffeine",
      LoginLocator.resolveLoginLocator("caffeine", "com{}github{}benmanes{}caffeine")
   ),
   PRIVATE_INTERNALSPAWNSTATE("org.slf4j", "slf4j-api", "1.7.36", "org.slf4j.ILoggerFactory"),
   INTERNAL_INTERNALSPAWNSTATE("org.slf4j", "slf4j-simple", "1.7.36", "org.slf4j.impl.SimpleLogger"),
   UPSTREAM_INTERNALSPAWNSTATE("com{}zaxxer{}hikari", "HikariCP", "4.0.3", "HikariConfig", LoginLocator.resolveLoginLocator("hikari", "com{}zaxxer{}hikari")),
   INCOMING_INTERNALSPAWNSTATE(
      "org.apache.commons", "commons-pool2", "2.12.1", "PoolUtils", LoginLocator.resolveLoginLocator("commonspool2", "org{}apache{}commons{}pool2")
   ),
   OUTGOING_INTERNALSPAWNSTATE(
      "redis{}clients{}authentication",
      "redis-authx-core",
      "0.1.1-beta2",
      LoginLocator.resolveLoginLocator("jedis", "redis{}clients{}jedis"),
      LoginLocator.resolveLoginLocator("redis.authx", "redis{}clients{}authentication"),
      LoginLocator.resolveLoginLocator("commonspool2", "org{}apache{}commons{}pool2"),
      LoginLocator.resolveLoginLocator("json", "org{}json")
   ),
   SECONDARY_INTERNALSPAWNSTATE(
      "redis{}clients",
      "jedis",
      "7.1.0",
      "UnifiedJedis",
      LoginLocator.resolveLoginLocator("jedis", "redis{}clients{}jedis"),
      LoginLocator.resolveLoginLocator("redis.authx", "redis{}clients{}authentication"),
      LoginLocator.resolveLoginLocator("commonspool2", "org{}apache{}commons{}pool2"),
      LoginLocator.resolveLoginLocator("json", "org{}json")
   ),
   DIRECT_INTERNALSPAWNSTATE("org{}mariadb{}jdbc", "mariadb-java-client", "3.5.5", LoginLocator.resolveLoginLocator("mariadb", "org{}mariadb{}jdbc")),
   LINKED_INTERNALSPAWNSTATE("com{}mysql", "mysql-connector-j", "9.4.0", LoginLocator.resolveLoginLocator("mysql", "com{}mysql")),
   ROOT_INTERNALSPAWNSTATE("org{}postgresql", "postgresql", "42.7.7"),
   TOP_INTERNALSPAWNSTATE("com.h2database", "h2", "1.4.199"),
   FAST_INTERNALSPAWNSTATE("com.h2database", "h2", "2.1.214"),
   SAFE_INTERNALSPAWNSTATE("com.h2database", "h2", "2.3.232"),
   SECURE_INTERNALSPAWNSTATE("org.xerial", "sqlite-jdbc", "3.50.3.0", "org.sqlite.JDBC");

   private final String name;
   private final String activeName;
   private final String pendingName;
   private final String currentName;
   private final List<LoginLocator> entries;

   @Override
   public String getMessage() {
      return this.currentName;
   }

   @Override
   public String retrieveMessage() {
      return this.name;
   }

   @Override
   public String getVersion() {
      return this.pendingName;
   }

   InternalSpawnState(String output, String context, String data, LoginLocator... value) {
      this(output, context, data, null, value);
   }

   @Override
   public boolean getState() {
      switch (this) {
         case INTERNAL_SPAWN_STATE:
         case ACTIVE_INTERNALSPAWNSTATE:
         case PENDING_INTERNALSPAWNSTATE:
         case TOP_INTERNALSPAWNSTATE:
         case FAST_INTERNALSPAWNSTATE:
         case SAFE_INTERNALSPAWNSTATE:
         case SECURE_INTERNALSPAWNSTATE:
            return true;
         default:
            return this.fetchState();
      }
   }

   @Override
   public boolean fetchState() {
      int target = PrivateLoginGate.loadCount() >= 11 ? 1 : 0;
      switch (this) {
         case INTERNAL_SPAWN_STATE:
         case ACTIVE_INTERNALSPAWNSTATE:
         case PENDING_INTERNALSPAWNSTATE:
         case TOP_INTERNALSPAWNSTATE:
         case FAST_INTERNALSPAWNSTATE:
         case SAFE_INTERNALSPAWNSTATE:
         case SECURE_INTERNALSPAWNSTATE:
            return false;
         case CURRENT_INTERNALSPAWNSTATE:
         case PRIMARY_INTERNALSPAWNSTATE:
         case MAIN_INTERNALSPAWNSTATE:
         case LOCAL_INTERNALSPAWNSTATE:
         case REMOTE_INTERNALSPAWNSTATE:
         case CACHED_INTERNALSPAWNSTATE:
         case STORED_INTERNALSPAWNSTATE:
         case VERIFIED_INTERNALSPAWNSTATE:
         case PRIVATE_INTERNALSPAWNSTATE:
         case INTERNAL_INTERNALSPAWNSTATE:
         case UPSTREAM_INTERNALSPAWNSTATE:
         case INCOMING_INTERNALSPAWNSTATE:
         case OUTGOING_INTERNALSPAWNSTATE:
         case SECONDARY_INTERNALSPAWNSTATE:
         case DIRECT_INTERNALSPAWNSTATE:
         case LINKED_INTERNALSPAWNSTATE:
         case ROOT_INTERNALSPAWNSTATE:
         default:
            return true;
         case AUTHENTICATED_INTERNALSPAWNSTATE:
         case SHARED_INTERNALSPAWNSTATE:
            return target != 0 ? this == SHARED_INTERNALSPAWNSTATE : this == AUTHENTICATED_INTERNALSPAWNSTATE;
      }
   }

   @Override
   public List<LoginLocator> loadCollection() {
      return this.entries;
   }

   @Override
   public String fetchMessage() {
      return this.activeName;
   }

   public static boolean checkState(IndirectSessionHandler<?> instance, PasswordHashBridge target, TightSenderAdapter[] input) {
      String[] output = target.dataFile.list();
      if (output == null) {
         throw new RuntimeException("Relocated dependencies files cannot be null!");
      }

      LiveLoginCheckpoint context = new LiveLoginCheckpoint();
      HashSet data = new HashSet<>(Arrays.asList(output));
      TightSenderAdapter[] value = Arrays.stream(values()).filter(TightSenderAdapter::getState).toArray(TightSenderAdapter[]::new);
      TightSenderAdapter[] result = Arrays.stream(input).filter(TightSenderAdapter::getState).toArray(TightSenderAdapter[]::new);
      TightSenderAdapter[] request = new TightSenderAdapter[value.length + result.length];
      System.arraycopy(value, 0, request, 0, value.length);
      System.arraycopy(result, 0, request, value.length, result.length);
      int response = 0;
      int source = 0;
      int entry = 0;

      for (TightSenderAdapter content : request) {
         String payload = content.handleMessage(false);
         if (content.loadCollection().isEmpty()) {
            if (!data.contains(payload)) {
               if (!data.contains(payload + ".tmp")) {
                  response++;
               }

               entry++;
            }
         } else if (!data.contains(payload)) {
            if (!data.contains(payload + ".tmp")) {
               response++;
            }

            source++;
            entry++;
         }
      }

      data.clear();
      int notice = entry;
      int event = Math.min(Runtime.getRuntime().availableProcessors(), notice);
      ThreadPoolExecutor packet;
      if (notice > 0) {
         String session = instance.retrieveMessage() + " Dependency Resolver #%s";
         AtomicInteger player = new AtomicInteger();
         ThreadFactory holder = Executors.defaultThreadFactory();
         ThreadFactory reference = outputValue -> {
            Thread contextValue = holder.newThread(outputValue);
            contextValue.setName(String.format(session, player.getAndIncrement()));
            return contextValue;
         };
         packet = (ThreadPoolExecutor)Executors.newFixedThreadPool(event, reference);
      } else {
         packet = null;
      }

      AtomicInteger account = new AtomicInteger(request.length);
      if (response > 0) {
         if (packet == null) {
            throw new IllegalStateException("Executor cannot be null!");
         }

         PasswordHashContainer.processMessage("§bDownloading required dependencies" + (event > 1 ? " (using " + event + " cores)" : "..."));

         for (TightSenderAdapter subject : request) {
            packet.execute(() -> {
               String contextValue = subject.getMessageForMessage();

               try {
                  if (VerifiedPasswordHashHasher.as()) {
                     PasswordHashContainer.dispatchMessage("Downloading " + contextValue + "... (" + Thread.currentThread().getName() + ")");
                  }

                  target.verifyState(subject, true, true);
               } finally {
                  if (VerifiedPasswordHashHasher.as()) {
                     PasswordHashContainer.dispatchMessage("Finished downloading " + contextValue + ". (" + Thread.currentThread().getName() + ")");
                  }

                  if (account.decrementAndGet() <= 0) {
                     synchronized (packet) {
                        packet.notify();
                     }
                  }
               }
            });
         }
      }

      if (packet != null && response > 0) {
         synchronized (packet) {
            try {
               packet.wait();
            } catch (InterruptedException argument) {
               PasswordHashContainer.sendThrowable(argument);
            }
         }
      }

      if (VerifiedPasswordHashHasher.as()) {
         PasswordHashContainer.dispatchMessage("Download process finished. Unlocking thread... (" + Thread.currentThread().getName() + ")");
      }

      if (source > 0) {
         if (packet == null) {
            throw new IllegalStateException("Executor cannot be null!");
         }

         PasswordHashContainer.processMessage("§bRelocating dependencies" + (event > 1 ? " (using " + event + " cores)" : "..."));
         target.sharedPasswordHashProvider = new SharedPasswordHashProvider(target);
         account.set(request.length);

         for (TightSenderAdapter spawn : request) {
            packet.execute(() -> {
               String contextValue = spawn.getMessageForMessage();

               try {
                  if (VerifiedPasswordHashHasher.as()) {
                     PasswordHashContainer.dispatchMessage("Relocating " + contextValue + "... (" + Thread.currentThread().getName() + ")");
                  }

                  target.processPath(spawn);
               } finally {
                  if (VerifiedPasswordHashHasher.as()) {
                     PasswordHashContainer.dispatchMessage("Finished relocating " + contextValue + ". (" + Thread.currentThread().getName() + ")");
                  }

                  if (account.decrementAndGet() <= 0) {
                     synchronized (packet) {
                        packet.notify();
                     }
                  }
               }
            });
         }
      } else {
         target.sharedPasswordHashProvider = new SharedPasswordHashProvider(target);
      }

      if (packet != null && source > 0) {
         synchronized (packet) {
            try {
               packet.wait();
            } catch (InterruptedException attribute) {
               PasswordHashContainer.sendThrowable(attribute);
            }
         }
      }

      if (VerifiedPasswordHashHasher.as()) {
         PasswordHashContainer.dispatchMessage("Relocate process finished. Unlocking thread... (" + Thread.currentThread().getName() + ")");
      }

      Collection connection = target.computeCollection(input);
      if (!connection.isEmpty()) {
         PasswordHashContainer.processMessage("§bRemoving unused dependencies (" + connection.size() + ")");
         connection.forEach(instanceValue -> {
            if (VerifiedPasswordHashHasher.as()) {
               PasswordHashContainer.dispatchMessage("Removing " + instanceValue.getAbsolutePath() + "... (" + Thread.currentThread().getName() + ")");
            }

            if (!instanceValue.delete()) {
               instanceValue.deleteOnExit();
            }
         });
         if (VerifiedPasswordHashHasher.as()) {
            PasswordHashContainer.dispatchMessage("Cleanup process finished.");
         }
      }

      byte client = 1;

      for (TightSenderAdapter setting : request) {
         if (!target.validateState(setting, true, true)) {
            client = 0;
            break;
         }
      }

      if (client == 0) {
         boolean position = PendingPasswordHashHasher.getPendingPasswordHashHasher().processVerifiedLoginGate("https://www.google.com").fetchState();
         PasswordHashContainer.updateMessage("");
         PasswordHashContainer.updateMessage("Dependencies could not be downloaded and enabled.");
         PasswordHashContainer.updateMessage("");
         if (!position) {
            PasswordHashContainer.updateMessage("This likely is a hosting error! (internet unavailable or HTTPS port closed)");
            PasswordHashContainer.updateMessage("Please contact your hosting provider's support.");
         } else {
            PasswordHashContainer.updateMessage("Please contact our team:");
            PasswordHashContainer.updateMessage(" Discord: §bwww.nickuc.com/discord");
            PasswordHashContainer.updateMessage(" Email: §fsupport@nickuc.com");
         }

         PasswordHashContainer.updateMessage("");
         PasswordHashContainer.updateMessage("Server will SHUTDOWN in 30 seconds.");
         PasswordHashContainer.updateMessage("");

         try {
            Thread.sleep(30000L);
         } catch (InterruptedException property) {
            PasswordHashContainer.sendThrowable(property);
         }
      } else if (notice > 0) {
         PasswordHashContainer.processMessage("§bAll dependencies were loaded successfully. Took " + context.fetchMessage() + "s");
      }

      if (packet != null) {
         packet.shutdown();
      }

      return (boolean)client;
   }

   InternalSpawnState(String output, String context, String data, String value, LoginLocator... result) {
      this.name = output.replace("{}", ".");
      this.activeName = context.replace("{}", ".");
      this.pendingName = data;
      this.currentName = value;
      this.entries = RemoteLoginBarrier.createRemoteLoginBarrier(Arrays.asList(result));
   }
}
