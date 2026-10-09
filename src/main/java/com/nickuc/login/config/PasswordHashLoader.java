package com.nickuc.login.config;

import com.nickuc.login.auth.message.MessageProcessor;
import com.nickuc.login.auth.login.RemoteLoginBarrier;
import com.nickuc.login.auth.settings.SettingsBarrier;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.DumperOptions.FlowStyle;
import org.yaml.snakeyaml.constructor.Constructor;
import com.nickuc.login.platform.listener.ChildListenerContract;
import com.nickuc.login.platform.sender.IncomingSenderAdapter;
import com.nickuc.login.security.hashing.MainPasswordHashVerifier;
import com.nickuc.login.security.hashing.PendingPasswordHashProvider;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;


public class PasswordHashLoader implements IncomingSenderAdapter<String>, ChildListenerContract {
   private static File dataFile;
   private final ThreadLocal<Yaml> threadLocal = ThreadLocal.withInitial(() -> {
      DumperOptions targetValue = new DumperOptions();
      targetValue.setDefaultFlowStyle(FlowStyle.BLOCK);
      SettingsBarrier inputValue = new SettingsBarrier(this, targetValue);
      return new Yaml(new Constructor(new LoaderOptions()), inputValue, targetValue);
   });
   private PendingPasswordHashProvider pendingPasswordHashProvider;
   private final File activeDataFile;

   public PasswordHashLoader(String target) {
      this(target, true);
   }

   public PasswordHashLoader(File target) {
      this(target, true);
   }

   public synchronized boolean loadState() {
      return this.hasState(false);
   }

   public synchronized boolean hasState(boolean target) {
      if (!this.activeDataFile.exists()) {
         return false;
      }

      try {
         BufferedReader input = Files.newBufferedReader(this.activeDataFile.toPath());

         byte result;
         try {
            StringBuilder output = new StringBuilder();

            String context;
            while ((context = input.readLine()) != null) {
               if (output.length() > 0) {
                  output.append("\n");
               }

               output.append(context);
            }

            String data = output.toString();
            if (target) {
               data = data.replaceAll("!<[^>\\s]+>", "");
            }

            Map value = (Map)this.threadLocal.get().loadAs(data, LinkedHashMap.class);
            if (value == null) {
               value = new LinkedHashMap();
            }

            this.pendingPasswordHashProvider = new PendingPasswordHashProvider(value, null);
            result = 1;
         } catch (Throwable response) {
            if (input != null) {
               try {
                  input.close();
               } catch (Throwable request) {
                  response.addSuppressed(request);
               }
            }

            throw response;
         }

         if (input != null) {
            input.close();
         }

         return (boolean)result;
      } catch (Exception source) {
         PasswordHashContainer.handleMessage("Cannot load " + this.activeDataFile + " config file, aborting load", source);
         return false;
      }
   }

   public boolean resolveState() {
      return this.pendingPasswordHashProvider != null;
   }

   public PasswordHashLoader(String target, File input, boolean output) {
      this(new File(input, target), output);
   }

   public boolean hasState(@Nonnull String target) {
      return this.canState(target);
   }

   public synchronized boolean findState() {
      if (!this.activeDataFile.exists()) {
         return false;
      } else if (!this.activeDataFile.delete()) {
         PasswordHashContainer.updateMessage("Cannot delete " + this.activeDataFile + " config file!");
         return false;
      } else {
         this.pendingPasswordHashProvider = null;
         return true;
      }
   }

   @Nullable
   public List<?> resolveCollection(String target, @Nullable List<?> input) {
      if (this.resolveState()) {
         List output = this.pendingPasswordHashProvider.resolveCollection(target, input);
         return output == null ? input : output;
      } else {
         return input;
      }
   }

   @Nonnull
   public List<String> processCollection(String target) {
      return this.computeCollection(target, RemoteLoginBarrier.currentEntries);
   }

   private synchronized boolean checkState(String target, boolean input) {
      boolean output = this.activeDataFile.exists();
      if (!output) {
         this.findState();

         try {
            MessageProcessor.checkState(target, this.activeDataFile);
         } catch (IOException data) {
            throw new RuntimeException(data);
         }
      }

      if (input) {
         this.loadState();
      }

      return !output;
   }

   @Override
   public <T> T findObject() {
      return (T)(this.resolveState() ? this.pendingPasswordHashProvider : null);
   }

   public Object buildObject(@Nonnull String target) {
      return this.resolveState() ? this.pendingPasswordHashProvider.handleObject(target) : null;
   }

   public PasswordHashLoader(File target, boolean input) {
      this.activeDataFile = target;
      if (input) {
         this.loadState();
      }
   }

   public boolean canState(String target) {
      return this.resolveState() && this.pendingPasswordHashProvider.handleObject(target) != null;
   }

   public synchronized boolean getState() {
      try {
         if (this.pendingPasswordHashProvider == null) {
            if (!MessageProcessor.isState(this.activeDataFile)) {
               throw new IOException("Cannot create empty config file " + this.activeDataFile + "!");
            }

            this.loadState();
            return true;
         } else {
            OutputStreamWriter target = new OutputStreamWriter(Files.newOutputStream(this.activeDataFile.toPath()), StandardCharsets.UTF_8);

            try {
               this.threadLocal.get().dump(this.pendingPasswordHashProvider.sessions, target);
            } catch (Throwable data) {
               try {
                  target.close();
               } catch (Throwable context) {
                  data.addSuppressed(context);
               }

               throw data;
            }

            target.close();
            return true;
         }
      } catch (IOException value) {
         PasswordHashContainer.handleMessage("Cannot save config file " + this.activeDataFile + "!", value);
         return false;
      }
   }

   public static void updateFile(File instance) {
      dataFile = instance;
   }

   public synchronized boolean hasStateForState(String target) {
      return this.checkState(target, true);
   }

   @Nullable
   public List<String> computeCollection(String target, @Nullable List<String> input) {
      if (!this.resolveState()) {
         return input;
      }

      List output = this.pendingPasswordHashProvider.resolveCollection(target, null);
      return output == null ? input : output.stream().map(instance -> instance != null ? instance.toString() : "null").collect(Collectors.toList());
   }

   public synchronized boolean validateState(String target) {
      return this.checkState(target, false);
   }

   public PasswordHashLoader(String target, File input) {
      this(target, input, true);
   }

   public Set<String> loadSet(String target) {
      if (!this.resolveState()) {
         return MainPasswordHashVerifier.activePlayers;
      }

      Collection input = target.isEmpty()
         ? this.pendingPasswordHashProvider.resolveCollection()
         : this.pendingPasswordHashProvider.buildPendingPasswordHashProvider(target).resolveCollection();
      input.removeIf(String::isEmpty);
      return new LinkedHashSet<>(input);
   }

   public synchronized boolean resolveStateForState() {
      return this.activeDataFile.exists();
   }

   public File retrieveFile() {
      return this.activeDataFile;
   }

   public void updateMessage(String target, Object input) {
      if (!this.resolveState()) {
         this.getState();
      }

      if (this.resolveState()) {
         this.pendingPasswordHashProvider.updateMessage(target, input);
      }
   }

   @Nonnull
   public List<?> createCollection(String target) {
      return this.resolveCollection(target, RemoteLoginBarrier.activeEntries);
   }

   @Override
   public String toString() {
      return "YamlConfig{file=" + this.activeDataFile + ", config=" + this.pendingPasswordHashProvider + '}';
   }

   public PasswordHashLoader(String target, boolean input) {
      this(new File(dataFile, target), input);
   }
}
