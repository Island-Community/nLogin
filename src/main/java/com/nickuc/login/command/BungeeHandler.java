package com.nickuc.login.command;

import com.nickuc.login.auth.message.MessageProcessor;
import com.nickuc.login.config.PasswordHashContainer;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import com.nickuc.login.platform.session.IndirectSessionHandler;
import com.nickuc.login.platform.proxy.SilentProxyState;
import com.nickuc.login.security.hashing.VerifiedPasswordHashHasher;
import java.io.File;
import java.io.InputStream;
import java.util.Collections;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import javax.annotation.Nullable;

public class BungeeHandler {
   private static final Yaml yaml = new Yaml(new Constructor(new LoaderOptions()));

   public static void processIndirectSessionHandler(IndirectSessionHandler<?> instance, File target) {
      SilentProxyState input = instance.findIncomingLoginGate().retrieveSilentProxyState();
      String output = input == SilentProxyState.ACTIVE_SILENTPROXYSTATE ? "bungee.yml" : "plugin.yml";
      String context = instance.fetchParentSettingsLookup().retrieveFile().getName();

      try {
         File[] data = target.listFiles();
         if (data == null) {
            return;
         }

         for (File response : data) {
            if (!response.isDirectory()) {
               String source = response.getName();
               if (!context.equals(source) && source.endsWith(".jar")) {
                  try {
                     Map entry = handleTable(response, output);
                     if (entry != null) {
                        String record = (String)entry.get("name");
                        if (instance.retrieveMessage().equals(record) && !response.delete()) {
                           if (VerifiedPasswordHashHasher.as()) {
                              PasswordHashContainer.updateMessage(
                                 "Unable to remove old jars from the plugin: permission denied? [checking the file \"" + source + "\"]"
                              );
                           }

                           response.deleteOnExit();
                        }
                     }
                  } catch (Throwable item) {
                     if (VerifiedPasswordHashHasher.as()) {
                        PasswordHashContainer.handleMessage(
                           "Unable to remove old jars from the plugin: " + item.getMessage() + " [checking the file \"" + source + "\"]", item
                        );
                     }
                  }
               }
            }
         }
      } catch (Exception element) {
         PasswordHashContainer.sendThrowable(element);
         PasswordHashContainer.updateMessage("Unable to remove old jars from the plugin");
      }
   }

   public static void handleIndirectSessionHandler(IndirectSessionHandler<?> instance, File target) {
      if (target.isDirectory()) {
         sendIndirectSessionHandler(instance, target);
      } else if (MessageProcessor.validateState(target, 2592000000L)) {
         if (VerifiedPasswordHashHasher.as()) {
            PasswordHashContainer.dispatchMessage("Deleting old file: \"" + target + "\"");
         }

         if (!target.delete()) {
            target.deleteOnExit();
         }
      }
   }

   @Nullable
   public static Map<String, Object> handleTable(File instance, String target) {
      if (target != null) {
         JarFile input = new JarFile(instance);

         try {
            JarEntry output = input.getJarEntry(target);
            if (output != null) {
               InputStream context = input.getInputStream(output);

               try {
                  return (Map<String, Object>)yaml.load(context);
               } finally {
                  if (Collections.singletonList(context).get(0) != null) {
                     context.close();
                  }
               }
            }
         } finally {
            if (Collections.singletonList(input).get(0) != null) {
               input.close();
            }
         }
      }

      return null;
   }

   private static void sendIndirectSessionHandler(IndirectSessionHandler<?> instance, File target) {
      File[] input = target.listFiles();
      if (input != null) {
         for (File value : input) {
            handleIndirectSessionHandler(instance, value);
         }
      }
   }
}
