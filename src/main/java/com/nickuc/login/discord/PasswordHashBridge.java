package com.nickuc.login.discord;

import com.nickuc.login.auth.login.LoginProcessor;
import com.nickuc.login.config.PasswordHashContainer;
import com.nickuc.login.model.ChainedLoginKind;
import com.nickuc.login.model.InternalSpawnState;
import com.nickuc.login.model.PlatformCatalog;
import com.nickuc.login.loader.MemClassLoader;
import com.nickuc.login.platform.session.IndirectSessionHandler;
import com.nickuc.login.platform.sender.TightSenderAdapter;
import com.nickuc.login.security.hashing.MainPasswordHashVerifier;
import com.nickuc.login.security.hashing.PendingPasswordHashHasher;
import com.nickuc.login.security.hashing.SharedPasswordHashProvider;
import com.nickuc.login.spawn.LoginLocator;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.zip.ZipFile;
import javax.annotation.Nullable;


public class PasswordHashBridge {
   public SharedPasswordHashProvider sharedPasswordHashProvider;
   private final MemClassLoader memClassLoader;
   private final Map<MainPasswordHashVerifier<TightSenderAdapter>, LoginProcessor> sessions;
   public final File dataFile;
   private final Map<TightSenderAdapter, Path> activeSessions = new HashMap<>();
   private final String name;

   @Nullable
   private String loadMessage(TightSenderAdapter target) {
      String input = target.getMessage();
      if (input != null) {
         List output = target.loadCollection();
         if (!output.isEmpty()) {
            String context = ((LoginLocator)output.get(0)).loadMessage();
            input = String.format("com.nickuc.%s.lib.%s", this.name, context + "." + input);
         }
      }

      return input;
   }

   public boolean validateState(TightSenderAdapter target, boolean input) {
      File output = target.handleFile(this, false);
      if (!output.exists()) {
         if (input) {
            PasswordHashContainer.updateMessage("Unable to check zip integrity of the dependency " + target.getMessageForMessage() + ": file does not exists.");
         }

         return false;
      } else if (output.isDirectory()) {
         if (input) {
            PasswordHashContainer.updateMessage("Unable to check zip integrity of the dependency " + target.getMessageForMessage() + ": file is a directory.");
         }

         return false;
      } else {
         try {
            ZipFile context = new ZipFile(output);

            int value;
            try {
               String data = this.loadMessage(target);
               value = data != null && context.getEntry(data.replace('.', '/') + ".class") == null ? 0 : 1;
            } catch (Throwable request) {
               try {
                  context.close();
               } catch (Throwable result) {
                  request.addSuppressed(result);
               }

               throw request;
            }

            context.close();
            return (boolean)value;
         } catch (IOException response) {
            return false;
         }
      }
   }

   public LoginProcessor computeLoginProcessor(TightSenderAdapter... target) {
      if (target.length == 0) {
         throw new IllegalArgumentException("Dependencies cannot be empty!");
      }

      for (TightSenderAdapter data : target) {
         if (!this.activeSessions.containsKey(data)) {
            throw new IllegalStateException("Dependency " + data + " is not loaded.");
         }
      }

      MainPasswordHashVerifier request = MainPasswordHashVerifier.resolveMainPasswordHashVerifier(target);
      synchronized (this.sessions) {
         LoginProcessor source = this.sessions.get(request);
         if (source != null) {
            return source;
         }

         URL[] record = request.stream().map(this.activeSessions::get).map(instance -> {
            try {
               return instance.toUri().toURL();
            } catch (MalformedURLException input) {
               throw new RuntimeException(input);
            }
         }).toArray(URL[]::new);
         source = new LoginProcessor(record);
         this.sessions.put(request, source);
         return source;
      }
   }

   public String fetchMessage() {
      return this.name;
   }

   public boolean verifyState(TightSenderAdapter... target) {
      for (TightSenderAdapter data : target) {
         if (!this.validateState(data, true, false)) {
            return false;
         }
      }

      return true;
   }

   public boolean verifyState(TightSenderAdapter target, boolean input, boolean output) {
      File context = target.handleFile(this, false);
      if (context.exists()) {
         return true;
      }

      File data = target.handleFile(this, true);

      for (ChainedLoginKind response : ChainedLoginKind.values()) {
         if (response.isState(target, data)) {
            PlatformCatalog source = response.loadPlatformCatalog();
            String entry = response.resolveMessage(target, source);
            String record = source.buildMessage(data);
            if (!record.equals(entry)) {
               if (output && data.delete()) {
                  return this.verifyState(target, input, false);
               }

               if (input) {
                  String item = target.getMessageForMessage();
                  PasswordHashContainer.updateMessage("Unable to download dependency " + item + ": checksum error! " + record + " != " + entry);
               }

               if (!data.delete()) {
                  data.deleteOnExit();
               }

               return false;
            }

            return true;
         }
      }

      if (input) {
         String element = target.getMessageForMessage();
         PasswordHashContainer.updateMessage("Unable to download dependency " + element + ".");
      }

      return false;
   }

   @Nullable
   public Path processPath(TightSenderAdapter target) {
      File input = target.handleFile(this, false);
      if (input.exists()) {
         return input.toPath();
      }

      File output = target.handleFile(this, true);
      if (!target.loadCollection().isEmpty()) {
         try {
            this.sharedPasswordHashProvider.saveFile(output, input, target);
         } catch (Exception data) {
            throw new RuntimeException("Unable to relocate the dependency " + target.getMessageForMessage(), data);
         }

         if (!output.delete()) {
            output.deleteOnExit();
         }
      } else {
         if (!output.renameTo(input)) {
            PasswordHashContainer.updateMessage("Unable to move dependency " + target.getMessageForMessage() + ": insufficient permissions?");
            return null;
         }

         if (!output.delete()) {
            output.deleteOnExit();
         }
      }

      return input.toPath();
   }

   public PasswordHashBridge(IndirectSessionHandler<?> target, MemClassLoader input) {
      this.sessions = new HashMap<>();
      this.memClassLoader = input;
      this.name = target.retrieveMessage().substring(1).toLowerCase(Locale.ENGLISH);
      File output = new File(target.fetchFile(), "libraries");
      if (!output.exists() && !output.mkdirs()) {
         PasswordHashContainer.updateMessage("");
         PasswordHashContainer.updateMessage("Unable to create libraries folder: insufficient permissions?");
         PasswordHashContainer.updateMessage("");
         PasswordHashContainer.updateMessage("Please contact our team:");
         PasswordHashContainer.updateMessage(" Discord: §bwww.nickuc.com/discord");
         PasswordHashContainer.updateMessage(" Email: §fsupport@nickuc.com");
         PasswordHashContainer.updateMessage("");
         PasswordHashContainer.updateMessage("Server will SHUTDOWN in 30 seconds.");
         PasswordHashContainer.updateMessage("");

         try {
            Thread.sleep(30000L);
         } catch (InterruptedException value) {
            PasswordHashContainer.sendThrowable(value);
         }

         throw new RuntimeException("Unable to create libraries folder");
      } else {
         String context = target.retrieveMessage();
         this.dataFile = new File(output, context.toLowerCase(Locale.ENGLISH));
         if (!this.dataFile.exists() && !this.dataFile.mkdirs()) {
            PasswordHashContainer.updateMessage("");
            PasswordHashContainer.updateMessage("Unable to create plugin libraries folder: insufficient permissions?");
            PasswordHashContainer.updateMessage("");
            PasswordHashContainer.updateMessage("Please contact our team:");
            PasswordHashContainer.updateMessage(" Discord: §bwww.nickuc.com/discord");
            PasswordHashContainer.updateMessage(" Email: §fsupport@nickuc.com");
            PasswordHashContainer.updateMessage("");
            PasswordHashContainer.updateMessage("Server will SHUTDOWN in 30 seconds.");
            PasswordHashContainer.updateMessage("");

            try {
               Thread.sleep(30000L);
            } catch (InterruptedException result) {
               PasswordHashContainer.sendThrowable(result);
            }

            throw new RuntimeException("Unable to create libraries folder");
         }
      }
   }

   public boolean validateState(TightSenderAdapter target, boolean input, boolean output) {
      if (this.activeSessions.containsKey(target)) {
         return true;
      }

      if (!this.verifyState(target, true, true)) {
         return false;
      }

      Path context = this.processPath(target);
      if (context == null) {
         return false;
      }

      byte data = this.loadMask(target, !input, output);
      switch (data) {
         case 0:
            this.activeSessions.put(target, context);
            return true;
         case 1:
            this.canState(target);
         default:
            if (!input) {
               return false;
            } else {
               String value = target.getMessageForMessage();
               PasswordHashContainer.performMessage("The " + value + " dependency is broken. Trying to reinstall...");
               return this.canState(target) && this.validateState(target, false, output);
            }
      }
   }

   private byte loadMask(TightSenderAdapter target, boolean input, boolean output) {
      if (!this.validateState(target, input)) {
         return 1;
      }

      String context = this.loadMessage(target);
      File data = target.handleFile(this, false);

      try {
         if (output && !target.fetchState()) {
            return 0;
         }

         this.memClassLoader.addJarToClasspath(data.toURI().toURL());
         Throwable value = null;
         int result = context == null ? 1 : 0;
         if (result == 0) {
            try {
               this.memClassLoader.loadClass(context);
               result = 1;
            } catch (Throwable response) {
               value = response;
            }
         }

         if (result == 0) {
            String request = target.getMessageForMessage();
            PasswordHashContainer.handleMessage("Unable to enable the dependency " + request + ".", value);
            return 2;
         } else {
            return 0;
         }
      } catch (MalformedURLException source) {
         throw new RuntimeException("Unable to inject the dependency " + target.getMessageForMessage() + ".", source);
      }
   }

   public boolean canState(TightSenderAdapter target) {
      return this.hasState(target, true) && this.hasState(target, false);
   }

   public boolean canState(Set<TightSenderAdapter> target) {
      for (TightSenderAdapter output : target) {
         if (!this.validateState(output, true, false)) {
            return false;
         }
      }

      return true;
   }

   public boolean verifyState(TightSenderAdapter target) {
      return target.handleFile(this, false).exists();
   }

   public Collection<File> computeCollection(TightSenderAdapter[] target) {
      File[] input = this.dataFile.listFiles();
      if (input != null && input.length != 0) {
         InternalSpawnState[] output = InternalSpawnState.values();
         HashSet context = new HashSet(output.length + target.length);
         context.addAll(Arrays.asList(output));
         context.addAll(Arrays.asList(target));
         HashSet data = new HashSet<>(Arrays.asList(input));
         data.removeAll(context.stream().map(targetValue -> targetValue.handleFile(this, false)).collect(Collectors.toSet()));
         return MainPasswordHashVerifier.processMainPasswordHashVerifier(data);
      } else {
         return Collections.emptySet();
      }
   }

   public boolean hasState(TightSenderAdapter target, boolean input) {
      File output = target.handleFile(this, input);
      if (output.exists() && !output.delete()) {
         output.deleteOnExit();
         String context = target.getMessageForMessage();
         PasswordHashContainer.updateMessage("Unable to delete dependency " + context + ": insufficient permissions?");
         return false;
      } else {
         return true;
      }
   }

   public boolean hasState(IndirectSessionHandler<?> target, TightSenderAdapter[] input) {
      try {
         return InternalSpawnState.checkState(target, this, input);
      } catch (Throwable result) {
         boolean context = PendingPasswordHashHasher.getPendingPasswordHashHasher().processVerifiedLoginGate("https://www.google.com").fetchState();
         PasswordHashContainer.sendThrowable(result);
         PasswordHashContainer.updateMessage("");
         PasswordHashContainer.updateMessage("Dependencies could not be downloaded and enabled.");
         PasswordHashContainer.updateMessage("");
         if (!context) {
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
         } catch (InterruptedException value) {
            PasswordHashContainer.sendThrowable(value);
         }

         return false;
      }
   }

   public boolean canStateForState(TightSenderAdapter target) {
      return target.handleFile(this, true).exists();
   }
}
